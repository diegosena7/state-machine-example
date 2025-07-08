# Projeto Spring Boot - Consent State Machine com RabbitMQ

### Funcionamento da State Machine
A State Machine (Máquina de Estados) neste projeto gerencia as transições de estado dos consentimentos, seguindo um fluxo definido:
1. Estado Inicial (AUTHORISED) → Evento (EXPIRE) → Estado Final (EXPIRED)

A máquina garante que as transições só ocorram de acordo com as regras definidas na configuração.


### Fluxo de Execução:
1. : `processConsent`
    - Valida o ID do consentimento
    - Busca a entidade
    - Valida o estado atual
    - Inicia o processo de transição

2. : `configureStateMachine`
    - Para a máquina atual
    - Reseta para o estado atual da entidade
    - Reinicia a máquina

3. : `executeStateTransition`
    - Envia o evento EXPIRE
    - Verifica se a transição foi bem-sucedida
    - Atualiza o estado no banco de dados

4. : `updateConsentState`
    - Persiste o novo estado no banco de dados
