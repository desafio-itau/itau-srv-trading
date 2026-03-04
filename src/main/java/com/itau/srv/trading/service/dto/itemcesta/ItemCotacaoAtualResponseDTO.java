package com.itau.srv.trading.service.dto.itemcesta;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Item da cesta com cotação atual do ativo")
public record ItemCotacaoAtualResponseDTO(
        @Schema(description = "Código do ticker do ativo", example = "PETR4")
        String ticker,

        @Schema(description = "Percentual de alocação na cesta", example = "30.00")
        BigDecimal percentual,

        @Schema(description = "Cotação atual do ativo", example = "35.50")
        BigDecimal cotacaoAtual
) {
}
