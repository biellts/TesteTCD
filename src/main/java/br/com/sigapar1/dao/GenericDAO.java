package br.com.sigapar1.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

public abstract class GenericDAO<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    // Agora o EM é injetado na classe concreta (que é @Stateless)
    @PersistenceContext(unitName = "sigaparPU")
    protected EntityManager em;

    private final Class<T> entityClass;

    protected GenericDAO(Class<T> entityClass) {
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
        TypedQuery<T> query = em.createQuery(jpql, entityClass);
        return query.getResultList();
    }

    // Método opcional pra facilitar queries com WHERE
    protected TypedQuery<T> createQuery(String jpql) {
        return em.createQuery(jpql, entityClass);
    }
}