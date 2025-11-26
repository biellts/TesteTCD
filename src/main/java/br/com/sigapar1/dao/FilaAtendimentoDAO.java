package br.com.sigapar1.dao;

import br.com.sigapar1.entity.FilaAtendimento;
import jakarta.enterprise.context.Dependent;
import java.util.List;

@Dependent
public class FilaAtendimentoDAO extends GenericDAO<FilaAtendimento> {

    public FilaAtendimentoDAO() {
        super(FilaAtendimento.class);
    }

    public List<FilaAtendimento> listarFila() {
        return getEntityManager()
                .createQuery(
                    "SELECT f FROM FilaAtendimento f ORDER BY f.prioridade DESC, f.timestampEntrada ASC",
                    FilaAtendimento.class
                )
                .getResultList();
    }
}
