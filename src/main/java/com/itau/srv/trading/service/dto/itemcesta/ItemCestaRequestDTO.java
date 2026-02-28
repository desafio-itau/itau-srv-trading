package com.itau.srv.trading.service.dto.itemcesta;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ItemCestaRequestDTO(
        @Size(max = 10, message = "O ticker deve ter no máximo 10 caracteres")
        @NotBlank(message = "Ticker não pode ser branco")
        String ticker,
        @DecimalMin(value = "0.01", message = "O percentual deve ser maior que 0.01")
        @DecimalMax(value = "100.00", message = "O percentual deve ser menor que 100.00")
        @NotNull(message = "Percentual não pode ser nulo")
        BigDecimal percentual
) {
}
