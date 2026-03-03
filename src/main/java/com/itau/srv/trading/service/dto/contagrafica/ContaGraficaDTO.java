package com.itau.srv.trading.service.dto.contagrafica;

import java.time.LocalDateTime;

public record ContaGraficaDTO(
        Long id,
        String numeroConta,
        String tipoConta,
        LocalDateTime dataCriacao
) {
}
