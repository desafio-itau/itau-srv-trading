package com.itau.srv.trading.service.dto.rebalanceamento;

import java.time.LocalDateTime;

public record RebalancementoEventDTO(
        Long cestaAnteriorId,
        Long cestaAtualId,
        LocalDateTime dataExecucao
) {
}