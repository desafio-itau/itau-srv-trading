package com.itau.srv.trading.service.dto.cesta;

import com.itau.srv.trading.service.dto.itemcesta.ItemCotacaoAtualResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public record CestaRecomendacaoAtivaResponseDTO(
        Long cestaId,
        String nome,
        Boolean ativa,
        LocalDateTime dataCriacao,
        List<ItemCotacaoAtualResponseDTO> itens
) {
}
