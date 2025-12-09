package br.com.sigapar1.controller;

import br.com.sigapar1.entity.Guiche;
import br.com.sigapar1.service.GuicheService;
import br.com.sigapar1.util.JsfUtil;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class GuicheController implements Serializable {

    @Inject
    private GuicheService service;

    private Guiche guiche = new Guiche();
    private List<Guiche> lista;

    @PostConstruct
    public void init() {
        lista = service.listarTodos();
    }

    public void salvar() {
        service.salvar(guiche);
        JsfUtil.addInfo("Guichê salvo!");
        guiche = new Guiche();
        lista = service.listarTodos();
    }

    public void editar(Guiche g) {
        this.guiche = g;
    }

    public void excluir(Long id) {
        service.excluir(id);
        JsfUtil.addInfo("Guichê inativado!");
        lista = service.listarTodos();
    }

    public Guiche getGuiche() { return guiche; }
    public void setGuiche(Guiche g) { this.guiche = g; }
    public List<Guiche> getLista() { return lista; }
}
