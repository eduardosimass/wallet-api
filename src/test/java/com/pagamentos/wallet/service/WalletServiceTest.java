package com.pagamentos.wallet.service;

import com.pagamentos.wallet.domain.Wallet;
import com.pagamentos.wallet.exception.WalletDataException;
import com.pagamentos.wallet.exception.WalletNotFoundException;
import com.pagamentos.wallet.repository.WalletRepository;
import com.pagamentos.wallet.service.dto.WalletBalanceDto;
import com.pagamentos.wallet.service.dto.WalletDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @InjectMocks
    private WalletService walletService;

    @Mock
    private WalletRepository walletRepository;

    @Test
    @DisplayName("Deve criar carteira com sucesso quando dados são válidos")
    void createWalletSuccess() {
        // Arrange (Preparação)
        WalletDto dto = new WalletDto("Tony Stark", "12345678900", "tony@stark.com", "123456", BigDecimal.TEN, LocalDateTime.now());
        when(walletRepository.existsByCpf(any())).thenReturn(false);
        when(walletRepository.existsByEmail(any())).thenReturn(false);
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act (Ação)
        Wallet created = walletService.createWallet(dto);

        // Assert (Verificação)
        assertNotNull(created);
        assertEquals("tony@stark.com", created.getEmail());
        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF já existe")
    void createWalletDuplicateCpf() {
        WalletDto dto = new WalletDto("Tony Stark", "12345678900", "tony@stark.com", "123456", BigDecimal.TEN, LocalDateTime.now());
        when(walletRepository.existsByCpf(dto.documento())).thenReturn(true);

        assertThrows(WalletDataException.class, () -> walletService.createWallet(dto));
        verify(walletRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar erro quando carteira não encontrada no saldo")
    void buscaSaldoNotFound() {
        WalletBalanceDto dto = new WalletBalanceDto("123", "email@teste.com");
        when(walletRepository.findByCpfOrEmail(any(), any())).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> walletService.buscaSaldo(dto));
    }
}