package br.com.sigapar1.controller;

import br.com.sigapar1.entity.Agendamento;
import br.com.sigapar1.entity.Horario;
import br.com.sigapar1.entity.Servico;
import br.com.sigapar1.entity.Usuario;
import br.com.sigapar1.service.AgendamentoService;
import br.com.sigapar1.util.JsfUtil;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Named
@SessionScoped
public class AgendamentoController implements Serializable {

    @Inject
    private AgendamentoService service;

    @Inject
    private LoginController login;

    private Long idServico;
    private LocalDate data;
    private Long idHorario;

    private List<Horario> horarios;

    public void carregarHorarios() {
        this.horarios = service.horariosDisponiveis(idServico, data);
    }

    public String confirmar() {

        Agendamento a = new Agendamento();

        a.setData(data);
        a.setHorario(new Horario(idHorario));
        a.setServico(new Servico(idServico));
        a.setUsuario((Usuario) login.getUsuarioLogado());

        if (service.existeConflito(a)) {
            JsfUtil.addError("Horário já ocupado!");
            return null;
        }

        service.salvar(a);

        return "/agendamento/meus.xhtml?faces-redirect=true";
    }

    public Long getIdServico() { return idServico; }
    public void setIdServico(Long idServico) { this.idServico = idServico; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public Long getIdHorario() { return idHorario; }
    public void setIdHorario(Long idHorario) { this.idHorario = idHorario; }

    public List<Horario> getHorarios() { return horarios; }
}
