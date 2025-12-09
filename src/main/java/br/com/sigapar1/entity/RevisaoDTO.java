package br.com.sigapar1.entity;

import java.io.Serializable;
import java.time.LocalDate;

public class RevisaoDTO implements Serializable {

    private Servico servico;
    private EspacoAtendimento espaco;

    // Agora está correto ✔
    private LocalDate data;

    private Horario horario;

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

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }
}
