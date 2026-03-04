package com.itau.srv.trading.service.dto.cesta;

import com.itau.srv.trading.service.dto.itemcesta.ItemCestaResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Cesta no histórico com data de desativação (se aplicável)")
public record CestaHistoricoResponseDTO(
        @Schema(description = "ID da cesta", example = "1")
        Long id,

        @Schema(description = "Nome da cesta", example = "Top Five - Fevereiro 2026")
        String nome,

        @Schema(description = "Indica se a cesta está ativa", example = "false")
        Boolean ativa,

        @Schema(description = "Data e hora de criação", example = "2026-02-01T09:00:00")
        LocalDateTime dataCriacao,

        @Schema(description = "Data e hora de desativação (null se ainda ativa)", example = "2026-03-01T09:00:00", nullable = true)
        LocalDateTime dataDesativacao,

        @Schema(description = "Lista de itens da cesta")
        List<ItemCestaResponseDTO> itens
) {
}
