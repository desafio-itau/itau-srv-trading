package com.itau.srv.trading.service.mapper;

import com.itau.srv.trading.service.dto.cotacaob3.CotacaoB3;
import com.itau.srv.trading.service.model.Cotacao;
import org.springframework.stereotype.Component;

@Component
public class CotacaoMapper {

    public Cotacao mapearParaCotacao(CotacaoB3 dto) {
        Cotacao cotacao = new Cotacao();

        cotacao.setTicker(dto.ticker());
        cotacao.setPrecoAbertura(dto.precoAbertura());
        cotacao.setPrecoMaximo(dto.precoMaximo());
        cotacao.setPrecoMinimo(dto.precoMinimo());
        cotacao.setPrecoFechamento(dto.precoFechamento());
        cotacao.setDataPregao(dto.dataPregao());
        cotacao.setTipoMercado(dto.tipoMercado());

        return cotacao;
    }

    public CotacaoB3 mapearParaCotacaoB3(Cotacao cotacao) {
        return new CotacaoB3(
                cotacao.getDataPregao(),
                cotacao.getTicker(),
                cotacao.getTipoMercado(),
                cotacao.getPrecoAbertura(),
                cotacao.getPrecoMaximo(),
                cotacao.getPrecoMinimo(),
                cotacao.getPrecoFechamento()
        );
    }
}
