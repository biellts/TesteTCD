package br.com.sigapar1.controller;

import br.com.sigapar1.entity.AtendenteServicoEspaco;
import br.com.sigapar1.entity.EspacoAtendimento;
import br.com.sigapar1.entity.Role;
import br.com.sigapar1.entity.Usuario;
import br.com.sigapar1.service.AtendenteServicoEspacoService;
import br.com.sigapar1.service.LogAcessoService;
import br.com.sigapar1.service.UsuarioService;
import br.com.sigapar1.util.JsfUtil;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Named
@SessionScoped
public class LoginController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private UsuarioService usuarioService;

    @Inject
    private LogAcessoService logService;
    @Inject
    private AtendenteServicoEspacoService atendenteServicoEspacoService;

    private Usuario usuarioLogado;
    private String email;
    private String senha;

    public LoginController() {
    }

    // ============================================================
    // LOGIN
    // ============================================================
    public String login() {
        try {
            usuarioLogado = usuarioService.autenticar(email, senha);

            if (usuarioLogado == null) {
                JsfUtil.addError("Credenciais inválidas!");
                return null;
            }

            // -----------------------------
            // Guardar usuário na sessão
            // -----------------------------
            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();
            Map<String, Object> sessionMap = ec.getSessionMap();
            sessionMap.put("usuarioLogado", usuarioLogado);

            // -----------------------------
            // Registro de Log de Acesso (RF001)
            // -----------------------------
            String ip = ((jakarta.servlet.http.HttpServletRequest) ec.getRequest()).getRemoteAddr();

            logService.registrar(usuarioLogado.getEmail(),
                    usuarioLogado.getRole().name(),
                    ip);

            // -----------------------------
            // Redirecionamento por perfil
            // -----------------------------
            Role r = usuarioLogado.getRole();

            if (r == Role.ROLE_ADMIN) {
                return "/admin/dashboard?faces-redirect=true";
            } else if (r == Role.ROLE_RECEPTIONIST) {
                return "/recepcao/dashboard?faces-redirect=true";
            } else if (r == Role.ROLE_ATTENDANT) {
                return "/atendente/home?faces-redirect=true";
            } else {
                return "/usuarios/dashboard?faces-redirect=true";
            }

        } catch (Exception e) {
            JsfUtil.addError("Erro ao autenticar: " + e.getMessage());
            return null;
        }
    }

    // ============================================================
    // LOGOUT
    // ============================================================
    public String logout() {
        try {
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.getExternalContext().invalidateSession();
        } catch (Exception ignored) {
        }

        usuarioLogado = null;
        email = null;
        senha = null;

        return "/publico/escolher-acesso?faces-redirect=true";
    }

    // ============================================================
    // Roles Helpers
    // ============================================================
    public boolean temRole(String role) {
        if (usuarioLogado == null || usuarioLogado.getRole() == null) {
            return false;
        }

        try {
            Role r = Role.valueOf(role);
            return usuarioLogado.getRole() == r;
        } catch (IllegalArgumentException e) {
            return false;
        }
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

    public boolean isLogado() {
        return usuarioLogado != null;
    }

    // ============================================================
    // Getters e Setters
    // ============================================================
    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

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

    public EspacoAtendimento getEspacoAtendimentoDoUsuario() {
        if (usuarioLogado == null) {
            return null;
        }

        // pega a lista de vínculos do usuário logado
        List<AtendenteServicoEspaco> listaVinculos = atendenteServicoEspacoService.listarPorAtendente(usuarioLogado.getId());

        if (listaVinculos.isEmpty()) {
            return null;
        }

        // retorna o primeiro espaço encontrado (ou você pode filtrar por algum critério)
        return listaVinculos.get(0).getEspaco();
    }

}
