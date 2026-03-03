package com.itau.srv.trading.service.controller;

import com.itau.common.library.exception.RecursoNaoEncontradoException;
import com.itau.srv.trading.service.dto.cotacaob3.CotacaoB3;
import com.itau.srv.trading.service.service.CotacaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cotacoes")
@RequiredArgsConstructor
@Slf4j
public class CotacaoController {

    private final CotacaoService cotacaoService;

    @PostMapping
    public ResponseEntity<List<CotacaoB3>> salvarCotacoesDoTxt() {
        log.info("Iniciando processo para salvar cotações no banco de dados.");

        return ResponseEntity.ok(cotacaoService.salvarCotacoes());
    }

    @GetMapping("/{ticker}")
    public ResponseEntity<CotacaoB3> obterCotacaoFechamento(@PathVariable String ticker) {
        log.info("Buscando cotação de fechamento para ticker: {}", ticker);

        return ResponseEntity.ok(cotacaoService.obterCotacaoFechamento(ticker));
    }

    @GetMapping
    public ResponseEntity<List<CotacaoB3>> listarCotacoes() {
        log.info("Buscando cotações da B3.");

        List<CotacaoB3> cotacoes = cotacaoService.buscarCotacoes();

        log.info("Cotações da B3 buscadas com sucesso. Quantidade: {}", cotacoes.size());
        return ResponseEntity.ok(cotacoes);
    }
}
