package br.com.sigapar1.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

public abstract class GenericDAO<T> {

    @PersistenceContext
    protected EntityManager em;

    private Class<T> entityClass;

    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
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
        return em.createQuery(jpql, entityClass)
                 .getResultList();
    }
}
