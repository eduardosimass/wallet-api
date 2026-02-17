💸 Wallet API - Sistema de Pagamentos Simplificado
Esta é uma API robusta para gerenciamento de carteiras digitais e realização de transferências financeiras (P2P), desenvolvida com foco total em consistência de dados, concorrência e resiliência.

O projeto simula o backend de uma fintech, resolvendo desafios críticos como a prevenção de gastos duplos e o tratamento de deadlocks em alta volumetria de transações.

🛠️ Tecnologias e Ferramentas
Java 17 & Spring Boot 3: Base da aplicação.

PostgreSQL: Banco de dados relacional para persistência transacional.

Apache Kafka: Mensageria assíncrona para o serviço de notificações.

Spring Data JPA: Abstração de banco de dados com suporte a Locks.

Docker & Docker Compose: Containerização de toda a infraestrutura.

Swagger/OpenAPI 3: Documentação interativa da API.

JUnit 5 & Mockito: Garantia de qualidade via testes unitários.

🚀 Diferenciais Técnicos (O "Porquê")
1. Consistência e Concorrência (Pessimistic Locking)
   Em sistemas financeiros, a consistência é inegociável. Utilize PESSIMISTIC_WRITE para garantir que, durante uma transferência, o saldo de uma carteira não possa ser alterado por outra transação simultânea. Isso evita o problema de Lost Update (Atualização Perdida).

2. Estratégia Anti-Deadlock (Lock Ordering)
   Um problema comum em transferências paralelas (A -> B e B -> A ao mesmo tempo) é o Deadlock Circular. Implementei uma lógica que ordena os IDs antes de solicitar o Lock no banco. O sistema sempre bloqueia o registro com o menor ID primeiro, garantindo que o banco de dados nunca entre em estado de travamento mútuo.

3. Arquitetura Orientada a Eventos (Event-Driven)
   A notificação de sucesso da transação é desacoplada via Kafka. Assim que a transferência é persistida, um evento é disparado. Isso garante que:

A API responda rapidamente ao usuário.

O sistema seja resiliente (se o serviço de notificação cair, a mensagem fica no Kafka para processamento posterior).

4. Tratamento de Erros Padronizado (RFC 7807)
   A API utiliza o padrão ProblemDetail, retornando erros semânticos e estruturados (400, 404, 422), facilitando a integração com o Front-end e sistemas externos.

📋 Como Executar
1. Clonar o repositório:

Bash
git clone 
2. Subir a infraestrutura (Postgres e Kafka):

Bash
docker-compose up -d
3. Executar a aplicação:

Bash
./mvnw spring-boot:run
4. Acessar a documentação:
   Acesse http://localhost:8080/swagger-ui.html para testar os endpoints.

🧪 Testes
Para rodar a suíte de testes unitários e validar a integridade do sistema:

Bash
./mvnw test
Desenvolvido por Eduardo Simas

Engenheiro de Software focado em soluções escaláveis.