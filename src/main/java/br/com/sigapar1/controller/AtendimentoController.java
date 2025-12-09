package br.com.sigapar1.controller;

import br.com.sigapar1.entity.Agendamento;
import br.com.sigapar1.entity.Servico;
import br.com.sigapar1.entity.Usuario;
import br.com.sigapar1.entity.StatusAgendamento;

import br.com.sigapar1.service.AtendimentoService;
import br.com.sigapar1.service.AgendamentoService;
import br.com.sigapar1.service.ServicoService;
import br.com.sigapar1.util.JsfUtil;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class AtendimentoController implements Serializable {

    @Inject
    private AtendimentoService atendimentoService;

    @Inject
    private AgendamentoService agendamentoService;

    @Inject
    private ServicoService servicoService;

    @Inject
    private LoginController login;

    @Inject
    private UsuarioController usuarioController;

    // Atendimento atual
    private Agendamento atual;

    // UC12
    private String acoesRealizadas;
    private String observacoes;
    private String statusConclusao;

    // Reencaminhamento
    private Long idServicoReencaminhar;
    private List<Servico> servicos;

    @PostConstruct
    public void init() {
        limparCampos();
        servicos = servicoService.listarTodos();
    }

    // ===========================================================
    // CHAMAR PRÓXIMO CLIENTE (UC11) — por ESPAÇO
    // ===========================================================
    public void chamar() {
        try {
            Usuario atendente = login.getUsuarioLogado();
            if (atendente == null) {
                JsfUtil.addError("Usuário não logado.");
                return;
            }

            if (!"DISPONIVEL".equals(atendente.getStatus())) {
                JsfUtil.addWarn("Você precisa estar DISPONÍVEL para chamar o próximo cliente.");
                return;
            }

   
            // Marca atendente como ocupado
            atendente.setStatus("OCUPADO");
            usuarioController.atualizar(atendente);

            atendimentoService.iniciarAtendimento(atual, atendente);

            JsfUtil.addSuccess("Cliente chamado: " + atual.getUsuario().getNome());

        } catch (Exception e) {
            JsfUtil.addError("Erro ao chamar próximo: " + e.getMessage());
        }
    }

    // ===========================================================
    // FINALIZAR ATENDIMENTO (UC12)
    // ===========================================================
    public void finalizarComDetalhes() {
        try {
            if (atual == null) {
                JsfUtil.addError("Nenhum atendimento em andamento.");
                return;
            }

            boolean naoCompareceu = "NAO_COMPARECEU".equals(statusConclusao);

            if (!naoCompareceu) {
                if (acoesRealizadas == null || acoesRealizadas.trim().length() < 20) {
                    JsfUtil.addError("A descrição deve ter no mínimo 20 caracteres.");
                    return;
                }
            }

            if (statusConclusao == null || statusConclusao.isBlank()) {
                JsfUtil.addError("Selecione o resultado do atendimento.");
                return;
            }

            // ===========================================================
            // REENCAMINHAMENTO
            // ===========================================================
            if ("REENCAMINHADO".equals(statusConclusao)) {

                if (idServicoReencaminhar == null) {
                    JsfUtil.addError("Selecione o serviço de destino.");
                    return;
                }

                Agendamento novo = agendamentoService.gerarReencaminhamento(
                        atual.getUsuario(),
                        idServicoReencaminhar
                );

                JsfUtil.addSuccess("Usuário reencaminhado para: " + novo.getServico().getNome());
            }

            // MONTA TEXTO
            StringBuilder texto = new StringBuilder();

            if (!naoCompareceu) {
                texto.append("AÇÕES REALIZADAS: ").append(acoesRealizadas);
            }

            if (observacoes != null && !observacoes.isBlank()) {
                texto.append("\nOBSERVAÇÕES: ").append(observacoes);
            }

            atendimentoService.finalizarAtendimento(atual.getId(), texto.toString());

            // Atualiza STATUS do agendamento usando enum
            switch (statusConclusao) {
                case "CONCLUIDO" ->
                        atual.setStatus(StatusAgendamento.CONCLUIDO);

                case "REENCAMINHADO" ->
                        atual.setStatus(StatusAgendamento.REMARCADO);

                case "CANCELADO_USUARIO", "NAO_COMPARECEU" ->
                        atual.setStatus(StatusAgendamento.CANCELADO);

                default ->
                        throw new IllegalArgumentException("Status inválido: " + statusConclusao);
            }

            agendamentoService.atualizar(atual);

            // libera atendente
            Usuario atendente = login.getUsuarioLogado();
            atendente.setStatus("DISPONIVEL");
            usuarioController.atualizar(atendente);

            JsfUtil.addSuccess("Atendimento finalizado com sucesso!");

            limparCampos();

        } catch (Exception e) {
            JsfUtil.addError("Erro ao finalizar: " + e.getMessage());
        }
    }

    // ===========================================================
    // LIMPAR CAMPOS
    // ===========================================================
    private void limparCampos() {
        atual = null;
        acoesRealizadas = null;
        observacoes = null;
        statusConclusao = null;
        idServicoReencaminhar = null;
    }

    // GETTERS / SETTERS
    public Agendamento getAtual() { return atual; }

    public String getAcoesRealizadas() { return acoesRealizadas; }
    public void setAcoesRealizadas(String acoesRealizadas) { this.acoesRealizadas = acoesRealizadas; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public String getStatusConclusao() { return statusConclusao; }
    public void setStatusConclusao(String statusConclusao) { this.statusConclusao = statusConclusao; }

    public Long getIdServicoReencaminhar() { return idServicoReencaminhar; }
    public void setIdServicoReencaminhar(Long idServicoReencaminhar) { this.idServicoReencaminhar = idServicoReencaminhar; }

    public List<Servico> getServicos() { return servicos; }
}
