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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para CotacaoController")
class CotacaoControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private CotacaoService cotacaoService;

    @InjectMocks
    private CotacaoController cotacaoController;

    private List<CotacaoB3> cotacoesSalvas;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders
                .standaloneSetup(cotacaoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        CotacaoB3 cotacao1 = new CotacaoB3(
                LocalDate.of(2026, 2, 28),
                "PETR4",
                10,
                new BigDecimal("35.00"),
                new BigDecimal("36.50"),
                new BigDecimal("34.00"),
                new BigDecimal("35.75")
        );

        CotacaoB3 cotacao2 = new CotacaoB3(
                LocalDate.of(2026, 2, 28),
                "VALE3",
                10,
                new BigDecimal("62.00"),
                new BigDecimal("63.00"),
                new BigDecimal("61.00"),
                new BigDecimal("62.50")
        );

        CotacaoB3 cotacao3 = new CotacaoB3(
                LocalDate.of(2026, 2, 28),
                "ITUB4",
                10,
                new BigDecimal("30.00"),
                new BigDecimal("31.00"),
                new BigDecimal("29.50"),
                new BigDecimal("30.50")
        );

        cotacoesSalvas = List.of(cotacao1, cotacao2, cotacao3);
    }

    @Test
    @DisplayName("Deve listar cotações com sucesso")
    void deveListarCotacoesComSucesso() throws Exception {
        // Given
        List<CotacaoB3> cotacoes = List.of(
                new CotacaoB3(
                        LocalDate.of(2026, 1, 31),
                        "PETR4",
                        10,
                        new BigDecimal("35.00"),
                        new BigDecimal("36.00"),
                        new BigDecimal("34.00"),
                        new BigDecimal("35.50")
                ),
                new CotacaoB3(
                        LocalDate.of(2026, 1, 31),
                        "VALE3",
                        10,
                        new BigDecimal("62.00"),
                        new BigDecimal("63.00"),
                        new BigDecimal("61.00"),
                        new BigDecimal("62.50")
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
                10,
                new BigDecimal("62.00"),
                new BigDecimal("63.00"),
                new BigDecimal("61.00"),
                new BigDecimal("62.50")
        );

        when(cotacaoService.obterCotacaoFechamento("VALE3")).thenReturn(cotacao);

        // When & Then
        mockMvc.perform(get("/api/cotacoes/VALE3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticker").value("VALE3"));

        verify(cotacaoService).obterCotacaoFechamento("VALE3");
    }

    @Test
    @DisplayName("Deve salvar cotações do TXT e retornar 200 OK com lista de cotações")
    void deveSalvarCotacoesDoTxtComSucesso() throws Exception {
        // Given
        when(cotacaoService.salvarCotacoes()).thenReturn(cotacoesSalvas);

        // When & Then
        mockMvc.perform(post("/api/cotacoes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].ticker").value("PETR4"))
                .andExpect(jsonPath("$[0].precoFechamento").value(35.75))
                .andExpect(jsonPath("$[1].ticker").value("VALE3"))
                .andExpect(jsonPath("$[1].precoFechamento").value(62.50))
                .andExpect(jsonPath("$[2].ticker").value("ITUB4"))
                .andExpect(jsonPath("$[2].precoFechamento").value(30.50));

        verify(cotacaoService, times(1)).salvarCotacoes();
    }

    @Test
    @DisplayName("Deve chamar o service para salvar cotações")
    void deveChamarServiceParaSalvarCotacoes() throws Exception {
        // Given
        when(cotacaoService.salvarCotacoes()).thenReturn(cotacoesSalvas);

        // When
        mockMvc.perform(post("/api/cotacoes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Then
        verify(cotacaoService, times(1)).salvarCotacoes();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver cotações para salvar")
    void deveRetornarListaVaziaQuandoNaoHouverCotacoes() throws Exception {
        // Given
        when(cotacaoService.salvarCotacoes()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(post("/api/cotacoes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(cotacaoService, times(1)).salvarCotacoes();
    }

    @Test
    @DisplayName("Deve retornar todos os campos das cotações salvas")
    void deveRetornarTodosCamposDasCotacoesSalvas() throws Exception {
        // Given
        when(cotacaoService.salvarCotacoes()).thenReturn(cotacoesSalvas);

        // When & Then
        mockMvc.perform(post("/api/cotacoes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ticker").exists())
                .andExpect(jsonPath("$[0].dataPregao").exists())
                .andExpect(jsonPath("$[0].tipoMercado").exists())
                .andExpect(jsonPath("$[0].precoAbertura").exists())
                .andExpect(jsonPath("$[0].precoMaximo").exists())
                .andExpect(jsonPath("$[0].precoMinimo").exists())
                .andExpect(jsonPath("$[0].precoFechamento").exists());

        verify(cotacaoService, times(1)).salvarCotacoes();
    }

    @Test
    @DisplayName("Deve retornar Content-Type application/json")
    void deveRetornarContentTypeApplicationJson() throws Exception {
        // Given
        when(cotacaoService.salvarCotacoes()).thenReturn(cotacoesSalvas);

        // When & Then
        mockMvc.perform(post("/api/cotacoes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(cotacaoService, times(1)).salvarCotacoes();
    }

    @Test
    @DisplayName("Deve processar múltiplas cotações na resposta")
    void deveProcessarMultiplasCotacoesNaResposta() throws Exception {
        // Given
        when(cotacaoService.salvarCotacoes()).thenReturn(cotacoesSalvas);

        // When & Then
        mockMvc.perform(post("/api/cotacoes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].ticker").value("PETR4"))
                .andExpect(jsonPath("$[1].ticker").value("VALE3"))
                .andExpect(jsonPath("$[2].ticker").value("ITUB4"));

        verify(cotacaoService, times(1)).salvarCotacoes();
    }

    @Test
    @DisplayName("Deve retornar preços corretos de cada cotação")
    void deveRetornarPrecosCorretosDeCadaCotacao() throws Exception {
        // Given
        when(cotacaoService.salvarCotacoes()).thenReturn(cotacoesSalvas);

        // When & Then
        mockMvc.perform(post("/api/cotacoes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].precoAbertura").value(35.00))
                .andExpect(jsonPath("$[0].precoMaximo").value(36.50))
                .andExpect(jsonPath("$[0].precoMinimo").value(34.00))
                .andExpect(jsonPath("$[0].precoFechamento").value(35.75))
                .andExpect(jsonPath("$[1].precoAbertura").value(62.00))
                .andExpect(jsonPath("$[1].precoMaximo").value(63.00))
                .andExpect(jsonPath("$[1].precoMinimo").value(61.00))
                .andExpect(jsonPath("$[1].precoFechamento").value(62.50));

        verify(cotacaoService, times(1)).salvarCotacoes();
    }

    @Test
    @DisplayName("Deve retornar tipo de mercado correto para cada cotação")
    void deveRetornarTipoDeMercadoCorreto() throws Exception {
        // Given
        when(cotacaoService.salvarCotacoes()).thenReturn(cotacoesSalvas);

        // When & Then
        mockMvc.perform(post("/api/cotacoes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoMercado").value(10))
                .andExpect(jsonPath("$[1].tipoMercado").value(10))
                .andExpect(jsonPath("$[2].tipoMercado").value(10));

        verify(cotacaoService, times(1)).salvarCotacoes();
    }

    @Test
    @DisplayName("Deve aceitar requisição sem body")
    void deveAceitarRequisicaoSemBody() throws Exception {
        // Given
        when(cotacaoService.salvarCotacoes()).thenReturn(cotacoesSalvas);

        // When & Then
        mockMvc.perform(post("/api/cotacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

        verify(cotacaoService, times(1)).salvarCotacoes();
    }

    @Test
    @DisplayName("Deve processar endpoint correto POST /api/cotacoes")
    void deveProcessarEndpointCorreto() throws Exception {
        // Given
        when(cotacaoService.salvarCotacoes()).thenReturn(cotacoesSalvas);

        // When & Then
        mockMvc.perform(post("/api/cotacoes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(cotacaoService, times(1)).salvarCotacoes();
    }

    @Test
    @DisplayName("Deve chamar service apenas uma vez")
    void deveChamarServiceApenasUmaVez() throws Exception {
        // Given
        when(cotacaoService.salvarCotacoes()).thenReturn(cotacoesSalvas);

        // When
        mockMvc.perform(post("/api/cotacoes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Then
        verify(cotacaoService, times(1)).salvarCotacoes();
        verifyNoMoreInteractions(cotacaoService);
    }

    @Test
    @DisplayName("Deve retornar resposta mesmo com uma única cotação")
    void deveRetornarRespostaComUmaCotacao() throws Exception {
        // Given
        List<CotacaoB3> umaCotacao = List.of(cotacoesSalvas.get(0));
        when(cotacaoService.salvarCotacoes()).thenReturn(umaCotacao);

        // When & Then
        mockMvc.perform(post("/api/cotacoes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].ticker").value("PETR4"));

        verify(cotacaoService, times(1)).salvarCotacoes();
    }

    @Test
    @DisplayName("Deve processar grande quantidade de cotações")
    void deveProcessarGrandeQuantidadeDeCotacoes() throws Exception {
        // Given
        List<CotacaoB3> muitasCotacoes = List.of(
                cotacoesSalvas.get(0),
                cotacoesSalvas.get(1),
                cotacoesSalvas.get(2),
                cotacoesSalvas.get(0),
                cotacoesSalvas.get(1)
        );
        when(cotacaoService.salvarCotacoes()).thenReturn(muitasCotacoes);

        // When & Then
        mockMvc.perform(post("/api/cotacoes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));

        verify(cotacaoService, times(1)).salvarCotacoes();
    }
}
