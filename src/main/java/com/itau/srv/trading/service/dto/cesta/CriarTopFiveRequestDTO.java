package com.itau.srv.trading.service.dto.cesta;

import com.itau.srv.trading.service.dto.itemcesta.ItemCestaRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CriarTopFiveRequestDTO(
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        @NotBlank(message = "Nome não pode ser branco")
        String nome,
        @Valid
        List<ItemCestaRequestDTO> itens
) {
}
