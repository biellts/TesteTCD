package br.com.sigapar1.dao;

import br.com.sigapar1.entity.DisponibilidadeAtendente;
import jakarta.ejb.Stateless;

@Stateless
public class DisponibilidadeAtendenteDAO extends GenericDAO<DisponibilidadeAtendente> {

    public DisponibilidadeAtendenteDAO() {
        super(DisponibilidadeAtendente.class);
    }

    public void excluir(Long id) {
        DisponibilidadeAtendente d = getEntityManager().find(DisponibilidadeAtendente.class, id);
        if (d != null) {
            getEntityManager().remove(d);
        }
    }
}
