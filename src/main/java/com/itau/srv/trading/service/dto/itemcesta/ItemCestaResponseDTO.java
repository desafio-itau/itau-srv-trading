package com.itau.srv.trading.service.dto.itemcesta;

import java.math.BigDecimal;

public record ItemCestaResponseDTO(
        String ticker,
        BigDecimal percentual
) {
}
