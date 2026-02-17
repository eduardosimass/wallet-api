package com.pagamentos.wallet.repository;

import com.pagamentos.wallet.domain.Transaction;
import com.pagamentos.wallet.domain.Wallet;
import org.springframework.data.jpa.mapping.JpaPersistentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
