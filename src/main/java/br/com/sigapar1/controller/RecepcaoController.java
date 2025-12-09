package br.com.sigapar1.controller;

import br.com.sigapar1.dao.AgendamentoDAO;
import br.com.sigapar1.entity.Agendamento;
import br.com.sigapar1.entity.Checkin;
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
public class RecepcaoController implements Serializable {

    @Inject
    private UsuarioController usuarioController;

    @Inject
    private AgendamentoDAO agendamentoDAO;

    @Inject
    private CheckinService checkinService;

    private String termoBusca;
    private List<Agendamento> lista;
    private Agendamento selecionado;

    @PostConstruct
    public void init() {
        lista = agendamentoDAO.buscarPorData(LocalDate.now());
    }

    public void buscar() {
        if (termoBusca == null || termoBusca.trim().isEmpty()) {
            lista = agendamentoDAO.buscarPorData(LocalDate.now());
        } else {
            lista = agendamentoDAO.buscarPorTermo(termoBusca, LocalDate.now());
        }

        if (lista.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Nenhum agendamento encontrado."));
        }
    }

    public void confirmarCheckin() {
        try {
            if (selecionado == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Selecione um agendamento.", null));
                return;
            }

            Usuario recep = usuarioController.getUsuarioLogado();

            checkinService.realizarCheckinRecepcao(selecionado, recep);

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Check-in realizado com sucesso!"));

            init(); // recarregar lista

        } catch (BusinessException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), null));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Erro inesperado ao realizar check-in.", null));
        }
    }

    // Getters / Setters

    public String getTermoBusca() { return termoBusca; }
    public void setTermoBusca(String termoBusca) { this.termoBusca = termoBusca; }

    public List<Agendamento> getLista() { return lista; }

    public Agendamento getSelecionado() { return selecionado; }
    public void setSelecionado(Agendamento selecionado) { this.selecionado = selecionado; }
}
