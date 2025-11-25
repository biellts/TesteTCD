package br.com.sigapar1.service;

import br.com.sigapar1.dao.ServicoDAO;
import br.com.sigapar1.entity.Servico;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class ServicoService {

    @Inject
    private ServicoDAO dao;

    public void salvar(Servico s) {
        if (s.getId() == null) dao.salvar(s);
        else dao.atualizar(s);
    }

    public List<Servico> listarTodos() {
        return dao.listarTodos();
    }

    public void excluir(Long id) {
        dao.remover(dao.buscarPorId(id));
    }
}
