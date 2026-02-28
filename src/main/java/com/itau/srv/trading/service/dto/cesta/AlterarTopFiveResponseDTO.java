package com.itau.srv.trading.service.dto.cesta;

import com.itau.srv.trading.service.dto.itemcesta.ItemCestaResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public record AlterarTopFiveResponseDTO(
        Long cestaId,
        String nome,
        Boolean ativa,
        LocalDateTime dataCriacao,
        List<ItemCestaResponseDTO> itens,
        CestaAnteriorDesativadaDTO cestaAnteriorDesativada,
        Boolean rebalanceamentoDisparado,
        List<String> ativosRemovidos,
        List<String> ativosAdicionados,
        String mensagem
) implements CestaResponseDTO {
}
