package br.com.sigapar1.dao;

import br.com.sigapar1.entity.Checkin;
import br.com.sigapar1.entity.Agendamento;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class CheckinDao {

    @PersistenceContext
    private EntityManager em;

    public Checkin salvar(Checkin checkin) {
        em.persist(checkin);
        return checkin;
    }

    public Checkin atualizar(Checkin checkin) {
        return em.merge(checkin);
    }

    public Checkin buscarPorId(Long id) {
        return em.find(Checkin.class, id);
    }

    public List<Checkin> listarTodos() {
        return em.createQuery("SELECT c FROM Checkin c", Checkin.class)
                 .getResultList();
    }

    public Checkin buscarPorAgendamento(Agendamento agendamento) {
        try {
            return em.createQuery(
                "SELECT c FROM Checkin c WHERE c.agendamento = :ag",
                Checkin.class
            ).setParameter("ag", agendamento)
             .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
