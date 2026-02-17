package com.pagamentos.wallet.service;

import com.pagamentos.wallet.domain.Transaction;
import com.pagamentos.wallet.domain.Wallet;
import com.pagamentos.wallet.exception.WalletDataException;
import com.pagamentos.wallet.exception.WalletNotFoundException;
import com.pagamentos.wallet.repository.TransactionRepository;
import com.pagamentos.wallet.repository.WalletRepository;
import com.pagamentos.wallet.service.dto.TransferDto;
import com.pagamentos.wallet.service.dto.TransferNotificationDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final KafkaProducerService kafkaService;


    @Transactional(rollbackOn = Exception.class)
    public Transaction transfer(TransferDto transferDto){
        if (transferDto.payerId().equals(transferDto.payeeId())){
            throw new WalletDataException("Você não pode transferir dinheiro para si mesmo.");
        }
        UUID firstId = transferDto.payerId().compareTo(transferDto.payeeId()) < 0 ? transferDto.payerId() : transferDto.payeeId();
        UUID secondId = transferDto.payerId().compareTo(transferDto.payeeId()) < 0 ? transferDto.payeeId() : transferDto.payerId();

        Wallet lock1 = walletRepository.findByIdWithLock(firstId)
                .orElseThrow(() -> new WalletNotFoundException("Carteira ID " + firstId + " não encontrada!"));

        Wallet lock2 = walletRepository.findByIdWithLock(secondId)
                .orElseThrow(() -> new WalletNotFoundException("Carteira ID " + secondId + " não encontrada!"));

        Wallet payer = transferDto.payerId().equals(lock1.getId()) ? lock1 : lock2;
        Wallet payee = transferDto.payeeId().equals(lock1.getId()) ? lock1 : lock2;

        payer.debit(transferDto.value());
        payee.credit(transferDto.value());

        walletRepository.save(payer);
        walletRepository.save(payee);

        var transaction = Transaction.builder()
                .payer(payer)
                .payee(payee)
                .amount(transferDto.value())
                .type(com.pagamentos.wallet.domain.TransactionType.PAGAMENTO_TERCEIROS)
                .timestamp(java.time.LocalDateTime.now())
                .description("Transferência entre carteiras")
                .build();

        transaction = transactionRepository.save(transaction);

        var notification = new TransferNotificationDto(
                transaction.getId(),
                transaction.getPayer().getId(),
                transaction.getPayee().getId(),
                transaction.getAmount(),
                transaction.getTimestamp().toString());

        kafkaService.sendTransferNotification(notification);
        return transaction;

    }

}
