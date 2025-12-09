package br.com.sigapar1.controller;

import br.com.sigapar1.entity.Usuario;
import br.com.sigapar1.service.UsuarioService;
import br.com.sigapar1.util.HashUtil;
import br.com.sigapar1.util.JsfUtil;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@ViewScoped
public class PerfilController implements Serializable {

    @Inject
    private LoginController login;

    @Inject
    private UsuarioService service;

    private String senhaAtual;
    private String senhaNova;
    private String senhaConfirmar;

    public void salvar() {

        Usuario u = login.getUsuarioLogado();

        if (u == null) {
            JsfUtil.addError("Você precisa estar logado.");
            return;
        }

        // ------- ATUALIZAÇÃO DE SENHA (CORRIGIDA COM HASH) -------
        if (senhaAtual != null && !senhaAtual.isBlank()) {

            // Verificar senha atual usando HASH
            if (!HashUtil.verificar(senhaAtual, u.getSenha())) {
                JsfUtil.addError("A senha atual está incorreta.");
                return;
            }

            if (senhaNova == null || senhaNova.isBlank()) {
                JsfUtil.addError("Informe a nova senha.");
                return;
            }

            if (!senhaNova.equals(senhaConfirmar)) {
                JsfUtil.addError("A confirmação da senha não corresponde.");
                return;
            }

            // Aplicar hash na nova senha
            u.setSenha(HashUtil.gerarHash(senhaNova));
        }
        // ----------------------------------------------------------

        try {
            service.atualizar(u);
            JsfUtil.addSuccess("Perfil atualizado com sucesso!");

            senhaAtual = "";
            senhaNova = "";
            senhaConfirmar = "";

        } catch (Exception e) {
            JsfUtil.addError("Erro ao atualizar perfil: " + e.getMessage());
        }
    }

    public Usuario getUsuario() {
        return login.getUsuarioLogado();
    }

    public String getSenhaAtual() { return senhaAtual; }
    public void setSenhaAtual(String senhaAtual) { this.senhaAtual = senhaAtual; }

    public String getSenhaNova() { return senhaNova; }
    public void setSenhaNova(String senhaNova) { this.senhaNova = senhaNova; }

    public String getSenhaConfirmar() { return senhaConfirmar; }
    public void setSenhaConfirmar(String senhaConfirmar) { this.senhaConfirmar = senhaConfirmar; }
}
