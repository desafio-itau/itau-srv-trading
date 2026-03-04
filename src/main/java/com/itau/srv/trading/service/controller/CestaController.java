package com.itau.srv.trading.service.controller;

import com.itau.common.library.generic.ControllerGenerico;
import com.itau.srv.trading.service.dto.cesta.CestaRecomendacaoResponseDTO;
import com.itau.srv.trading.service.dto.cesta.CestaResponseDTO;
import com.itau.srv.trading.service.dto.cesta.CriarTopFiveRequestDTO;
import com.itau.srv.trading.service.dto.cesta.HistoricoCestaResponseDTO;
import com.itau.srv.trading.service.dto.custodiamaster.CustodiaMasterResponseDTO;
import com.itau.srv.trading.service.service.CestaService;
import com.itau.srv.trading.service.service.CustodiaMaterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cestas de Recomendação", description = "Endpoints para gerenciamento de cestas Top Five e rebalanceamento")
@Slf4j
@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
public class CestaController implements ControllerGenerico {

    private final CestaService cestaService;
    private final CustodiaMaterService custodiaMaterService;

    @Operation(
        summary = "Criar ou alterar cesta Top Five",
        description = "Cria uma nova cesta Top Five ou altera a cesta ativa existente. Se já existir uma cesta ativa, " +
                      "a mesma será desativada e uma nova será criada, disparando o processo de rebalanceamento."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Cesta criada ou alterada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CestaResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados inválidos fornecidos (quantidade de ativos diferente de 5, percentuais incorretos, etc.)",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Erro interno ao processar a cesta",
            content = @Content
        )
    })
    @PostMapping("/cesta")
    public ResponseEntity<CestaResponseDTO> criarOuAlterarCesta(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados da cesta Top Five com 5 ativos e percentuais que somam 100%",
                required = true
            )
            @RequestBody @Valid CriarTopFiveRequestDTO dto) {
        log.info("Criando ou alterando cesta.");

        CestaResponseDTO resposta = cestaService.criarOuAlterarCesta(dto);

        log.info("Cesta criada ou alterada com sucesso.");
        return ResponseEntity
                .created(gerarHeaderLocation(resposta.cestaId()))
                .body(resposta);
    }

    @Operation(
        summary = "Obter cesta ativa",
        description = "Retorna os detalhes da cesta Top Five atualmente ativa, incluindo cotações atuais dos ativos"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cesta ativa encontrada",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CestaRecomendacaoResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Nenhuma cesta ativa encontrada",
            content = @Content
        )
    })
    @GetMapping("/cesta/atual")
    public ResponseEntity<CestaRecomendacaoResponseDTO> obterCestaAtiva() {
        log.info("Buscando cesta ativa.");

        return ResponseEntity.ok(cestaService.obterCestaAtiva());
    }

    @Operation(
        summary = "Obter histórico de cestas",
        description = "Retorna o histórico completo de todas as cestas criadas, incluindo ativas e desativadas"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Histórico de cestas retornado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = HistoricoCestaResponseDTO.class)
            )
        )
    })
    @GetMapping("/cesta/historico")
    public ResponseEntity<HistoricoCestaResponseDTO> obterHistoricoCestas() {
        log.info("Buscando histórico de cestas.");

        return ResponseEntity.ok(cestaService.obterHistoricoCestas());
    }

    @Operation(
        summary = "Obter custódia master",
        description = "Retorna a custódia da conta master com todos os ativos e resíduos"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Custódia master retornada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CustodiaMasterResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Erro ao buscar custódia master",
            content = @Content
        )
    })
    @GetMapping("/conta-master/custodia")
    public ResponseEntity<CustodiaMasterResponseDTO> obterCustodiaMaster() {
        return ResponseEntity.ok(custodiaMaterService.buscarCustodiaMaster());
    }

    @Operation(
        summary = "Obter cesta por ID",
        description = "Retorna os detalhes de uma cesta específica pelo seu identificador"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cesta encontrada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CestaRecomendacaoResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Cesta não encontrada",
            content = @Content
        )
    })
    @GetMapping("/{cestaId}")
    public ResponseEntity<CestaRecomendacaoResponseDTO> obterCestaPorId(
            @Parameter(description = "ID da cesta", example = "1", required = true)
            @PathVariable Long cestaId) {
        return ResponseEntity.ok(cestaService.obterCestaPorId(cestaId));
    }
}
