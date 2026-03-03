package com.itau.srv.trading.service.dto.cesta;

import java.util.List;

public record HistoricoCestaResponseDTO(
        List<CestaHistoricoResponseDTO> cestas
) {
}
