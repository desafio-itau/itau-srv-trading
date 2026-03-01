package com.itau.srv.trading.service.dto.custodiamaster;

import java.math.BigDecimal;
import java.util.List;

public record CustodiaMasterResponseDTO(
        ContaMasterResponseDTO contaMaster,
        List<CustodiaResponseDTO> custodia,
        BigDecimal valorTotalResiduo
) {
}
