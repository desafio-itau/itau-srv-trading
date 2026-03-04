package com.itau.srv.trading.service.dto.cesta;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Informações da cesta anterior que foi desativada")
public record CestaAnteriorDesativadaDTO(
        @Schema(description = "ID da cesta desativada", example = "1")
        Long cestaId,

        @Schema(description = "Nome da cesta desativada", example = "Top Five - Fevereiro 2026")
        String nome,

        @Schema(description = "Data e hora de desativação", example = "2026-03-01T09:00:00")
        LocalDateTime dataDesativacao
) {
}
