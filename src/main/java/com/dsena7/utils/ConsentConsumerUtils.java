package com.dsena7.utils;

import com.dsena7.service.ConsentStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConsentConsumerUtils {

    private final ConsentStateService consentStateService;

    @RabbitListener(queues = "consent.queue")
    public void receivedMessage(String consentId) throws IllegalAccessException {
        log.info("Evento consumido para o consentId {}", consentId);
        consentStateService.processConsent(consentId);
    }
}
