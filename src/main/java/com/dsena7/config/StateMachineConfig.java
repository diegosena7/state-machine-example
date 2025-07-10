package com.dsena7.config;

import com.dsena7.model.ConsentEventEnum;
import com.dsena7.model.ConsentStateEnum;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<ConsentStateEnum, ConsentEventEnum> {

    /**
     * Configura os estados possíveis da máquina de estados.
     * Define o estado inicial como AUTHORISED e o estado EXPIRED como final.
     *
     * @param states Configurador de estados da máquina
     * @throws Exception se houver erro na configuração
     */
    @Override
    public void configure(StateMachineStateConfigurer<ConsentStateEnum, ConsentEventEnum> states) throws Exception {
        states.withStates()
                .initial(ConsentStateEnum.AUTHORISED)
                .states(EnumSet.allOf(ConsentStateEnum.class))
                .end(ConsentStateEnum.EXPIRED)
                .initial(ConsentStateEnum.AWAITING_AUTHORISATION)
                .states(EnumSet.allOf(ConsentStateEnum.class))
                .end(ConsentStateEnum.REJECTED);
    }

    /**
     * Configura as transições permitidas entre os estados.
     * Define que é possível transicionar de AUTHORISED para EXPIRED através do evento EXPIRE.
     * Transição é a mudança de estado provocada por um determinado evento, nesse caso temos
     * a mudança de AUTHORISED (source), para EXPIRED (target) com base no evento de EXPIRE (event).
     * @param transitions Configurador de transições da máquina
     * @throws Exception se houver erro na configuração
     */
    @Override
    public void configure(StateMachineTransitionConfigurer<ConsentStateEnum, ConsentEventEnum> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(ConsentStateEnum.AUTHORISED)
                    .target(ConsentStateEnum.EXPIRED)
                    .event(ConsentEventEnum.EXPIRE)
                .and()
                .withExternal()
                    .source(ConsentStateEnum.AWAITING_AUTHORISATION)
                    .target(ConsentStateEnum.REJECTED)
                    .event(ConsentEventEnum.REJECT);
    }
}
