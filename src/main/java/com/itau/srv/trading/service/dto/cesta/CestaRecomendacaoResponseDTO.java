package com.itau.srv.trading.service.dto.cesta;

import com.itau.srv.trading.service.dto.itemcesta.ItemCotacaoAtualResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Cesta de recomendação com cotações atuais dos ativos")
public record CestaRecomendacaoResponseDTO(
        @Schema(description = "ID da cesta", example = "1")
        Long cestaId,

        @Schema(description = "Nome da cesta", example = "Top Five - Fevereiro 2026")
        String nome,

        @Schema(description = "Indica se a cesta está ativa", example = "true")
        Boolean ativa,

        @Schema(description = "Data e hora de criação", example = "2026-02-01T09:00:00")
        LocalDateTime dataCriacao,

        @Schema(description = "Lista de itens com cotações atuais")
        List<ItemCotacaoAtualResponseDTO> itens
) {
}
