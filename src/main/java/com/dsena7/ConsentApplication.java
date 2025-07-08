package com.dsena7;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Classe principal da aplicação de gerenciamento de consentimentos.
 * Configura e inicializa o Spring Boot com suporte a MongoDB e RabbitMQ.
 */
@SpringBootApplication
@EnableMongoRepositories
@OpenAPIDefinition(
        info = @Info(
                title = "API de Gerenciamento de Consentimentos",
                version = "1.0",
                description = "API para gerenciamento de estados de consentimentos utilizando Spring State Machine"
        )
)
public class ConsentApplication {

    /**
     * Método principal que inicia a aplicação Spring Boot.
     *
     * @param args argumentos de linha de comando
     */
    public static void main(String[] args) {
        SpringApplication.run(ConsentApplication.class, args);
    }
}