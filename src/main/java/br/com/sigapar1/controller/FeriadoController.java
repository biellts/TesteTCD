package br.com.sigapar1.controller;

import br.com.sigapar1.entity.Feriado;
import br.com.sigapar1.service.FeriadoService;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class FeriadoController implements Serializable {

    @Inject
    private FeriadoService service;

    private Feriado feriado = new Feriado();

    public void salvar() {
        service.salvar(feriado);
        feriado = new Feriado();
    }

    public void excluir(Long id) {
        service.remover(id);
    }

    public List<Feriado> listar() {
        return service.listar();
    }

    public Feriado getFeriado() { return feriado; }
}
