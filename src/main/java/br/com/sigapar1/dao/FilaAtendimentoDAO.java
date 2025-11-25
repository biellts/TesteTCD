package br.com.sigapar1.dao;

import br.com.sigapar1.entity.FilaAtendimento;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class FilaAtendimentoDAO extends GenericDAO<FilaAtendimento> {

    public FilaAtendimentoDAO() { super(FilaAtendimento.class); }

    public List<FilaAtendimento> listarFila() {
        return em.createQuery(
            "SELECT f FROM FilaAtendimento f ORDER BY f.prioridade DESC, f.timestampEntrada ASC",
            FilaAtendimento.class
        ).getResultList();
    }
}
