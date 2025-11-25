package br.com.sigapar1.dao;

import br.com.sigapar1.entity.Horario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class HorarioDAO {

    @PersistenceContext
    private EntityManager em;

    public List<Horario> findAll() {
        return em.createQuery("SELECT h FROM Horario h ORDER BY h.hora", Horario.class).getResultList();
    }

    public List<Horario> findDisponiveis() {
        return em.createQuery("SELECT h FROM Horario h WHERE h.disponivel = true ORDER BY h.hora", Horario.class).getResultList();
    }

    public Horario findById(Long id) {
        return em.find(Horario.class, id);
    }

    public void save(Horario h) {
        if (h.getId() == null) em.persist(h);
        else em.merge(h);
    }

    public void delete(Long id) {
        Horario h = findById(id);
        if (h != null) em.remove(h);
    }
}
