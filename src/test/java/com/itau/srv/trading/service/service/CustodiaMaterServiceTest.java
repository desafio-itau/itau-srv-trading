package com.itau.srv.trading.service.service;

import com.itau.srv.trading.service.dto.custodiamaster.ContaMasterResponseDTO;
import com.itau.srv.trading.service.dto.custodiamaster.CustodiaMasterResponseDTO;
import com.itau.srv.trading.service.dto.custodiamaster.CustodiaResponseDTO;
import com.itau.srv.trading.service.feign.CustodiaMasterFeignClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para CustodiaMaterService")
class CustodiaMaterServiceTest {

    @Mock
    private CustodiaMasterFeignClient custodiaMasterFeignClient;

    @InjectMocks
    private CustodiaMaterService custodiaMaterService;

    private CustodiaMasterResponseDTO custodiaMasterResponse;

    @BeforeEach
    void setUp() {
        ContaMasterResponseDTO contaMaster = new ContaMasterResponseDTO(
                1L,
                "MASTER001",
                "MASTER"
        );

        List<CustodiaResponseDTO> custodias = List.of(
                new CustodiaResponseDTO(
                        "PETR4",
                        1000,
                        new BigDecimal("35.50"),
                        new BigDecimal("35500.00"),
                        "COMPRA"
                ),
                new CustodiaResponseDTO(
                        "VALE3",
                        500,
                        new BigDecimal("62.00"),
                        new BigDecimal("31000.00"),
                        "COMPRA"
                )
        );

        custodiaMasterResponse = new CustodiaMasterResponseDTO(
                contaMaster,
                custodias,
                new BigDecimal("5000.00")
        );
    }

    @Test
    @DisplayName("Deve buscar custodia master com sucesso")
    void deveBuscarCustodiaMasterComSucesso() {
        // Given
        when(custodiaMasterFeignClient.buscarCustodiaMaster()).thenReturn(custodiaMasterResponse);

        // When
        CustodiaMasterResponseDTO resultado = custodiaMaterService.buscarCustodiaMaster();

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.contaMaster()).isNotNull();
        assertThat(resultado.contaMaster().id()).isEqualTo(1L);
        assertThat(resultado.contaMaster().numeroConta()).isEqualTo("MASTER001");
        assertThat(resultado.contaMaster().tipo()).isEqualTo("MASTER");
        assertThat(resultado.custodia()).hasSize(2);
        assertThat(resultado.valorTotalResiduo()).isEqualTo(new BigDecimal("5000.00"));

        verify(custodiaMasterFeignClient, times(1)).buscarCustodiaMaster();
    }

    @Test
    @DisplayName("Deve retornar todas as custodias da conta master")
    void deveRetornarTodasCustodiasDaContaMaster() {
        // Given
        when(custodiaMasterFeignClient.buscarCustodiaMaster()).thenReturn(custodiaMasterResponse);

        // When
        CustodiaMasterResponseDTO resultado = custodiaMaterService.buscarCustodiaMaster();

        // Then
        assertThat(resultado.custodia()).hasSize(2);
        assertThat(resultado.custodia().get(0).ticker()).isEqualTo("PETR4");
        assertThat(resultado.custodia().get(0).quantidade()).isEqualTo(1000);
        assertThat(resultado.custodia().get(0).precoMedio()).isEqualTo(new BigDecimal("35.50"));
        assertThat(resultado.custodia().get(1).ticker()).isEqualTo("VALE3");
        assertThat(resultado.custodia().get(1).quantidade()).isEqualTo(500);

        verify(custodiaMasterFeignClient).buscarCustodiaMaster();
    }

    @Test
    @DisplayName("Deve chamar Feign Client apenas uma vez")
    void deveChamarFeignClientApenasUmaVez() {
        // Given
        when(custodiaMasterFeignClient.buscarCustodiaMaster()).thenReturn(custodiaMasterResponse);

        // When
        custodiaMaterService.buscarCustodiaMaster();

        // Then
        verify(custodiaMasterFeignClient, times(1)).buscarCustodiaMaster();
        verifyNoMoreInteractions(custodiaMasterFeignClient);
    }

    @Test
    @DisplayName("Deve retornar custodia master com lista vazia de ativos")
    void deveRetornarCustodiaMasterComListaVaziaDeAtivos() {
        // Given
        ContaMasterResponseDTO contaMaster = new ContaMasterResponseDTO(1L, "MASTER001", "MASTER");
        CustodiaMasterResponseDTO custodiaMasterVazia = new CustodiaMasterResponseDTO(
                contaMaster,
                List.of(),
                new BigDecimal("10000.00")
        );

        when(custodiaMasterFeignClient.buscarCustodiaMaster()).thenReturn(custodiaMasterVazia);

        // When
        CustodiaMasterResponseDTO resultado = custodiaMaterService.buscarCustodiaMaster();

        // Then
        assertThat(resultado.custodia()).isEmpty();
        assertThat(resultado.valorTotalResiduo()).isEqualTo(new BigDecimal("10000.00"));

        verify(custodiaMasterFeignClient).buscarCustodiaMaster();
    }

    @Test
    @DisplayName("Deve retornar valor total de resíduo correto")
    void deveRetornarValorTotalResiduoCorreto() {
        // Given
        when(custodiaMasterFeignClient.buscarCustodiaMaster()).thenReturn(custodiaMasterResponse);

        // When
        CustodiaMasterResponseDTO resultado = custodiaMaterService.buscarCustodiaMaster();

        // Then
        assertThat(resultado.valorTotalResiduo()).isNotNull();
        assertThat(resultado.valorTotalResiduo()).isEqualTo(new BigDecimal("5000.00"));

        verify(custodiaMasterFeignClient).buscarCustodiaMaster();
    }

    @Test
    @DisplayName("Deve incluir origem da custodia para cada ativo")
    void deveIncluirOrigemDaCustodiaParaCadaAtivo() {
        // Given
        when(custodiaMasterFeignClient.buscarCustodiaMaster()).thenReturn(custodiaMasterResponse);

        // When
        CustodiaMasterResponseDTO resultado = custodiaMaterService.buscarCustodiaMaster();

        // Then
        assertThat(resultado.custodia().get(0).origem()).isEqualTo("COMPRA");
        assertThat(resultado.custodia().get(1).origem()).isEqualTo("COMPRA");

        verify(custodiaMasterFeignClient).buscarCustodiaMaster();
    }

    @Test
    @DisplayName("Deve retornar informações completas da conta master")
    void deveRetornarInformacoesCompletasDaContaMaster() {
        // Given
        when(custodiaMasterFeignClient.buscarCustodiaMaster()).thenReturn(custodiaMasterResponse);

        // When
        CustodiaMasterResponseDTO resultado = custodiaMaterService.buscarCustodiaMaster();

        // Then
        ContaMasterResponseDTO contaMaster = resultado.contaMaster();
        assertThat(contaMaster).isNotNull();
        assertThat(contaMaster.id()).isNotNull();
        assertThat(contaMaster.numeroConta()).isNotEmpty();
        assertThat(contaMaster.tipo()).isNotEmpty();

        verify(custodiaMasterFeignClient).buscarCustodiaMaster();
    }

    @Test
    @DisplayName("Deve retornar valores corretos de preço médio e valor atual")
    void deveRetornarValoresCorretosDePrecoMedioEValorAtual() {
        // Given
        when(custodiaMasterFeignClient.buscarCustodiaMaster()).thenReturn(custodiaMasterResponse);

        // When
        CustodiaMasterResponseDTO resultado = custodiaMaterService.buscarCustodiaMaster();

        // Then
        CustodiaResponseDTO primeiraCustomia = resultado.custodia().get(0);
        assertThat(primeiraCustomia.precoMedio()).isEqualTo(new BigDecimal("35.50"));
        assertThat(primeiraCustomia.valorAtual()).isEqualTo(new BigDecimal("35500.00"));

        CustodiaResponseDTO segundaCustomia = resultado.custodia().get(1);
        assertThat(segundaCustomia.precoMedio()).isEqualTo(new BigDecimal("62.00"));
        assertThat(segundaCustomia.valorAtual()).isEqualTo(new BigDecimal("31000.00"));

        verify(custodiaMasterFeignClient).buscarCustodiaMaster();
    }
}

