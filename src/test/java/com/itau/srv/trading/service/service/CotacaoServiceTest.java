package com.itau.srv.trading.service.service;

import com.itau.srv.trading.service.dto.cotacaob3.CotacaoB3;
import com.itau.srv.trading.service.mapper.CotacaoMapper;
import com.itau.srv.trading.service.model.Cotacao;
import com.itau.srv.trading.service.repository.CotacaoRepository;
import com.itau.srv.trading.service.util.CotahistParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para CotacaoService")
class CotacaoServiceTest {

    @Mock
    private CotahistParser cotahistParser;

    @Mock
    private CotacaoMapper cotacaoMapper;

    @Mock
    private CotacaoRepository cotacaoRepository;

    @InjectMocks
    private CotacaoService cotacaoService;

    private List<CotacaoB3> cotacoesB3;
    private List<Cotacao> cotacoes;

    @BeforeEach
    void setUp() {
        CotacaoB3 cotacaoB3Petr = new CotacaoB3(
                LocalDate.of(2026, 2, 28),
                "PETR4",
                10,
                new BigDecimal("35.00"),
                new BigDecimal("36.50"),
                new BigDecimal("34.00"),
                new BigDecimal("35.75")
        );

        CotacaoB3 cotacaoB3Vale = new CotacaoB3(
                LocalDate.of(2026, 2, 28),
                "VALE3",
                10,
                new BigDecimal("62.00"),
                new BigDecimal("63.00"),
                new BigDecimal("61.00"),
                new BigDecimal("62.50")
        );

        cotacoesB3 = List.of(cotacaoB3Petr, cotacaoB3Vale);

        Cotacao cotacaoPetr = new Cotacao();
        cotacaoPetr.setTicker("PETR4");
        cotacaoPetr.setTipoMercado(10);
        cotacaoPetr.setPrecoFechamento(new BigDecimal("35.75"));

        Cotacao cotacaoVale = new Cotacao();
        cotacaoVale.setTicker("VALE3");
        cotacaoVale.setTipoMercado(10);
        cotacaoVale.setPrecoFechamento(new BigDecimal("62.50"));

        cotacoes = List.of(cotacaoPetr, cotacaoVale);
    }

    @Test
    @DisplayName("Deve buscar todas as cotações do banco")
    void deveBuscarTodasCotacoes() {
        // Given
        when(cotacaoRepository.findAll()).thenReturn(cotacoes);
        when(cotacaoMapper.mapearParaCotacaoB3(any())).thenReturn(cotacoesB3.get(0), cotacoesB3.get(1));

        // When
        List<CotacaoB3> resultado = cotacaoService.buscarCotacoes();

        // Then
        assertThat(resultado).hasSize(2);
        verify(cotacaoRepository).findAll();
        verify(cotacaoMapper, times(2)).mapearParaCotacaoB3(any());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver cotações")
    void deveRetornarListaVaziaQuandoNaoHouverCotacoes() {
        // Given
        when(cotacaoRepository.findAll()).thenReturn(List.of());

        // When
        List<CotacaoB3> resultado = cotacaoService.buscarCotacoes();

        // Then
        assertThat(resultado).isEmpty();
        verify(cotacaoMapper, never()).mapearParaCotacaoB3(any());
    }

    @Test
    @DisplayName("Deve obter cotação de fechamento por ticker")
    void deveObterCotacaoFechamentoPorTicker() {
        // Given
        when(cotacaoRepository.findAll()).thenReturn(cotacoes);
        when(cotacaoMapper.mapearParaCotacaoB3(any())).thenReturn(cotacoesB3.get(0));

        // When
        CotacaoB3 resultado = cotacaoService.obterCotacaoFechamento("PETR4");

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.ticker()).isEqualTo("PETR4");
        assertThat(resultado.precoFechamento()).isEqualTo(new BigDecimal("35.75"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ticker não for encontrado")
    void deveLancarExcecaoQuandoTickerNaoEncontrado() {
        // Given
        when(cotacaoRepository.findAll()).thenReturn(cotacoes);

        // When & Then
        assertThatThrownBy(() -> cotacaoService.obterCotacaoFechamento("INVALID"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("COTACAO_NAO_ENCONTRADA");
    }

    @Test
    @DisplayName("Deve filtrar apenas mercado à vista (tipo 10)")
    void deveFiltrarApenasMercadoAVista() {
        // Given
        Cotacao cotacaoAVista = new Cotacao();
        cotacaoAVista.setTicker("PETR4");
        cotacaoAVista.setTipoMercado(10);

        Cotacao cotacaoFracionario = new Cotacao();
        cotacaoFracionario.setTicker("PETR4");
        cotacaoFracionario.setTipoMercado(20);

        when(cotacaoRepository.findAll()).thenReturn(List.of(cotacaoFracionario, cotacaoAVista));
        when(cotacaoMapper.mapearParaCotacaoB3(cotacaoAVista)).thenReturn(cotacoesB3.get(0));

        // When
        CotacaoB3 resultado = cotacaoService.obterCotacaoFechamento("PETR4");

        // Then
        assertThat(resultado.tipoMercado()).isEqualTo(10);
    }

    @Test
    @DisplayName("Deve salvar cotações parseadas do arquivo TXT")
    void deveSalvarCotacoesDoArquivo() {
        // Given
        when(cotahistParser.parseArquivo(anyString())).thenReturn(cotacoesB3);
        when(cotacaoMapper.mapearParaCotacao(any())).thenReturn(cotacoes.get(0), cotacoes.get(1));
        when(cotacaoRepository.saveAll(anyList())).thenReturn(cotacoes);
        when(cotacaoMapper.mapearParaCotacaoB3(any())).thenReturn(cotacoesB3.get(0), cotacoesB3.get(1));

        // When
        List<CotacaoB3> resultado = cotacaoService.salvarCotacoes();

        // Then
        assertThat(resultado).hasSize(2);
        verify(cotahistParser).parseArquivo("cotacoes/COTAHIST_M012026.TXT");
        verify(cotacaoRepository).saveAll(anyList());
        verify(cotacaoMapper, times(2)).mapearParaCotacao(any());
        verify(cotacaoMapper, times(2)).mapearParaCotacaoB3(any());
    }

    @Test
    @DisplayName("Deve processar arquivo com caminho correto")
    void deveProcessarArquivoComCaminhoCorreto() {
        // Given
        when(cotahistParser.parseArquivo(anyString())).thenReturn(List.of());
        when(cotacaoRepository.saveAll(anyList())).thenReturn(List.of());

        // When
        cotacaoService.salvarCotacoes();

        // Then
        verify(cotahistParser).parseArquivo("cotacoes/COTAHIST_M012026.TXT");
    }

    @Test
    @DisplayName("Deve mapear todas as cotações antes de salvar")
    void deveMaperarTodasCotacoesAntesDeSalvar() {
        // Given
        when(cotahistParser.parseArquivo(anyString())).thenReturn(cotacoesB3);
        when(cotacaoMapper.mapearParaCotacao(any())).thenReturn(new Cotacao());
        when(cotacaoRepository.saveAll(anyList())).thenReturn(cotacoes);
        when(cotacaoMapper.mapearParaCotacaoB3(any())).thenReturn(cotacoesB3.get(0));

        // When
        cotacaoService.salvarCotacoes();

        // Then
        verify(cotacaoMapper, times(2)).mapearParaCotacao(any());
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao salvar arquivo vazio")
    void deveRetornarListaVaziaAoSalvarArquivoVazio() {
        // Given
        when(cotahistParser.parseArquivo(anyString())).thenReturn(List.of());
        when(cotacaoRepository.saveAll(anyList())).thenReturn(List.of());

        // When
        List<CotacaoB3> resultado = cotacaoService.salvarCotacoes();

        // Then
        assertThat(resultado).isEmpty();
        verify(cotacaoMapper, never()).mapearParaCotacao(any());
    }
}

