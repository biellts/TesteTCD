package br.com.sigapar1.service;

import br.com.sigapar1.dao.TokenResetSenhaDAO;
import br.com.sigapar1.entity.TokenResetSenha;
import br.com.sigapar1.entity.Usuario;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.UUID;

@Stateless
public class TokenResetSenhaService {

    @Inject
    private TokenResetSenhaDAO dao;

    public TokenResetSenha gerarToken(Usuario usuario) {
        String token = UUID.randomUUID().toString();

        TokenResetSenha t = new TokenResetSenha(
                token,
                usuario,
                LocalDateTime.now().plusMinutes(30) // expira em 30 minutos
        );

        dao.salvar(t);
        return t;
    }

    public TokenResetSenha validarToken(String token) {
        TokenResetSenha t = dao.buscarPorToken(token);

        if (t == null) return null;
        if (t.isUsado()) return null;
        if (t.getDataExpiracao().isBefore(LocalDateTime.now())) return null;

        return t;
    }

    public void marcarComoUsado(TokenResetSenha token) {
        token.setUsado(true);
        dao.atualizar(token);
    }
}
