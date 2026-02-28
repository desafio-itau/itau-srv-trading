package com.itau.srv.trading.service.util;

import com.itau.srv.trading.service.dto.cotacaob3.CotacaoB3;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CotahistParser {

    public List<CotacaoB3> parseArquivo(String caminhoArquivo) {

        var cotacoes = new ArrayList<CotacaoB3>();

        try (var reader = Files.newBufferedReader(Paths.get(caminhoArquivo))) {
            String linha;

            while ((linha = reader.readLine()) != null) {

                if (linha.length() < 245) continue;

                var tipoRegistro = linha.substring(0, 2);

                if (!tipoRegistro.equals("01")) continue;

                var tipoMercado = Integer.parseInt(linha.substring(24, 27).trim());

                if (tipoMercado != 10 && tipoMercado != 20) continue;

                var cotacao = new CotacaoB3(
                        LocalDate.parse(linha.substring(2, 10), DateTimeFormatter.ofPattern("yyyyMMdd")),
                        linha.substring(12, 24).trim(),
                        linha.substring(10, 12).trim(),
                        tipoMercado,
                        linha.substring(27, 39).trim(),
                        parsePreco(linha.substring(56, 69)),
                        parsePreco(linha.substring(69, 82)),
                        parsePreco(linha.substring(82, 95)),
                        parsePreco(linha.substring(95, 108)),
                        parsePreco(linha.substring(108, 121)),
                        Long.parseLong(linha.substring(152, 170).trim()),
                        parsePreco(linha.substring(170, 188))
                );

                cotacoes.add(cotacao);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler arquivo de cotações: ", e);
        }

        return cotacoes;
    }

    public Optional<CotacaoB3> obterCotacaoFechamento(String pastaCotacoes, String ticker) {
        return parseArquivo(pastaCotacoes).stream()
                .filter(cotacao -> cotacao.ticker().equals(ticker) && cotacao.tipoMercado() == 10)
                .findFirst();
    }

    private BigDecimal parsePreco(String valorBruto) {
        var valorTrimmed = valorBruto.trim();

        try {
            long valor = Long.parseLong(valorTrimmed);
            if (valor == 0) return BigDecimal.ZERO;
            return BigDecimal.valueOf(valor).movePointLeft(2);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
