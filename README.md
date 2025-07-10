# Projeto Spring Boot - Consent State Machine com RabbitMQ

Este projeto demonstra o uso de uma **máquina de estados (State Machine)** com **Spring Boot**, integrada com **MongoDB**, **RabbitMQ** e **Docker**, para gerenciar o ciclo de vida de consentimentos.
---

## 🚀 Tecnologias Utilizadas
- **Spring Boot** – Framework principal da aplicação
- **Spring State Machine** – Controle e transição de estados
- **MongoDB** – Persistência de dados dos consentimentos
- **Docker** – Containerização da aplicação e dos serviços
- **RabbitMQ** – Comunicação assíncrona (mensageria)
Login padrão do RabbitMQ:
Usuário: guest
Senha: guest

### Pré-requisitos
- Java 17+
- Docker e Docker Compose
- Git

## ⚙️ Como Executar o Projeto
git clone https://github.com/diegosena7/state-machine-example.git
Acesse o repositório onde clonou o repositorio: cd seu-repositorio
Execute o comando: docker-compose up -d
Você pode rodar pela sua IDE (IntelliJ, Eclipse) ou via terminal: ./mvnw spring-boot:run

### Funcionamento da State Machine
A State Machine (Máquina de Estados) neste projeto gerencia as transições de estado dos consentimentos, seguindo um fluxo definido:
1. Estado Inicial (AUTHORISED) → Evento (EXPIRE) → Estado Final (EXPIRED)
2. Estado Inicial (AWAITING_AUTHORISATION) → Evento (REJECT) → Estado Final (REJECTED)

A máquina garante que as transições só ocorram de acordo com as regras definidas na configuração.

### Fluxo de Execução:
1. : `processConsent`
    - Valida o ID do consentimento
    - Busca a entidade
    - Valida o estado atual
    - Inicia o processo de transição
   
2. : `processStateMachineTransition`
   - Valida a regra necessária para dispachar a máquina de estado para EXPIRED/REJECTED
   
3. : `configureStateMachine`
    - Para a máquina atual
    - Reseta para o estado atual da entidade
    - Reinicia a máquina

4. : `executeStateTransitionToExpired`
    - Envia o evento EXPIRE
    - Verifica se a transição foi bem-sucedida
    - Atualiza o estado no banco de dados

5. : `executeStateTransitionToRejected`
   - Envia o evento REJECT
   - Verifica se a transição foi bem-sucedida
   - Atualiza o estado no banco de dados

6. : `updateConsentState`
    - Persiste o novo estado no banco de dados