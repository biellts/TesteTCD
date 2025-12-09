package br.com.sigapar1.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "disponibilidade_atendente")
public class DisponibilidadeAtendente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Seu controle de status (DISPONÍVEL / INDISPONÍVEL / PAUSA)
    @Enumerated(EnumType.STRING)
    private StatusAtendente status;

    private LocalDateTime alteradoEm;

    private String motivo;

    @ManyToOne
    private Usuario atendente;

    // ========================
    // NOVOS CAMPOS (TCD exige)
    // ========================

    private LocalDate data;          // EX: 2025-12-01
    private LocalTime inicio;        // EX: 09:00
    private LocalTime fim;           // EX: 17:00
    private Integer duracaoMinutos;  // EX: 30

    public DisponibilidadeAtendente() {}


    // ========================
    // GETTERS E SETTERS
    // ========================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StatusAtendente getStatus() { return status; }
    public void setStatus(StatusAtendente status) { this.status = status; }

    public LocalDateTime getAlteradoEm() { return alteradoEm; }
    public void setAlteradoEm(LocalDateTime alteradoEm) { this.alteradoEm = alteradoEm; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Usuario getAtendente() { return atendente; }
    public void setAtendente(Usuario atendente) { this.atendente = atendente; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public LocalTime getInicio() { return inicio; }
    public void setInicio(LocalTime inicio) { this.inicio = inicio; }

    public LocalTime getFim() { return fim; }
    public void setFim(LocalTime fim) { this.fim = fim; }

    public Integer getDuracaoMinutos() { return duracaoMinutos; }
    public void setDuracaoMinutos(Integer duracaoMinutos) { this.duracaoMinutos = duracaoMinutos; }
}
