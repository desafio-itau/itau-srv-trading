package com.itau.srv.trading.service.dto.itemcesta;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "Item de uma cesta com ticker e percentual de alocação")
public record ItemCestaRequestDTO(
        @Schema(
            description = "Código do ticker do ativo na B3",
            example = "PETR4",
            required = true,
            maxLength = 10
        )
        @Size(max = 10, message = "O ticker deve ter no máximo 10 caracteres")
        @NotBlank(message = "Ticker não pode ser branco")
        String ticker,

        @Schema(
            description = "Percentual de alocação do ativo na cesta",
            example = "30.00",
            required = true,
            minimum = "0.01",
            maximum = "100.00"
        )
        @DecimalMin(value = "0.01", message = "O percentual deve ser maior que 0.01")
        @DecimalMax(value = "100.00", message = "O percentual deve ser menor que 100.00")
        @NotNull(message = "Percentual não pode ser nulo")
        BigDecimal percentual
) {
}
