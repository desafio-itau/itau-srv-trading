package com.itau.srv.trading.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itau.common.library.exception.NegocioException;
import com.itau.common.library.handler.GlobalExceptionHandler;
import com.itau.srv.trading.service.dto.cesta.CriarTopFiveRequestDTO;
import com.itau.srv.trading.service.dto.cesta.CriarTopFiveResponseDTO;
import com.itau.srv.trading.service.dto.cotacaob3.CotacaoB3;
import com.itau.srv.trading.service.dto.itemcesta.ItemCestaRequestDTO;
import com.itau.srv.trading.service.dto.itemcesta.ItemCestaResponseDTO;
import com.itau.srv.trading.service.service.CestaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para CestaController")
class CestaControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private CestaService cestaService;

    @InjectMocks
    private CestaController cestaController;

    private CriarTopFiveRequestDTO requestDTO;
    private CriarTopFiveResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders
                .standaloneSetup(cestaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        List<ItemCestaRequestDTO> itens = List.of(
                new ItemCestaRequestDTO("PETR4", new BigDecimal("30.00")),
                new ItemCestaRequestDTO("VALE3", new BigDecimal("25.00")),
                new ItemCestaRequestDTO("ITUB4", new BigDecimal("20.00")),
                new ItemCestaRequestDTO("BBDC4", new BigDecimal("15.00")),
                new ItemCestaRequestDTO("WEGE3", new BigDecimal("10.00"))
        );

        requestDTO = new CriarTopFiveRequestDTO("Top Five - Fevereiro 2026", itens);

        List<ItemCestaResponseDTO> itensResponse = itens.stream()
                .map(item -> new ItemCestaResponseDTO(item.ticker(), item.percentual()))
                .toList();

        responseDTO = new CriarTopFiveResponseDTO(
                1L,
                "Top Five - Fevereiro 2026",
                true,
                LocalDateTime.of(2026, 2, 1, 9, 0, 0),
                itensResponse,
                false,
                "Primeira cesta cadastrada com sucesso."
        );
    }

    @Test
    @DisplayName("Deve criar cesta com sucesso e retornar 201")
    void deveCriarCestaComSucessoERetornar201() throws Exception {
        // Given
        when(cestaService.criarOuAlterarCesta(any(CriarTopFiveRequestDTO.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/api/admin/cesta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", containsString("/1")))
                .andExpect(jsonPath("$.cestaId").value(1))
                .andExpect(jsonPath("$.nome").value("Top Five - Fevereiro 2026"))
                .andExpect(jsonPath("$.ativa").value(true))
                .andExpect(jsonPath("$.rebalanceamentoDisparado").value(false))
                .andExpect(jsonPath("$.mensagem").value("Primeira cesta cadastrada com sucesso."))
                .andExpect(jsonPath("$.itens", hasSize(5)))
                .andExpect(jsonPath("$.itens[0].ticker").value("PETR4"))
                .andExpect(jsonPath("$.itens[0].percentual").value(30.00));

        verify(cestaService).criarOuAlterarCesta(any(CriarTopFiveRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar erro quando service lança NegocioException")
    void deveRetornarErroQuandoServiceLancaNegocioException() throws Exception {
        // Given
        when(cestaService.criarOuAlterarCesta(any(CriarTopFiveRequestDTO.class)))
                .thenThrow(new NegocioException("QUANTIDADE_ATIVOS_INVALIDA"));

        // When & Then
        mockMvc.perform(post("/api/admin/cesta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(cestaService).criarOuAlterarCesta(any(CriarTopFiveRequestDTO.class));
    }

    @Test
    @DisplayName("Deve listar cotações com sucesso")
    void deveListarCotacoesComSucesso() throws Exception {
        // Given
        List<CotacaoB3> cotacoes = List.of(
                new CotacaoB3(
                        LocalDate.of(2026, 1, 31),
                        "PETR4",
                        "02",
                        10,
                        "PETROBRAS",
                        new BigDecimal("35.00"),
                        new BigDecimal("36.00"),
                        new BigDecimal("34.00"),
                        new BigDecimal("35.50"),
                        new BigDecimal("35.25"),
                        1000000L,
                        new BigDecimal("35250000.00")
                ),
                new CotacaoB3(
                        LocalDate.of(2026, 1, 31),
                        "VALE3",
                        "02",
                        10,
                        "VALE",
                        new BigDecimal("62.00"),
                        new BigDecimal("63.00"),
                        new BigDecimal("61.00"),
                        new BigDecimal("62.50"),
                        new BigDecimal("62.25"),
                        800000L,
                        new BigDecimal("49800000.00")
                )
        );

        when(cestaService.buscarCotacoes()).thenReturn(cotacoes);

        // When & Then
        mockMvc.perform(get("/api/admin/cotacoes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].ticker").value("PETR4"))
                .andExpect(jsonPath("$[0].precoFechamento").value(35.50))
                .andExpect(jsonPath("$[1].ticker").value("VALE3"))
                .andExpect(jsonPath("$[1].precoFechamento").value(62.50));

        verify(cestaService).buscarCotacoes();
    }

    @Test
    @DisplayName("Deve obter cotação de fechamento por ticker com sucesso")
    void deveObterCotacaoFechamentoPorTickerComSucesso() throws Exception {
        // Given
        CotacaoB3 cotacao = new CotacaoB3(
                LocalDate.of(2026, 1, 31),
                "PETR4",
                "02",
                10,
                "PETROBRAS",
                new BigDecimal("35.00"),
                new BigDecimal("36.00"),
                new BigDecimal("34.00"),
                new BigDecimal("35.50"),
                new BigDecimal("35.25"),
                1000000L,
                new BigDecimal("35250000.00")
        );

        when(cestaService.obterCotacaoFechamento("PETR4")).thenReturn(Optional.of(cotacao));

        // When & Then
        mockMvc.perform(get("/api/admin/cotacoes/PETR4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticker").value("PETR4"))
                .andExpect(jsonPath("$.precoFechamento").value(35.50))
                .andExpect(jsonPath("$.nomeEmpresa").value("PETROBRAS"));

        verify(cestaService).obterCotacaoFechamento("PETR4");
    }

    @Test
    @DisplayName("Deve retornar erro 500 quando ticker não for encontrado")
    void deveRetornarErro500QuandoTickerNaoForEncontrado() throws Exception {
        // Given
        when(cestaService.obterCotacaoFechamento(anyString())).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/admin/cotacoes/INVALIDO")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(cestaService).obterCotacaoFechamento("INVALIDO");
    }

    @Test
    @DisplayName("Deve retornar Location header com ID da cesta criada")
    void deveRetornarLocationHeaderComIdDaCestaCriada() throws Exception {
        // Given
        CriarTopFiveResponseDTO responseComId5 = new CriarTopFiveResponseDTO(
                5L,
                "Top Five - Março 2026",
                true,
                LocalDateTime.now(),
                List.of(),
                false,
                "Primeira cesta cadastrada com sucesso."
        );

        when(cestaService.criarOuAlterarCesta(any(CriarTopFiveRequestDTO.class))).thenReturn(responseComId5);

        // When & Then
        mockMvc.perform(post("/api/admin/cesta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", containsString("/5")));

        verify(cestaService).criarOuAlterarCesta(any(CriarTopFiveRequestDTO.class));
    }

    @Test
    @DisplayName("Deve aceitar JSON válido e retornar response correto")
    void deveAceitarJsonValidoERetornarResponseCorreto() throws Exception {
        // Given
        when(cestaService.criarOuAlterarCesta(any(CriarTopFiveRequestDTO.class))).thenReturn(responseDTO);

        String jsonRequest = """
                {
                  "nome": "Top Five - Fevereiro 2026",
                  "itens": [
                    { "ticker": "PETR4", "percentual": 30.00 },
                    { "ticker": "VALE3", "percentual": 25.00 },
                    { "ticker": "ITUB4", "percentual": 20.00 },
                    { "ticker": "BBDC4", "percentual": 15.00 },
                    { "ticker": "WEGE3", "percentual": 10.00 }
                  ]
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/admin/cesta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cestaId").value(1))
                .andExpect(jsonPath("$.itens", hasSize(5)));

        verify(cestaService).criarOuAlterarCesta(any(CriarTopFiveRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar lista vazia de cotações quando não houver dados")
    void deveRetornarListaVaziaDeCotacoesQuandoNaoHouverDados() throws Exception {
        // Given
        when(cestaService.buscarCotacoes()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/admin/cotacoes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(cestaService).buscarCotacoes();
    }

    @Test
    @DisplayName("Deve chamar service com os parâmetros corretos")
    void deveChamarServiceComParametrosCorretos() throws Exception {
        // Given
        when(cestaService.criarOuAlterarCesta(any(CriarTopFiveRequestDTO.class))).thenReturn(responseDTO);

        // When
        mockMvc.perform(post("/api/admin/cesta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated());

        // Then
        verify(cestaService, times(1)).criarOuAlterarCesta(any(CriarTopFiveRequestDTO.class));
    }

    @Test
    @DisplayName("Deve retornar todos os campos da cesta no response")
    void deveRetornarTodosCamposDaCestaNoResponse() throws Exception {
        // Given
        when(cestaService.criarOuAlterarCesta(any(CriarTopFiveRequestDTO.class))).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(post("/api/admin/cesta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cestaId").exists())
                .andExpect(jsonPath("$.nome").exists())
                .andExpect(jsonPath("$.ativa").exists())
                .andExpect(jsonPath("$.dataCriacao").exists())
                .andExpect(jsonPath("$.itens").exists())
                .andExpect(jsonPath("$.rebalanceamentoDisparado").exists())
                .andExpect(jsonPath("$.mensagem").exists());

        verify(cestaService).criarOuAlterarCesta(any(CriarTopFiveRequestDTO.class));
    }

    @Test
    @DisplayName("Deve buscar cotação por ticker específico")
    void deveBuscarCotacaoPorTickerEspecifico() throws Exception {
        // Given
        CotacaoB3 cotacao = new CotacaoB3(
                LocalDate.of(2026, 1, 31),
                "VALE3",
                "02",
                10,
                "VALE",
                new BigDecimal("62.00"),
                new BigDecimal("63.00"),
                new BigDecimal("61.00"),
                new BigDecimal("62.50"),
                new BigDecimal("62.25"),
                800000L,
                new BigDecimal("49800000.00")
        );

        when(cestaService.obterCotacaoFechamento("VALE3")).thenReturn(Optional.of(cotacao));

        // When & Then
        mockMvc.perform(get("/api/admin/cotacoes/VALE3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticker").value("VALE3"))
                .andExpect(jsonPath("$.nomeEmpresa").value("VALE"));

        verify(cestaService).obterCotacaoFechamento("VALE3");
    }
}

