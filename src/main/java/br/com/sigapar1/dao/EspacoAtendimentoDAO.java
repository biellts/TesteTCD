package br.com.sigapar1.dao;

import br.com.sigapar1.entity.EspacoAtendimento;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EspacoAtendimentoDAO extends GenericDAO<EspacoAtendimento> {

    public EspacoAtendimentoDAO() {
        super(EspacoAtendimento.class);
    }
}
