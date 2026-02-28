package com.itau.srv.trading.service.dto.cesta;

import com.itau.srv.trading.service.dto.itemcesta.ItemCestaResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public record CestaHistoricoResponseDTO(
        Long id,
        String nome,
        Boolean ativa,
        LocalDateTime dataCriacao,
        LocalDateTime dataDesativacao,
        List<ItemCestaResponseDTO> itens
) {
}
