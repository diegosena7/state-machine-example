package com.dsena7.service;

import com.dsena7.exceptions.EntityNotFoundException;
import com.dsena7.exceptions.StateMachineTransitionException;
import com.dsena7.model.ConsentEntity;
import com.dsena7.model.ConsentEventEnum;
import com.dsena7.model.ConsentStateEnum;
import com.dsena7.repository.ConsentRepository;
import jakarta.persistence.PersistenceException;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Serviço responsável por gerenciar as transições de estado dos consentimentos.
 * Implementa a lógica de negócio para processar as mudanças de estado usando uma máquina de estados.
 */

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class ConsentStateServiceImpl implements ConsentStateService {

    private static final String PROCESSING_CONSENT_MESSAGE = "Processando consentimento com ID: {}";
    private static final String TRANSITION_SUCCESS_MESSAGE = "Transição de estado realizada com sucesso para consentimento: {}";
    private static final String TRANSITION_ERROR_MESSAGE = "Erro na transição de estado para consentimento: {}";
    private static final String STATE_VALIDATION_ERROR = "Consentimento %s não está no estado AUTHORISED. Estado atual: %s";
    private static final String CONSENT_NOT_FOUND = "Consentimento não encontrado: %s";
    private static final String SAVE_ERROR = "Erro ao salvar alteração de estado do consentimento";

    private final StateMachineFactory<ConsentStateEnum, ConsentEventEnum> factory;
    private final ConsentRepository consentRepository;

    /**
     * Processa um consentimento, realizando a transição de estado de AUTHORISED para EXPIRED.
     *
     * @param consentId Identificador único do consentimento
     * @throws IllegalArgumentException se o consentId for nulo ou vazio
     * @throws EntityNotFoundException se o consentimento não for encontrado
     * @throws IllegalStateException se o consentimento não estiver no estado AUTHORISED
     * @throws StateMachineTransitionException se houver erro na transição de estado
     */
    @Override
    public void processConsent(@NotBlank String consentId) {
        log.info(PROCESSING_CONSENT_MESSAGE, consentId);

        ConsentEntity entity = findConsent(consentId);
        validateConsentState(entity);
        processStateMachineTransition(entity);

        log.info(TRANSITION_SUCCESS_MESSAGE, consentId);
    }

    private ConsentEntity findConsent(String consentId) {
        log.info("Buscando consent com ID: {}", consentId);
        Optional<ConsentEntity> consent = consentRepository.findByConsentId(consentId);
        log.info("Resultado da busca: {}", consent.isPresent() ? "Encontrado" : "Não encontrado");
        return consent.orElseThrow(() -> new EntityNotFoundException(
                String.format(CONSENT_NOT_FOUND, consentId)));
    }


    private void validateConsentState(ConsentEntity entity) {
        if (entity.getState() != ConsentStateEnum.AUTHORISED) {
            throw new IllegalStateException(String.format(STATE_VALIDATION_ERROR,
                    entity.getConsentId(), entity.getState()));
        }
    }

    /**
     * Coordena o processo de transição de estado na máquina de estados.
     * Este método orquestra o fluxo completo de transição, incluindo a configuração
     * da máquina de estados e a execução da transição propriamente dita.
     *
     * @param entity A entidade de consentimento que terá seu estado alterado
     * @throws StateMachineTransitionException se ocorrer qualquer erro durante o processo de transição,
     *         seja na configuração da máquina de estados ou na execução da transição.
     *         A exceção incluirá o ID do consentimento na mensagem de erro para facilitar o diagnóstico.
     *
     * O processo inclui:
     * 1. Configuração da máquina de estados com o estado atual da entidade
     * 2. Execução da transição de estado
     * 3. Tratamento de erros com logging apropriado
     */
    private void processStateMachineTransition(ConsentEntity entity) {
        try {
            StateMachine<ConsentStateEnum, ConsentEventEnum> stateMachine = configureStateMachine(entity);
            executeStateTransition(stateMachine, entity);
        } catch (Exception e) {
            log.error(TRANSITION_ERROR_MESSAGE, entity.getConsentId(), e);
            throw new StateMachineTransitionException(
                    String.format("Falha ao processar transição de estado para consentimento: %s",
                            entity.getConsentId()), e);
        }
    }

    /**
     * Configura uma nova instância da máquina de estados para um consentimento específico.
     * Para a máquina atual, reseta seu estado e a reinicia com o estado atual da entidade.
     *
     * @param entity Entidade de consentimento
     * @return StateMachine configurada para o consentimento
     */
    private StateMachine<ConsentStateEnum, ConsentEventEnum> configureStateMachine(ConsentEntity entity) {
        StateMachine<ConsentStateEnum, ConsentEventEnum> stateMachine = factory.getStateMachine(entity.getConsentId());

        stateMachine.stopReactively().block();

        stateMachine.getStateMachineAccessor().doWithAllRegions(access -> {
            access.resetStateMachineReactively(new DefaultStateMachineContext<>(
                    entity.getState(), null, null, null)).block();
        });


        stateMachine.startReactively().block();

        return stateMachine;
    }

    /**
     * Executa a transição de estado na máquina de estados.
     * Envia o evento EXPIRE e atualiza o estado da entidade se a transição for bem-sucedida.
     *
     * @param sm Máquina de estados configurada
     * @param entity Entidade de consentimento
     * @throws StateMachineTransitionException se a transição falhar
     */
    private void executeStateTransition(StateMachine<ConsentStateEnum, ConsentEventEnum> sm, ConsentEntity entity) {
        try {
            boolean success = sm.sendEvent(Mono.just(MessageBuilder
                            .withPayload(ConsentEventEnum.EXPIRE)
                            .build()))
                    .blockFirst() != null;

            if (!success) {
                throw new StateMachineTransitionException(
                        String.format("Falha ao expirar o consentimento: %s", entity.getConsentId()));
            }

            updateConsentState(entity);
        } catch (Exception e) {
            throw new StateMachineTransitionException(
                    String.format("Erro ao executar transição de estado para consentimento: %s",
                            entity.getConsentId()), e);
        }
    }



    private void updateConsentState(ConsentEntity entity) {
        try {
            entity.setState(ConsentStateEnum.EXPIRED);
            consentRepository.save(entity);
        } catch (Exception e) {
            log.error("Erro ao salvar alteração de estado do consentimento: {}",
                    entity.getConsentId(), e);
            throw new PersistenceException(SAVE_ERROR, e);
        }
    }
}