package com.itau.srv.trading.service.mapper;

import com.itau.srv.trading.service.dto.cotacaob3.CotacaoB3;
import com.itau.srv.trading.service.model.Cotacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para CotacaoMapper")
class CotacaoMapperTest {

    @InjectMocks
    private CotacaoMapper cotacaoMapper;

    private CotacaoB3 cotacaoB3;
    private Cotacao cotacao;

    @BeforeEach
    void setUp() {
        cotacaoB3 = new CotacaoB3(
                LocalDate.of(2026, 2, 28),
                "PETR4",
                10,
                new BigDecimal("35.00"),
                new BigDecimal("36.50"),
                new BigDecimal("34.00"),
                new BigDecimal("35.75")
        );

        cotacao = new Cotacao();
        cotacao.setId(1L);
        cotacao.setTicker("PETR4");
        cotacao.setDataPregao(LocalDate.of(2026, 2, 28));
        cotacao.setTipoMercado(10);
        cotacao.setPrecoAbertura(new BigDecimal("35.00"));
        cotacao.setPrecoMaximo(new BigDecimal("36.50"));
        cotacao.setPrecoMinimo(new BigDecimal("34.00"));
        cotacao.setPrecoFechamento(new BigDecimal("35.75"));
    }

    @Test
    @DisplayName("Deve mapear CotacaoB3 para Cotacao corretamente")
    void deveMaperarCotacaoB3ParaCotacao() {
        // When
        Cotacao resultado = cotacaoMapper.mapearParaCotacao(cotacaoB3);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getTicker()).isEqualTo("PETR4");
        assertThat(resultado.getDataPregao()).isEqualTo(LocalDate.of(2026, 2, 28));
        assertThat(resultado.getTipoMercado()).isEqualTo(10);
        assertThat(resultado.getPrecoAbertura()).isEqualTo(new BigDecimal("35.00"));
        assertThat(resultado.getPrecoMaximo()).isEqualTo(new BigDecimal("36.50"));
        assertThat(resultado.getPrecoMinimo()).isEqualTo(new BigDecimal("34.00"));
        assertThat(resultado.getPrecoFechamento()).isEqualTo(new BigDecimal("35.75"));
    }

    @Test
    @DisplayName("Deve mapear Cotacao para CotacaoB3 corretamente")
    void deveMaperarCotacaoParaCotacaoB3() {
        // When
        CotacaoB3 resultado = cotacaoMapper.mapearParaCotacaoB3(cotacao);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.ticker()).isEqualTo("PETR4");
        assertThat(resultado.dataPregao()).isEqualTo(LocalDate.of(2026, 2, 28));
        assertThat(resultado.tipoMercado()).isEqualTo(10);
        assertThat(resultado.precoAbertura()).isEqualTo(new BigDecimal("35.00"));
        assertThat(resultado.precoMaximo()).isEqualTo(new BigDecimal("36.50"));
        assertThat(resultado.precoMinimo()).isEqualTo(new BigDecimal("34.00"));
        assertThat(resultado.precoFechamento()).isEqualTo(new BigDecimal("35.75"));
    }

    @Test
    @DisplayName("Deve preservar todos os preços ao mapear de DTO para entidade")
    void devePreservarTodosPrecosAoMapearDeDTO() {
        // Given
        CotacaoB3 dto = new CotacaoB3(
                LocalDate.now(),
                "VALE3",
                10,
                new BigDecimal("62.00"),
                new BigDecimal("63.50"),
                new BigDecimal("61.00"),
                new BigDecimal("62.75")
        );

        // When
        Cotacao resultado = cotacaoMapper.mapearParaCotacao(dto);

        // Then
        assertThat(resultado.getPrecoAbertura()).isEqualTo(new BigDecimal("62.00"));
        assertThat(resultado.getPrecoMaximo()).isEqualTo(new BigDecimal("63.50"));
        assertThat(resultado.getPrecoMinimo()).isEqualTo(new BigDecimal("61.00"));
        assertThat(resultado.getPrecoFechamento()).isEqualTo(new BigDecimal("62.75"));
    }

    @Test
    @DisplayName("Deve mapear corretamente tipo de mercado à vista")
    void deveMaperarTipoMercadoAVista() {
        // Given
        CotacaoB3 dto = new CotacaoB3(
                LocalDate.now(),
                "ITUB4",
                10, // mercado à vista
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("30.00")
        );

        // When
        Cotacao resultado = cotacaoMapper.mapearParaCotacao(dto);

        // Then
        assertThat(resultado.getTipoMercado()).isEqualTo(10);
    }

    @Test
    @DisplayName("Deve mapear corretamente tipo de mercado fracionário")
    void deveMaperarTipoMercadoFracionario() {
        // Given
        CotacaoB3 dto = new CotacaoB3(
                LocalDate.now(),
                "PETR4F",
                20, // mercado fracionário
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("35.00")
        );

        // When
        Cotacao resultado = cotacaoMapper.mapearParaCotacao(dto);

        // Then
        assertThat(resultado.getTipoMercado()).isEqualTo(20);
    }

    @Test
    @DisplayName("Deve mapear ticker corretamente")
    void deveMaperarTickerCorretamente() {
        // Given
        CotacaoB3 dto = new CotacaoB3(
                LocalDate.now(),
                "BBDC4",
                10,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );

        // When
        Cotacao resultado = cotacaoMapper.mapearParaCotacao(dto);

        // Then
        assertThat(resultado.getTicker()).isEqualTo("BBDC4");
    }

    @Test
    @DisplayName("Deve mapear data de pregão corretamente")
    void deveMaperarDataPregaoCorretamente() {
        // Given
        LocalDate dataPregao = LocalDate.of(2026, 1, 15);
        CotacaoB3 dto = new CotacaoB3(
                dataPregao,
                "WEGE3",
                10,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );

        // When
        Cotacao resultado = cotacaoMapper.mapearParaCotacao(dto);

        // Then
        assertThat(resultado.getDataPregao()).isEqualTo(dataPregao);
    }

    @Test
    @DisplayName("Deve realizar mapeamento bidirecional corretamente")
    void deveRealizarMapeamentoBidirecional() {
        // When
        Cotacao entidade = cotacaoMapper.mapearParaCotacao(cotacaoB3);
        CotacaoB3 dtoResultado = cotacaoMapper.mapearParaCotacaoB3(entidade);

        // Then
        assertThat(dtoResultado.ticker()).isEqualTo(cotacaoB3.ticker());
        assertThat(dtoResultado.dataPregao()).isEqualTo(cotacaoB3.dataPregao());
        assertThat(dtoResultado.tipoMercado()).isEqualTo(cotacaoB3.tipoMercado());
        assertThat(dtoResultado.precoAbertura()).isEqualTo(cotacaoB3.precoAbertura());
        assertThat(dtoResultado.precoMaximo()).isEqualTo(cotacaoB3.precoMaximo());
        assertThat(dtoResultado.precoMinimo()).isEqualTo(cotacaoB3.precoMinimo());
        assertThat(dtoResultado.precoFechamento()).isEqualTo(cotacaoB3.precoFechamento());
    }

    @Test
    @DisplayName("Deve mapear múltiplas cotações com preços diferentes")
    void deveMaperarMultiplasCotacoesComPrecosDiferentes() {
        // Given
        CotacaoB3 cotacao1 = new CotacaoB3(
                LocalDate.now(),
                "PETR4",
                10,
                new BigDecimal("35.00"),
                new BigDecimal("35.50"),
                new BigDecimal("34.50"),
                new BigDecimal("35.25")
        );

        CotacaoB3 cotacao2 = new CotacaoB3(
                LocalDate.now(),
                "VALE3",
                10,
                new BigDecimal("62.00"),
                new BigDecimal("63.00"),
                new BigDecimal("61.50"),
                new BigDecimal("62.50")
        );

        // When
        Cotacao resultado1 = cotacaoMapper.mapearParaCotacao(cotacao1);
        Cotacao resultado2 = cotacaoMapper.mapearParaCotacao(cotacao2);

        // Then
        assertThat(resultado1.getTicker()).isEqualTo("PETR4");
        assertThat(resultado1.getPrecoFechamento()).isEqualTo(new BigDecimal("35.25"));
        assertThat(resultado2.getTicker()).isEqualTo("VALE3");
        assertThat(resultado2.getPrecoFechamento()).isEqualTo(new BigDecimal("62.50"));
    }
}

