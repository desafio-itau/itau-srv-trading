package com.itau.srv.trading.service.dto.custodiamaster;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Custódia da conta master com todos os ativos")
public record CustodiaMasterResponseDTO(
        @Schema(description = "Informações da conta master")
        ContaMasterResponseDTO contaMaster,

        @Schema(description = "Lista de ativos na custódia")
        List<CustodiaResponseDTO> custodia,

        @Schema(description = "Valor total de resíduo (saldo não investido)", example = "5000.00")
        BigDecimal valorTotalResiduo
) {
}
