package com.itau.srv.trading.service.feign;

import com.itau.srv.trading.service.dto.rebalanceamento.RebalancementoEventDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${external-endpoints.itau-srv-rebalanceamento.name}", url = "${external-endpoints.itau-srv-rebalanceamento.url}")
public interface RebalanceamentoFeignClient {

    @PostMapping("/eventos")
    void publicarEventosRebalanceamento(@RequestBody RebalancementoEventDTO evento);
}
