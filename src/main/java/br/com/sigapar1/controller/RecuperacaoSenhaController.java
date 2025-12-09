package br.com.sigapar1.controller;

import br.com.sigapar1.service.RecuperacaoSenhaService;
import br.com.sigapar1.util.JsfUtil;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@RequestScoped
public class RecuperacaoSenhaController {

    private String email;
    private String token;
    private String senha;
    private String mensagem;

    @Inject
    private RecuperacaoSenhaService service;

    // GETTERS / SETTERS
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getMensagem() { return mensagem; }

    // Envia email com token
    public String enviarEmail() {

        service.solicitarRecuperacao(email);

        // Independentemente do email existir ou não
        return "/publico/email-enviado.xhtml?faces-redirect=true";
    }

    // Redefine a senha
    public String resetar() {

        boolean ok = service.redefinirSenha(token, senha);

        if (!ok) {
            JsfUtil.addError("Token inválido ou expirado.");
            return null;
        }

        JsfUtil.addInfo("Senha redefinida com sucesso!");
        return "/publico/escolher-acesso.xhtml?faces-redirect=true";
    }
}
