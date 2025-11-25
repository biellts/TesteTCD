package br.com.sigapar1.dao;

import br.com.sigapar1.entity.DisponibilidadeAtendente;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DisponibilidadeAtendenteDAO extends GenericDAO<DisponibilidadeAtendente> {

    public DisponibilidadeAtendenteDAO() {
        super(DisponibilidadeAtendente.class);
    }
}
