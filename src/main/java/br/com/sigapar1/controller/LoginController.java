package br.com.sigapar1.controller;

import br.com.sigapar1.entity.Usuario;
import br.com.sigapar1.entity.Role;
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

    public LoginController() {}

    // ============================================================
    //                      LOGIN
    // ============================================================
    public String login() {

        usuarioLogado = usuarioService.autenticar(email, senha);

        if (usuarioLogado == null) {
            JsfUtil.addError("Credenciais inválidas!");
            return null; // continua na mesma página
        }

        // ============================================================
        //      REDIRECIONAMENTO POR PAPEL (Role)
        // ============================================================
        if (usuarioLogado.getRole() == Role.ROLE_ADMIN) {
            return "/admin/dashboard.xhtml?faces-redirect=true";
        }

        if (usuarioLogado.getRole() == Role.ROLE_RECEPTIONIST) {
            return "/recepcao/dashboard.xhtml?faces-redirect=true";
        }

        if (usuarioLogado.getRole() == Role.ROLE_ATTENDANT) {
            return "/atendente/home.xhtml?faces-redirect=true";
        }

        // Usuário comum
        return "/usuario/dashboard.xhtml?faces-redirect=true";
    }

    // ============================================================
    //                      LOGOUT
    // ============================================================
    public String logout() {
        usuarioLogado = null;
        email = null;
        senha = null;
        return "/logout.xhtml?faces-redirect=true";
    }

    // ============================================================
    //         MÉTODOS QUE FALTAVAM (USADOS NO LAYOUT)
    // ============================================================

    public boolean temRole(String role) {
        if (usuarioLogado == null || usuarioLogado.getRole() == null)
            return false;

        try {
            Role r = Role.valueOf(role);
            return usuarioLogado.getRole() == r;
        } catch (IllegalArgumentException ex) {
            return false; // caso role enviada não exista
        }
    }

    public Role getRole() {
        return usuarioLogado != null ? usuarioLogado.getRole() : null;
    }

    public boolean isAdmin() {
        return usuarioLogado != null && usuarioLogado.getRole() == Role.ROLE_ADMIN;
    }

    public boolean isAtendente() {
        return usuarioLogado != null && usuarioLogado.getRole() == Role.ROLE_ATTENDANT;
    }

    public boolean isRecepcao() {
        return usuarioLogado != null && usuarioLogado.getRole() == Role.ROLE_RECEPTIONIST;
    }

    public boolean isUsuarioComum() {
        return usuarioLogado != null && usuarioLogado.getRole() == Role.ROLE_USER;
    }

    // ============================================================
    //                      GETTERS
    // ============================================================
    public boolean isLogado() {
        return usuarioLogado != null;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}
