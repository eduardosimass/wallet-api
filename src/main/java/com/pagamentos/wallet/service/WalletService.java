package com.pagamentos.wallet.service;

import com.pagamentos.wallet.domain.Wallet;
import com.pagamentos.wallet.exception.WalletDataException;
import com.pagamentos.wallet.exception.WalletNotFoundException;
import com.pagamentos.wallet.repository.WalletRepository;
import com.pagamentos.wallet.service.dto.WalletBalanceDto;
import com.pagamentos.wallet.service.dto.WalletDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;


    public Wallet createWallet(WalletDto walletDto){

        if (walletRepository.existsByCpf(walletDto.documento())) {
            throw new WalletDataException("Já existe uma carteira cadastrada com este CPF.");
        }

        if (walletRepository.existsByEmail(walletDto.email())) {
            throw new WalletDataException("Já existe uma carteira cadastrada com este Email.");

        }
        Wallet wallet =  Wallet.builder()
                .fullname(walletDto.fullname())
                .cpf(walletDto.documento())
                .email(walletDto.email())
                .password(walletDto.password())
                .balance(walletDto.balance())
                .build();
        return walletRepository.save(wallet);
    }


    public Wallet buscaSaldo(WalletBalanceDto walletBalanceDto){
        Optional<Wallet> saldoWallet = walletRepository.findByCpfOrEmail(walletBalanceDto.documento(), walletBalanceDto.email());
        return walletRepository.findByCpfOrEmail(walletBalanceDto.documento(), walletBalanceDto.email())
                .orElseThrow(() -> new WalletNotFoundException("Carteira não encontrada. Verifique o CPF ou Email informado."));
    }

}
