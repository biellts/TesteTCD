package br.com.sigapar1.service;

import br.com.sigapar1.dao.UsuarioDAO;
import br.com.sigapar1.entity.Usuario;
import br.com.sigapar1.util.HashUtil;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class LoginService {

    @Inject
    private UsuarioDAO usuarioDAO;

    public Usuario autenticar(String email, String senha) {
        Usuario u = usuarioDAO.buscarPorEmail(email);

        if (u != null && HashUtil.verificar(senha, u.getSenha()) && u.isAtivo()) {
            return u;
        }
        return null;
    }
}
