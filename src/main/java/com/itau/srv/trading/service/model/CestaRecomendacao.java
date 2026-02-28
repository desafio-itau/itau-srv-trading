package com.itau.srv.trading.service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "cestas_recomendacao")
@Setter
@Getter
public class CestaRecomendacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String nome;

    @Column(nullable = false)
    private Boolean ativa = true;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    private LocalDateTime dataDesativacao;

    @PrePersist
    private void prePersist() {
        this.dataCriacao = LocalDateTime.now();
    }
}
