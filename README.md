# Projeto Spring Boot - Consent State Machine com RabbitMQ

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