package br.com.sigapar1.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class FilaAtendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Agendamento agendamento;

    private String prioridade; // NORMAL / PRIORITARIO

    private LocalDateTime timestampEntrada;

    private String senha;

    public FilaAtendimento() {}

    public FilaAtendimento(Agendamento a, String prioridade, String senha) {
        this.agendamento = a;
        this.prioridade = prioridade;
        this.senha = senha;
        this.timestampEntrada = LocalDateTime.now();
    }

    // Getters e Setters
}
