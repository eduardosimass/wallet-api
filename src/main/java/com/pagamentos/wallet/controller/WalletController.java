package com.pagamentos.wallet.controller;

import com.pagamentos.wallet.domain.Wallet;
import com.pagamentos.wallet.service.WalletService;
import com.pagamentos.wallet.service.dto.BalanceDto;
import com.pagamentos.wallet.service.dto.WalletBalanceDto;
import com.pagamentos.wallet.service.dto.WalletDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Transferências", description = "Endpoint para criar novas Carteiras")
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;


    @PostMapping("/newWallet")
    @Operation(summary = "Criar nova Carteira", description = "Cria uma nova Carteira Digital")
    public ResponseEntity<Wallet> newWallet(@RequestBody @Valid WalletDto walletDto){
        Wallet wallet = walletService.createWallet(walletDto);
        return ResponseEntity.ok(wallet);
    }

    @PostMapping("/consultaSaldo")
    @Operation(summary = "Buscar Saldo", description = "Buscar Saldo de uma cateira")
    public ResponseEntity<BalanceDto> consultaSaldo(@RequestBody WalletBalanceDto walletBalanceDto){
        Wallet wallet = walletService.buscaSaldo(walletBalanceDto);
        BalanceDto balanceDto = BalanceDto
                .builder()
                .nome(wallet.getFullname())
                .balance(wallet.getBalance())
                .build();
        return ResponseEntity.ok(balanceDto);
    }
}
