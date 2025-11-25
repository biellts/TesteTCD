package br.com.sigapar1.entity;

import jakarta.persistence.*;

@Entity
public class ConfiguracaoSistema {

    @Id
    private Long id = 1L; // sempre único

    private Integer tempoSessaoMinutos = 30;

    private String regraIntercalacao = "1:1";

    private Boolean emailAtivo = false;

    private Integer capacidadePorSlot = 1;

    // Getters e Setters
}
