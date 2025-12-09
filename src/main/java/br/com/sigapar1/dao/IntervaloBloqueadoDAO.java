package br.com.sigapar1.dao;

import br.com.sigapar1.entity.IntervaloBloqueado;
import jakarta.ejb.Stateless;

import java.util.List;

@Stateless
public class IntervaloBloqueadoDAO extends GenericDAO<IntervaloBloqueado> {

    public IntervaloBloqueadoDAO() {
        super(IntervaloBloqueado.class);
    }

    public List<IntervaloBloqueado> listarPorDia(String diaSemana) {
        return getEntityManager()
                .createQuery("SELECT i FROM IntervaloBloqueado i WHERE i.diaSemana = :d", IntervaloBloqueado.class)
                .setParameter("d", diaSemana)
                .getResultList();
    }

    public void excluir(Long id) {
        IntervaloBloqueado i = buscarPorId(id);
        if (i != null) {
            getEntityManager().remove(i);
        }
    }

    public IntervaloBloqueado buscarPorId(Long id) {
        return id == null ? null : getEntityManager().find(IntervaloBloqueado.class, id);
    }

    public void salvar(IntervaloBloqueado intervalo) {
        getEntityManager().persist(intervalo);
    }

}
