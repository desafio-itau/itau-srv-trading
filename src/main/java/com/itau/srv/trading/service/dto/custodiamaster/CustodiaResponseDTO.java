package com.itau.srv.trading.service.dto.custodiamaster;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Custódia de um ativo na conta master")
public record CustodiaResponseDTO(
        @Schema(description = "Código do ticker do ativo", example = "PETR4")
        String ticker,

        @Schema(description = "Quantidade de papéis", example = "1000")
        Integer quantidade,

        @Schema(description = "Preço médio de aquisição", example = "35.50")
        BigDecimal precoMedio,

        @Schema(description = "Valor atual total do ativo (quantidade × cotação atual)", example = "35500.00")
        BigDecimal valorAtual,

        @Schema(description = "Origem da custódia", example = "COMPRA", allowableValues = {"COMPRA", "REBALANCEAMENTO"})
        String origem
) {
}
