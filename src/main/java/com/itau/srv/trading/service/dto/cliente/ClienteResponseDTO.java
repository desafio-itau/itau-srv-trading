package com.itau.srv.trading.service.dto.cliente;

import com.itau.srv.trading.service.dto.contagrafica.ContaGraficaDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ClienteResponseDTO(
        Long clienteId,
        String nome,
        String cpf,
        String email,
        BigDecimal valorMensal,
        Boolean ativo,
        LocalDateTime dataAdesao,
        ContaGraficaDTO contaGrafica
) {
}
