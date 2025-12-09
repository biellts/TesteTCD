package br.com.sigapar1.controller;

import br.com.sigapar1.entity.Agendamento;
import br.com.sigapar1.service.AgendamentoService;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Named
@ViewScoped
public class PainelController implements Serializable {

    @Inject private AgendamentoService agendamentoService;

    private Agendamento atendimentoAtual;
    private List<Agendamento> ultimasChamadas;
    private int filaSize;

    public void atualizarPainel() {
        atendimentoAtual = agendamentoService.buscarUltimoChamado();     // em atendimento agora
        ultimasChamadas = agendamentoService.listarUltimasChamadas(8);   // últimas 8
        filaSize = agendamentoService.contarFila();                      // pessoas na fila
    }

    // Getters
    public Agendamento getAtendimentoAtual() {
        if (atendimentoAtual == null) atualizarPainel();
        return atendimentoAtual;
    }

    public List<Agendamento> getUltimasChamadas() {
        if (ultimasChamadas == null) atualizarPainel();
        return ultimasChamadas != null ? ultimasChamadas : Collections.emptyList();
    }

    public int getFilaSize() {
        if (filaSize == 0) atualizarPainel();
        return filaSize;
    }
}