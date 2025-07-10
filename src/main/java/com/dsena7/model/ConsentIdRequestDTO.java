package com.dsena7.model;

import jakarta.validation.constraints.NotBlank;

public record ConsentIdRequestDTO(@NotBlank(message = "ConsentId não pode ser vazio ou nulo")String consentId) {
}
