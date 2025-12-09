package br.com.sigapar1.service;

import br.com.sigapar1.dao.RecuperacaoSenhaTokenDAO;
import br.com.sigapar1.dao.UsuarioDAO;
import br.com.sigapar1.entity.RecuperacaoSenhaToken;
import br.com.sigapar1.entity.Usuario;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

@Stateless
public class RecuperacaoSenhaService {

    @Inject
    private UsuarioDAO usuarioDAO;

    @Inject
    private RecuperacaoSenhaTokenDAO tokenDAO;

    @Inject
    private EmailService emailService;

    // ✔ Agora transacional (por ser @Stateless) — remove antigo e cria novo SEM erro!
    public boolean solicitarRecuperacao(String email) {

        Usuario u = usuarioDAO.buscarPorEmail(email);

        if (u == null)
            return false;

        // ✔ Remover token anterior (antes estava falhando!)
        RecuperacaoSenhaToken existente = tokenDAO.buscarPorUsuario(u);
        if (existente != null) {
            tokenDAO.excluir(existente);  // agora funciona porque está dentro do método @Stateless
        }

        // ✔ Criar novo token
        RecuperacaoSenhaToken novo = new RecuperacaoSenhaToken(u);
        tokenDAO.salvar(novo);

        // ✔ Novo link com pasta correta
        String link = "http://localhost:8080/sigapar/publico/resetar-senha.xhtml?token=" + novo.getToken();

        emailService.enviarEmailRecuperacao(u.getEmail(), u.getNome(), link);

        return true;
    }

    public Usuario validarToken(String token) {
        RecuperacaoSenhaToken t = tokenDAO.buscarPorToken(token);
        if (t == null || !t.isValido())
            return null;

        return t.getUsuario();
    }

    public boolean redefinirSenha(String token, String novaSenha) {

        RecuperacaoSenhaToken t = tokenDAO.buscarPorToken(token);

        if (t == null || !t.isValido())
            return false;

        Usuario u = t.getUsuario();
        u.setSenha(novaSenha); // ideal: aplicar hash

        usuarioDAO.atualizar(u);

        // ✔ remover token após uso
        tokenDAO.excluir(t);

        return true;
    }
}
