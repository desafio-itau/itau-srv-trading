package com.itau.srv.trading.service.dto.cesta;

import java.time.LocalDateTime;

public record CestaAnteriorDesativadaDTO(
        Long cestaId,
        String nome,
        LocalDateTime dataDesativacao
) {
}
