package com.itau.srv.trading.service.service;

import com.itau.common.library.exception.NegocioException;
import com.itau.srv.trading.service.dto.cesta.CestaRecomendacaoAtivaResponseDTO;
import com.itau.srv.trading.service.dto.cesta.CestaResponseDTO;
import com.itau.srv.trading.service.dto.cesta.CriarTopFiveRequestDTO;
import com.itau.srv.trading.service.dto.cesta.CriarTopFiveResponseDTO;
import com.itau.srv.trading.service.dto.itemcesta.ItemCestaRequestDTO;
import com.itau.srv.trading.service.mapper.CestaMapper;
import com.itau.srv.trading.service.model.CestaRecomendacao;
import com.itau.srv.trading.service.model.ItemCesta;
import com.itau.srv.trading.service.repository.CestaRecomendacaoRepository;
import com.itau.srv.trading.service.repository.ItemCestaRepository;
import com.itau.srv.trading.service.util.CotahistParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para CestaService")
class CestaServiceTest {

    @Mock
    private CestaMapper cestaMapper;

    @Mock
    private ItemCestaService itemCestaService;

    @Mock
    private CestaRecomendacaoRepository cestaRecomendacaoRepository;

    @Mock
    private ItemCestaRepository itemCestaRepository;

    @Mock
    private CotahistParser cotahistParser;

    @InjectMocks
    private CestaService cestaService;

    private CriarTopFiveRequestDTO requestDTOValido;
    private CestaRecomendacao cestaRecomendacao;
    private List<ItemCesta> itensCesta;

    @BeforeEach
    void setUp() {
        List<ItemCestaRequestDTO> itens = List.of(
                new ItemCestaRequestDTO("PETR4", new BigDecimal("30.00")),
                new ItemCestaRequestDTO("VALE3", new BigDecimal("25.00")),
                new ItemCestaRequestDTO("ITUB4", new BigDecimal("20.00")),
                new ItemCestaRequestDTO("BBDC4", new BigDecimal("15.00")),
                new ItemCestaRequestDTO("WEGE3", new BigDecimal("10.00"))
        );

        requestDTOValido = new CriarTopFiveRequestDTO("Top Five - Fevereiro 2026", itens);

        cestaRecomendacao = new CestaRecomendacao();
        cestaRecomendacao.setId(1L);
        cestaRecomendacao.setNome("Top Five - Fevereiro 2026");
        cestaRecomendacao.setAtiva(true);
        cestaRecomendacao.setDataCriacao(LocalDateTime.now());

        itensCesta = new ArrayList<>();
        for (ItemCestaRequestDTO item : itens) {
            ItemCesta itemCesta = new ItemCesta();
            itemCesta.setTicker(item.ticker());
            itemCesta.setPercentual(item.percentual());
            itemCesta.setCestaRecomendacao(cestaRecomendacao);
            itensCesta.add(itemCesta);
        }
    }

    @Test
    @DisplayName("Deve criar primeira cesta com sucesso")
    void deveCriarPrimeiraCestaComSucesso() {
        // Given
        when(cestaRecomendacaoRepository.findByAtivaTrue()).thenReturn(Optional.empty());
        when(cestaMapper.mapearParaCestaRecomendacao(any())).thenReturn(cestaRecomendacao);
        when(cestaRecomendacaoRepository.save(any())).thenReturn(cestaRecomendacao);
        when(itemCestaService.criarItensCesta(anyList(), any())).thenReturn(itensCesta);

        CriarTopFiveResponseDTO responseDTO = new CriarTopFiveResponseDTO(
                1L,
                "Top Five - Fevereiro 2026",
                true,
                LocalDateTime.now(),
                List.of(),
                false,
                "Primeira cesta cadastrada com sucesso."
        );
        when(cestaMapper.mapearParaTopFiveResponse(any(), anyList())).thenReturn(responseDTO);

        // When
        CestaResponseDTO resultado = cestaService.criarOuAlterarCesta(requestDTOValido);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).isInstanceOf(CriarTopFiveResponseDTO.class);

        verify(cestaRecomendacaoRepository).findByAtivaTrue();
        verify(cestaMapper).mapearParaCestaRecomendacao(requestDTOValido);
        verify(cestaRecomendacaoRepository).save(cestaRecomendacao);
        verify(itemCestaService).criarItensCesta(requestDTOValido.itens(), cestaRecomendacao);
        verify(cestaMapper).mapearParaTopFiveResponse(cestaRecomendacao, itensCesta);
    }

    @Test
    @DisplayName("Deve lançar exceção quando quantidade de itens é diferente de 5")
    void deveLancarExcecaoQuandoQuantidadeItensInvalida() {
        // Given
        List<ItemCestaRequestDTO> itensInvalidos = List.of(
                new ItemCestaRequestDTO("PETR4", new BigDecimal("50.00")),
                new ItemCestaRequestDTO("VALE3", new BigDecimal("50.00"))
        );
        CriarTopFiveRequestDTO requestInvalido = new CriarTopFiveRequestDTO("Cesta Inválida", itensInvalidos);

        // When & Then
        assertThatThrownBy(() -> cestaService.criarOuAlterarCesta(requestInvalido))
                .isInstanceOf(NegocioException.class)
                .hasMessage("QUANTIDADE_ATIVOS_INVALIDA");

        verify(cestaRecomendacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando soma dos percentuais não é 100")
    void deveLancarExcecaoQuandoSomaPercentuaisInvalida() {
        // Given
        List<ItemCestaRequestDTO> itensInvalidos = List.of(
                new ItemCestaRequestDTO("PETR4", new BigDecimal("30.00")),
                new ItemCestaRequestDTO("VALE3", new BigDecimal("25.00")),
                new ItemCestaRequestDTO("ITUB4", new BigDecimal("20.00")),
                new ItemCestaRequestDTO("BBDC4", new BigDecimal("15.00")),
                new ItemCestaRequestDTO("WEGE3", new BigDecimal("5.00")) // Total = 95%
        );
        CriarTopFiveRequestDTO requestInvalido = new CriarTopFiveRequestDTO("Cesta Inválida", itensInvalidos);

        // When & Then
        assertThatThrownBy(() -> cestaService.criarOuAlterarCesta(requestInvalido))
                .isInstanceOf(NegocioException.class)
                .hasMessage("PERCENTUAIS_INVALIDOS");

        verify(cestaRecomendacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve chamar alteração quando já existe cesta ativa")
    void deveChamarAlteracaoQuandoExisteCestaAtiva() {
        // Given
        when(cestaRecomendacaoRepository.findByAtivaTrue()).thenReturn(Optional.of(cestaRecomendacao));

        // When
        CestaResponseDTO resultado = cestaService.criarOuAlterarCesta(requestDTOValido);

        // Then
        // O método alterarCestaComRebalanceamento ainda não está implementado, então retorna null
        assertThat(resultado).isNull();

        verify(cestaRecomendacaoRepository).findByAtivaTrue();
        verify(cestaRecomendacaoRepository, never()).save(any());
        verify(itemCestaService, never()).criarItensCesta(anyList(), any());
    }

    @Test
    @DisplayName("Deve validar que percentuais somam exatamente 100")
    void deveValidarPercentuaisSomamExatamente100() {
        // Given
        List<ItemCestaRequestDTO> itensValidos = List.of(
                new ItemCestaRequestDTO("PETR4", new BigDecimal("30.00")),
                new ItemCestaRequestDTO("VALE3", new BigDecimal("25.00")),
                new ItemCestaRequestDTO("ITUB4", new BigDecimal("20.00")),
                new ItemCestaRequestDTO("BBDC4", new BigDecimal("15.00")),
                new ItemCestaRequestDTO("WEGE3", new BigDecimal("10.00"))
        );
        CriarTopFiveRequestDTO request = new CriarTopFiveRequestDTO("Cesta Válida", itensValidos);

        when(cestaRecomendacaoRepository.findByAtivaTrue()).thenReturn(Optional.empty());
        when(cestaMapper.mapearParaCestaRecomendacao(any())).thenReturn(cestaRecomendacao);
        when(cestaRecomendacaoRepository.save(any())).thenReturn(cestaRecomendacao);
        when(itemCestaService.criarItensCesta(anyList(), any())).thenReturn(itensCesta);

        CriarTopFiveResponseDTO responseDTO = new CriarTopFiveResponseDTO(
                1L, "Cesta Válida", true, LocalDateTime.now(), List.of(), false,
                "Primeira cesta cadastrada com sucesso."
        );
        when(cestaMapper.mapearParaTopFiveResponse(any(), anyList())).thenReturn(responseDTO);

        // When
        CestaResponseDTO resultado = cestaService.criarOuAlterarCesta(request);

        // Then
        assertThat(resultado).isNotNull();
        verify(cestaRecomendacaoRepository).save(any());
    }

    @Test
    @DisplayName("Deve rejeitar percentuais que somam mais de 100")
    void deveRejeitarPercentuaisMaiorQue100() {
        // Given
        List<ItemCestaRequestDTO> itensInvalidos = List.of(
                new ItemCestaRequestDTO("PETR4", new BigDecimal("30.00")),
                new ItemCestaRequestDTO("VALE3", new BigDecimal("30.00")),
                new ItemCestaRequestDTO("ITUB4", new BigDecimal("20.00")),
                new ItemCestaRequestDTO("BBDC4", new BigDecimal("15.00")),
                new ItemCestaRequestDTO("WEGE3", new BigDecimal("10.00")) // Total = 105%
        );
        CriarTopFiveRequestDTO requestInvalido = new CriarTopFiveRequestDTO("Cesta Inválida", itensInvalidos);

        // When & Then
        assertThatThrownBy(() -> cestaService.criarOuAlterarCesta(requestInvalido))
                .isInstanceOf(NegocioException.class)
                .hasMessage("PERCENTUAIS_INVALIDOS");
    }

    @Test
    @DisplayName("Deve permitir exatamente 5 itens")
    void devePermitirExatamente5Itens() {
        // Given
        when(cestaRecomendacaoRepository.findByAtivaTrue()).thenReturn(Optional.empty());
        when(cestaMapper.mapearParaCestaRecomendacao(any())).thenReturn(cestaRecomendacao);
        when(cestaRecomendacaoRepository.save(any())).thenReturn(cestaRecomendacao);
        when(itemCestaService.criarItensCesta(anyList(), any())).thenReturn(itensCesta);

        CriarTopFiveResponseDTO responseDTO = new CriarTopFiveResponseDTO(
                1L, "Top Five", true, LocalDateTime.now(), List.of(), false,
                "Primeira cesta cadastrada com sucesso."
        );
        when(cestaMapper.mapearParaTopFiveResponse(any(), anyList())).thenReturn(responseDTO);

        // When
        CestaResponseDTO resultado = cestaService.criarOuAlterarCesta(requestDTOValido);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(requestDTOValido.itens()).hasSize(5);
    }

    @Test
    @DisplayName("Deve rejeitar menos de 5 itens")
    void deveRejeitarMenosDe5Itens() {
        // Given
        List<ItemCestaRequestDTO> itensInvalidos = List.of(
                new ItemCestaRequestDTO("PETR4", new BigDecimal("60.00")),
                new ItemCestaRequestDTO("VALE3", new BigDecimal("40.00"))
        );
        CriarTopFiveRequestDTO requestInvalido = new CriarTopFiveRequestDTO("Cesta Inválida", itensInvalidos);

        // When & Then
        assertThatThrownBy(() -> cestaService.criarOuAlterarCesta(requestInvalido))
                .isInstanceOf(NegocioException.class)
                .hasMessage("QUANTIDADE_ATIVOS_INVALIDA");
    }

    @Test
    @DisplayName("Deve rejeitar mais de 5 itens")
    void deveRejeitarMaisDe5Itens() {
        // Given
        List<ItemCestaRequestDTO> itensInvalidos = List.of(
                new ItemCestaRequestDTO("PETR4", new BigDecimal("20.00")),
                new ItemCestaRequestDTO("VALE3", new BigDecimal("20.00")),
                new ItemCestaRequestDTO("ITUB4", new BigDecimal("20.00")),
                new ItemCestaRequestDTO("BBDC4", new BigDecimal("15.00")),
                new ItemCestaRequestDTO("WEGE3", new BigDecimal("15.00")),
                new ItemCestaRequestDTO("ABEV3", new BigDecimal("10.00")) // 6 itens
        );
        CriarTopFiveRequestDTO requestInvalido = new CriarTopFiveRequestDTO("Cesta Inválida", itensInvalidos);

        // When & Then
        assertThatThrownBy(() -> cestaService.criarOuAlterarCesta(requestInvalido))
                .isInstanceOf(NegocioException.class)
                .hasMessage("QUANTIDADE_ATIVOS_INVALIDA");
    }

    @Test
    @DisplayName("Deve obter cesta ativa com sucesso")
    void deveObterCestaAtivaComSucesso() {
        // Given
        when(cestaRecomendacaoRepository.findByAtivaTrue()).thenReturn(Optional.of(cestaRecomendacao));
        when(itemCestaRepository.findAllByCestaRecomendacao(cestaRecomendacao)).thenReturn(itensCesta);

        CestaRecomendacaoAtivaResponseDTO responseDTO = new CestaRecomendacaoAtivaResponseDTO(
                1L,
                "Top Five - Fevereiro 2026",
                true,
                LocalDateTime.now(),
                List.of()
        );
        when(cestaMapper.mapearParaCestaRecomendacaoAtivaResponse(cestaRecomendacao, itensCesta))
                .thenReturn(responseDTO);

        // When
        CestaRecomendacaoAtivaResponseDTO resultado = cestaService.obterCestaAtiva();

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.cestaId()).isEqualTo(1L);
        assertThat(resultado.nome()).isEqualTo("Top Five - Fevereiro 2026");
        assertThat(resultado.ativa()).isTrue();

        verify(cestaRecomendacaoRepository).findByAtivaTrue();
        verify(itemCestaRepository).findAllByCestaRecomendacao(cestaRecomendacao);
        verify(cestaMapper).mapearParaCestaRecomendacaoAtivaResponse(cestaRecomendacao, itensCesta);
    }

    @Test
    @DisplayName("Deve lançar exceção quando não encontrar cesta ativa")
    void deveLancarExcecaoQuandoNaoEncontrarCestaAtiva() {
        // Given
        when(cestaRecomendacaoRepository.findByAtivaTrue()).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> cestaService.obterCestaAtiva())
                .isInstanceOf(NegocioException.class)
                .hasMessage("CESTA_NAO_ENCONTRADA");

        verify(cestaRecomendacaoRepository).findByAtivaTrue();
        verify(itemCestaRepository, never()).findAllByCestaRecomendacao(any());
        verify(cestaMapper, never()).mapearParaCestaRecomendacaoAtivaResponse(any(), anyList());
    }

    @Test
    @DisplayName("Deve buscar itens da cesta ao obter cesta ativa")
    void deveBuscarItensDaCestaAoObterCestaAtiva() {
        // Given
        when(cestaRecomendacaoRepository.findByAtivaTrue()).thenReturn(Optional.of(cestaRecomendacao));
        when(itemCestaRepository.findAllByCestaRecomendacao(cestaRecomendacao)).thenReturn(itensCesta);

        CestaRecomendacaoAtivaResponseDTO responseDTO = new CestaRecomendacaoAtivaResponseDTO(
                1L,
                "Top Five - Fevereiro 2026",
                true,
                LocalDateTime.now(),
                List.of()
        );
        when(cestaMapper.mapearParaCestaRecomendacaoAtivaResponse(cestaRecomendacao, itensCesta))
                .thenReturn(responseDTO);

        // When
        cestaService.obterCestaAtiva();

        // Then
        verify(itemCestaRepository).findAllByCestaRecomendacao(cestaRecomendacao);
    }
}


