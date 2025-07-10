package com.dsena7.utils;

import com.dsena7.model.ConsentIdRequestDTO;
import com.dsena7.service.ConsentStateService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    public void receivedMessage(String message) throws IllegalAccessException {
        log.info("Evento consumido para o consentId {}", message);
        ObjectMapper objectMapper = new ObjectMapper();
        ConsentIdRequestDTO dto;
        try {
            dto = objectMapper.readValue(message, ConsentIdRequestDTO.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Payload inválido: " + message, e);
        }
        consentStateService.processConsent(dto.consentId());
    }
}