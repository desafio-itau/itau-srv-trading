package com.itau.srv.trading.service.service;

import com.itau.srv.trading.service.dto.cotacaob3.CotacaoB3;
import com.itau.srv.trading.service.dto.itemcesta.ItemCestaRequestDTO;
import com.itau.srv.trading.service.model.CestaRecomendacao;
import com.itau.srv.trading.service.model.ItemCesta;
import com.itau.srv.trading.service.repository.ItemCestaRepository;
import com.itau.srv.trading.service.util.CotahistParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para ItemCestaService")
class ItemCestaServiceTest {

    @Mock
    private ItemCestaRepository itemCestaRepository;

    @Mock
    private CotahistParser cotahistParser;

    @InjectMocks
    private ItemCestaService itemCestaService;

    @Captor
    private ArgumentCaptor<List<ItemCesta>> itemCestaListCaptor;

    private CestaRecomendacao cestaRecomendacao;
    private List<ItemCestaRequestDTO> itensRequest;
    private CotacaoB3 cotacaoValida;

    @BeforeEach
    void setUp() {
        cestaRecomendacao = new CestaRecomendacao();
        cestaRecomendacao.setId(1L);
        cestaRecomendacao.setNome("Top Five - Fevereiro 2026");
        cestaRecomendacao.setAtiva(true);

        itensRequest = List.of(
                new ItemCestaRequestDTO("PETR4", new BigDecimal("30.00")),
                new ItemCestaRequestDTO("VALE3", new BigDecimal("25.00")),
                new ItemCestaRequestDTO("ITUB4", new BigDecimal("20.00")),
                new ItemCestaRequestDTO("BBDC4", new BigDecimal("15.00")),
                new ItemCestaRequestDTO("WEGE3", new BigDecimal("10.00"))
        );

        cotacaoValida = new CotacaoB3(
                LocalDate.now(),
                "PETR4",
                10,
                new BigDecimal("35.00"),
                new BigDecimal("36.00"),
                new BigDecimal("34.00"),
                new BigDecimal("35.50")
        );
    }

    @Test
    @DisplayName("Deve criar itens da cesta com sucesso")
    void deveCriarItensCestaComSucesso() {
        // Given
        when(cotahistParser.obterCotacaoFechamento(anyString(), anyString()))
                .thenReturn(Optional.of(cotacaoValida));

        List<ItemCesta> itensEsperados = itensRequest.stream()
                .map(item -> {
                    ItemCesta itemCesta = new ItemCesta();
                    itemCesta.setTicker(item.ticker());
                    itemCesta.setPercentual(item.percentual());
                    itemCesta.setCestaRecomendacao(cestaRecomendacao);
                    return itemCesta;
                })
                .toList();

        when(itemCestaRepository.saveAll(anyList())).thenReturn(itensEsperados);

        // When
        List<ItemCesta> resultado = itemCestaService.criarItensCesta(itensRequest, cestaRecomendacao);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(5);

        verify(cotahistParser, times(5)).obterCotacaoFechamento(anyString(), anyString());
        verify(itemCestaRepository).saveAll(itemCestaListCaptor.capture());

        List<ItemCesta> itensSalvos = itemCestaListCaptor.getValue();
        assertThat(itensSalvos).hasSize(5);
        assertThat(itensSalvos.get(0).getTicker()).isEqualTo("PETR4");
        assertThat(itensSalvos.get(0).getPercentual()).isEqualTo(new BigDecimal("30.00"));
        assertThat(itensSalvos.get(0).getCestaRecomendacao()).isEqualTo(cestaRecomendacao);
    }

    @Test
    @DisplayName("Deve lançar exceção quando ticker não é encontrado no COTAHIST")
    void deveLancarExcecaoQuandoTickerNaoEncontrado() {
        // Given
        when(cotahistParser.obterCotacaoFechamento(anyString(), eq("PETR4")))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> itemCestaService.criarItensCesta(itensRequest, cestaRecomendacao))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cotação de fechamento não encontrada para ticker: PETR4");

        verify(cotahistParser).obterCotacaoFechamento(anyString(), eq("PETR4"));
        verify(itemCestaRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Deve validar todos os tickers antes de salvar")
    void deveValidarTodosTickersAntesDeSalvar() {
        // Given
        when(cotahistParser.obterCotacaoFechamento(anyString(), anyString()))
                .thenReturn(Optional.of(cotacaoValida));

        List<ItemCesta> itensEsperados = itensRequest.stream()
                .map(item -> {
                    ItemCesta itemCesta = new ItemCesta();
                    itemCesta.setTicker(item.ticker());
                    itemCesta.setPercentual(item.percentual());
                    itemCesta.setCestaRecomendacao(cestaRecomendacao);
                    return itemCesta;
                })
                .toList();

        when(itemCestaRepository.saveAll(anyList())).thenReturn(itensEsperados);

        // When
        itemCestaService.criarItensCesta(itensRequest, cestaRecomendacao);

        // Then
        verify(cotahistParser).obterCotacaoFechamento(anyString(), eq("PETR4"));
        verify(cotahistParser).obterCotacaoFechamento(anyString(), eq("VALE3"));
        verify(cotahistParser).obterCotacaoFechamento(anyString(), eq("ITUB4"));
        verify(cotahistParser).obterCotacaoFechamento(anyString(), eq("BBDC4"));
        verify(cotahistParser).obterCotacaoFechamento(anyString(), eq("WEGE3"));
    }

    @Test
    @DisplayName("Deve vincular todos os itens à cesta correta")
    void deveVincularTodosItensACestaCorreta() {
        // Given
        when(cotahistParser.obterCotacaoFechamento(anyString(), anyString()))
                .thenReturn(Optional.of(cotacaoValida));

        List<ItemCesta> itensEsperados = itensRequest.stream()
                .map(item -> {
                    ItemCesta itemCesta = new ItemCesta();
                    itemCesta.setTicker(item.ticker());
                    itemCesta.setPercentual(item.percentual());
                    itemCesta.setCestaRecomendacao(cestaRecomendacao);
                    return itemCesta;
                })
                .toList();

        when(itemCestaRepository.saveAll(anyList())).thenReturn(itensEsperados);

        // When
        itemCestaService.criarItensCesta(itensRequest, cestaRecomendacao);

        // Then
        verify(itemCestaRepository).saveAll(itemCestaListCaptor.capture());

        List<ItemCesta> itensSalvos = itemCestaListCaptor.getValue();
        itensSalvos.forEach(item -> {
            assertThat(item.getCestaRecomendacao()).isEqualTo(cestaRecomendacao);
            assertThat(item.getCestaRecomendacao().getId()).isEqualTo(1L);
        });
    }

    @Test
    @DisplayName("Deve preservar os percentuais de cada item")
    void devePreservarPercentuaisDeCadaItem() {
        // Given
        when(cotahistParser.obterCotacaoFechamento(anyString(), anyString()))
                .thenReturn(Optional.of(cotacaoValida));

        List<ItemCesta> itensEsperados = itensRequest.stream()
                .map(item -> {
                    ItemCesta itemCesta = new ItemCesta();
                    itemCesta.setTicker(item.ticker());
                    itemCesta.setPercentual(item.percentual());
                    itemCesta.setCestaRecomendacao(cestaRecomendacao);
                    return itemCesta;
                })
                .toList();

        when(itemCestaRepository.saveAll(anyList())).thenReturn(itensEsperados);

        // When
        itemCestaService.criarItensCesta(itensRequest, cestaRecomendacao);

        // Then
        verify(itemCestaRepository).saveAll(itemCestaListCaptor.capture());

        List<ItemCesta> itensSalvos = itemCestaListCaptor.getValue();
        assertThat(itensSalvos.get(0).getPercentual()).isEqualTo(new BigDecimal("30.00"));
        assertThat(itensSalvos.get(1).getPercentual()).isEqualTo(new BigDecimal("25.00"));
        assertThat(itensSalvos.get(2).getPercentual()).isEqualTo(new BigDecimal("20.00"));
        assertThat(itensSalvos.get(3).getPercentual()).isEqualTo(new BigDecimal("15.00"));
        assertThat(itensSalvos.get(4).getPercentual()).isEqualTo(new BigDecimal("10.00"));
    }

    @Test
    @DisplayName("Deve lançar exceção no segundo ticker inválido")
    void deveLancarExcecaoNoSegundoTickerInvalido() {
        // Given
        when(cotahistParser.obterCotacaoFechamento(anyString(), eq("PETR4")))
                .thenReturn(Optional.of(cotacaoValida));
        when(cotahistParser.obterCotacaoFechamento(anyString(), eq("VALE3")))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> itemCestaService.criarItensCesta(itensRequest, cestaRecomendacao))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cotação de fechamento não encontrada para ticker: VALE3");

        verify(cotahistParser).obterCotacaoFechamento(anyString(), eq("PETR4"));
        verify(cotahistParser).obterCotacaoFechamento(anyString(), eq("VALE3"));
        verify(cotahistParser, never()).obterCotacaoFechamento(anyString(), eq("ITUB4"));
        verify(itemCestaRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Deve criar itens com tickers em ordem correta")
    void deveCriarItensComTickersEmOrdemCorreta() {
        // Given
        when(cotahistParser.obterCotacaoFechamento(anyString(), anyString()))
                .thenReturn(Optional.of(cotacaoValida));

        List<ItemCesta> itensEsperados = itensRequest.stream()
                .map(item -> {
                    ItemCesta itemCesta = new ItemCesta();
                    itemCesta.setTicker(item.ticker());
                    itemCesta.setPercentual(item.percentual());
                    itemCesta.setCestaRecomendacao(cestaRecomendacao);
                    return itemCesta;
                })
                .toList();

        when(itemCestaRepository.saveAll(anyList())).thenReturn(itensEsperados);

        // When
        itemCestaService.criarItensCesta(itensRequest, cestaRecomendacao);

        // Then
        verify(itemCestaRepository).saveAll(itemCestaListCaptor.capture());

        List<ItemCesta> itensSalvos = itemCestaListCaptor.getValue();
        assertThat(itensSalvos.get(0).getTicker()).isEqualTo("PETR4");
        assertThat(itensSalvos.get(1).getTicker()).isEqualTo("VALE3");
        assertThat(itensSalvos.get(2).getTicker()).isEqualTo("ITUB4");
        assertThat(itensSalvos.get(3).getTicker()).isEqualTo("BBDC4");
        assertThat(itensSalvos.get(4).getTicker()).isEqualTo("WEGE3");
    }
}

