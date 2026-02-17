package com.pagamentos.wallet.service.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferNotificationDto(
        Long transactionId,
        UUID payerId,
        UUID payeeId,
        BigDecimal amount,
        String timestamp
) {}
