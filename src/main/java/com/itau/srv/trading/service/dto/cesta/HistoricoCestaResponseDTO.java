package com.itau.srv.trading.service.dto.cesta;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Histórico completo de cestas (ativas e desativadas)")
public record HistoricoCestaResponseDTO(
        @Schema(description = "Lista de todas as cestas no histórico")
        List<CestaHistoricoResponseDTO> cestas
) {
}
