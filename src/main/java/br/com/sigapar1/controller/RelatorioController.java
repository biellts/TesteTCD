package br.com.sigapar1.controller;

import br.com.sigapar1.entity.Agendamento;
import br.com.sigapar1.service.RelatorioService;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Named
@ViewScoped
public class RelatorioController implements Serializable {

    @Inject
    private RelatorioService service;

    private LocalDate inicio;
    private LocalDate fim;

    private List<Agendamento> resultado;

    public void gerar() {
        resultado = service.atendimentosPorPeriodo(inicio, fim);
    }

    // Getters/setters
}
