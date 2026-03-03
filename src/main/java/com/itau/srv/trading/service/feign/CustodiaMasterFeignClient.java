package com.itau.srv.trading.service.feign;

import com.itau.srv.trading.service.dto.custodiamaster.CustodiaMasterResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "${external-endpoints.itau-srv-custodia-master.name}", url = "${external-endpoints.itau-srv-custodia-master.url}")
public interface CustodiaMasterFeignClient {

    @GetMapping
    CustodiaMasterResponseDTO buscarCustodiaMaster();
}
