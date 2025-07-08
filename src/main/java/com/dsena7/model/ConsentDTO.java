package com.dsena7.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConsentDTO(@NotBlank String consentId, @NotNull ConsentStateEnum state) {
}
