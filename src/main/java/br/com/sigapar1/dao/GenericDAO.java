package br.com.sigapar1.dao;

import jakarta.enterprise.context.Dependent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

@Dependent
public abstract class GenericDAO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "sigaparPU")
    private EntityManager em;

    private final Class<T> entityClass;

    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected EntityManager getEntityManager() {
        return em;
    }

    public void salvar(T entity) {
        em.persist(entity);
    }

    public T atualizar(T entity) {
        return em.merge(entity);
    }

    public void remover(T entity) {
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }

    public T buscarPorId(Long id) {
        return em.find(entityClass, id);
    }

    public List<T> listarTodos() {
        String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
        return em.createQuery(jpql, entityClass).getResultList();
    }
}
