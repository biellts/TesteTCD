package br.com.sigapar1.controller;

import br.com.sigapar1.service.UsuarioService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("confirmarEmailBean")
@RequestScoped
public class ConfirmarEmailBean {

    @Inject
    private UsuarioService usuarioService;

    private String token;
    private boolean sucesso;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public boolean isSucesso() { return sucesso; }

    // chamado no f:metadata
    public void validarToken() {
        if (token != null && !token.isBlank()) {
            sucesso = usuarioService.confirmarEmail(token);
        } else {
            sucesso = false;
        }
    }
}
