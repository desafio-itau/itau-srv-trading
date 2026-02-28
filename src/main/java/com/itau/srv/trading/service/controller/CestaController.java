package com.itau.srv.trading.service.controller;

import com.itau.common.library.generic.ControllerGenerico;
import com.itau.srv.trading.service.dto.cesta.CestaRecomendacaoAtivaResponseDTO;
import com.itau.srv.trading.service.dto.cesta.CestaResponseDTO;
import com.itau.srv.trading.service.dto.cesta.CriarTopFiveRequestDTO;
import com.itau.srv.trading.service.dto.cesta.HistoricoCestaResponseDTO;
import com.itau.srv.trading.service.service.CestaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/cesta/atual")
    public ResponseEntity<CestaRecomendacaoAtivaResponseDTO> obterCestaAtiva() {
        log.info("Buscando cesta ativa.");

        return ResponseEntity.ok(cestaService.obterCestaAtiva());
    }

    @GetMapping("/cesta/historico")
    public ResponseEntity<HistoricoCestaResponseDTO> obterHistoricoCestas() {
        log.info("Buscando histórico de cestas.");

        return ResponseEntity.ok(cestaService.obterHistoricoCestas());
    }
}
