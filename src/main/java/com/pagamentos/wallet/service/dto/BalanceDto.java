package com.pagamentos.wallet.service.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Data
@Getter
public class BalanceDto {

    private String nome;
    private BigDecimal balance = BigDecimal.ZERO;

}
