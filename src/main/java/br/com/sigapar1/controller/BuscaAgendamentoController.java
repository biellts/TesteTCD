package br.com.sigapar1.controller;

import br.com.sigapar1.entity.*;
import br.com.sigapar1.service.AgendamentoService;
import br.com.sigapar1.util.BusinessException;
import br.com.sigapar1.util.JsfUtil;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;

@Named
@ViewScoped
public class BuscaAgendamentoController implements Serializable {

    @Inject
    private AgendamentoService service;
    @Inject
    private LoginController login;

    private String busca;           // CPF ou Protocolo
    private Agendamento agendamento;

    public void buscar() {
        if (busca == null || busca.trim().isEmpty()) {
            JsfUtil.addError("Informe o CPF ou protocolo.");
            agendamento = null;
            return;
        }

        String termo = busca.trim().replaceAll("\\D", ""); // remove pontos, traços, etc.

        // 1. Primeiro tenta por protocolo (ex: ABC123)
        if (busca.matches(".*[A-Za-z].*")) {
            agendamento = service.buscarPorProtocolo(busca.toUpperCase());
        }

        // 2. Se não encontrou ou é só número → tenta por CPF
        if (agendamento == null && termo.length() >= 3) {
            agendamento = service.buscarPorCpf(termo);
        }

        if (agendamento == null) {
            JsfUtil.addError("Agendamento não encontrado com os dados informados.");
        } else {
            JsfUtil.addSuccess("Agendamento encontrado: " + agendamento.getProtocolo());
        }
    }

    public void cancelar() {
        try {
            service.cancelar(agendamento.getId(), login.getUsuarioLogado());
            JsfUtil.addSuccess("Agendamento cancelado com sucesso.");
            agendamento = null;
        } catch (BusinessException e) {
            JsfUtil.addError(e.getMessage());
        }
    }

    public void checkIn() {
        try {
            service.fazerCheckin(agendamento, login.getUsuarioLogado());
            agendamento.setStatus(StatusAgendamento.EM_FILA); // ← CORRETO!
            JsfUtil.addSuccess("Check-in realizado! Cliente está na fila.");
        } catch (BusinessException e) {
            JsfUtil.addError(e.getMessage());
        }
    }

    // Verifica perfil do usuário logado
    public boolean isRecepcao() {
        Usuario u = login.getUsuarioLogado();
        return u != null && u.getRole() == Role.ROLE_RECEPTIONIST;
    }

    public boolean isAtendente() {
        Usuario u = login.getUsuarioLogado();
        return u != null && u.getRole() == Role.ROLE_ATTENDANT;
    }

    public boolean podeCancelar() {
        if (agendamento == null) {
            return false;
        }
        if (agendamento.getStatus() != StatusAgendamento.AGENDADO) {
            return false;
        }
        return agendamento.getDataHora().isAfter(java.time.LocalDateTime.now().plusHours(2));
    }

    // Getters e Setters
    public String getBusca() {
        return busca;
    }

    public void setBusca(String busca) {
        this.busca = busca;
    }

    public Agendamento getAgendamento() {
        return agendamento;
    }

    public String getDataHoraFormatada() {
        if (agendamento == null || agendamento.getDataHora() == null) {
            return "";
        }
        return agendamento.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getStatusCor() {
        if (agendamento == null || agendamento.getStatus() == null) {
            return "#ffffff";
        }

        switch (agendamento.getStatus()) {
            case AGENDADO:
                return "#d4edda";        // verde claro
            case REMARCADO:
                return "#fff3cd";        // amarelo claro
            case EM_FILA:
                return "#cce5ff";        // azul bem claro
            case EM_ATENDIMENTO:
                return "#d1ecf1";        // ciano claro
            case CONCLUIDO:
                return "#c3e6cb";        // verde sucesso
            case CANCELADO:
                return "#f5c6cb";        // vermelho claro
            default:
                return "#ffffff";        // branco (nunca deve cair aqui)
        }
    }
}
