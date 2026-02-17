# 💸 Wallet API — Sistema de Pagamentos P2P com Alta Concorrência

API backend para gerenciamento de **carteiras digitais** e **transferências financeiras P2P**, projetada para simular cenários reais de uma fintech.

O foco do projeto é resolver problemas **críticos de sistemas financeiros**, como:

- Prevenção de **double spending**
- Controle de **concorrência em alto volume**
- Eliminação de **deadlocks**
- Consistência transacional forte

Tudo isso priorizando **clareza arquitetural**, **resiliência** e **boas práticas de engenharia**.

---

## 🛠️ Tecnologias e Ferramentas

- Java 17 + Spring Boot 3  
- PostgreSQL  
- Apache Kafka  
- Spring Data JPA (Locks pessimistas)  
- Docker & Docker Compose  
- Swagger / OpenAPI 3  
- JUnit 5 & Mockito  

---

## 🚀 Diferenciais Técnicos (O *Porquê*)

### 1️⃣ Consistência de Dados com Pessimistic Locking

Durante uma transferência, o saldo da carteira é protegido com `PESSIMISTIC_WRITE`, impedindo alterações simultâneas.

✔ Evita Lost Update  
✔ Impede double spending  
✔ Garante atomicidade  

---

### 2️⃣ Estratégia Anti-Deadlock (Lock Ordering)

Os IDs das carteiras são ordenados antes do lock.  
O sistema sempre bloqueia primeiro o menor ID.

✔ Deadlock impossível por construção  

---

### 3️⃣ Arquitetura Orientada a Eventos

Após a transferência, um evento é publicado no Kafka para notificação.

✔ Alta performance  
✔ Desacoplamento  
✔ Resiliência  

---

### 4️⃣ Tratamento de Erros (RFC 7807)

Uso de **ProblemDetail** com erros semânticos:

- 400 — Requisição inválida  
- 404 — Não encontrado  
- 422 — Regra de negócio  

---

## 📋 Como Executar

```bash
git clone https://github.com/eduardosimass/Wallet
docker-compose up -d
./mvnw spring-boot:run
```

Swagger:
```
http://localhost:8080/swagger-ui.html
```

---

## 🧪 Testes

```bash
./mvnw test
```

---

## 👨‍💻 Autor

Eduardo Simas  
Engenheiro de Software focado em sistemas escaláveis e arquitetura backend.
