package br.com.sigapar1.dao;

import br.com.sigapar1.entity.EspacoAtendimento;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Stateless
public class EspacoAtendimentoDAO implements Serializable {

    @PersistenceContext
    private EntityManager em;

    public void salvar(EspacoAtendimento e) {
        em.persist(e);
    }

    public void atualizar(EspacoAtendimento e) {
        em.merge(e);
    }

    /**
     * Buscar por ID com FETCH JOIN (evita LazyInitializationException)
     */
    public EspacoAtendimento buscarPorId(Long id) {
        if (id == null) return null;

        try {
            return em.createQuery(
                    "SELECT e FROM EspacoAtendimento e " +
                    "LEFT JOIN FETCH e.servicos " +
                    "WHERE e.id = :id",
                    EspacoAtendimento.class
            )
            .setParameter("id", id)
            .getSingleResult();

        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Listar tudo com serviços carregados (para telas)
     */
    public List<EspacoAtendimento> listarTodos() {
        try {
            return em.createQuery(
                    "SELECT DISTINCT e FROM EspacoAtendimento e " +
                    "LEFT JOIN FETCH e.servicos " +
                    "ORDER BY e.descricao ASC",
                    EspacoAtendimento.class
            ).getResultList();

        } catch (Exception e) {
            System.err.println("Erro ao listar espaços de atendimento: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<EspacoAtendimento> findAll() {
        return listarTodos();
    }

    /**
     * Verificar se existe agendamento vinculado
     */
    public boolean possuiAgendamentosVinculados(Long idEspaco) {
        if (idEspaco == null) return false;

        try {
            Long count = em.createQuery(
                    "SELECT COUNT(a) FROM Agendamento a WHERE a.espaco.id = :idEspaco",
                    Long.class
            )
            .setParameter("idEspaco", idEspaco)
            .getSingleResult();

            return count != null && count > 0;

        } catch (NoResultException e) {
            return false;

        } catch (Exception e) {
            System.err.println("Erro ao verificar agendamentos vinculados: " + e.getMessage());
            return false;
        }
    }

    /**
     * Remoção física
     */
    public void excluir(Long id) {
        EspacoAtendimento e = buscarPorId(id);
        if (e != null) {
            em.remove(em.contains(e) ? e : em.merge(e));
        }
    }
}
