package com.itau.srv.trading.service.controller;

import com.itau.common.library.generic.ControllerGenerico;
import com.itau.srv.trading.service.dto.cesta.CestaResponseDTO;
import com.itau.srv.trading.service.dto.cesta.CriarTopFiveRequestDTO;
import com.itau.srv.trading.service.dto.cesta.CriarTopFiveResponseDTO;
import com.itau.srv.trading.service.dto.cotacaob3.CotacaoB3;
import com.itau.srv.trading.service.service.CestaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
public class CestaController implements ControllerGenerico {

    private final CestaService cestaService;

    @PostMapping("/cesta")
    public ResponseEntity<CestaResponseDTO> criarOuAlterarCesta(@RequestBody @Valid CriarTopFiveRequestDTO dto) {
        log.info("Criando ou alterando cesta.");

        CestaResponseDTO resposta = cestaService.criarOuAlterarCesta(dto);

        log.info("Cesta criada ou alterada com sucesso.");
        return ResponseEntity
                .created(gerarHeaderLocation(resposta.cestaId()))
                .body(resposta);
    }

    @GetMapping("/cotacoes")
    public ResponseEntity<List<CotacaoB3>> listarCotacoes() {
        log.info("Buscando cotações da B3.");

        List<CotacaoB3> cotacoes = cestaService.buscarCotacoes();

        log.info("Cotações da B3 buscadas com sucesso. Quantidade: {}", cotacoes.size());
        return ResponseEntity.ok(cotacoes);
    }

    @GetMapping("/cotacoes/{ticker}")
    public ResponseEntity<CotacaoB3> obterCotacaoFechamento(@PathVariable String ticker) {
        log.info("Buscando cotação de fechamento para ticker: {}", ticker);

        return ResponseEntity
                .ok(cestaService.obterCotacaoFechamento(ticker)
                        .orElseThrow(() -> {
                            log.error("Cotação não encontrada para ticker: {}", ticker);
                            return new RuntimeException("COTACAO_NAO_ENCONTRADA");
                        })
                );
    }
}
