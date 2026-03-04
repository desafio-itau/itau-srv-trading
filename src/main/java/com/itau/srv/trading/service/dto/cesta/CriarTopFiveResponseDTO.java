package com.itau.srv.trading.service.dto.cesta;

import com.itau.srv.trading.service.dto.itemcesta.ItemCestaResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Response da criação da primeira cesta Top Five")
public record CriarTopFiveResponseDTO(
        @Schema(description = "ID da cesta criada", example = "1")
        Long cestaId,

        @Schema(description = "Nome da cesta", example = "Top Five - Fevereiro 2026")
        String nome,

        @Schema(description = "Indica se a cesta está ativa", example = "true")
        Boolean ativa,

        @Schema(description = "Data e hora de criação da cesta", example = "2026-02-01T09:00:00")
        LocalDateTime dataCriacao,

        @Schema(description = "Lista de itens que compõem a cesta")
        List<ItemCestaResponseDTO> itens,

        @Schema(description = "Indica se o rebalanceamento foi disparado (sempre false na primeira criação)", example = "false")
        Boolean rebalanceamentoDisparado,

        @Schema(description = "Mensagem de confirmação", example = "Primeira cesta cadastrada com sucesso.")
        String mensagem
) implements CestaResponseDTO {
}
