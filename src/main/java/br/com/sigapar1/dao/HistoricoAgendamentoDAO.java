package br.com.sigapar1.dao;

import br.com.sigapar1.entity.HistoricoAgendamento;
import jakarta.ejb.Stateless;

@Stateless
public class HistoricoAgendamentoDAO extends GenericDAO<HistoricoAgendamento> {

    public HistoricoAgendamentoDAO() {
        super(HistoricoAgendamento.class);
    }

    public void salvar(HistoricoAgendamento h) {
        getEntityManager().persist(h);
    }
}
