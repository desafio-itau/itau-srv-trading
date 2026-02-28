package com.itau.srv.trading.service.dto.itemcesta;

import java.math.BigDecimal;

public record ItemCotacaoAtualResponseDTO(
        String ticker,
        BigDecimal percentual,
        BigDecimal cotacaoAtual
) {
}
