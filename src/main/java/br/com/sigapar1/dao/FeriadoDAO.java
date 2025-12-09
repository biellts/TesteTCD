package br.com.sigapar1.dao;

import br.com.sigapar1.entity.Feriado;
import jakarta.ejb.Stateless;

import java.time.LocalDate;
import java.util.List;

@Stateless
public class FeriadoDAO extends GenericDAO<Feriado> {

    public FeriadoDAO() {
        super(Feriado.class);
    }

    public void salvar(Feriado f) {
        getEntityManager().persist(f);
    }

    public Feriado atualizar(Feriado f) {
        return getEntityManager().merge(f);
    }

    public void excluir(Long id) {
        Feriado f = buscarPorId(id);
        if (f != null) {
            getEntityManager().remove(f);
        }
    }

    public Feriado buscarPorId(Long id) {
        return id == null ? null : getEntityManager().find(Feriado.class, id);
    }

    public List<Feriado> listarTodos() {
        return getEntityManager()
                .createQuery("SELECT f FROM Feriado f ORDER BY f.data DESC", Feriado.class)
                .getResultList();
    }

    public boolean isFeriado(LocalDate data) {
        Long count = getEntityManager()
                .createQuery("SELECT COUNT(f) FROM Feriado f WHERE f.data = :data", Long.class)
                .setParameter("data", data)
                .getSingleResult();
        return count > 0;
    }
}
