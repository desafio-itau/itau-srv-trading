package com.itau.srv.trading.service.mapper;

import com.itau.srv.trading.service.dto.cesta.CriarTopFiveRequestDTO;
import com.itau.srv.trading.service.dto.cesta.CriarTopFiveResponseDTO;
import com.itau.srv.trading.service.dto.itemcesta.ItemCestaRequestDTO;
import com.itau.srv.trading.service.model.CestaRecomendacao;
import com.itau.srv.trading.service.model.ItemCesta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para CestaMapper")
class CestaMapperTest {

    @InjectMocks
    private CestaMapper cestaMapper;

    private CriarTopFiveRequestDTO requestDTO;
    private CestaRecomendacao cestaRecomendacao;
    private List<ItemCesta> itensCesta;

    @BeforeEach
    void setUp() {
        List<ItemCestaRequestDTO> itensRequest = List.of(
                new ItemCestaRequestDTO("PETR4", new BigDecimal("30.00")),
                new ItemCestaRequestDTO("VALE3", new BigDecimal("25.00")),
                new ItemCestaRequestDTO("ITUB4", new BigDecimal("20.00")),
                new ItemCestaRequestDTO("BBDC4", new BigDecimal("15.00")),
                new ItemCestaRequestDTO("WEGE3", new BigDecimal("10.00"))
        );

        requestDTO = new CriarTopFiveRequestDTO("Top Five - Fevereiro 2026", itensRequest);

        cestaRecomendacao = new CestaRecomendacao();
        cestaRecomendacao.setId(1L);
        cestaRecomendacao.setNome("Top Five - Fevereiro 2026");
        cestaRecomendacao.setAtiva(true);
        cestaRecomendacao.setDataCriacao(LocalDateTime.of(2026, 2, 1, 9, 0, 0));

        itensCesta = new ArrayList<>();
        for (ItemCestaRequestDTO item : itensRequest) {
            ItemCesta itemCesta = new ItemCesta();
            itemCesta.setId((long) (itensCesta.size() + 1));
            itemCesta.setTicker(item.ticker());
            itemCesta.setPercentual(item.percentual());
            itemCesta.setCestaRecomendacao(cestaRecomendacao);
            itensCesta.add(itemCesta);
        }
    }

    @Test
    @DisplayName("Deve mapear request DTO para CestaRecomendacao corretamente")
    void deveMapeararRequestParaCestaRecomendacao() {
        // When
        CestaRecomendacao resultado = cestaMapper.mapearParaCestaRecomendacao(requestDTO);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Top Five - Fevereiro 2026");
        assertThat(resultado.getAtiva()).isTrue();
        assertThat(resultado.getId()).isNull(); // ID ainda não foi gerado
        assertThat(resultado.getDataCriacao()).isNull(); // Data criada pelo @PrePersist
    }

    @Test
    @DisplayName("Deve mapear CestaRecomendacao e Itens para response DTO corretamente")
    void deveMapeararCestaRecomendacaoParaResponseDTO() {
        // When
        CriarTopFiveResponseDTO resultado = cestaMapper.mapearParaTopFiveResponse(cestaRecomendacao, itensCesta);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.cestaId()).isEqualTo(1L);
        assertThat(resultado.nome()).isEqualTo("Top Five - Fevereiro 2026");
        assertThat(resultado.ativa()).isTrue();
        assertThat(resultado.dataCriacao()).isEqualTo(LocalDateTime.of(2026, 2, 1, 9, 0, 0));
        assertThat(resultado.rebalanceamentoDisparado()).isFalse();
        assertThat(resultado.mensagem()).isEqualTo("Primeira cesta cadastrada com sucesso.");
    }

    @Test
    @DisplayName("Deve mapear itens da cesta corretamente para response DTO")
    void deveMapeararItensDaCestaParaResponseDTO() {
        // When
        CriarTopFiveResponseDTO resultado = cestaMapper.mapearParaTopFiveResponse(cestaRecomendacao, itensCesta);

        // Then
        assertThat(resultado.itens()).isNotNull();
        assertThat(resultado.itens()).hasSize(5);

        assertThat(resultado.itens().get(0).ticker()).isEqualTo("PETR4");
        assertThat(resultado.itens().get(0).percentual()).isEqualTo(new BigDecimal("30.00"));

        assertThat(resultado.itens().get(1).ticker()).isEqualTo("VALE3");
        assertThat(resultado.itens().get(1).percentual()).isEqualTo(new BigDecimal("25.00"));

        assertThat(resultado.itens().get(2).ticker()).isEqualTo("ITUB4");
        assertThat(resultado.itens().get(2).percentual()).isEqualTo(new BigDecimal("20.00"));

        assertThat(resultado.itens().get(3).ticker()).isEqualTo("BBDC4");
        assertThat(resultado.itens().get(3).percentual()).isEqualTo(new BigDecimal("15.00"));

        assertThat(resultado.itens().get(4).ticker()).isEqualTo("WEGE3");
        assertThat(resultado.itens().get(4).percentual()).isEqualTo(new BigDecimal("10.00"));
    }

    @Test
    @DisplayName("Deve criar resposta com rebalanceamento disparado = false")
    void deveCriarRespostaComRebalanceamentoFalse() {
        // When
        CriarTopFiveResponseDTO resultado = cestaMapper.mapearParaTopFiveResponse(cestaRecomendacao, itensCesta);

        // Then
        assertThat(resultado.rebalanceamentoDisparado()).isFalse();
    }

    @Test
    @DisplayName("Deve incluir mensagem de sucesso na resposta")
    void deveIncluirMensagemDeSucessoNaResposta() {
        // When
        CriarTopFiveResponseDTO resultado = cestaMapper.mapearParaTopFiveResponse(cestaRecomendacao, itensCesta);

        // Then
        assertThat(resultado.mensagem()).isNotNull();
        assertThat(resultado.mensagem()).isEqualTo("Primeira cesta cadastrada com sucesso.");
    }

    @Test
    @DisplayName("Deve mapear cesta vazia de itens")
    void deveMapeararCestaVaziaDeItens() {
        // Given
        List<ItemCesta> itensVazio = new ArrayList<>();

        // When
        CriarTopFiveResponseDTO resultado = cestaMapper.mapearParaTopFiveResponse(cestaRecomendacao, itensVazio);

        // Then
        assertThat(resultado.itens()).isNotNull();
        assertThat(resultado.itens()).isEmpty();
    }

    @Test
    @DisplayName("Deve preservar ordem dos itens no mapeamento")
    void devePreservarOrdemDosItensNoMapeamento() {
        // When
        CriarTopFiveResponseDTO resultado = cestaMapper.mapearParaTopFiveResponse(cestaRecomendacao, itensCesta);

        // Then
        List<String> tickersEsperados = List.of("PETR4", "VALE3", "ITUB4", "BBDC4", "WEGE3");
        List<String> tickersResultado = resultado.itens().stream()
                .map(item -> item.ticker())
                .toList();

        assertThat(tickersResultado).containsExactlyElementsOf(tickersEsperados);
    }

    @Test
    @DisplayName("Deve mapear cesta com nome customizado")
    void deveMapeararCestaComNomeCustomizado() {
        // Given
        CriarTopFiveRequestDTO requestCustomizado = new CriarTopFiveRequestDTO(
                "Cesta Personalizada - Março 2026",
                requestDTO.itens()
        );

        // When
        CestaRecomendacao resultado = cestaMapper.mapearParaCestaRecomendacao(requestCustomizado);

        // Then
        assertThat(resultado.getNome()).isEqualTo("Cesta Personalizada - Março 2026");
    }

    @Test
    @DisplayName("Deve sempre criar cesta ativa")
    void deveSempreCriarCestaAtiva() {
        // When
        CestaRecomendacao resultado = cestaMapper.mapearParaCestaRecomendacao(requestDTO);

        // Then
        assertThat(resultado.getAtiva()).isTrue();
    }

    @Test
    @DisplayName("Deve mapear todos os campos de ItemCestaResponseDTO")
    void deveMapeararTodosCamposDeItemCestaResponseDTO() {
        // When
        CriarTopFiveResponseDTO resultado = cestaMapper.mapearParaTopFiveResponse(cestaRecomendacao, itensCesta);

        // Then
        resultado.itens().forEach(item -> {
            assertThat(item.ticker()).isNotNull();
            assertThat(item.percentual()).isNotNull();
            assertThat(item.percentual()).isGreaterThan(BigDecimal.ZERO);
        });
    }
}

