package br.com.sigapar1.controller;

import br.com.sigapar1.entity.Role;
import br.com.sigapar1.entity.Usuario;
import br.com.sigapar1.service.UsuarioService;
import br.com.sigapar1.util.JsfUtil;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;   // ← CORRETO!
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class UsuarioController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private UsuarioService service;

    private Usuario usuario;
    private List<Usuario> lista;

    @PostConstruct
    public void init() {
        usuario = new Usuario(); // garante que não é null
        lista = service.listarTodos();
        System.out.println("CDI OK → UsuarioController iniciado");
    }

    // SALVAR (ADMIN)
    public void salvar() {
        limparMascaras();
        service.salvar(usuario);
        JsfUtil.addInfo("Usuário salvo com sucesso!");

        resetarUsuario();
        lista = service.listarTodos();
    }

    // SALVAR PÚBLICO (CRIAÇÃO DE CONTA)
    public String salvarPublico() {
        try {
            limparMascaras();
            usuario.setRole(Role.ROLE_USER);

            service.salvar(usuario);

            JsfUtil.addInfo("Conta criada com sucesso!");

            resetarUsuario();

            return "/publico/sucesso-cadastro.xhtml?faces-redirect=true";

        } catch (Exception e) {
            JsfUtil.addError("Erro ao criar conta: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // RESETAR
    private void resetarUsuario() {
        usuario = new Usuario();
    }

    // BUSCAR
    public Usuario buscarPorEmail(String email) {
        return service.buscarPorEmail(email);
    }

    // EDITAR
    public void editar(Usuario u) {
        usuario = u;
    }

    // EXCLUIR
    public void excluir(Long id) {
        service.excluir(id);
        JsfUtil.addInfo("Usuário removido!");
        lista = service.listarTodos();
    }

    // REMOVER MÁSCARAS
    private void limparMascaras() {
        if (usuario.getCpf() != null)
            usuario.setCpf(usuario.getCpf().replaceAll("\\D", ""));

        if (usuario.getTelefone() != null)
            usuario.setTelefone(usuario.getTelefone().replaceAll("\\D", ""));
    }

    // GETTERS
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
