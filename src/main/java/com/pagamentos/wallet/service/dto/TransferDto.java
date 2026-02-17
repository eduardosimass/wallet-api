package com.pagamentos.wallet.service.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferDto(UUID payerId, UUID payeeId, BigDecimal value){

}