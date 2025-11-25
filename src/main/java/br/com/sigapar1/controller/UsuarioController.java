package br.com.sigapar1.controller;

import br.com.sigapar1.entity.Usuario;
import br.com.sigapar1.service.UsuarioService;
import br.com.sigapar1.util.JsfUtil;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class UsuarioController implements Serializable {

    @Inject
    private UsuarioService service;

    private Usuario usuario = new Usuario();
    private List<Usuario> lista;

    @PostConstruct
    public void init() {
        lista = service.listarTodos();
    }

    public void salvar() {
        service.salvar(usuario);
        JsfUtil.addInfo("Usuário salvo!");
        usuario = new Usuario();
        lista = service.listarTodos();
    }

    public void editar(Usuario u) {
        usuario = u;
    }

    public void excluir(Long id) {
        service.excluir(id);
        JsfUtil.addInfo("Usuário removido!");
        lista = service.listarTodos();
    }

    // GETTERS e SETTERS obrigatórios para o XHTML

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Usuario> getLista() {
        return lista;
    }

    public void setLista(List<Usuario> lista) {
        this.lista = lista;
    }
}
