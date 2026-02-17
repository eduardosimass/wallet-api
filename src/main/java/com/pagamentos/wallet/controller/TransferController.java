package com.pagamentos.wallet.controller;

import com.pagamentos.wallet.domain.Transaction;
import com.pagamentos.wallet.service.TransferService;
import com.pagamentos.wallet.service.dto.TransferDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@Tag(name = "Transferências", description = "Endpoints para realização de transações financeiras")
@RequestMapping("/transfer")
public class TransferController {

    @Autowired
    private TransferService transferService;


    @Operation(summary = "Realizar transferência", description = "Transfere valores entre duas carteiras (Payer -> Payee)")
    @PostMapping
    public ResponseEntity<Transaction> performTransfer(@RequestBody @Valid TransferDto transferDto) throws InterruptedException, ExecutionException {
        Transaction transaction = transferService.transfer(transferDto);
        return ResponseEntity.ok(transaction);
    }




}
