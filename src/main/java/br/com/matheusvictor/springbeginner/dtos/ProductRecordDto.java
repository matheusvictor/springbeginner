package br.com.matheusvictor.springbeginner.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

// records são imutáveis, seus atributos são privados e finais, não podendo ser modificados
public record ProductRecordDto(@NotBlank String name, String description, @NotNull BigDecimal price) {
    public ProductRecordDto {
        if (description == null) {
            description = "Description not available.";
        }
    }
}
