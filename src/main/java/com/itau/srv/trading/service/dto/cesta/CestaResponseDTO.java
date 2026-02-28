package com.itau.srv.trading.service.dto.cesta;

public sealed interface CestaResponseDTO
        permits CriarTopFiveResponseDTO, AlterarTopFiveResponseDTO {
    Long cestaId();
}
