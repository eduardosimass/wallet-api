CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE wallets (
                         id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                         full_name VARCHAR(100) NOT NULL,
                         cpf VARCHAR(14) UNIQUE NOT NULL,
                         email VARCHAR(100) UNIQUE NOT NULL,
                         password VARCHAR(255) NOT NULL,
                         balance DECIMAL(20, 2) NOT NULL DEFAULT 0.00,
                         version BIGINT NOT NULL DEFAULT 0,
                         created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- CONSTRAINT DE SEGURANÇA: O banco rejeita saldo negativo.
    -- Mesmo que o Java erre a conta, o banco impede a transação.
                         CONSTRAINT chk_wallet_balance_non_negative CHECK (balance >= 0)
);

-- 2. Tabela de TRANSAÇÕES (Transactions)
CREATE TABLE transactions (
                              id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

                              payer_wallet_id UUID REFERENCES wallets(id),

                              payee_wallet_id UUID REFERENCES wallets(id),

                              value DECIMAL(20, 2) NOT NULL,
                              type VARCHAR(20) NOT NULL, -- TRANSFER, DEPOSIT, WITHDRAW

                              idempotency_key VARCHAR(255) UNIQUE,

                              created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

                              CONSTRAINT chk_transaction_value_positive CHECK (value > 0)
);

CREATE TABLE outbox (
                        id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                        aggregate_type VARCHAR(255) NOT NULL, -- Ex: "Transaction"
                        aggregate_id VARCHAR(255) NOT NULL,   -- O ID da transação
                        type VARCHAR(255) NOT NULL,           -- Ex: "TRANSACTION_CREATED"
                        payload JSONB NOT NULL,               -- O conteúdo do evento em JSON
                        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                        processed BOOLEAN DEFAULT FALSE       -- Marcado como true após envio ao Kafka
);

CREATE INDEX idx_transactions_payer ON transactions(payer_wallet_id);
CREATE INDEX idx_transactions_payee ON transactions(payee_wallet_id);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);