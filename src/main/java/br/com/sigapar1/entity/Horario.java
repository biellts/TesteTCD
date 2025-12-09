package br.com.sigapar1.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "horario")
public class Horario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Exemplo: MONDAY, TUESDAY, SATURDAY...
    @Column(name = "dia_semana", nullable = false)
    private String diaSemana;

    @Column(nullable = false)
    private LocalTime hora;

    @Column(name = "capacidade_max")
    private Integer capacidadeMax;

    @Column(name = "capacidade_atual")
    private Integer capacidadeAtual = 0;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(nullable = false)
    private boolean disponivel = true;
    @ManyToOne
    @JoinColumn(name = "id_servico", nullable = false)
    private Servico servico;

    public Horario() {
    }

    public Horario(Long id) {
        this.id = id;
    }

    // ============ GETTERS / SETTERS ============
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public Integer getCapacidadeMax() {
        return capacidadeMax;
    }

    public void setCapacidadeMax(Integer capacidadeMax) {
        this.capacidadeMax = capacidadeMax;
    }

    public Integer getCapacidadeAtual() {
        return capacidadeAtual == null ? 0 : capacidadeAtual;
    }

    public void setCapacidadeAtual(Integer capacidadeAtual) {
        this.capacidadeAtual = capacidadeAtual == null ? 0 : capacidadeAtual;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    // ============ MÉTODOS ÚTEIS ============
    @Transient
    public String getHoraFormatada() {
        if (hora == null) {
            return "";
        }
        return hora.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    @Transient
    public boolean isLotado() {
        if (capacidadeMax == null) {
            return false;
        }
        return getCapacidadeAtual() >= capacidadeMax;
    }

    @Override
    public String toString() {
        return getHoraFormatada();
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

}
