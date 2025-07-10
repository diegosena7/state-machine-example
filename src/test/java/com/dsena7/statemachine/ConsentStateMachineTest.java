package com.dsena7.statemachine;

import com.dsena7.model.ConsentEventEnum;
import com.dsena7.model.ConsentStateEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ConsentStateMachineTest {

    @Autowired
    private StateMachineFactory<ConsentStateEnum, ConsentEventEnum> stateMachineFactory;

    @Test
    void shouldNotAllowTransitionFromAwaitingToExpired() {
        // Arrange
        StateMachine<ConsentStateEnum, ConsentEventEnum> stateMachine =
                stateMachineFactory.getStateMachine("test-consent-id");

        stateMachine.stopReactively().block();
        stateMachine.getStateMachineAccessor().doWithAllRegions(access -> {
            access.resetStateMachineReactively(
                    new DefaultStateMachineContext<>(ConsentStateEnum.AWAITING_AUTHORISATION, null, null, null)
            ).block();
        });
        stateMachine.startReactively().block();

        // Act
        var result = stateMachine.sendEvent(
                MessageBuilder.withPayload(ConsentEventEnum.EXPIRE).build()
        );

        // Assert
        assertNotNull(result, "O resultado do envio do evento não deve ser nulo.");
        assertEquals(ConsentStateEnum.AWAITING_AUTHORISATION, stateMachine.getState().getId(),
                "O estado não deveria ter mudado, pois a transição não é permitida.");

        // Verifica explicitamente que a transição não ocorreu
        assertNotEquals(ConsentStateEnum.EXPIRED, stateMachine.getState().getId(),
                "A máquina de estados não deveria permitir a transição de AWAITING_AUTHORISATION para EXPIRED.");
    }

    @Test
    void shouldAllowTransitionFromAuthorisedToExpired() {
        StateMachine<ConsentStateEnum, ConsentEventEnum> stateMachine =
                stateMachineFactory.getStateMachine("test-consent-id-2");

        stateMachine.stopReactively().block();
        stateMachine.getStateMachineAccessor().doWithAllRegions(access -> {
            access.resetStateMachineReactively(
                    new DefaultStateMachineContext<>(ConsentStateEnum.AUTHORISED, null, null, null)
            ).block();
        });
        stateMachine.startReactively().block();

        var result = stateMachine.sendEvent(
                MessageBuilder.withPayload(ConsentEventEnum.EXPIRE).build()
        );

        assertNotNull(result, "O evento foi processado.");
        assertEquals(ConsentStateEnum.EXPIRED, stateMachine.getState().getId(),
                "A transição válida deveria mudar o estado para EXPIRED.");
    }

}
