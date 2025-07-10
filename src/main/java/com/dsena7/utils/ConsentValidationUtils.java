package com.dsena7.utils;

import com.dsena7.exceptions.StateMachineTransitionException;
import com.dsena7.model.ConsentEntity;
import com.dsena7.model.ConsentStateEnum;

public class ConsentValidationUtils {

    private static final String STATE_VALIDATION_ERROR = "Consentimento %s com estádo %s inválido.";

    public static void validateConsentState(ConsentEntity entity) {
        if (entity.getState() != ConsentStateEnum.AUTHORISED && entity.getState() != ConsentStateEnum.AWAITING_AUTHORISATION) {
            throw new StateMachineTransitionException(String.format(STATE_VALIDATION_ERROR,
                    entity.getConsentId(), entity.getState()));
        }
    }
}
