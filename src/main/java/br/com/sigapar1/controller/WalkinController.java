package br.com.sigapar1.controller;

import br.com.sigapar1.entity.Agendamento;
import br.com.sigapar1.entity.Servico;
import br.com.sigapar1.entity.StatusAgendamento;
import br.com.sigapar1.entity.Usuario;

import br.com.sigapar1.service.AgendamentoSimplificadoService;
import br.com.sigapar1.service.ServicoService;
import br.com.sigapar1.service.UsuarioService;

import br.com.sigapar1.util.BusinessException;
import br.com.sigapar1.util.JsfUtil;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Named
@ViewScoped
public class WalkinController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private AgendamentoSimplificadoService agendamentoService;

    @Inject
    private UsuarioService usuarioService;

    @Inject
    private ServicoService servicoService;

    // Campos do formulário
    private String nome;
    private String cpf;
    private String telefone;
    private Long servicoId;

    private List<Servico> servicos;

    // Agendamento criado para exibir na tela
    private Agendamento agendamentoCriado;

    @PostConstruct
    public void init() {
        servicos = servicoService.listarAtivos();
    }

    public void registrarWalkin() {
        try {
            // Validações básicas
            if (nome == null || nome.trim().isEmpty()) {
                throw new BusinessException("Nome é obrigatório.");
            }
            if (cpf == null || cpf.replaceAll("\\D", "").length() != 11) {
                throw new BusinessException("CPF inválido. Digite exatamente 11 números.");
            }
            if (servicoId == null) {
                throw new BusinessException("Selecione um serviço.");
            }

            // Busca ou cria usuário rapidamente
            Usuario usuario = usuarioService.buscarOuCriarRAPIDO(nome.trim(), cpf, telefone);

            Servico servico = servicoService.buscarPorId(servicoId);
            if (servico == null) {
                throw new BusinessException("Serviço não encontrado.");
            }

            // Cria o agendamento walk-in
            Agendamento ag = new Agendamento();
            ag.setUsuario(usuario);
            ag.setServico(servico);
            ag.setData(LocalDate.now());
            ag.setDataHora(LocalDateTime.now());

            // Flags walk-in
            ag.setCheckin(true);
            ag.setChamado(false);
            ag.setEmAtendimento(false);
            ag.setFinalizado(false);
            ag.setAtivo(true);

            // Status do enum
            ag.setStatus(StatusAgendamento.EM_FILA);

            ag.setProtocolo("WLK" + System.currentTimeMillis());

            // Salva
            agendamentoService.salvar(ag);

            // Carrega detalhes completos
            this.agendamentoCriado = agendamentoService.buscarAgendamentoComDetalhes(ag.getId());

            JsfUtil.addSuccessMessage("Cliente adicionado à fila! Protocolo: " + ag.getProtocolo());

            limparFormulario();

        } catch (BusinessException e) {
            JsfUtil.addErrorMessage(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            JsfUtil.addErrorMessage("Erro ao registrar walk-in.");
        }
    }

    private void limparFormulario() {
        nome = null;
        cpf = null;
        telefone = null;
        servicoId = null;
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Long getServicoId() {
        return servicoId;
    }
    public void setServicoId(Long servicoId) {
        this.servicoId = servicoId;
    }

    public List<Servico> getServicos() {
        return servicos;
    }

    public Agendamento getAgendamentoCriado() {
        return agendamentoCriado;
    }

    // Retorna o nome do serviço selecionado
    public String getNomeServicoSelecionado() {
        if (servicoId == null) {
            return "Nenhum serviço selecionado";
        }
        return servicos.stream()
                .filter(s -> s.getId().equals(servicoId))
                .findFirst()
                .map(Servico::getNome)
                .orElse("Serviço não encontrado");
    }
}
