package com.itau.srv.trading.service.mapper;

import com.itau.srv.trading.service.dto.cesta.CriarTopFiveRequestDTO;
import com.itau.srv.trading.service.dto.cesta.CriarTopFiveResponseDTO;
import com.itau.srv.trading.service.dto.itemcesta.ItemCestaResponseDTO;
import com.itau.srv.trading.service.model.CestaRecomendacao;
import com.itau.srv.trading.service.model.ItemCesta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CestaMapper {

    private static final String CRIAR_TOP_FIVE = "Primeira cesta cadastrada com sucesso.";

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
}
