package br.com.sigapar1.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "agendamento")
public class Agendamento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String protocolo;

    @Column(nullable = false)
    private LocalDate data;

    @Column(name = "datahora", nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusAgendamento status = StatusAgendamento.AGENDADO;

    @Column(length = 20)
    private String prioridade;

    @Column(name = "horacheckin")
    private LocalTime horaCheckin;

    @Column(name = "horachamado")
    private LocalTime horaChamado;

    @Column(name = "observacoes", length = 500)
    private String observacoes;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    // ===== CAMPOS QUE FALTAVAM NO ENTITY =====

    @Column(nullable = false)
    private Boolean chamado = false;

    @Column(nullable = false)
    private Boolean checkin = false;

    @Column(name = "ematendimento", nullable = false)
    private Boolean emAtendimento = false;

    @Column(nullable = false)
    private Boolean finalizado = false;

    // ===== RELACIONAMENTOS =====

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horario_id", nullable = false)
    private Horario horario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atendente_id")
    private Usuario atendente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "espaco_id")
    private EspacoAtendimento espaco;

    @PrePersist
    // ===== CALLBACKS =====

    public void prePersist() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();

        if (this.status == null) {
            this.status = StatusAgendamento.AGENDADO;
        }

        if (this.chamado == null) this.chamado = false;
        if (this.checkin == null) this.checkin = false;
        if (this.emAtendimento == null) this.emAtendimento = false;
        if (this.finalizado == null) this.finalizado = false;
    }

    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }

    // ===== CONSTRUTOR =====
    public Agendamento() {}

    // ===== GETTERS & SETTERS =====

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getProtocolo() {
        return protocolo;
    }
    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public LocalDate getData() {
        return data;
    }
    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public StatusAgendamento getStatus() {
        return status;
    }
    public void setStatus(StatusAgendamento status) {
        this.status = status;
    }

    public String getPrioridade() {
        return prioridade;
    }
    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    public LocalTime getHoraCheckin() {
        return horaCheckin;
    }
    public void setHoraCheckin(LocalTime horaCheckin) {
        this.horaCheckin = horaCheckin;
    }

    public LocalTime getHoraChamado() {
        return horaChamado;
    }
    public void setHoraChamado(LocalTime horaChamado) {
        this.horaChamado = horaChamado;
    }

    public String getObservacoes() {
        return observacoes;
    }
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public boolean isAtivo() {
        return ativo;
    }
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public Boolean getChamado() {
        return chamado;
    }
    public void setChamado(Boolean chamado) {
        this.chamado = chamado;
    }

    public Boolean getCheckin() {
        return checkin;
    }
    public void setCheckin(Boolean checkin) {
        this.checkin = checkin;
    }

    public Boolean getEmAtendimento() {
        return emAtendimento;
    }
    public void setEmAtendimento(Boolean emAtendimento) {
        this.emAtendimento = emAtendimento;
    }

    public Boolean getFinalizado() {
        return finalizado;
    }
    public void setFinalizado(Boolean finalizado) {
        this.finalizado = finalizado;
    }

    public Horario getHorario() {
        return horario;
    }
    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    public Servico getServico() {
        return servico;
    }
    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getAtendente() {
        return atendente;
    }
    public void setAtendente(Usuario atendente) {
        this.atendente = atendente;
    }

    public EspacoAtendimento getEspaco() {
        return espaco;
    }
    public void setEspaco(EspacoAtendimento espaco) {
        this.espaco = espaco;
    }

    @Override
    public String toString() {
        return "Agendamento{" +
                "id=" + id +
                ", protocolo='" + protocolo + '\'' +
                ", data=" + data +
                ", status=" + status +
                ", ativo=" + ativo +
                '}';
    }
}
