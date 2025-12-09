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

    public void processarConfirmacao() {
        if (token == null || token.isBlank()) {
            addMsg("Token inválido ou não informado.");
            return;
        }

        try {
            boolean confirmado = usuarioService.confirmarEmail(token);

            if (confirmado) {
                addMsg("Email confirmado com sucesso! Sua conta está ativa.");
            } else {
                addMsg("Token inválido ou já utilizado.");
            }

        } catch (Exception e) {
            addMsg("Ocorreu um erro ao confirmar o e-mail.");
            e.printStackTrace();
        }
    }

    private void addMsg(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
    }
}
