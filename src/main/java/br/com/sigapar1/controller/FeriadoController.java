package br.com.sigapar1.controller;

import br.com.sigapar1.entity.Feriado;
import br.com.sigapar1.service.FeriadoService;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Named
@SessionScoped
public class FeriadoController implements Serializable {

    @Inject
    private FeriadoService feriadoService;

    private List<Feriado> feriados;
    private LocalDate novaData;

    public void listar() {
        feriados = feriadoService.listarTodos();
    }

    public void adicionar() {
        if (novaData != null) {
            feriadoService.adicionar(novaData);
            novaData = null;
            listar();
        }
    }

    public void excluir(Long id) {
        feriadoService.remover(id);
        listar();
    }

    public List<Feriado> getFeriados() {
        return feriados;
    }

    public LocalDate getNovaData() {
        return novaData;
    }

    public void setNovaData(LocalDate novaData) {
        this.novaData = novaData;
    }
}
