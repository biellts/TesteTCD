package br.com.sigapar1.controller;

import br.com.sigapar1.dao.AgendamentoDAO;
import br.com.sigapar1.entity.Agendamento;
import br.com.sigapar1.entity.Usuario;
import br.com.sigapar1.service.CheckinService;
import br.com.sigapar1.util.BusinessException;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Named
@ViewScoped
public class CheckinController implements Serializable {

    @Inject
    private AgendamentoDAO agendamentoDAO;

    @Inject
    private CheckinService checkinService;

    @Inject
    private UsuarioController usuarioController;

    private String termoBusca;
    private List<Agendamento> lista;
    private Agendamento selecionado;

    @PostConstruct
    public void init() {
        carregarAgendamentosDoDia();
    }

    // ==========================================================
    // BUSCAR (por nome, CPF, protocolo)
    // ==========================================================
    public void buscar() {
        try {
            if (termoBusca == null || termoBusca.trim().isEmpty()) {
                carregarAgendamentosDoDia();
                return;
            }

            lista = agendamentoDAO.buscarPorTermo(termoBusca.trim(), LocalDate.now());

            if (lista == null || lista.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN,
                                "Nenhum agendamento encontrado.", null));
            }

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Erro ao buscar agendamentos.", null));
        }
    }

    // ==========================================================
    // LISTA DO DIA
    // ==========================================================
    private void carregarAgendamentosDoDia() {
        lista = agendamentoDAO.buscarPorData(LocalDate.now());
    }

    // ==========================================================
    // SELECIONAR
    // ==========================================================
    public void selecionar(Agendamento ag) {
        this.selecionado = ag;
    }

    // ==========================================================
    // CONFIRMAR CHECK-IN
    // ==========================================================
    public void confirmarCheckin() {
        try {
            if (selecionado == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN,
                                "Selecione um agendamento.", null));
                return;
            }

            Usuario recepcionista = usuarioController.getUsuarioLogado();

            checkinService.realizarCheckinRecepcao(selecionado, recepcionista);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Check-in realizado com sucesso!", null));

            carregarAgendamentosDoDia();
            selecionado = null;

        } catch (BusinessException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            e.getMessage(), null));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Erro ao realizar check-in.", null));
        }
    }

    // ==========================================================
    // GETTERS / SETTERS
    // ==========================================================
    public String getTermoBusca() {
        return termoBusca;
    }

    public void setTermoBusca(String termoBusca) {
        this.termoBusca = termoBusca;
    }

    public List<Agendamento> getLista() {
        return lista;
    }

    public Agendamento getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Agendamento selecionado) {
        this.selecionado = selecionado;
    }
}
