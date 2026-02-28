package com.itau.srv.trading.service.dto.cotacaob3;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CotacaoB3(
        LocalDate dataPregao,
        String ticker,
        String codigoBDI,
        Integer tipoMercado,
        String nomeEmpresa,
        BigDecimal precoAbertura,
        BigDecimal precoMaximo,
        BigDecimal precoMinimo,
        BigDecimal precoFechamento,
        BigDecimal precoMedio,
        Long quantidadeNegociada,
        BigDecimal volumeNegociado
) {
}
