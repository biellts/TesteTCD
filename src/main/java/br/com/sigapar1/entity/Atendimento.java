// Atendimento.java
package br.com.sigapar1.entity;

import jakarta.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "atendimento")
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime inicio;

    private LocalDateTime fim;

    @Enumerated(EnumType.STRING)
    private StatusAtendimento status;

    // descrição das ações realizadas (mínimo 20 caracteres - validar no service/UI)
    @Column(columnDefinition = "text")
    private String acao;

    @Column(columnDefinition = "text")
    private String observacao;

    @ManyToOne
    private Agendamento agendamento;

    @ManyToOne
    private Usuario atendente;

    @ManyToOne
    private Guiche guiche;

    // duração em segundos (calculada automaticamente ao finalizar)
    private Long duracaoSegundos;

    // optional: serviço de destino caso reencaminhado
    @ManyToOne
    private Servico servicoDestino;

    public Atendimento() {}

    // getters / setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getInicio() { return inicio; }
    public void setInicio(LocalDateTime inicio) { this.inicio = inicio; }

    public LocalDateTime getFim() { return fim; }
    public void setFim(LocalDateTime fim) { this.fim = fim; }

    public StatusAtendimento getStatus() { return status; }
    public void setStatus(StatusAtendimento status) { this.status = status; }

    public String getAcao() { return acao; }
    public void setAcao(String acao) { this.acao = acao; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public Agendamento getAgendamento() { return agendamento; }
    public void setAgendamento(Agendamento agendamento) { this.agendamento = agendamento; }

    public Usuario getAtendente() { return atendente; }
    public void setAtendente(Usuario atendente) { this.atendente = atendente; }

    public Guiche getGuiche() { return guiche; }
    public void setGuiche(Guiche guiche) { this.guiche = guiche; }

    public Long getDuracaoSegundos() { return duracaoSegundos; }
    public void setDuracaoSegundos(Long duracaoSegundos) { this.duracaoSegundos = duracaoSegundos; }

    public Servico getServicoDestino() { return servicoDestino; }
    public void setServicoDestino(Servico servicoDestino) { this.servicoDestino = servicoDestino; }

    // utilitário para calcular duração se início/fim definidos
    public void calcularDuracao() {
        if (inicio != null && fim != null) {
            Duration d = Duration.between(inicio, fim);
            this.duracaoSegundos = d.getSeconds();
        }
    }
}
