package br.com.sigapar1.dao;

import br.com.sigapar1.entity.Guiche;
import jakarta.ejb.Stateless;

import java.util.List;

@Stateless
public class GuicheDAO extends GenericDAO<Guiche> {

    public GuicheDAO() {
        super(Guiche.class);
    }

    public List<Guiche> listarAtivos() {
        return getEntityManager()
                .createQuery("SELECT g FROM Guiche g WHERE g.ativo = true ORDER BY g.numero",
                        Guiche.class)
                .getResultList();
    }
}
