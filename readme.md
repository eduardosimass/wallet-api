# 💸 Wallet API - Sistema de Pagamentos Simplificado

Uma API robusta para transferências financeiras entre usuários e lojistas, com foco em concorrência e consistência de dados.

## 🚀 Tecnologias
- **Java 17** & **Spring Boot 3**
- **PostgreSQL** (Banco de dados relacional)
- **Apache Kafka** (Mensageria assíncrona para notificações)
- **Docker & Docker Compose** (Containerização)
- **Spring Data JPA** (Com Pessimistic Locking)
- **Swagger/OpenAPI** (Documentação)

## ⚡ Destaques da Arquitetura
1.  **Concorrência e Locks:** Utilização de `PESSIMISTIC_WRITE` no banco de dados para garantir que o saldo não seja alterado simultaneamente por duas transações.
2.  **Prevenção de Deadlock:** Implementação de estratégia de ordenação de recursos (Lock Ordering) baseada nos IDs das carteiras, evitando travamento mútuo do banco de dados em alta concorrência.
3.  **Mensageria:** Integração com Kafka para desacoplar o serviço de notificações do fluxo principal da transação (Event-Driven).
4.  **Segurança:** Validações de negócio (saldo insuficiente, transferências para si mesmo).

## 🛠️ Como rodar
```bash
docker-compose up -d
./mvnw spring-boot:run

