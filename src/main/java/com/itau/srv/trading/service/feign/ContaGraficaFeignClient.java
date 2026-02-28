package com.itau.srv.trading.service.feign;

import com.itau.srv.trading.service.dto.contagrafica.ContaGraficaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "${external-endpoints.itau-srv-conta-grafica.name}", url = "${external-endpoints.itau-srv-conta-grafica.url}")
public interface ContaGraficaFeignClient {

    @GetMapping("/{id}")
    ContaGraficaDTO buscarContaGrafica(@PathVariable Long id);
}
