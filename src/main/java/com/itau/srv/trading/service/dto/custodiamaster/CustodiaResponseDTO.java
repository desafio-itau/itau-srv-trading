package com.itau.srv.trading.service.dto.custodiamaster;

import java.math.BigDecimal;

public record CustodiaResponseDTO(
        String ticker,
        Integer quantidade,
        BigDecimal precoMedio,
        BigDecimal valorAtual,
        String origem
) {
}
