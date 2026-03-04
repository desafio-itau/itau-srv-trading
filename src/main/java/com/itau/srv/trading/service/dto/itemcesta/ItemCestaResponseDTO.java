package com.itau.srv.trading.service.dto.itemcesta;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Item de uma cesta na resposta")
public record ItemCestaResponseDTO(
        @Schema(description = "Código do ticker do ativo", example = "PETR4")
        String ticker,

        @Schema(description = "Percentual de alocação do ativo na cesta", example = "30.00")
        BigDecimal percentual
) {
}
