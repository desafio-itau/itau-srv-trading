package com.itau.srv.trading.service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cotacoes")
@Setter
@Getter
public class Cotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dataPregao;

    private String ticker;

    private Integer tipoMercado;

    private BigDecimal precoAbertura;

    private BigDecimal precoMaximo;

    private BigDecimal precoMinimo;

    private BigDecimal precoFechamento;
}
