package br.com.sigapar1.dao;

import br.com.sigapar1.entity.Atendimento;
import br.com.sigapar1.entity.Usuario;

import jakarta.enterprise.context.Dependent;
import java.util.List;

@Dependent
public class AtendimentoDAO extends GenericDAO<Atendimento> {

    public AtendimentoDAO() {
        super(Atendimento.class);
    }

    public List<Atendimento> listarPorAtendente(Usuario atendente) {
        return getEntityManager()
                .createQuery(
                    "SELECT a FROM Atendimento a WHERE a.atendente = :u ORDER BY a.inicio DESC",
                    Atendimento.class
                )
                .setParameter("u", atendente)
                .getResultList();
    }
}
