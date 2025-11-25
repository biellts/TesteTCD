package br.com.sigapar1.controller;

import br.com.sigapar1.entity.Agendamento;
import br.com.sigapar1.entity.Usuario;
import br.com.sigapar1.service.AgendamentoService;
import br.com.sigapar1.service.AtendimentoService;
import br.com.sigapar1.util.JsfUtil;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class AtendimentoController implements Serializable {

    @Inject
    private AtendimentoService atendimentoService;

    @Inject
    private AgendamentoService agendamentoService;

    @Inject
    private LoginController login;

    private Agendamento atual;

    public void chamar() {
        atual = agendamentoService.buscarProximoDaFila();

        if (atual == null) {
            JsfUtil.addWarn("Nenhum cliente na fila.");
            return;
        }

        atendimentoService.iniciarAtendimento(atual, login.getUsuarioLogado());
    }

    public void finalizar() {
        atendimentoService.finalizarAtendimento(atual.getId(), "Atendimento finalizado.");
        JsfUtil.addInfo("Atendimento finalizado!");
        atual = null;
    }

    // getters/setters
}
