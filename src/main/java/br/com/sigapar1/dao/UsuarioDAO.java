package br.com.sigapar1.dao;

import br.com.sigapar1.entity.Usuario;
import jakarta.enterprise.context.Dependent;
import jakarta.persistence.NoResultException;

@Dependent
public class UsuarioDAO extends GenericDAO<Usuario> {

    public UsuarioDAO() {
        super(Usuario.class);
    }

    public Usuario buscarPorEmail(String email) {
        try {
            return getEntityManager()
                .createQuery("SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class)
                .setParameter("email", email)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Usuario buscarPorCpf(String cpf) {
        try {
            return getEntityManager()
                .createQuery("SELECT u FROM Usuario u WHERE u.cpf = :cpf", Usuario.class)
                .setParameter("cpf", cpf)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void create(Usuario u) {
        salvar(u);
    }

    public void update(Usuario u) {
        atualizar(u);
    }

    public Usuario findById(Long id) {
        return buscarPorId(id);
    }

    public void delete(Usuario u) {
        remover(u);
    }

    public java.util.List<Usuario> findAll() {
        return listarTodos();
    }
}
