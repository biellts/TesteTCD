package br.com.sigapar1.service;

import br.com.sigapar1.dao.FilaAtendimentoDAO;
import br.com.sigapar1.entity.Agendamento;
import br.com.sigapar1.entity.FilaAtendimento;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class FilaAtendimentoService {

    @Inject
    private FilaAtendimentoDAO dao;

    public void adicionar(Agendamento a, String prioridade, String senha) {
        FilaAtendimento f = new FilaAtendimento(a, prioridade, senha);
        dao.salvar(f);
    }

    public List<FilaAtendimento> listar() {
        return dao.listarFila();
    }

    public void remover(Long id) {
        dao.remover(dao.buscarPorId(id));
    }
}
