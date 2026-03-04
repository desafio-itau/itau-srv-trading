package com.itau.srv.trading.service.dto.cesta;

import com.itau.srv.trading.service.dto.itemcesta.ItemCestaRequestDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Request para criação ou alteração de uma cesta Top Five")
public record CriarTopFiveRequestDTO(
        @Schema(
            description = "Nome da cesta",
            example = "Top Five - Fevereiro 2026",
            required = true,
            minLength = 3,
            maxLength = 100
        )
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        @NotBlank(message = "Nome não pode ser branco")
        String nome,

        @Schema(
            description = "Lista de 5 itens que compõem a cesta. Os percentuais devem somar 100%",
            required = true
        )
        @Valid
        List<ItemCestaRequestDTO> itens
) {
}
