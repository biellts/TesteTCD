package br.com.sigapar1.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "atendente_servico_espaco") 
public class AtendenteServicoEspaco implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "atendente_id", nullable = false)
    private Usuario atendente;

    @ManyToOne
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico;

    @ManyToOne
    @JoinColumn(name = "espaco_id", nullable = false)
    private EspacoAtendimento espaco;

    @ManyToOne
    @JoinColumn(name = "horario_id", nullable = false)
    private Horario horario;

    public AtendenteServicoEspaco() {}

    // ================= GETTERS / SETTERS =================

    public Long getId() {
        return id;
    }

    // NECESSÁRIO para edição e merge()
    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getAtendente() {
        return atendente;
    }

    public void setAtendente(Usuario atendente) {
        this.atendente = atendente;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public EspacoAtendimento getEspaco() {
        return espaco;
    }

    public void setEspaco(EspacoAtendimento espaco) {
        this.espaco = espaco;
    }

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }
}
