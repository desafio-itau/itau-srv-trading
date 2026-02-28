package com.itau.srv.trading.service.repository;

import com.itau.srv.trading.service.model.CestaRecomendacao;
import com.itau.srv.trading.service.model.ItemCesta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemCestaRepository extends JpaRepository<ItemCesta, Long> {
    List<ItemCesta> findAllByCestaRecomendacao(CestaRecomendacao cestaRecomendacao);
}
