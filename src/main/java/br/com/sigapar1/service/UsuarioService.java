package br.com.sigapar1.service;

import br.com.sigapar1.dao.UsuarioDAO;
import br.com.sigapar1.entity.Role;
import br.com.sigapar1.entity.Usuario;
import br.com.sigapar1.util.HashUtil;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.security.SecureRandom;
import java.util.List;

@RequestScoped
public class UsuarioService {

    @Inject
    private UsuarioDAO dao;

    private static final String CHARS
            = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789@#$%&*";
    private static final SecureRandom RANDOM = new SecureRandom();

    public UsuarioService() {
    }

    // ============================================================
    // AUTENTICAÇÃO
    // ============================================================
    public Usuario autenticar(String email, String senhaDigitada) {
        Usuario usuario = dao.buscarPorEmail(email);

        if (usuario == null) {
            return null;
        }

        if (!usuario.isAtivo()) {
            return null;
        }

        boolean senhaCorreta = HashUtil.verificar(senhaDigitada, usuario.getSenha());
        return senhaCorreta ? usuario : null;
    }

    // ============================================================
    // SALVAR / ATUALIZAR
    // ============================================================
    public void salvar(Usuario u) {

        validar(u);

        if (u.getId() == null) {
            u.setSenha(HashUtil.gerarHash(u.getSenha()));
            dao.salvar(u);
            return;
        }

        if (senhaNaoCriptografada(u.getSenha())) {
            u.setSenha(HashUtil.gerarHash(u.getSenha()));
        }

        dao.atualizar(u);
    }

    public void atualizar(Usuario u) {
        if (senhaNaoCriptografada(u.getSenha())) {
            u.setSenha(HashUtil.gerarHash(u.getSenha()));
        }

        dao.atualizar(u);
    }

    private boolean senhaNaoCriptografada(String senha) {
        return senha != null
                && !senha.isBlank()
                && !senha.startsWith("$2a$");
    }

    // ============================================================
    // CONSULTAS
    // ============================================================
    public List<Usuario> listarTodos() {
        return dao.listarTodos();
    }

    public List<Usuario> listarPorRole(Role role) {
        return dao.listarPorRole(role);
    }

    public List<Usuario> listarTodosPorRole(Role role) {
        return dao.listarTodosPorRole(role);
    }

    public Usuario buscarPorEmail(String email) {
        return dao.buscarPorEmail(email);
    }

    public Usuario buscarPorTokenConfirmacao(String token) {
        return dao.buscarPorTokenConfirmacao(token);
    }

    public Usuario buscarPorId(Long id) {
        return dao.find(id);
    }

    // ============================================================
    // EXCLUSÃO
    // ============================================================
    public void excluir(Long id) {
        dao.excluir(id);
    }

    // ============================================================
    // VALIDAÇÕES
    // ============================================================
    private void validar(Usuario u) {

        Long idAtual = (u.getId() == null ? null : u.getId());

        if (dao.emailJaExiste(u.getEmail(), idAtual)) {
            throw new RuntimeException("Já existe um usuário com este e-mail.");
        }

        if (dao.cpfJaExiste(u.getCpf(), idAtual)) {
            throw new RuntimeException("Já existe um usuário com este CPF.");
        }

        if (u.getId() == null && (u.getSenha() == null || u.getSenha().length() < 6)) {
            throw new RuntimeException("A senha deve ter pelo menos 6 caracteres.");
        }
    }

    // ============================================================
    // SENHA TEMPORÁRIA
    // ============================================================
    public String gerarSenhaTemporaria() {
        int length = 10;
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int idx = RANDOM.nextInt(CHARS.length());
            sb.append(CHARS.charAt(idx));
        }

        return sb.toString();
    }

    // ============================================================
    // CONFIRMAÇÃO DE E-MAIL — VERSÃO CORRETA
    // ============================================================
    public boolean confirmarEmail(String token) {

        Usuario usuario = dao.buscarPorTokenConfirmacao(token);

        if (usuario == null) {
            return false;
        }

        usuario.setAtivo(true);
        usuario.setEmailConfirmationToken(null);

        dao.atualizar(usuario);

        return true;
    }

    // ============================================================
    // HASH MANUAL
    // ============================================================
    public String criptografarSenha(String senha) {
        if (senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("Senha inválida.");
        }
        return HashUtil.gerarHash(senha);
    }

    public Usuario buscarOuCriarRAPIDO(String nome, String cpf, String telefone) {

        // 1. Tenta buscar por CPF
        Usuario existente = dao.buscarPorCpf(cpf);
        if (existente != null) {
            return existente;
        }

        // 2. Criar rápido
        Usuario novo = new Usuario();
        novo.setNome(nome);
        novo.setCpf(cpf);
        novo.setTelefone(telefone);

        // CPF como e-mail fake (evita erro nas validações)
        novo.setEmail(cpf + "@walkin.local");

        // senha temporária
        String senhaTemp = gerarSenhaTemporaria();
        novo.setSenha(HashUtil.gerarHash(senhaTemp));

        // Ativo e role default
        novo.setAtivo(true);
        novo.setRole(Role.ROLE_USER);

        dao.salvar(novo);

        return novo;
    }

    // ============================================================
    // MÉTODO AUXILIAR: Salva sem fazer hash (já foi criptografado)
    // ============================================================
    public void salvarSemHashar(Usuario u) {
        validar(u);
        if (u.getId() == null) {
            dao.salvar(u);
        } else {
            dao.atualizar(u);
        }
    }

}
