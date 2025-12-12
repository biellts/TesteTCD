package br.com.sigapar1.controller;

import br.com.sigapar1.entity.Usuario;
import br.com.sigapar1.entity.EspacoAtendimento;
import br.com.sigapar1.entity.Guiche;
import br.com.sigapar1.entity.Role;

import br.com.sigapar1.service.UsuarioService;
import br.com.sigapar1.service.EmailService;
import br.com.sigapar1.service.EspacoAtendimentoService;
import br.com.sigapar1.service.GuicheService;

import br.com.sigapar1.util.JsfUtil;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class UsuarioController implements Serializable {

    @Inject private UsuarioService service;
    @Inject private EmailService emailService; // Mantido caso você use no admin
    @Inject private EspacoAtendimentoService espacoService;
    @Inject private GuicheService guicheService;

    private Usuario usuario;
    private List<Usuario> lista;

    private String confirmaSenha;
    private boolean aceitouTermos;
    private boolean gerarSenhaTemporaria = false;

    public UsuarioController() {}

    @PostConstruct
    public void init() {
        usuario = new Usuario();
        lista = service.listarTodos();
    }

    // ============================================================
    // CRUD INTERNO (ADMIN)
    // ============================================================
    public void salvar() {
        try {
            limparMascaras();
            boolean novo = (usuario.getId() == null);

            if (novo || gerarSenhaTemporaria) {
                String senhaTemp = service.gerarSenhaTemporaria();
                usuario.setSenha(senhaTemp);
                emailService.enviarSenhaTemporaria(usuario.getEmail(), usuario.getNome(), senhaTemp);
                
                // Criptografa a senha temporária ANTES de salvar
                usuario.setSenha(service.criptografarSenha(senhaTemp));
                service.salvarSemHashar(usuario);
            } else {
                service.salvar(usuario);
            }

            JsfUtil.addSuccess("Usuário salvo com sucesso!");
            resetarUsuario();
            lista = service.listarTodos();

        } catch (Exception e) {
            JsfUtil.addError("Erro ao salvar: " + e.getMessage());
        }
    }


    // ============================================================
    // CADASTRO PÚBLICO (SEM CONFIRMAÇÃO DE E-MAIL)
    // ============================================================
    public String salvarPublico() {
        try {
            // validação senha
            if (!usuario.getSenha().equals(confirmaSenha)) {
                JsfUtil.addError("As senhas não coincidem.");
                return null;
            }

            // valida termos
            if (!aceitouTermos) {
                JsfUtil.addError("Você deve aceitar os Termos de Uso.");
                return null;
            }

            limparMascaras();
            usuario.setRole(Role.ROLE_USER);

            // ================================================
            // ❌ CONFIRMAÇÃO DE EMAIL DESATIVADA
            // usuario.setAtivo(false);
            // usuario.setEmailConfirmationToken(UUID.randomUUID().toString());
            // emailService.enviarEmailConfirmacao(...);
            // return "/publico/verifique-email.xhtml";

            // ================================================
            // ✅ USUÁRIO NASCE ATIVO POR PADRÃO NA ENTIDADE
            // NÃO precisa setAtivo(true)
            // ================================================
            // Ativa e confirma o usuário imediatamente e envia email de boas-vindas
            usuario.setAtivo(true);
            usuario.setEmailConfirmed(true);

            // criptografar senha AQUI
            usuario.setSenha(service.criptografarSenha(usuario.getSenha()));

            // salva no banco SEM fazer hash novamente (já foi criptografado)
            service.salvarSemHashar(usuario);

            // enviar email de boas-vindas com link direto para login
            String link = "http://localhost:8080/sigapar/usuarios/login_usuario.xhtml";
            emailService.enviarEmailConfirmacao(usuario.getEmail(), usuario.getNome(), link);

            JsfUtil.addSuccess("Cadastro realizado com sucesso! Você já pode efetuar o login.");

            resetarUsuario();
            return "/publico/confirmar-email.xhtml?faces-redirect=true";

        } catch (Exception e) {
            JsfUtil.addError("Erro ao cadastrar: " + e.getMessage());
            return null;
        }
    }


    // ============================================================
    // EDIÇÃO
    // ============================================================
    public void editar(Usuario u) {
        if (u != null && u.getId() != null) {
            this.usuario = service.buscarPorId(u.getId());
        } else {
            this.usuario = new Usuario();
        }
        gerarSenhaTemporaria = false;
    }

    public void atualizar(Usuario u) {
        try {
            if (u.getSenha() != null && !u.getSenha().startsWith("$2a$")) {
                u.setSenha(service.criptografarSenha(u.getSenha()));
            }

            service.atualizar(u);
            JsfUtil.addSuccess("Dados atualizados!");

        } catch (Exception e) {
            JsfUtil.addError("Erro ao atualizar: " + e.getMessage());
        }
    }

    // ============================================================
    // AÇÕES ADMIN
    // ============================================================
    public void alterarStatus(Long id) {
        try {
            Usuario u = service.buscarPorId(id);

            if (u == null) return;

            u.setAtivo(!u.isAtivo());
            service.atualizar(u);

            JsfUtil.addSuccess("Status alterado: " + (u.isAtivo() ? "ATIVO" : "INATIVO"));
            lista = service.listarTodos();

        } catch (Exception e) {
            JsfUtil.addError("Erro ao alterar status: " + e.getMessage());
        }
    }

    public void excluir(Long id) {
        try {
            service.excluir(id);
            JsfUtil.addInfo("Usuário removido!");
            lista = service.listarTodos();
        } catch (Exception e) {
            JsfUtil.addError("Erro ao excluir: " + e.getMessage());
        }
    }

    // ============================================================
    // UTILITÁRIOS
    // ============================================================
    public void resetarUsuario() {
        usuario = new Usuario();
        gerarSenhaTemporaria = false;
        confirmaSenha = null;
        aceitouTermos = false;
    }

    private void limparMascaras() {
        if (usuario.getCpf() != null)
            usuario.setCpf(usuario.getCpf().replaceAll("\\D", ""));
        if (usuario.getTelefone() != null)
            usuario.setTelefone(usuario.getTelefone().replaceAll("\\D", ""));
    }

    public Usuario getUsuarioLogado() {
        return (Usuario) FacesContext.getCurrentInstance()
                .getExternalContext()
                .getSessionMap()
                .get("usuarioLogado");
    }


    // ============================================================
    // GETTERS / SETTERS
    // ============================================================
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public List<Usuario> getLista() { return lista; }

    public String getConfirmaSenha() { return confirmaSenha; }
    public void setConfirmaSenha(String confirmaSenha) { this.confirmaSenha = confirmaSenha; }

    public boolean isAceitouTermos() { return aceitouTermos; }
    public void setAceitouTermos(boolean aceitouTermos) { this.aceitouTermos = aceitouTermos; }

    public boolean isGerarSenhaTemporaria() { return gerarSenhaTemporaria; }
    public void setGerarSenhaTemporaria(boolean gerarSenhaTemporaria) { this.gerarSenhaTemporaria = gerarSenhaTemporaria; }

    public List<EspacoAtendimento> getEspacos() { return espacoService.listarTodos(); }
    public List<Guiche> getGuiches() { return guicheService.listarTodos(); }
}
