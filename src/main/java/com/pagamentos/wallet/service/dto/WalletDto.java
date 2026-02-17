package com.pagamentos.wallet.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WalletDto(
        @NotBlank(message = "O nome completo não pode estar em branco")
        String fullname,
        @NotBlank(message = "O CPF é obrigatório")
        @CPF(message = "CPF inválido")
        String documento,
        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Formato de email inválido")
        String email,
        @NotBlank(message = "A senha é obrigatória")
        String password,
        @NotNull(message = "O saldo inicial não pode ser nulo")
        BigDecimal balance,
        LocalDateTime createdAt
) {
}
