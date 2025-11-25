package br.com.sigapar1.controller;

import br.com.sigapar1.entity.Usuario;
import br.com.sigapar1.service.UsuarioService;
import br.com.sigapar1.util.JsfUtil;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class LoginController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private UsuarioService usuarioService;

    private Usuario usuarioLogado;
    private String email;
    private String senha;

    public String login() {
        usuarioLogado = usuarioService.autenticar(email, senha);

        if (usuarioLogado == null) {
            JsfUtil.addError("Credenciais inválidas!");
            return null;
        }

        return "/index.xhtml?faces-redirect=true";
    }

    public String logout() {
        usuarioLogado = null;
        email = null;
        senha = null;
        return "/login.xhtml?faces-redirect=true";
    }

    public boolean isLogado() {
        return usuarioLogado != null;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    // ------ GETTERS & SETTERS OBRIGATÓRIOS PARA JSF ------ //

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
