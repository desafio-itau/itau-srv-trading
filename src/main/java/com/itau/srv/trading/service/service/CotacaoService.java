package com.itau.srv.trading.service.service;

import com.itau.srv.trading.service.dto.cotacaob3.CotacaoB3;
import com.itau.srv.trading.service.util.CotahistParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CotacaoService {

    private final CotahistParser cotahistParser;

    public List<CotacaoB3> buscarCotacoes() {
        return cotahistParser.parseArquivo("cotacoes/COTAHIST_M012026.TXT");
    }

    public Optional<CotacaoB3> obterCotacaoFechamento(String ticker) {
        return cotahistParser.obterCotacaoFechamento("cotacoes/COTAHIST_M012026.TXT", ticker);
    }
}
