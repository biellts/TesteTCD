package br.com.sigapar1.controller;

import br.com.sigapar1.entity.Agendamento;
import br.com.sigapar1.entity.Horario;
import br.com.sigapar1.service.AgendamentoSimplificadoService;
import br.com.sigapar1.util.JsfUtil;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Named("recepcaoController")
@ViewScoped
public class RecepcaoController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private AgendamentoSimplificadoService service;

    // Filtros de busca
    private String buscaCpf;
    private String buscaProtocolo;
    private LocalDate dataBusca = LocalDate.now(); // padrão: hoje

    // Resultados
    private List<Agendamento> agendamentosEncontrados = new ArrayList<>();
    private Agendamento agendamentoSelecionado;

    @PostConstruct
    public void init() {
        buscarAgendamentosDoDia();
    }

    // ========================================
    // BUSCAR AGENDAMENTOS DO DIA (padrão)
    // ========================================
    public void buscarAgendamentosDoDia() {
        this.buscaCpf = null;
        this.buscaProtocolo = null;
        carregarAgendamentosPorData(dataBusca);
    }

    // ========================================
    // BUSCA PRINCIPAL (CPF ou Protocolo)
    // ========================================
    public void buscar() {
        agendamentosEncontrados.clear();

        boolean temCpf = buscaCpf != null && !buscaCpf.trim().isEmpty();
        boolean temProtocolo = buscaProtocolo != null && !buscaProtocolo.trim().isEmpty();

        if (!temCpf && !temProtocolo && dataBusca == null) {
            JsfUtil.addErrorMessage("Informe pelo menos um filtro.");
            return;
        }

        try {
            // 1. Busca por protocolo (prioridade maior)
            if (temProtocolo) {
                Agendamento ag = service.buscarPorProtocolo(buscaProtocolo.trim());
                if (ag != null) {
                    agendamentosEncontrados.add(ag);
                } else {
                    JsfUtil.addInfoMessage("Protocolo não encontrado.");
                }
            }

            // 2. Busca por CPF (só se não achou protocolo ou se ambos foram informados)
            if (temCpf && (agendamentosEncontrados.isEmpty() || temCpf)) {
                String cpfLimpo = buscaCpf.replaceAll("\\D", "");
                if (cpfLimpo.length() == 11) {
                    List<Agendamento> porCpf = service.listarAgendamentosPorCpf(cpfLimpo);
                    if (!porCpf.isEmpty()) {
                        // Se já tem resultado do protocolo, evita duplicar
                        for (Agendamento ag : porCpf) {
                            if (!agendamentosEncontrados.contains(ag)) {
                                agendamentosEncontrados.add(ag);
                            }
                        }
                    } else if (!temProtocolo) {
                        JsfUtil.addInfoMessage("Nenhum agendamento encontrado para este CPF.");
                    }
                } else {
                    JsfUtil.addErrorMessage("CPF inválido.");
                }
            }

            // 3. Se não informou nada além da data, busca por data
            if (agendamentosEncontrados.isEmpty() && !temCpf && !temProtocolo) {
                carregarAgendamentosPorData(dataBusca);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JsfUtil.addErrorMessage("Erro na busca.");
        }
    }

    private void carregarAgendamentosPorData(LocalDate data) {
        if (data == null) return;
        agendamentosEncontrados = service.listarAgendamentosPorData(data);
        if (agendamentosEncontrados.isEmpty()) {
            JsfUtil.addInfoMessage("Nenhum agendamento encontrado para " + formatarData(data) + ".");
        }
    }

    // ========================================
    // LIMPAR BUSCA
    // ========================================
    public void limpar() {
        buscaCpf = null;
        buscaProtocolo = null;
        dataBusca = LocalDate.now();
        buscarAgendamentosDoDia();
    }

    // ========================================
    // FORMATADORES
    // ========================================
    public String formatarData(LocalDate data) {
        if (data == null) return "";
        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy (EEEE)", new Locale("pt", "BR")));
    }

    public String formatarHora(Horario h) {
        if (h == null || h.getHora() == null) return "-";
        return h.getHora().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    // ========================================
    // GETTERS & SETTERS
    // ========================================
    public String getBuscaCpf() { return buscaCpf; }
    public void setBuscaCpf(String buscaCpf) { this.buscaCpf = buscaCpf; }

    public String getBuscaProtocolo() { return buscaProtocolo; }
    public void setBuscaProtocolo(String buscaProtocolo) { this.buscaProtocolo = buscaProtocolo; }

    public LocalDate getDataBusca() { return dataBusca; }
    public void setDataBusca(LocalDate dataBusca) { this.dataBusca = dataBusca; }

    public List<Agendamento> getAgendamentosEncontrados() { return agendamentosEncontrados; }

    public Agendamento getAgendamentoSelecionado() { return agendamentoSelecionado; }
    public void setAgendamentoSelecionado(Agendamento agendamentoSelecionado) {
        this.agendamentoSelecionado = agendamentoSelecionado;
    }
}