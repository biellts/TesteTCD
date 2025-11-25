package br.com.sigapar1.dao;

import br.com.sigapar1.entity.Feriado;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FeriadoDAO extends GenericDAO<Feriado> {
    public FeriadoDAO() { super(Feriado.class); }
}
