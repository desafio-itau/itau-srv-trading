package com.itau.srv.trading.service.controller;

import com.itau.srv.trading.service.dto.cotacaob3.CotacaoB3;
import com.itau.srv.trading.service.service.CotacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Cotações", description = "Endpoints para gerenciamento de cotações da B3")
@RestController
@RequestMapping("/api/cotacoes")
@RequiredArgsConstructor
@Slf4j
public class CotacaoController {

    private final CotacaoService cotacaoService;

    @Operation(
        summary = "Salvar cotações do arquivo TXT",
        description = "Processa e salva as cotações da B3 a partir do arquivo COTAHIST no banco de dados"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cotações salvas com sucesso",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = CotacaoB3.class))
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Erro interno ao processar cotações",
            content = @Content
        )
    })
    @PostMapping
    public ResponseEntity<List<CotacaoB3>> salvarCotacoesDoTxt() {
        log.info("Iniciando processo para salvar cotações no banco de dados.");

        return ResponseEntity.ok(cotacaoService.salvarCotacoes());
    }

    @Operation(
        summary = "Obter cotação por ticker",
        description = "Retorna a cotação de fechamento de um ativo específico pelo seu ticker"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cotação encontrada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CotacaoB3.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Cotação não encontrada para o ticker informado",
            content = @Content
        )
    })
    @GetMapping("/{ticker}")
    public ResponseEntity<CotacaoB3> obterCotacaoFechamento(
            @Parameter(description = "Código do ticker do ativo", example = "PETR4", required = true)
            @PathVariable String ticker) {
        log.info("Buscando cotação de fechamento para ticker: {}", ticker);

        return ResponseEntity.ok(cotacaoService.obterCotacaoFechamento(ticker));
    }

    @Operation(
        summary = "Listar todas as cotações",
        description = "Retorna a lista completa de cotações da B3 armazenadas no banco de dados"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de cotações retornada com sucesso",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = CotacaoB3.class))
            )
        )
    })
    @GetMapping
    public ResponseEntity<List<CotacaoB3>> listarCotacoes() {
        log.info("Buscando cotações da B3.");

        List<CotacaoB3> cotacoes = cotacaoService.buscarCotacoes();

        log.info("Cotações da B3 buscadas com sucesso. Quantidade: {}", cotacoes.size());
        return ResponseEntity.ok(cotacoes);
    }
}
