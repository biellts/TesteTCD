package br.com.sigapar1.controller;

import br.com.sigapar1.entity.Agendamento;
import br.com.sigapar1.entity.Servico;
import br.com.sigapar1.service.AgendamentoService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class FilaAdminController implements Serializable {

    @Inject private AgendamentoService service;

    private List<Agendamento> filaCompleta;
    private List<Agendamento> filaFiltrada;
    private List<Servico> servicos;

    private Long filtroServicoId;  // único filtro (opcional)

    @PostConstruct
    public void init() {
        servicos = service.listarServicos();
        atualizarFila();
    }

    public void atualizarFila() {
        filaCompleta = service.listarFilaOrdenada(); // ordena por prioridade e hora
        filtrar(); // aplica filtro de serviço (se houver)
    }

    public void filtrar() {
        if (filtroServicoId == null) {
            filaFiltrada = filaCompleta;
        } else {
            filaFiltrada = filaCompleta.stream()
                .filter(a -> a.getServico().getId().equals(filtroServicoId))
                .collect(Collectors.toList());
        }
    }

    public void limparFiltros() {
        filtroServicoId = null;
        filtrar();
    }

    public String calcularTempoEspera(Agendamento a) {
        if (a.getHoraCheckin() == null) return "0";
        Duration d = Duration.between(a.getHoraCheckin(), LocalTime.now());
        long minutos = d.toMinutes();
        return String.valueOf(minutos);
    }

    // GETTERS E SETTERS
    public List<Agendamento> getFilaFiltrada() { return filaFiltrada; }
    public List<Servico> getServicos() { return servicos; }
    public Long getFiltroServicoId() { return filtroServicoId; }
    public void setFiltroServicoId(Long filtroServicoId) { 
        this.filtroServicoId = filtroServicoId;
        filtrar();
    }
}