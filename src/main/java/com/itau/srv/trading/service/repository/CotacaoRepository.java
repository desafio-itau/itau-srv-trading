package com.itau.srv.trading.service.repository;

import com.itau.srv.trading.service.model.Cotacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CotacaoRepository extends JpaRepository<Cotacao, Long> {
}
