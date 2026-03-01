package com.itau.srv.trading.service.service;

import com.itau.common.library.exception.NegocioException;
import com.itau.srv.trading.service.dto.cesta.*;
import com.itau.srv.trading.service.dto.itemcesta.ItemCestaRequestDTO;
import com.itau.srv.trading.service.mapper.CestaMapper;
import com.itau.srv.trading.service.model.CestaRecomendacao;
import com.itau.srv.trading.service.model.ItemCesta;
import com.itau.srv.trading.service.repository.CestaRecomendacaoRepository;
import com.itau.srv.trading.service.repository.ItemCestaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CestaService {

    private final CestaMapper cestaMapper;
    private final ItemCestaService itemCestaService;
    private final CestaRecomendacaoRepository cestaRecomendacaoRepository;
    private final ItemCestaRepository itemCestaRepository;

    @Transactional
    public CestaResponseDTO criarOuAlterarCesta(CriarTopFiveRequestDTO dto) {

        if (!dto.itens().isEmpty() && dto.itens().size() != 5) {
            log.error("Quantidade de itens da cesta deve ser 5.");
            throw new NegocioException("QUANTIDADE_ATIVOS_INVALIDA");
        }

        BigDecimal percentualTotal = dto.itens().stream()
                .map(ItemCestaRequestDTO::percentual)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (percentualTotal.compareTo(BigDecimal.valueOf(100)) != 0) {
            log.error("O percentual total da cesta deve ser 100.");
            throw new NegocioException("PERCENTUAIS_INVALIDOS");
        }

        log.info("Verificando se existe uma cesta ativa.");

        Optional<CestaRecomendacao> cestaAtiva = cestaRecomendacaoRepository.findByAtivaTrue();

        if (cestaAtiva.isEmpty()) {
            return criarPrimeiraCesta(dto);
        }

        return alterarCestaComRebalanceamento(dto, cestaAtiva.get());
    }

    @Transactional(readOnly = true)
    public CestaRecomendacaoAtivaResponseDTO obterCestaAtiva() {
        CestaRecomendacao cestaRecomendacao = cestaRecomendacaoRepository.findByAtivaTrue()
                .orElseThrow(() -> {
                    log.error("Nenhuma cesta ativa encontrada.");
                    return new NegocioException("CESTA_NAO_ENCONTRADA");
                });

        log.info("Cesta ativa encontrada. Procurando itens da cesta.");

        List<ItemCesta> itens = itemCestaRepository.findAllByCestaRecomendacao(cestaRecomendacao);

        log.info("Itens da cesta encontrados com cotações: {}.", itens);

        return cestaMapper.mapearParaCestaRecomendacaoAtivaResponse(cestaRecomendacao, itens);
    }

    @Transactional(readOnly = true)
    public HistoricoCestaResponseDTO obterHistoricoCestas() {
        HistoricoCestaResponseDTO historico = new HistoricoCestaResponseDTO(new ArrayList<>());

        List<CestaRecomendacao> cestas = cestaRecomendacaoRepository.findAll();

        log.info("Cestas encontradas: {}.", cestas.size());

        for (CestaRecomendacao cesta : cestas) {
            log.info("Procurando itens da cesta: {}.", cesta.getId());

            List<ItemCesta> itens = itemCestaRepository.findAllByCestaRecomendacao(cesta);

            log.info("Itens da cesta encontrados.");

            historico.cestas().add(cestaMapper.mapearParaCestaHistoricoResponse(cesta, itens));
        }

        return historico;
    }

    private AlterarTopFiveResponseDTO alterarCestaComRebalanceamento(
            CriarTopFiveRequestDTO dto, CestaRecomendacao cestaAtual) {
        log.info("Cesta ativa encontrada. Rebalanceamento sendo disparado.");

        // Lógica de rebalanceamento
        // ...

        return null;
    }

    private CriarTopFiveResponseDTO criarPrimeiraCesta(CriarTopFiveRequestDTO dto) {
        log.info("Nenhuma cesta ativa. Primeira cesta top five sendo criada!.");

        CestaRecomendacao cestaRecomendacao = cestaMapper.mapearParaCestaRecomendacao(dto);

        CestaRecomendacao cestaCriada = cestaRecomendacaoRepository.save(cestaRecomendacao);

        log.info("Cesta criada com sucesso.");

        List<ItemCesta> itensCesta = itemCestaService.criarItensCesta(dto.itens(), cestaCriada);

        log.info("Itens da cesta criados com sucesso.");

        return cestaMapper.mapearParaTopFiveResponse(cestaCriada, itensCesta);
    }
}
