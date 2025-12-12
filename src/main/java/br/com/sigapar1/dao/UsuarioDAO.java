package br.com.sigapar1.dao;

import br.com.sigapar1.entity.Role;
import br.com.sigapar1.entity.Usuario;

import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Stateless
public class UsuarioDAO extends GenericDAO<Usuario> {

    public UsuarioDAO() {
        super(Usuario.class);
    }

    // ============================================================
    // BUSCA POR ID
    // ============================================================
    public Usuario find(Long id) {
        if (id == null) {
            return null;
        }
        return getEntityManager().find(Usuario.class, id);
    }

    // ============================================================
    // BUSCA POR EMAIL
    // ============================================================
    public Usuario buscarPorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        try {
            return getEntityManager()
                    .createQuery(
                            "SELECT u FROM Usuario u WHERE LOWER(u.email) = LOWER(:email)",
                            Usuario.class)
                    .setParameter("email", email.trim())
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    // ============================================================
    // BUSCA POR CPF
    // ============================================================
    public Usuario buscarPorCpf(String cpf) {

        if (cpf == null || cpf.trim().isEmpty()) {
            return null;
        }

        String limpo = cpf.replaceAll("\\D", "");

        if (limpo.length() != 11) {
            return null;
        }

        try {
            return getEntityManager()
                    .createQuery(
                            "SELECT u FROM Usuario u WHERE u.cpf = :cpf",
                            Usuario.class)
                    .setParameter("cpf", limpo)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    // ============================================================
    // LISTAGENS
    // ============================================================
    public List<Usuario> listarTodos() {
        return getEntityManager()
                .createQuery(
                        "SELECT u FROM Usuario u ORDER BY u.nome ASC",
                        Usuario.class)
                .getResultList();
    }

    public List<Usuario> listarPorRole(Role role) {
        return getEntityManager()
                .createQuery(
                        "SELECT u FROM Usuario u "
                        + "WHERE u.role = :role AND u.ativo = true "
                        + "ORDER BY u.nome ASC",
                        Usuario.class)
                .setParameter("role", role)
                .getResultList();
    }

    public List<Usuario> listarTodosPorRole(Role role) {
        return getEntityManager()
                .createQuery(
                        "SELECT u FROM Usuario u "
                        + "WHERE u.role = :role "
                        + "ORDER BY u.nome ASC",
                        Usuario.class)
                .setParameter("role", role)
                .getResultList();
    }

    public List<Usuario> listarAtendentes() {
        return listarPorRole(Role.ROLE_ATTENDANT);
    }

    public List<Usuario> listarAtivos() {
        return getEntityManager()
                .createQuery(
                        "SELECT u FROM Usuario u WHERE u.ativo = true ORDER BY u.nome ASC",
                        Usuario.class)
                .getResultList();
    }

    // ============================================================
    // BUSCA POR TOKEN DE CONFIRMAÇÃO
    // ============================================================
    public Usuario buscarPorTokenConfirmacao(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        try {
            return getEntityManager()
                    .createQuery(
                            "SELECT u FROM Usuario u WHERE u.emailConfirmationToken = :token",
                            Usuario.class)
                    .setParameter("token", token)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    // ============================================================
    // EXCLUSÃO
    // ============================================================
    public void excluir(Long id) {
        Usuario u = find(id);
        if (u != null) {

            // Remove serviços vinculados
            if (u.getServicosAtendente() != null) {
                u.getServicosAtendente().clear();
            }

            // Desvincular guiche e espaço, se necessário
            u.setGuiche(null);
            u.setEspacoAtendimento(null);

            // Merge para garantir que a entidade está gerenciada
            u = getEntityManager().merge(u);

            // Remove usuário
            getEntityManager().remove(u);
        }
    }

    // ============================================================
    // VALIDAÇÕES - EMAIL / CPF EXISTENTES
    // ============================================================
    public boolean emailJaExiste(String email, Long excluirId) {

        if (email == null || email.isBlank()) {
            return false;
        }

        String jpql = "SELECT COUNT(u) FROM Usuario u WHERE LOWER(u.email) = LOWER(:email)";

        if (excluirId != null) {
            jpql += " AND u.id != :id";
        }

        TypedQuery<Long> query
                = getEntityManager().createQuery(jpql, Long.class);

        query.setParameter("email", email.trim());

        if (excluirId != null) {
            query.setParameter("id", excluirId);
        }

        return query.getSingleResult() > 0;
    }

    public boolean cpfJaExiste(String cpf, Long excluirId) {

        if (cpf == null || cpf.isBlank()) {
            return false;
        }

        String limpo = cpf.replaceAll("\\D", "");

        String jpql = "SELECT COUNT(u) FROM Usuario u WHERE u.cpf = :cpf";

        if (excluirId != null) {
            jpql += " AND u.id != :id";
        }

        TypedQuery<Long> query
                = getEntityManager().createQuery(jpql, Long.class);

        query.setParameter("cpf", limpo);

        if (excluirId != null) {
            query.setParameter("id", excluirId);
        }

        return query.getSingleResult() > 0;
    }

    public Usuario buscarPorToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        try {
            return getEntityManager()
                    .createQuery(
                            "SELECT u FROM Usuario u WHERE u.emailConfirmationToken = :token",
                            Usuario.class)
                    .setParameter("token", token)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    public Usuario atualizar(Usuario u) {
        return em.merge(u);
    }

    public Usuario buscarPorId(Long id) {
        return em.find(Usuario.class, id);
    }

}
