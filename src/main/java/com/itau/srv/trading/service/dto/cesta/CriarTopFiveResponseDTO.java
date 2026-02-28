package com.itau.srv.trading.service.dto.cesta;

import com.itau.srv.trading.service.dto.itemcesta.ItemCestaResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public record CriarTopFiveResponseDTO(
        Long cestaId,
        String nome,
        Boolean ativa,
        LocalDateTime dataCriacao,
        List<ItemCestaResponseDTO> itens,
        Boolean rebalanceamentoDisparado,
        String mensagem
) implements CestaResponseDTO {
}
