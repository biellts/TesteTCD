package br.com.sigapar1.service;

import br.com.sigapar1.dao.UsuarioDAO;
import br.com.sigapar1.entity.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class UsuarioService {

    @Inject
    private UsuarioDAO dao;

    // 🔹 Método solicitado
    public Usuario buscarPorEmail(String email) {
        return dao.buscarPorEmail(email);
    }

    public Usuario autenticar(String email, String senha) {
        Usuario u = dao.buscarPorEmail(email);
        if (u != null && u.getSenha() != null && u.getSenha().equals(senha) && u.isAtivo()) {
            return u;
        }
        return null;
    }

    public void salvar(Usuario u) {
        if (u.getId() == null) {
            dao.salvar(u);
        } else {
            dao.atualizar(u);
        }
    }

    public void excluir(Long id) {
        Usuario u = dao.buscarPorId(id);
        if (u != null) {
            dao.remover(u);
        }
    }

    public List<Usuario> listarTodos() {
        return dao.listarTodos();
    }
}
