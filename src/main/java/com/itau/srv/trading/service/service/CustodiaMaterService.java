package com.itau.srv.trading.service.service;

import com.itau.srv.trading.service.dto.custodiamaster.CustodiaMasterResponseDTO;
import com.itau.srv.trading.service.feign.CustodiaMasterFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustodiaMaterService {

    private final CustodiaMasterFeignClient custodiaMasterFeignClient;

    public CustodiaMasterResponseDTO buscarCustodiaMaster() {
        log.info("Buscando custodia master.");
        return custodiaMasterFeignClient.buscarCustodiaMaster();
    }
}
