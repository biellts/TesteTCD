package br.com.sigapar1.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fila_atendimento")
public class FilaAtendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "agendamento_id")
    private Agendamento agendamento;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recepcionista_id")
    private Usuario recepcionista;

    @Column(nullable = false)
    private LocalDateTime timestampEntrada;

    private String senha;

    public FilaAtendimento() {
        this.timestampEntrada = LocalDateTime.now();
    }

    public FilaAtendimento(Agendamento agendamento, Usuario recepcionista, String senha) {
        this.agendamento = agendamento;
        this.recepcionista = recepcionista;
        this.senha = senha;
        this.timestampEntrada = LocalDateTime.now();
    }

    // ======================
    // GETTERS E SETTERS
    // ======================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Agendamento getAgendamento() {
        return agendamento;
    }

    public void setAgendamento(Agendamento agendamento) {
        this.agendamento = agendamento;
    }

    public Usuario getRecepcionista() {
        return recepcionista;
    }

    public void setRecepcionista(Usuario recepcionista) {
        this.recepcionista = recepcionista;
    }

    public LocalDateTime getTimestampEntrada() {
        return timestampEntrada;
    }

    public void setTimestampEntrada(LocalDateTime timestampEntrada) {
        this.timestampEntrada = timestampEntrada;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
