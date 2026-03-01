package com.itau.srv.trading.service.dto.cotacaob3;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CotacaoB3(
        LocalDate dataPregao,
        String ticker,
        Integer tipoMercado,
        BigDecimal precoAbertura,
        BigDecimal precoMaximo,
        BigDecimal precoMinimo,
        BigDecimal precoFechamento
) {
}
