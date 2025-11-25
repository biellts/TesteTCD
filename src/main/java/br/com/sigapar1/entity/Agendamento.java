package br.com.sigapar1.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "agendamento")
public class Agendamento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // protocolo único simples
    @Column(unique = true)
    private String protocolo;

    // data usada no agendamento
    private LocalDate data;

    // seu controller usa isso, então precisamos ter
    private LocalDateTime dataHora;

    // prioridade usada nos controllers/services (MESMO SEM LÓGICA)
    private String prioridade;

    @ManyToOne
    private Horario horario;

    @ManyToOne
    private Servico servico;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private Usuario atendente;

    private boolean checkin;
    private boolean chamado;
    private boolean emAtendimento;
    private boolean finalizado;

    public Agendamento() {}

    // ===== Getters & Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProtocolo() { return protocolo; }
    public void setProtocolo(String protocolo) { this.protocolo = protocolo; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }

    public Horario getHorario() { return horario; }
    public void setHorario(Horario horario) { this.horario = horario; }

    public Servico getServico() { return servico; }
    public void setServico(Servico servico) { this.servico = servico; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Usuario getAtendente() { return atendente; }
    public void setAtendente(Usuario atendente) { this.atendente = atendente; }

    public boolean isCheckin() { return checkin; }
    public void setCheckin(boolean checkin) { this.checkin = checkin; }

    public boolean isChamado() { return chamado; }
    public void setChamado(boolean chamado) { this.chamado = chamado; }

    public boolean isEmAtendimento() { return emAtendimento; }
    public void setEmAtendimento(boolean emAtendimento) { this.emAtendimento = emAtendimento; }

    public boolean isFinalizado() { return finalizado; }
    public void setFinalizado(boolean finalizado) { this.finalizado = finalizado; }
}
