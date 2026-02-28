package com.itau.srv.trading.service.repository;

import com.itau.srv.trading.service.model.CestaRecomendacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CestaRecomendacaoRepository extends JpaRepository<CestaRecomendacao, Long> {
    @Query("SELECT c FROM CestaRecomendacao c WHERE c.ativa = true")
    Optional<CestaRecomendacao> findByAtivaTrue();
}
