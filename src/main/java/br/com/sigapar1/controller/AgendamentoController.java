package br.com.sigapar1.controller;

import br.com.sigapar1.entity.*;
import br.com.sigapar1.service.AgendamentoService;
import br.com.sigapar1.service.AtendenteServicoEspacoService;
import br.com.sigapar1.util.BusinessException;
import br.com.sigapar1.util.JsfUtil;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class AgendamentoController implements Serializable {

    @Inject
    private AgendamentoService service;

    @Inject
    private AtendenteServicoEspacoService vinculoService;

    @Inject
    private LoginController login;

    // campos...
    private Long idServicoBusca;
    private Long idHorarioSelecionado;
    private RevisaoDTO revisao = new RevisaoDTO();

    private List<LocalDate> diasDisponiveis = new ArrayList<>();
    private LocalDate diaSelecionado;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private List<Servico> servicos;
    private List<Servico> servicosAtivos;
    private List<EspacoAtendimento> espacos;
    private List<Horario> horariosDisponiveis = new ArrayList<>();

    // DASHBOARD
    private List<Agendamento> meusAgendamentos = List.of();
    private Agendamento proximoAgendamento;
    private List<Agendamento> ultimos3Agendamentos = List.of();

    // demais campos...
    private Agendamento agendamentoAtual;
    private Long idHorarioNovo;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    private Agendamento agendamentoSelecionado;
    private boolean modalCancelarAberto = false;

    @PostConstruct
    public void init() {
        try {
            // DEBUG: confirmar execução
            System.out.println("AgendamentoController.init() chamado em " + LocalDateTime.now());
            carregarServicosAtivos();
            carregarDashboard();

            // DEBUG: resumo após carga
            Usuario u = login != null ? login.getUsuarioLogado() : null;
            System.out.println("Usuario logado (init): " + (u == null ? "NULL" : u.getId() + " - " + u.getNome()));
            System.out.println("meusAgendamentos.size() = " + (meusAgendamentos == null ? "null" : meusAgendamentos.size()));
            System.out.println("proximoAgendamento = " + (proximoAgendamento == null ? "null" : proximoAgendamento.getProtocolo()));
            System.out.println("ultimos3Agendamentos.size() = " + (ultimos3Agendamentos == null ? "null" : ultimos3Agendamentos.size()));

        } catch (Exception e) {
            // mostra erro em tela e no log
            System.err.println("Erro em init AgendamentoController: " + e.getMessage());
            e.printStackTrace();
            JsfUtil.addError("Erro ao iniciar página de agendamento: " + e.getMessage());
        }
    }

    private void carregarServicosAtivos() {
        servicos = service.listarServicos();
        servicosAtivos = servicos == null ? List.of() : servicos.stream()
                .filter(Servico::isAtivo)
                .sorted(Comparator.comparing(Servico::getDescricao))
                .toList();
    }

    public void carregarDiasDisponiveis() {
        try {
            diasDisponiveis = new ArrayList<>();
            horariosDisponiveis = List.of();
            diaSelecionado = null;

            if (idServicoBusca == null) {
                return;
            }

            List<AtendenteServicoEspaco> vinculos = vinculoService.listar()
                    .stream()
                    .filter(v -> v.getServico().getId().equals(idServicoBusca))
                    .toList();

            LocalDate hoje = LocalDate.now();
            for (int i = 0; i < 30; i++) {
                LocalDate dia = hoje.plusDays(i);
                boolean diaTemCapacidade = vinculos.stream().anyMatch(v
                        -> v.getHorario().getCapacidadeAtual() < v.getHorario().getCapacidadeMax()
                );
                if (diaTemCapacidade) {
                    diasDisponiveis.add(dia);
                }
            }

        } catch (Exception e) {
            System.err.println("Erro carregarDiasDisponiveis: " + e.getMessage());
            JsfUtil.addError("Erro ao carregar dias: " + e.getMessage());
        }
    }

    public void carregarHorarios() {
        try {
            horariosDisponiveis = new ArrayList<>();
            if (idServicoBusca == null || diaSelecionado == null) {
                return;
            }

            List<AtendenteServicoEspaco> vinculos = vinculoService.listar()
                    .stream()
                    .filter(v -> v.getServico().getId().equals(idServicoBusca))
                    .toList();

            horariosDisponiveis = vinculos.stream()
                    .filter(v -> v.getHorario().getCapacidadeAtual() < v.getHorario().getCapacidadeMax())
                    .map(AtendenteServicoEspaco::getHorario)
                    .distinct()
                    .sorted(Comparator.comparing(Horario::getHora))
                    .toList();

        } catch (Exception e) {
            System.err.println("Erro carregarHorarios: " + e.getMessage());
            JsfUtil.addError("Erro ao carregar horários: " + e.getMessage());
        }
    }

    public void limparDataEHorario() {
        revisao.setData(null);
        idHorarioSelecionado = null;
        horariosDisponiveis = List.of();
        diasDisponiveis = List.of();
        diaSelecionado = null;
    }

    public String confirmar() {
        try {
            Usuario usuario = login.getUsuarioLogado();
            if (usuario == null) {
                JsfUtil.addError("Você precisa estar logado.");
                return null;
            }

            LocalDate dataParaVer = (revisao.getData() != null) ? revisao.getData() : diaSelecionado;
            if (idServicoBusca == null || dataParaVer == null || idHorarioSelecionado == null) {
                JsfUtil.addError("Preencha todos os campos obrigatórios.");
                return null;
            }

            Servico servico = service.buscarServicoPorId(idServicoBusca);
            Horario horario = service.buscarHorarioPorId(idHorarioSelecionado);

            List<AtendenteServicoEspaco> vinculosDisponiveis = vinculoService.listar().stream()
                    .filter(v -> v.getServico().getId().equals(idServicoBusca))
                    .filter(v -> v.getHorario().getId().equals(idHorarioSelecionado))
                    .filter(v -> v.getHorario().getCapacidadeAtual() < v.getHorario().getCapacidadeMax())
                    .toList();

            if (vinculosDisponiveis.isEmpty()) {
                JsfUtil.addError("Nenhum vínculo disponível para este serviço e horário.");
                return null;
            }

            AtendenteServicoEspaco vinculo = vinculosDisponiveis.get(0);

            Agendamento a = new Agendamento();
            a.setUsuario(usuario);
            a.setServico(servico);
            a.setEspaco(vinculo.getEspaco());
            a.setAtendente(vinculo.getAtendente());
            a.setData(dataParaVer);
            a.setHorario(horario);
            a.setDataHora(LocalDateTime.of(dataParaVer, horario.getHora()));
            a.setStatus(StatusAgendamento.AGENDADO);

            service.salvar(a);
            JsfUtil.addSuccess("Agendamento realizado! Protocolo: " + a.getProtocolo());
            carregarDashboard();

            return "/publico/agendamento-concluido.xhtml?faces-redirect=true&protocolo=" + a.getProtocolo();
        } catch (BusinessException e) {
            JsfUtil.addError(e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Erro confirmar agendamento: " + e.getMessage());
            e.printStackTrace();
            JsfUtil.addError("Erro ao agendar: " + e.getMessage());
            return null;
        }
    }

    public void prepararReagendamento(Agendamento a) {
        this.agendamentoAtual = a;
        this.idServicoBusca = a.getServico().getId();
        this.revisao.setData(null);
        this.idHorarioNovo = null;
        this.horariosDisponiveis = new ArrayList<>();
        this.diasDisponiveis = new ArrayList<>();
        this.diaSelecionado = null;
        carregarDiasDisponiveis();
    }

    public String confirmarReagendamento() {
        try {
            if (agendamentoAtual == null || idHorarioNovo == null) {
                JsfUtil.addError("Dados inválidos para reagendamento.");
                return null;
            }

            service.reagendar(
                    agendamentoAtual.getId(),
                    idHorarioNovo,
                    login.getUsuarioLogado()
            );

            JsfUtil.addSuccess("Reagendamento realizado com sucesso!");
            return "/agendamento/meus?faces-redirect=true";

        } catch (BusinessException e) {
            JsfUtil.addError(e.getMessage());
            return null;
        }
    }

    public boolean podeCancelar(Agendamento a) {
        return service.podeCancelar(a);
    }

    public List<Agendamento> getMeusAgendamentosCancelaveis() {
        Usuario u = login.getUsuarioLogado();
        if (u == null || meusAgendamentos == null) {
            return List.of();
        }
        return meusAgendamentos.stream()
                .filter(a -> a.getStatus() == StatusAgendamento.AGENDADO
                || a.getStatus() == StatusAgendamento.REMARCADO)
                .filter(service::podeCancelar)
                .sorted(Comparator.comparing(Agendamento::getDataHora))
                .toList();
    }

    public void selecionarParaCancelar(Agendamento ag) {
        this.agendamentoSelecionado = ag;
        this.modalCancelarAberto = true;
    }

    public void fecharModalCancelar() {
        this.modalCancelarAberto = false;
    }

    public void cancelar(Long id) {
        try {
            service.cancelar(id, login.getUsuarioLogado());
            carregarDashboard();
            JsfUtil.addSuccess("Agendamento cancelado com sucesso!");
        } catch (BusinessException e) {
            JsfUtil.addError(e.getMessage());
        }
    }

    public void fazerCheckinDireto(Agendamento a) {
        try {
            if (a == null) {
                JsfUtil.addError("Agendamento inválido.");
                return;
            }
            service.fazerCheckin(a, login.getUsuarioLogado());
            JsfUtil.addSuccess("Check-in realizado com sucesso!");
            carregarDashboard();
        } catch (BusinessException e) {
            JsfUtil.addError(e.getMessage());
        }
    }

    private void carregarDashboard() {
        try {
            Usuario u = login != null ? login.getUsuarioLogado() : null;
            System.out.println("carregarDashboard -> usuario = " + (u == null ? "NULL" : u.getId()));

            if (u == null) {
                meusAgendamentos = List.of();
                proximoAgendamento = null;
                ultimos3Agendamentos = List.of();
                JsfUtil.addInfo("Nenhum usuário logado (debug).");
                return;
            }

            // primeira tentativa: listarPorUsuario
            meusAgendamentos = service.listarPorUsuario(u);
            System.out.println("meusAgendamentos (listarPorUsuario) size = " + (meusAgendamentos == null ? "null" : meusAgendamentos.size()));

            // se retornou vazio, tentamos buscar por protocolo/cpf como fallback
            if (meusAgendamentos == null || meusAgendamentos.isEmpty()) {
                // fallback de debug: buscar por CPF (se existir)
                String cpf = u.getCpf();
                if (cpf != null && !cpf.isBlank()) {
                    Agendamento aCpf = service.buscarPorCpf(cpf);
                    if (aCpf != null) {
                        meusAgendamentos = new ArrayList<>();
                        meusAgendamentos.add(aCpf);
                        System.out.println("fallback buscarPorCpf retornou 1 item (debug).");
                    }
                }
            }

            // próximo agendamento
            try {
                proximoAgendamento = service.buscarProximo(u);
            } catch (Exception e) {
                System.err.println("Erro ao buscarProximo: " + e.getMessage());
                proximoAgendamento = null;
            }
            System.out.println("proximoAgendamento (buscarProximo) = " + (proximoAgendamento == null ? "null" : proximoAgendamento.getProtocolo()));

            // ultimos 3 — se meusAgendamentos estiver preenchido usamos ele, senão consultamos lista e cortamos
            if (meusAgendamentos != null && !meusAgendamentos.isEmpty()) {
                ultimos3Agendamentos = meusAgendamentos.stream()
                        .sorted(Comparator.comparing(Agendamento::getDataHora).reversed())
                        .limit(3)
                        .toList();
            } else {
                // fallback: tentar buscar últimos 3 via service (se existir) ou deixar vazio
                try {
                    // se o service tiver um método buscarUltimos3, ele será chamado; caso contrário, essa chamada falhará e iremos para lista vazia
                    ultimos3Agendamentos = service.listarPorUsuario(u).stream()
                            .sorted(Comparator.comparing(Agendamento::getDataHora).reversed())
                            .limit(3)
                            .toList();
                } catch (Exception ex) {
                    ultimos3Agendamentos = List.of();
                }
            }

            System.out.println("ultimos3Agendamentos size = " + (ultimos3Agendamentos == null ? "null" : ultimos3Agendamentos.size()));

        } catch (Exception e) {
            System.err.println("Erro carregarDashboard: " + e.getMessage());
            e.printStackTrace();
            JsfUtil.addError("Erro ao carregar dashboard: " + e.getMessage());
            meusAgendamentos = List.of();
            proximoAgendamento = null;
            ultimos3Agendamentos = List.of();
        }
    }

    // formatadores e getters/setters mantidos iguais ao seu original...
    public String formatarData(Agendamento a) {
        if (a == null || a.getData() == null) {
            return "";
        }
        return a.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String formatarHora(Horario h) {
        if (h == null) {
            return "";
        }
        return h.getHora().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public String formatarDataSimples(LocalDate d) {
        return d == null ? "" : d.format(dateFormatter);
    }

    public String dataHoraFormatada(Agendamento a) {
        return (a == null || a.getDataHora() == null)
                ? ""
                : a.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getDescricaoServicoSelecionado() {
        if (idServicoBusca == null || servicosAtivos == null) {
            return "";
        }
        return servicosAtivos.stream()
                .filter(s -> s.getId().equals(idServicoBusca))
                .findFirst()
                .map(Servico::getDescricao)
                .orElse("");
    }

    // getters/setters (copiar do seu original)...
    public Long getIdServicoBusca() {
        return idServicoBusca;
    }

    public void setIdServicoBusca(Long idServicoBusca) {
        this.idServicoBusca = idServicoBusca;
    }

    public Long getIdHorarioSelecionado() {
        return idHorarioSelecionado;
    }

    public void setIdHorarioSelecionado(Long idHorarioSelecionado) {
        this.idHorarioSelecionado = idHorarioSelecionado;
    }

    public RevisaoDTO getRevisao() {
        return revisao;
    }

    public List<Servico> getServicosAtivos() {
        return servicosAtivos;
    }

    public List<Horario> getHorariosDisponiveis() {
        return horariosDisponiveis;
    }

    public List<LocalDate> getDiasDisponiveis() {
        return diasDisponiveis;
    }

    public LocalDate getDiaSelecionado() {
        return diaSelecionado;
    }

    public void setDiaSelecionado(LocalDate diaSelecionado) {
        this.diaSelecionado = diaSelecionado;
    }

    public Agendamento getProximoAgendamento() {
        return proximoAgendamento;
    }

    public List<Agendamento> getUltimos3Agendamentos() {
        return ultimos3Agendamentos;
    }

    public Agendamento getAgendamentoAtual() {
        return agendamentoAtual;
    }

    public Long getIdHorarioNovo() {
        return idHorarioNovo;
    }

    public void setIdHorarioNovo(Long idHorarioNovo) {
        this.idHorarioNovo = idHorarioNovo;
    }

    public Agendamento getAgendamentoSelecionado() {
        return agendamentoSelecionado;
    }

    public boolean isModalCancelarAberto() {
        return modalCancelarAberto;
    }

    public List<Agendamento> getMeusAgendamentos() {
        return meusAgendamentos;
    }

    public DateTimeFormatter getFormatter() {
        return formatter;
    }

    public Date getDataParaJSF(Agendamento a) {
        return java.sql.Date.valueOf(a.getData());
    }

}
