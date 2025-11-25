package br.com.sigapar1.controller;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class AdminController implements Serializable {

    public String irServicos() {
        return "/admin/servicos.xhtml?faces-redirect=true";
    }

    public String irUsuarios() {
        return "/admin/usuarios.xhtml?faces-redirect=true";
    }

    public String irGuiches() {
        return "/admin/guiches.xhtml?faces-redirect=true";
    }
}
