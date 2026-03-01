package com.itau.srv.trading.service.dto.custodiamaster;

public record ContaMasterResponseDTO(
        Long id,
        String numeroConta,
        String tipo
) {
}
