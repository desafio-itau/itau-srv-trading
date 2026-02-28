package com.itau.srv.trading.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itau.common.library.handler.GlobalExceptionHandler;
import com.itau.srv.trading.service.dto.cotacaob3.CotacaoB3;
import com.itau.srv.trading.service.service.CotacaoService;
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
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para CotacaoController")
class CotacaoControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private CotacaoService cotacaoService;

    @InjectMocks
    private CotacaoController cotacaoController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders
                .standaloneSetup(cotacaoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
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

        when(cotacaoService.buscarCotacoes()).thenReturn(cotacoes);

        // When & Then
        mockMvc.perform(get("/api/cotacoes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].ticker").value("PETR4"))
                .andExpect(jsonPath("$[0].precoFechamento").value(35.50))
                .andExpect(jsonPath("$[1].ticker").value("VALE3"))
                .andExpect(jsonPath("$[1].precoFechamento").value(62.50));

        verify(cotacaoService).buscarCotacoes();
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

        when(cotacaoService.obterCotacaoFechamento("PETR4")).thenReturn(Optional.of(cotacao));

        // When & Then
        mockMvc.perform(get("/api/cotacoes/PETR4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticker").value("PETR4"))
                .andExpect(jsonPath("$.precoFechamento").value(35.50))
                .andExpect(jsonPath("$.nomeEmpresa").value("PETROBRAS"));

        verify(cotacaoService).obterCotacaoFechamento("PETR4");
    }

    @Test
    @DisplayName("Deve retornar erro 404 quando ticker não for encontrado")
    void deveRetornarErro404QuandoTickerNaoForEncontrado() throws Exception {
        // Given
        when(cotacaoService.obterCotacaoFechamento(anyString())).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/cotacoes/INVALIDO")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(cotacaoService).obterCotacaoFechamento("INVALIDO");
    }

    @Test
    @DisplayName("Deve retornar lista vazia de cotações quando não houver dados")
    void deveRetornarListaVaziaDeCotacoesQuandoNaoHouverDados() throws Exception {
        // Given
        when(cotacaoService.buscarCotacoes()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/cotacoes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(cotacaoService).buscarCotacoes();
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

        when(cotacaoService.obterCotacaoFechamento("VALE3")).thenReturn(Optional.of(cotacao));

        // When & Then
        mockMvc.perform(get("/api/cotacoes/VALE3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticker").value("VALE3"))
                .andExpect(jsonPath("$.nomeEmpresa").value("VALE"));

        verify(cotacaoService).obterCotacaoFechamento("VALE3");
    }
}
