package br.com.sigapar1.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "checkin")
public class Checkin implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- RELACIONAMENTO COM AGENDAMENTO ---
    @OneToOne
    @JoinColumn(name = "agendamento_id", nullable = false)
    private Agendamento agendamento;

    // --- USUÁRIO QUE REALIZOU O CHECK-IN (OPCIONAL) ---
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // --- DATA/HORA DO CHECK-IN ---
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_hora_checkin", nullable = false)
    private Date dataHoraCheckin;

    // --- STATUS DO CHECK-IN (opcional: pode ser "REALIZADO", "CANCELADO"...) ---
    @Column(name = "status", length = 30)
    private String status;

    // --- OBSERVAÇÃO OPCIONAL ---
    @Column(name = "observacao", length = 255)
    private String observacao;

    // =========================================
    // GETTERS E SETTERS
    // =========================================

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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Date getDataHoraCheckin() {
        return dataHoraCheckin;
    }

    public void setDataHoraCheckin(Date dataHoraCheckin) {
        this.dataHoraCheckin = dataHoraCheckin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
