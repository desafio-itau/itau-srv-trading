package com.itau.srv.trading.service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_cesta")
@Setter
@Getter
public class ItemCesta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cesta_id")
    private CestaRecomendacao cestaRecomendacao;

    @Column(length = 10, nullable = false)
    private String ticker;

    @Column(scale = 2, precision = 5, nullable = false)
    private BigDecimal percentual;
}
