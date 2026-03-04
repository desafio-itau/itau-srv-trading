package com.itau.srv.trading.service.dto.cotacaob3;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Cotação de um ativo da B3")
public record CotacaoB3(
        @Schema(description = "Data do pregão", example = "2026-02-28")
        LocalDate dataPregao,

        @Schema(description = "Código do ticker do ativo", example = "PETR4")
        String ticker,

        @Schema(description = "Tipo de mercado (10 = à vista, 20 = fracionário)", example = "10")
        Integer tipoMercado,

        @Schema(description = "Preço de abertura", example = "35.00")
        BigDecimal precoAbertura,

        @Schema(description = "Preço máximo do dia", example = "36.50")
        BigDecimal precoMaximo,

        @Schema(description = "Preço mínimo do dia", example = "34.00")
        BigDecimal precoMinimo,

        @Schema(description = "Preço de fechamento", example = "35.75")
        BigDecimal precoFechamento
) {
}
