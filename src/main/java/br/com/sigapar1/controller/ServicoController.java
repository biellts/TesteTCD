package br.com.sigapar1.controller;

import br.com.sigapar1.entity.Servico;
import br.com.sigapar1.service.ServicoService;
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
    private ServicoService service;

    private Servico servico = new Servico();
    private List<Servico> lista;

    @PostConstruct
    public void init() {
        lista = service.listarTodos();
    }

    public void salvar() {
        service.salvar(servico);
        JsfUtil.addInfo("Serviço salvo!");
        servico = new Servico();
        lista = service.listarTodos();
    }

    public void editar(Servico s) {
        servico = s;
    }

    public void excluir(Long id) {
        service.excluir(id);
        JsfUtil.addInfo("Serviço removido!");
        lista = service.listarTodos();
    }

    // getters/setters
}
