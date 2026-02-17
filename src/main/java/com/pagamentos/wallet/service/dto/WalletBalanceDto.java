package com.pagamentos.wallet.service.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WalletBalanceDto(String documento, String email) {
}
