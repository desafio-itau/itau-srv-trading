package com.itau.srv.trading.service.service;

import com.itau.srv.trading.service.dto.itemcesta.ItemCestaRequestDTO;
import com.itau.srv.trading.service.model.CestaRecomendacao;
import com.itau.srv.trading.service.model.ItemCesta;
import com.itau.srv.trading.service.repository.ItemCestaRepository;
import com.itau.srv.trading.service.util.CotahistParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemCestaService {

    private final ItemCestaRepository itemCestaRepository;
    private final CotahistParser cotahistParser;

    public List<ItemCesta> criarItensCesta(List<ItemCestaRequestDTO> itens, CestaRecomendacao cesta) {
        log.info("Criando itens da cesta.");

        List<ItemCesta> itensCesta = new ArrayList<>();

        for (ItemCestaRequestDTO item : itens) {
            cotahistParser.obterCotacaoFechamento("cotacoes/COTAHIST_M012026.TXT", item.ticker())
                    .orElseThrow(() -> {
                        log.error("Cotação de fechamento não encontrada para ticker: {}", item.ticker());
                        return new RuntimeException("Cotação de fechamento não encontrada para ticker: " + item.ticker());
                    });
            ItemCesta itemCesta = new ItemCesta();
            itemCesta.setTicker(item.ticker());
            itemCesta.setPercentual(item.percentual());
            itemCesta.setCestaRecomendacao(cesta);
            itensCesta.add(itemCesta);
        }

        return itemCestaRepository.saveAll(itensCesta);
    }
}
