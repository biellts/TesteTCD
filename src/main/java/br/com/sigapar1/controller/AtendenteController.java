package br.com.sigapar1.controller;

import br.com.sigapar1.entity.Usuario;
import br.com.sigapar1.service.UsuarioService;
import br.com.sigapar1.service.FilaAtendimentoService;
import br.com.sigapar1.util.JsfUtil;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@SessionScoped
public class AtendenteController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private UsuarioService usuarioService;

    @Inject
    private FilaAtendimentoService filaService;

    @Inject
    private LoginController loginController;

    private boolean modoPausa;
    private String motivoPausa;
    private int fila;

    // =============================================================
    // INIT
    // =============================================================
    @PostConstruct
    public void init() {
        modoPausa = false;
        motivoPausa = null;
        atualizarFila();
    }

    // =============================================================
    // GETTERS
    // =============================================================

    public boolean isModoPausa() {
        return modoPausa;
    }

    public String getMotivoPausa() {
        return motivoPausa;
    }

    public int getFila() {
        return fila;
    }

    // =============================================================
    // SETTERS (OBRIGATÓRIOS PARA JSF!)
    // =============================================================

    public void setModoPausa(boolean modoPausa) {
        this.modoPausa = modoPausa;
    }

    public void setMotivoPausa(String motivoPausa) {
        this.motivoPausa = motivoPausa;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }

    // =============================================================
    // MÉTODOS PRINCIPAIS
    // =============================================================

    public void atualizarFila() {
        try {
            this.fila = filaService.listar().size();
        } catch (Exception e) {
            this.fila = 0;
        }
    }

    public void ficarDisponivel() {
        Usuario u = loginController.getUsuarioLogado();
        u.setStatus("DISPONIVEL");
        u.setMotivoPausa(null);
        usuarioService.salvar(u);

        modoPausa = false;
        atualizarFila();
        JsfUtil.addInfoMessage("Status atualizado: Disponível");
    }

    public void ficarIndisponivel() {
        Usuario u = loginController.getUsuarioLogado();
        u.setStatus("INDISPONIVEL");
        u.setMotivoPausa(null);
        usuarioService.salvar(u);

        modoPausa = false;
        atualizarFila();
        JsfUtil.addInfoMessage("Status atualizado: Indisponível");
    }

    public void abrirPausa() {
        this.modoPausa = true;
    }

    public void cancelarPausa() {
        this.modoPausa = false;
        this.motivoPausa = null;
    }

    public void pausar() {
        Usuario u = loginController.getUsuarioLogado();
        u.setStatus("PAUSA");
        u.setMotivoPausa(motivoPausa);
        usuarioService.salvar(u);

        modoPausa = false;
        atualizarFila();
        JsfUtil.addWarnMessage("Atendente entrou em pausa.");
    }
}
