package com.itau.srv.trading.service.service;

import com.itau.srv.trading.service.dto.cotacaob3.CotacaoB3;
import com.itau.srv.trading.service.mapper.CotacaoMapper;
import com.itau.srv.trading.service.model.Cotacao;
import com.itau.srv.trading.service.repository.CotacaoRepository;
import com.itau.srv.trading.service.util.CotahistParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CotacaoService {

    private final CotahistParser cotahistParser;
    private final CotacaoMapper cotacaoMapper;
    private final CotacaoRepository cotacaoRepository;

    public List<CotacaoB3> buscarCotacoes() {
        return cotacaoRepository.findAll()
                .stream()
                .map(cotacaoMapper::mapearParaCotacaoB3)
                .toList();
    }

    public CotacaoB3 obterCotacaoFechamento(String ticker) {
        String tickerFormatado = ticker.endsWith("F") ? ticker.substring(0, ticker.length() - 1) : ticker;

        Cotacao cotacao = cotacaoRepository.findAll()
                .stream()
                .filter(cotacaoEncontrada -> cotacaoEncontrada.getTicker().equals(tickerFormatado) && cotacaoEncontrada.getTipoMercado() == 10)
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Cotação de fechamento não encontrada para ticker: {}", ticker);
                    return new RuntimeException("COTACAO_NAO_ENCONTRADA");
                });

        return cotacaoMapper.mapearParaCotacaoB3(cotacao);
    }

    @Transactional
    public List<CotacaoB3> salvarCotacoes() {
        log.info("Iniciando processo para fazer o parse do arquivo TXT.");
        List<CotacaoB3> cotacoesB3 = cotahistParser.parseArquivo("cotacoes/COTAHIST_M012026.TXT");
        log.info("Arquivo TXT parseado com sucesso.");

        List<Cotacao> cotacoes = cotacoesB3
                .stream()
                .map(cotacaoMapper::mapearParaCotacao)
                .toList();

        List<Cotacao> cotacoesSalvas = cotacaoRepository.saveAll(cotacoes);

        log.info("Cotacoes salvas com sucesso.");

        return cotacoesSalvas
                .stream()
                .map(cotacaoMapper::mapearParaCotacaoB3)
                .toList();
    }
}
