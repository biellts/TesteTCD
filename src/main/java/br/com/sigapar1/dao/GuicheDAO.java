package br.com.sigapar1.dao;

import br.com.sigapar1.entity.Guiche;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GuicheDAO extends GenericDAO<Guiche> {

    public GuicheDAO() {
        super(Guiche.class);
    }
}
