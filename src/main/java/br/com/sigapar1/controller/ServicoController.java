package br.com.sigapar1.controller;

import br.com.sigapar1.entity.Servico;
import br.com.sigapar1.entity.ServicoHistorico;
import br.com.sigapar1.service.ServicoService;
import br.com.sigapar1.service.ServicoHistoricoService;
import br.com.sigapar1.util.JsfUtil;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class ServicoController implements Serializable {

    @Inject
    private ServicoHistoricoService historicoService;

    @Inject
    private ServicoService service;

    private Servico servico;
    private List<Servico> lista;

    @PostConstruct
    public void init() {
        novo();
        listar();
    }

    private void novo() {
        servico = new Servico();
        servico.setAtivo(true);
    }

    private void listar() {
        lista = service.listarAtivos();   // apenas ativos
    }

    public void salvar() {
        try {
            Servico existente = service.buscarPorNome(servico.getNome());
            if (existente != null && !existente.getId().equals(servico.getId())) {
                JsfUtil.addError("Já existe um serviço com este nome.");
                return;
            }

            service.salvar(servico);
            JsfUtil.addSuccess("Serviço salvo com sucesso!");
            novo();
            listar();

        } catch (Exception e) {
            JsfUtil.addError("Erro ao salvar serviço: " + e.getMessage());
        }
    }

    public void editar(Servico s) {
        this.servico = s;
    }

    public void toggleStatus(Servico s) {
        s.setAtivo(!s.isAtivo());
        service.salvar(s);
        JsfUtil.addInfo(s.isAtivo()
                ? "Serviço ativado com sucesso."
                : "Serviço inativado com sucesso.");

        listar();
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public List<Servico> getLista() {
        return lista;
    }

    // Histórico
    private Servico servicoSelecionado;
    private List<ServicoHistorico> historico;

    public Servico getServicoSelecionado() {
        return servicoSelecionado;
    }

    public void setServicoSelecionado(Servico servicoSelecionado) {
        this.servicoSelecionado = servicoSelecionado;
    }

    public List<ServicoHistorico> getHistorico() {
        return historico;
    }

    public void selecionarServico(Servico s) {
        this.servicoSelecionado = s;
        this.historico = historicoService.listarPorServico(s.getId());
    }

    public void excluir(Servico s) {
        try {
            service.excluir(s.getId());
            JsfUtil.addSuccess("Serviço excluído (inativado) com sucesso!");
            listar();
        } catch (Exception e) {
            JsfUtil.addError("Erro ao excluir serviço: " + e.getMessage());
        }
    }
}
