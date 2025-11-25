package br.com.sigapar1.controller;

import br.com.sigapar1.entity.Agendamento;
import br.com.sigapar1.service.RecepcaoService;
import br.com.sigapar1.util.JsfUtil;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@ViewScoped
public class RecepcaoController implements Serializable {

    @Inject
    private RecepcaoService service;

    private String cpf;
    private Agendamento agendamento;

    public void buscar() {
        agendamento = service.buscarPorCpf(cpf);

        if (agendamento == null) {
            JsfUtil.addError("Nenhum agendamento encontrado.");
        }
    }

    public void checkIn() {
        service.checkIn(agendamento.getId());
        JsfUtil.addInfo("Check-in realizado!");
    }

    // getters/setters
}
