package com.pagamentos.wallet.service;

import com.pagamentos.wallet.domain.Wallet;
import com.pagamentos.wallet.repository.WalletRepository;
import com.pagamentos.wallet.service.dto.TransferDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransferServiceConcurrencyTest {

    @Autowired
    private TransferService transferService;

    @Autowired
    private WalletRepository walletRepository;

    @Test
    @DisplayName("Deve prevenir Deadlock usando UUIDs e manter consistência do saldo")
    void testConcurrentTransfers() throws ExecutionException, InterruptedException {

        // 1. SETUP: Criar duas carteiras reais no banco
        Wallet w1 = Wallet.builder()
                .fullname("Tony Stark")
                .cpf("111.111.111-11")
                .email("tony@stark.com")
                .password("jarvis123")
                .balance(new BigDecimal("1000.00"))
                .build();

        Wallet w2 = Wallet.builder()
                .fullname("Steve Rogers")
                .cpf("222.222.222-22")
                .email("cap@america.com")
                .password("shield123")
                .balance(new BigDecimal("1000.00"))
                .build();

        w1 = walletRepository.save(w1);
        w2 = walletRepository.save(w2);

        // Capturamos os UUIDs gerados
        final UUID id1 = w1.getId();
        final UUID id2 = w2.getId();
        final BigDecimal amount = new BigDecimal("100.00");

        // 2. ACTION: Executar transferências cruzadas simultâneas

        // Thread 1: Carteira 1 -> Carteira 2
        CompletableFuture<Void> transfer1 = CompletableFuture.runAsync(() -> {
            System.out.println("Thread 1: Transferindo de " + id1 + " para " + id2);
            transferService.transfer(new TransferDto(id1, id2, amount));
        });

        // Thread 2: Carteira 2 -> Carteira 1 (O cenário clássico de Deadlock)
        CompletableFuture<Void> transfer2 = CompletableFuture.runAsync(() -> {
            System.out.println("Thread 2: Transferindo de " + id2 + " para " + id1);
            transferService.transfer(new TransferDto(id2, id1, amount));
        });

        // Aguarda o término das duas operações
        CompletableFuture.allOf(transfer1, transfer2).get();

        // 3. ASSERT: Validar consistência
        // Matemática: 1000 - 100 + 100 = 1000. O saldo deve permanecer intacto.

        Wallet w1Updated = walletRepository.findById(id1).orElseThrow();
        Wallet w2Updated = walletRepository.findById(id2).orElseThrow();

        System.out.println("Saldo Final W1: " + w1Updated.getBalance());
        System.out.println("Saldo Final W2: " + w2Updated.getBalance());

        assertEquals(0, w1Updated.getBalance().compareTo(new BigDecimal("1000.00")), "Saldo W1 incorreto!");
        assertEquals(0, w2Updated.getBalance().compareTo(new BigDecimal("1000.00")), "Saldo W2 incorreto!");
    }

}