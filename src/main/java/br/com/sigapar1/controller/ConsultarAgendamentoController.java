package br.com.sigapar1.controller;

import br.com.sigapar1.entity.Agendamento;
import br.com.sigapar1.service.AgendamentoService;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

import java.util.List;

@Named
@RequestScoped
public class ConsultarAgendamentoController {

    @EJB
    private AgendamentoService agendamentoService;

    private String cpfOuProtocolo;
    private List<Agendamento> resultados;
    private Agendamento selecionado;

    public void consultar() {
        resultados = agendamentoService.buscarPorCpfOuProtocolo(cpfOuProtocolo);
    }

    public boolean podeCancelar(Agendamento a) {
        return agendamentoService.podeCancelar(a);
    }

    public boolean podeReagendar(Agendamento a) {
        return agendamentoService.podeReagendar(a);
    }

    public String formatarDataHora(Agendamento a) {
        return agendamentoService.formatarDataHora(a);
    }

    // GETTERS / SETTERS
    public String getCpfOuProtocolo() { return cpfOuProtocolo; }
    public void setCpfOuProtocolo(String cpfOuProtocolo) { this.cpfOuProtocolo = cpfOuProtocolo; }

    public List<Agendamento> getResultados() { return resultados; }

    public Agendamento getSelecionado() { return selecionado; }
    public void setSelecionado(Agendamento selecionado) { this.selecionado = selecionado; }
}
