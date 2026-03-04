package com.itau.srv.trading.service.dto.cesta;

import com.itau.srv.trading.service.dto.itemcesta.ItemCestaResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Response da alteração de uma cesta Top Five existente")
public record AlterarTopFiveResponseDTO(
        @Schema(description = "ID da nova cesta criada", example = "2")
        Long cestaId,

        @Schema(description = "Nome da nova cesta", example = "Top Five - Março 2026")
        String nome,

        @Schema(description = "Indica se a cesta está ativa", example = "true")
        Boolean ativa,

        @Schema(description = "Data e hora de criação da nova cesta", example = "2026-03-01T09:00:00")
        LocalDateTime dataCriacao,

        @Schema(description = "Lista de itens que compõem a nova cesta")
        List<ItemCestaResponseDTO> itens,

        @Schema(description = "Informações da cesta anterior que foi desativada")
        CestaAnteriorDesativadaDTO cestaAnteriorDesativada,

        @Schema(description = "Indica se o rebalanceamento foi disparado", example = "true")
        Boolean rebalanceamentoDisparado,

        @Schema(description = "Lista de tickers dos ativos removidos", example = "[\"BBDC4\", \"WEGE3\"]")
        List<String> ativosRemovidos,

        @Schema(description = "Lista de tickers dos ativos adicionados", example = "[\"ABEV3\", \"RENT3\"]")
        List<String> ativosAdicionados,

        @Schema(description = "Mensagem informativa sobre a alteração",
                example = "Cesta atualizada. Rebalanceamento disparado para 150 clientes ativos.")
        String mensagem
) implements CestaResponseDTO {
}
