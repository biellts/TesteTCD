package br.com.sigapar1.controller;

import br.com.sigapar1.dao.DisponibilidadeAtendenteDAO;
import br.com.sigapar1.dao.UsuarioDAO;
import br.com.sigapar1.entity.DisponibilidadeAtendente;
import br.com.sigapar1.entity.Usuario;
import br.com.sigapar1.service.DisponibilidadeAtendenteService;
import br.com.sigapar1.util.JsfUtil;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class DisponibilidadeAtendenteController implements Serializable {

    @Inject
    private DisponibilidadeAtendenteDAO dao;

    @Inject
    private UsuarioDAO usuarioDAO;

    @Inject
    private DisponibilidadeAtendenteService service;

    private DisponibilidadeAtendente disp;
    private List<DisponibilidadeAtendente> lista;

    public DisponibilidadeAtendenteController() {
    }

    @PostConstruct
    public void init() {
        disp = new DisponibilidadeAtendente();
        lista = dao.listarTodos();
    }

    // =========================
    // MÉTODOS PRINCIPAIS
    // =========================

    public void salvar() {
        try {
            service.salvarDisponibilidade(disp);
            JsfUtil.addInfo("Disponibilidade salva com sucesso!");

            disp = new DisponibilidadeAtendente(); // limpa formulário
            lista = dao.listarTodos(); // atualiza tabela
        } catch (Exception e) {
            JsfUtil.addError("Erro ao salvar disponibilidade: " + e.getMessage());
        }
    }

    public void editar(DisponibilidadeAtendente d) {
        this.disp = d;
    }

    public void excluir(Long id) {
        try {
            dao.excluir(id);
            lista = dao.listarTodos();
            JsfUtil.addInfo("Disponibilidade removida!");
        } catch (Exception e) {
            JsfUtil.addError("Erro ao excluir: " + e.getMessage());
        }
    }

    // =========================
    // LISTAR ATENDENTES (usado no XHTML)
    // =========================
    public List<Usuario> getAtendentes() {
        return usuarioDAO.listarAtendentes();
    }

    // =========================
    // GETTERS E SETTERS
    // =========================

    public DisponibilidadeAtendente getDisp() {
        return disp;
    }

    public void setDisp(DisponibilidadeAtendente disp) {
        this.disp = disp;
    }

    public List<DisponibilidadeAtendente> getLista() {
        return lista;
    }

    public void setLista(List<DisponibilidadeAtendente> lista) {
        this.lista = lista;
    }
}
