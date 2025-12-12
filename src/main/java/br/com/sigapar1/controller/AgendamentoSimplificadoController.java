package br.com.sigapar1.controller;

import br.com.sigapar1.entity.Agendamento;
import br.com.sigapar1.entity.Horario;
import br.com.sigapar1.entity.Servico;
import br.com.sigapar1.entity.AtendenteServicoEspaco;
import br.com.sigapar1.entity.StatusAgendamento;
import br.com.sigapar1.entity.Usuario;
import br.com.sigapar1.service.AgendamentoSimplificadoService;
import br.com.sigapar1.util.BusinessException;
import br.com.sigapar1.util.JsfUtil;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Named("agendamentoSimplificadoController")
@ViewScoped
public class AgendamentoSimplificadoController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private AgendamentoSimplificadoService service;

    @Inject
    private LoginController loginController;

    // ==========================
    // SELEÇÃO PRINCIPAL
    // ==========================
    private Long idServicoSelecionado;
    private LocalDate dataSelecionada;
    private Long idHorarioSelecionado;

    private List<Servico> servicos = new ArrayList<>();
    private List<LocalDate> diasDisponiveis = new ArrayList<>();
    private List<Horario> horariosDisponiveis = new ArrayList<>();
    private List<Agendamento> meusAgendamentos = new ArrayList<>();

    // ==========================
    // CONSULTA POR CPF OU PROTOCOLO
    // ==========================
    private String cpfOuProtocolo;
    private List<Agendamento> agendamentosEncontrados = new ArrayList<>();
    private Agendamento agendamentoSelecionado;

    // ==========================
    // REAGENDAMENTO
    // ==========================
    private LocalDate novaData;
    private Long novoHorarioId;
    private List<Horario> horariosParaReagendamento = new ArrayList<>();

    // ==========================
    // INIT
    // ==========================
    @PostConstruct
    public void init() {
        try {
            servicos = service.listarServicosAtivos().stream()
                    .filter(s -> s.getId() != null)
                    .collect(Collectors.toMap(Servico::getId, s -> s, (a, b) -> a))
                    .values().stream().collect(Collectors.toList());

            carregarMeusAgendamentos();

            // Se houver parâmetro "id" na URL, carregar agendamento para reagendamento
            String idParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
            if (idParam != null && !idParam.isBlank()) {
                try {
                    Long idAgendamento = Long.parseLong(idParam);
                    Agendamento ag = service.buscarAgendamentoComDetalhes(idAgendamento);
                    if (ag != null) {
                        prepararReagendamento(ag);
                    } else {
                        JsfUtil.addErrorMessage("Agendamento não encontrado.");
                    }
                } catch (NumberFormatException e) {
                    JsfUtil.addErrorMessage("ID de agendamento inválido.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            servicos = new ArrayList<>();
        }
    }

    // ==========================
    // MUDANÇAS DE SELEÇÃO
    // ==========================
    public void aoSelecionarServico() {
        dataSelecionada = null;
        idHorarioSelecionado = null;
        horariosDisponiveis.clear();
        if (idServicoSelecionado == null) {
            diasDisponiveis.clear();
            return;
        }
        diasDisponiveis = service.buscarDiasDisponiveisParaServico(idServicoSelecionado);
    }

    public void aoSelecionarData() {
        idHorarioSelecionado = null;
        horariosDisponiveis.clear();
        if (idServicoSelecionado == null || dataSelecionada == null) {
            return;
        }
        List<Horario> loaded = service.buscarHorariosPorServicoEData(idServicoSelecionado, dataSelecionada);
        horariosDisponiveis = loaded.stream()
                .filter(h -> h.getId() != null)
                .collect(Collectors.toMap(Horario::getId, h -> h, (a, b) -> a))
                .values().stream().collect(Collectors.toList());
    }

    public void aoSelecionarHorario() {
        System.out.println(">>> aoSelecionarHorario() chamado - idHora: " + idHorarioSelecionado);
    }

    // ==========================
    // CONFIRMAR AGENDAMENTO
    // ==========================
    public String confirmar() {
        try {
            Usuario usuario = loginController.getUsuarioLogado();
            if (usuario == null) {
                JsfUtil.addErrorMessage("Você precisa estar logado para agendar.");
                return null;
            }
            if (idServicoSelecionado == null || dataSelecionada == null || idHorarioSelecionado == null) {
                JsfUtil.addErrorMessage("Selecione serviço, data e horário.");
                return null;
            }
            Agendamento ag = service.confirmarAgendamento(idServicoSelecionado, idHorarioSelecionado, dataSelecionada, usuario);
            JsfUtil.addSuccessMessage("Agendamento confirmado! Protocolo: " + ag.getProtocolo());

            limpar();
            carregarMeusAgendamentos();

            return "sucesso.xhtml?faces-redirect=true&protocolo=" + ag.getProtocolo();
        } catch (BusinessException e) {
            JsfUtil.addErrorMessage(e.getMessage());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            JsfUtil.addErrorMessage("Erro ao confirmar agendamento: " + e.getMessage());
            return null;
        }
    }

    // ==========================
    // MEUS AGENDAMENTOS
    // ==========================
    public void carregarMeusAgendamentos() {
        try {
            Usuario usuario = loginController.getUsuarioLogado();
            if (usuario != null) {
                meusAgendamentos = service.listarAgendamentosDoUsuario(usuario);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Abre detalhes carregando o agendamento completo
    public void verDetalhes(Agendamento ag) {
        if (ag == null || ag.getId() == null) {
            this.agendamentoSelecionado = null;
            return;
        }
        this.agendamentoSelecionado = service.buscarAgendamentoComDetalhes(ag.getId());
    }

    // ==========================
    // LIMPAR FORM
    // ==========================
    private void limpar() {
        idServicoSelecionado = null;
        dataSelecionada = null;
        idHorarioSelecionado = null;
        diasDisponiveis.clear();
        horariosDisponiveis.clear();
    }

    // ==========================
    // FORMATADORES
    // ==========================
    public String formatarData(LocalDate data) {
        if (data == null) {
            return "";
        }
        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy (EEEE)", new Locale("pt", "BR")));
    }

    public String formatarHora(Horario horario) {
        if (horario == null) {
            return "";
        }
        return horario.getHora().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    // ==========================
    // CONSULTAR AGENDAMENTO
    // ==========================
    public void consultarAgendamento() {
        agendamentosEncontrados.clear();
        try {
            if (cpfOuProtocolo == null || cpfOuProtocolo.isBlank()) {
                JsfUtil.addErrorMessage("Informe CPF ou protocolo para consulta.");
                return;
            }

            String filtro = cpfOuProtocolo.replaceAll("\\D", "");

            if (filtro.length() == 11) {
                agendamentosEncontrados.addAll(service.listarAgendamentosPorCpf(filtro));
            } else {
                Agendamento ag = service.buscarPorProtocolo(cpfOuProtocolo);
                if (ag != null) {
                    agendamentosEncontrados.add(ag);
                }
            }

            if (agendamentosEncontrados.isEmpty()) {
                JsfUtil.addErrorMessage("Nenhum agendamento encontrado com os dados informados.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsfUtil.addErrorMessage("Erro ao consultar agendamento.");
        }
    }

    // ==========================
    // CANCELAR AGENDAMENTO
    // ==========================
    public void cancelarAgendamento(Agendamento ag) {
        if (ag == null) {
            return;
        }
        LocalDate agora = LocalDate.now();
        if (!ag.getData().minusDays(1).isAfter(agora)) {
            JsfUtil.addErrorMessage("Não é possível cancelar com menos de 24h de antecedência.");
            return;
        }
        ag.setStatus(StatusAgendamento.CANCELADO);
        service.salvar(ag);
        carregarMeusAgendamentos();
        JsfUtil.addSuccessMessage("Agendamento cancelado com sucesso!");
    }

    // ==========================
    // REAGENDAR AGENDAMENTO
    // ==========================
    public void prepararReagendamento(Agendamento ag) {
        if (ag == null) {
            return;
        }
        // Garantir que Servico e Horario sejam inicializados
        this.agendamentoSelecionado = service.buscarAgendamentoComDetalhes(ag.getId());
        this.idServicoSelecionado = agendamentoSelecionado.getServico().getId();
        this.diasDisponiveis = service.buscarDiasDisponiveisParaServico(idServicoSelecionado);
        this.novaData = agendamentoSelecionado.getData();
        carregarHorariosParaNovaData();
        this.novoHorarioId = agendamentoSelecionado.getHorario() != null ? agendamentoSelecionado.getHorario().getId() : null;
    }

    public void aoSelecionarNovaData() {
        carregarHorariosParaNovaData();
        this.novoHorarioId = null;
    }

    private void carregarHorariosParaNovaData() {
        if (novaData != null && idServicoSelecionado != null) {
            horariosParaReagendamento = service.buscarHorariosPorServicoEData(idServicoSelecionado, novaData);
        } else {
            horariosParaReagendamento.clear();
        }
    }

    public void confirmarReagendamento(Agendamento ag) {
        if (ag == null || novaData == null || novoHorarioId == null) {
            JsfUtil.addErrorMessage("Selecione nova data e horário.");
            return;
        }

        Horario horarioEscolhido = horariosParaReagendamento.stream()
                .filter(h -> novoHorarioId.equals(h.getId()))
                .findFirst().orElse(null);

        if (horarioEscolhido == null) {
            JsfUtil.addErrorMessage("Horário selecionado não está mais disponível.");
            return;
        }

        // Recarrega Horario no contexto gerenciado para garantir consistência
        Horario horarioGerenciado = service.buscarHorarioPorId(horarioEscolhido.getId());
        if (horarioGerenciado == null || !horarioGerenciado.isDisponivel()) {
            JsfUtil.addErrorMessage("Horário selecionado não está mais disponível.");
            return;
        }

        try {
            // Atualiza os dados do agendamento
            ag.setData(novaData);
            ag.setHorario(horarioGerenciado);
            ag.setDataHora(novaData.atTime(horarioGerenciado.getHora()));
            ag.setStatus(StatusAgendamento.REMARCADO);

            // Antes de salvar, atualiza atendente/espaco conforme novo horário
            AtendenteServicoEspaco ase = service.obterAtendenteServicoEspaco(idServicoSelecionado, horarioGerenciado.getId());
            if (ase != null) {
                ag.setEspaco(ase.getEspaco());
                ag.setAtendente(ase.getAtendente());
            } else {
                ag.setEspaco(null);
                ag.setAtendente(null);
            }

            // Salva no banco e obtém instância gerenciada
            Agendamento saved = service.salvar(ag);

            // Atualiza a lista de agendamentos do usuário
            carregarMeusAgendamentos();

            // Atualiza o objeto selecionado para refletir a nova data
            this.agendamentoSelecionado = service.buscarAgendamentoComDetalhes(saved.getId());

            JsfUtil.addSuccessMessage("Reagendamento realizado com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            JsfUtil.addErrorMessage("Erro ao reagendar: " + e.getMessage());
        }
    }

    // ==========================
    // LIMPAR SELEÇÃO DE DETALHES
    // ==========================
    public void limparSelecao() {
        this.agendamentoSelecionado = null;
    }

    // ==========================
    // GETTERS & SETTERS
    // ==========================
    public Long getIdServicoSelecionado() {
        return idServicoSelecionado;
    }

    public void setIdServicoSelecionado(Long idServicoSelecionado) {
        this.idServicoSelecionado = idServicoSelecionado;
    }

    public LocalDate getDataSelecionada() {
        return dataSelecionada;
    }

    public void setDataSelecionada(LocalDate dataSelecionada) {
        this.dataSelecionada = dataSelecionada;
    }

    public Long getIdHorarioSelecionado() {
        return idHorarioSelecionado;
    }

    public void setIdHorarioSelecionado(Long idHorarioSelecionado) {
        this.idHorarioSelecionado = idHorarioSelecionado;
    }

    public List<Servico> getServicos() {
        return servicos;
    }

    public List<LocalDate> getDiasDisponiveis() {
        return diasDisponiveis;
    }

    public List<Horario> getHorariosDisponiveis() {
        return horariosDisponiveis;
    }

    public List<Agendamento> getMeusAgendamentos() {
        return meusAgendamentos;
    }

    public String getCpfOuProtocolo() {
        return cpfOuProtocolo;
    }

    public void setCpfOuProtocolo(String cpfOuProtocolo) {
        this.cpfOuProtocolo = cpfOuProtocolo;
    }

    public List<Agendamento> getAgendamentosEncontrados() {
        return agendamentosEncontrados;
    }

    public Agendamento getAgendamentoSelecionado() {
        return agendamentoSelecionado;
    }

    public void setAgendamentoSelecionado(Agendamento agendamentoSelecionado) {
        this.agendamentoSelecionado = agendamentoSelecionado;
    }

    public LocalDate getNovaData() {
        return novaData;
    }

    public void setNovaData(LocalDate novaData) {
        this.novaData = novaData;
    }

    public Long getNovoHorarioId() {
        return novoHorarioId;
    }

    public void setNovoHorarioId(Long novoHorarioId) {
        this.novoHorarioId = novoHorarioId;
    }

    public List<Horario> getHorariosParaReagendamento() {
        return horariosParaReagendamento;
    }

    // --------------------------
    // AUXILIARES DE FORMATOS
    // --------------------------
    public String getHorarioSelecionadoParaReagendamento() {
        if (novoHorarioId == null || horariosParaReagendamento == null) {
            return "-";
        }
        for (Horario h : horariosParaReagendamento) {
            if (h.getId().equals(novoHorarioId) && h.getHora() != null) {
                return h.getHora().format(DateTimeFormatter.ofPattern("HH:mm"));
            }
        }
        return "-";
    }

    public String getNomeServicoSelecionado() {
        if (idServicoSelecionado == null) {
            return "-";
        }
        return servicos.stream().filter(s -> s.getId().equals(idServicoSelecionado))
                .findFirst().map(Servico::getNome).orElse("-");
    }

    public String getHorarioSelecionadoFormatado() {
        if (idHorarioSelecionado == null || horariosDisponiveis == null) {
            return "-";
        }
        return horariosDisponiveis.stream().filter(h -> h.getId().equals(idHorarioSelecionado))
                .findFirst().map(h -> h.getHora().format(DateTimeFormatter.ofPattern("HH:mm"))).orElse("-");
    }

    public String getDataSelecionadaFormatada() {
        if (dataSelecionada == null) {
            return "-";
        }
        return formatarData(dataSelecionada);
    }

    public String getNomeEspaco(Agendamento ag) {
        return ag.getEspaco() != null ? ag.getEspaco().getDescricao() : "-";
    }

    public String reagendar(Agendamento a) {
        return "reagendar.xhtml?faces-redirect=true&amp;id=" + a.getId();
    }
    
}
