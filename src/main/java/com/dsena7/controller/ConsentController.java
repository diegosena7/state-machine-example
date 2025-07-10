package com.dsena7.controller;

import com.dsena7.model.ConsentDTO;
import com.dsena7.model.ConsentEntity;
import com.dsena7.model.ConsentStateEnum;
import com.dsena7.repository.ConsentRepository;
import com.dsena7.utils.ConsentValidationUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.PersistenceException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("v1/consents")
@RequiredArgsConstructor
@Slf4j
public class ConsentController {

    private final RabbitTemplate rabbitTemplate;

    private final ConsentRepository consentRepository;

    @PostMapping("/event")
    @Operation(summary = "Publica um consentId na fila RabbitMQ")
    public ResponseEntity<String> consentEvent(@RequestBody String consentId){
        log.info("Enviando consentId: {} para a fila.", consentId);
        rabbitTemplate.convertAndSend("consent.queue", consentId);
        return ResponseEntity.accepted().body("ConsentId: " + consentId +  "enviado par a fila.");
    }

    @PostMapping("/save")
    @Operation(summary = "Insere um consentimento com estado AUTHORISED na base Mongo DB")
    public ResponseEntity<ConsentEntity> insertConsent(@RequestBody @Valid ConsentDTO consentDTO) {
        log.info("Iniciando inserção do consentId: {} na base", consentDTO.consentId());
        try {
            ConsentEntity entity = ConsentEntity.builder()
                    .consentId(consentDTO.consentId())
                    .state(consentDTO.state())
                    .createdAt(LocalDateTime.now())
                    .expiratedAt(LocalDateTime.now().plusMinutes(1))
                    .build();

            ConsentValidationUtils.validateConsentState(entity);

            log.info("Entidade construída: {}", entity);

            ConsentEntity savedEntity = consentRepository.save(entity);
            log.info("Entidade salva com sucesso: {}", savedEntity);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEntity);
        } catch (Exception exception) {
            log.error("Erro ao salvar consentimento: {}", consentDTO.consentId(), exception);
            throw new PersistenceException(
                    String.format("Erro ao inserir consentimento: %s na base de dados",
                            consentDTO.consentId()), exception);
        }
    }

}
