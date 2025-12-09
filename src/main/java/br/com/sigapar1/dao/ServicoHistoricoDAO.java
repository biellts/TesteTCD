package br.com.sigapar1.dao;

import br.com.sigapar1.entity.ServicoHistorico;
import jakarta.ejb.Stateless;
import java.util.List;

@Stateless
public class ServicoHistoricoDAO extends GenericDAO<ServicoHistorico> {

    public ServicoHistoricoDAO() {
        super(ServicoHistorico.class);
    }

    /**
     * Lista o histórico ordenado pela data (mais recente primeiro)
     */
    public List<ServicoHistorico> listarPorServico(Long servicoId) {
        return createQuery(
                "SELECT h FROM ServicoHistorico h " +
                "WHERE h.servico.id = :id " +
                "ORDER BY h.dataAlteracao DESC")
                .setParameter("id", servicoId)
                .getResultList();
    }

    /**
     * Lista todos os registros do serviço, sem ordenação especial
     */
    public List<ServicoHistorico> listarTudoPorServico(Long servicoId) {
        return createQuery(
                "SELECT h FROM ServicoHistorico h " +
                "WHERE h.servico.id = :id")
                .setParameter("id", servicoId)
                .getResultList();
    }
}
