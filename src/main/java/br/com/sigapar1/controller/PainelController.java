package br.com.sigapar1.controller;

import br.com.sigapar1.entity.Agendamento;
import br.com.sigapar1.service.PainelService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.List;

@Named
@RequestScoped
public class PainelController {

    @Inject
    private PainelService painelService;

    private List<Agendamento> fila;

    @PostConstruct
    public void init() {
        fila = painelService.listarFilaPainel();
    }

    public List<Agendamento> getFila() {
        return fila;
    }
}
