package br.com.sigapar1.controller;

import br.com.sigapar1.service.UsuarioService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@RequestScoped
public class ConfirmarEmailController {

    @Inject
    private UsuarioService usuarioService;

    private String token;

    @PostConstruct
    public void init() {
        token = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap()
                .get("token");
    }

    public String processarConfirmacao() {
        if (token == null || token.isBlank()) {
            addMsg("Token inválido ou não informado.");
            return "/publico/confirmar-email.xhtml?faces-redirect=true";
        }

        try {
            // buscar usuário associado ao token
            br.com.sigapar1.entity.Usuario u = usuarioService.buscarPorTokenConfirmacao(token);
            if (u == null) {
                addMsg("Token inválido ou já utilizado.");
                return "/publico/confirmar-email.xhtml?faces-redirect=true";
            }

            boolean confirmado = usuarioService.confirmarEmail(token);

            if (confirmado) {
                // recarrega usuário atualizado e coloca na sessão como logado
                br.com.sigapar1.entity.Usuario atualizado = usuarioService.buscarPorEmail(u.getEmail());
                FacesContext fc = FacesContext.getCurrentInstance();
                fc.getExternalContext().getSessionMap().put("usuarioLogado", atualizado);
                return "/usuarios/dashboard?faces-redirect=true";
            } else {
                addMsg("Token inválido ou já utilizado.");
                return "/publico/confirmar-email.xhtml?faces-redirect=true";
            }

        } catch (Exception e) {
            addMsg("Ocorreu um erro ao confirmar o e-mail.");
            e.printStackTrace();
            return "/publico/confirmar-email.xhtml?faces-redirect=true";
        }
    }

    private void addMsg(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
    }
}
