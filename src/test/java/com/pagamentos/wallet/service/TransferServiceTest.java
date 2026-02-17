package com.pagamentos.wallet.service;

import com.pagamentos.wallet.domain.Transaction;
import com.pagamentos.wallet.domain.Wallet;
import com.pagamentos.wallet.exception.WalletDataException;
import com.pagamentos.wallet.repository.TransactionRepository;
import com.pagamentos.wallet.repository.WalletRepository;
import com.pagamentos.wallet.service.dto.TransferDto;
import com.pagamentos.wallet.service.dto.TransferNotificationDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @InjectMocks
    private TransferService transferService;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private KafkaProducerService kafkaService;

    @Test
    @DisplayName("Deve realizar transferência com sucesso e notificar Kafka")
    void transferSuccess() {
        // Arrange
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID(); // Garante IDs diferentes

        // Mockamos carteiras reais
        Wallet payer = Wallet.builder().id(id1).balance(new BigDecimal("100.00")).build();
        Wallet payee = Wallet.builder().id(id2).balance(new BigDecimal("0.00")).build();
        Transaction transactionMock = Transaction.builder().id(1L).payer(payer).payee(payee).amount(BigDecimal.TEN).timestamp(java.time.LocalDateTime.now()).build();

        // Simulamos o banco encontrando as carteiras
        // Obs: Como não sabemos qual ID é maior (UUID aleatório), usamos any() para simplificar este teste específico
        when(walletRepository.findByIdWithLock(any(UUID.class))).thenReturn(Optional.of(payer)).thenReturn(Optional.of(payee));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transactionMock);

        TransferDto dto = new TransferDto(id1, id2, BigDecimal.TEN);

        // Act
        transferService.transfer(dto);

        // Assert
        assertEquals(new BigDecimal("90.00"), payer.getBalance()); // 100 - 10
        assertEquals(new BigDecimal("10.00"), payee.getBalance()); // 0 + 10

        // Verifica se salvou as carteiras
        verify(walletRepository, times(2)).save(any(Wallet.class));

        // Verifica se enviou pro Kafka
        verify(kafkaService).sendTransferNotification(any(TransferNotificationDto.class));
    }

    @Test
    @DisplayName("Anti-Deadlock: Deve sempre bloquear o menor ID primeiro")
    void testAntiDeadlockOrdering() {
        // Arrange - Criamos dois UUIDs e garantimos que sabemos quem é o menor
        UUID idA = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID idB = UUID.fromString("00000000-0000-0000-0000-000000000002");

        Wallet w1 = Wallet.builder().id(idA).balance(BigDecimal.TEN).build();
        Wallet w2 = Wallet.builder().id(idB).balance(BigDecimal.TEN).build();
        Transaction tMock = Transaction.builder().id(1L).payer(w1).payee(w2).amount(BigDecimal.ONE).timestamp(java.time.LocalDateTime.now()).build();

        when(walletRepository.findByIdWithLock(idA)).thenReturn(Optional.of(w1));
        when(walletRepository.findByIdWithLock(idB)).thenReturn(Optional.of(w2));
        when(transactionRepository.save(any())).thenReturn(tMock);

        // Cenário 1: Transferir de A(Menor) para B(Maior)
        transferService.transfer(new TransferDto(idA, idB, BigDecimal.ONE));

        // Cenário 2: Transferir de B(Maior) para A(Menor)
        transferService.transfer(new TransferDto(idB, idA, BigDecimal.ONE));

        // Assert - A "Mágica" do Mockito InOrder
        // Vamos verificar se, em AMBOS os casos, o findByIdWithLock foi chamado primeiro para o ID_A (menor)

        InOrder inOrder = inOrder(walletRepository);

        // Na primeira chamada
        inOrder.verify(walletRepository).findByIdWithLock(idA); // Primeiro lock
        inOrder.verify(walletRepository).findByIdWithLock(idB); // Segundo lock

        // Na segunda chamada (mesmo invertendo os parametros, a ordem de lock deve ser a mesma!)
        inOrder.verify(walletRepository).findByIdWithLock(idA); // Primeiro lock
        inOrder.verify(walletRepository).findByIdWithLock(idB); // Segundo lock
    }

    @Test
    @DisplayName("Deve falhar se tentar transferir para si mesmo")
    void transferSelfFailure() {
        UUID id = UUID.randomUUID();
        TransferDto dto = new TransferDto(id, id, BigDecimal.TEN);

        assertThrows(WalletDataException.class, () -> transferService.transfer(dto));
        verifyNoInteractions(kafkaService); // Garante que não mandou nada pro Kafka
    }
}