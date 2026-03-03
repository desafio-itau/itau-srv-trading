package com.itau.srv.trading.service.mapper;

import com.itau.srv.trading.service.dto.cesta.*;
import com.itau.srv.trading.service.dto.itemcesta.ItemCestaResponseDTO;
import com.itau.srv.trading.service.dto.itemcesta.ItemCotacaoAtualResponseDTO;
import com.itau.srv.trading.service.dto.cesta.AlterarTopFiveResponseDTO;
import com.itau.srv.trading.service.model.CestaRecomendacao;
import com.itau.srv.trading.service.model.ItemCesta;
import com.itau.srv.trading.service.util.CotahistParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CestaMapper {

    private static final String CRIAR_TOP_FIVE = "Primeira cesta cadastrada com sucesso.";
    private final CotahistParser cotahistParser;

    public CestaRecomendacao mapearParaCestaRecomendacao(CriarTopFiveRequestDTO dto) {
        CestaRecomendacao cestaRecomendacao = new CestaRecomendacao();

        cestaRecomendacao.setNome(dto.nome());
        cestaRecomendacao.setAtiva(true);

        return cestaRecomendacao;
    }

    public CriarTopFiveResponseDTO mapearParaTopFiveResponse(CestaRecomendacao cesta, List<ItemCesta> itens) {
        return new CriarTopFiveResponseDTO(
                cesta.getId(),
                cesta.getNome(),
                cesta.getAtiva(),
                cesta.getDataCriacao(),
                mapearParaItemCestaResponse(itens),
                false,
                CRIAR_TOP_FIVE
        );
    }

    public CestaRecomendacaoResponseDTO mapearParaCestaRecomendacaoAtivaResponse(CestaRecomendacao cesta, List<ItemCesta> itens) {
        return new CestaRecomendacaoResponseDTO(
                cesta.getId(),
                cesta.getNome(),
                cesta.getAtiva(),
                cesta.getDataCriacao(),
                mapearParaItemCotacaoAtualResponse(itens)
        );
    }

    public CestaHistoricoResponseDTO mapearParaCestaHistoricoResponse(CestaRecomendacao cesta, List<ItemCesta> itens) {
        return new CestaHistoricoResponseDTO(
                cesta.getId(),
                cesta.getNome(),
                cesta.getAtiva(),
                cesta.getDataCriacao(),
                cesta.getDataDesativacao(),
                mapearParaItemCestaResponse(itens)
        );
    }

    public AlterarTopFiveResponseDTO mapearParaAlterarTopFiveResponse(CestaRecomendacao novaCesta, CestaRecomendacao cestaDesativada, List<ItemCesta> itensAtuais, List<String> itensAdicionados, List<String> itensRemovidos, Integer quantidadeClientes) {
        return new AlterarTopFiveResponseDTO(
                novaCesta.getId(),
                novaCesta.getNome(),
                novaCesta.getAtiva(),
                novaCesta.getDataCriacao(),
                mapearParaItemCestaResponse(itensAtuais),
                mapearParaCestaAnteriorDesativada(cestaDesativada),
                true,
                itensRemovidos,
                itensAdicionados,
                "Cesta atualizada. Rebalanceamento disparado para " + quantidadeClientes + " clientes ativos."
        );
    }

    private List<ItemCestaResponseDTO> mapearParaItemCestaResponse(List<ItemCesta> itens) {
        List<ItemCestaResponseDTO> itensResponse = new ArrayList<>();

        for (ItemCesta item : itens) {
            ItemCestaResponseDTO itemResponse = new ItemCestaResponseDTO(
                    item.getTicker(),
                    item.getPercentual()
            );
            itensResponse.add(itemResponse);
        }

        return itensResponse;
    }

    private List<ItemCotacaoAtualResponseDTO> mapearParaItemCotacaoAtualResponse(List<ItemCesta> itens) {
        List<ItemCotacaoAtualResponseDTO> itensResponse = new ArrayList<>();

        for (ItemCesta item : itens) {
            cotahistParser.obterCotacaoFechamento("cotacoes/COTAHIST_M012026.TXT", item.getTicker())
                    .ifPresent(cotacao -> {
                        ItemCotacaoAtualResponseDTO itemResponse = new ItemCotacaoAtualResponseDTO(
                                item.getTicker(),
                                item.getPercentual(),
                                cotacao.precoFechamento()
                        );
                        itensResponse.add(itemResponse);
                    });
        }

        return itensResponse;
    }

    private CestaAnteriorDesativadaDTO mapearParaCestaAnteriorDesativada(CestaRecomendacao cesta) {
        return new CestaAnteriorDesativadaDTO(
                cesta.getId(),
                cesta.getNome(),
                cesta.getDataDesativacao()
        );
    }
}
