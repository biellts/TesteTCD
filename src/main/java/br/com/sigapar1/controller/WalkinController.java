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

    // Campos da tela
    private String nome;
    private String cpf;
    private String telefone;

    private Long servicoId;
    private List<Servico> servicos;

    private Servico servicoSelecionado;

    @PostConstruct
    public void init() {
        servicos = servicoService.listarAtivos();
    }

    public void carregarServicoSelecionado() {
        if (servicoId != null) {
            servicoSelecionado = servicoService.buscarPorId(servicoId);
        }
    }

    public void registrarWalkin() {
        try {
            // Buscar ou criar usuário rápido
            Usuario usuario = usuarioService.buscarOuCriarRAPIDO(nome, cpf, telefone);

            carregarServicoSelecionado();
            if (servicoSelecionado == null) {
                throw new BusinessException("Serviço inválido.");
            }

            // Criar agendamento tipo walk-in na fila
            Agendamento ag = new Agendamento();
            ag.setUsuario(usuario);
            ag.setServico(servicoSelecionado);

            // Walk-in = agora
            ag.setData(LocalDate.now());
            ag.setDataHora(LocalDateTime.now());

            // Flags padrão para fila
            ag.setCheckin(true);          // já se apresentou
            ag.setChamado(false);         // ainda não chamado
            ag.setFinalizado(false);      // ainda não finalizado
            ag.setEmAtendimento(false);   // não em atendimento ainda
            ag.setAtivo(true);

            // STATUS = está na fila
            ag.setStatus(StatusAgendamento.EM_FILA);

            // Protocolo único
            ag.setProtocolo("WLK" + System.currentTimeMillis());

            // Salvar
            agendamentoService.salvar(ag);

            JsfUtil.addSuccessMessage("Cliente adicionado à fila! Protocolo: " + ag.getProtocolo());
            limparFormulario();

        } catch (BusinessException e) {
            JsfUtil.addErrorMessage(e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            JsfUtil.addErrorMessage("Erro ao registrar atendimento walk-in.");
        }
    }

    private void limparFormulario() {
        nome = null;
        cpf = null;
        telefone = null;
        servicoId = null;
        servicoSelecionado = null;
    }

    // Getters & Setters
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

    public Servico getServicoSelecionado() {
        return servicoSelecionado;
    }
}
