package br.com.sigapar1.service;

import br.com.sigapar1.dao.GuicheDAO;
import br.com.sigapar1.entity.Guiche;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class GuicheService {

    @Inject
    private GuicheDAO dao;

    public void salvar(Guiche g) {
        if (g.getId() == null) dao.salvar(g);
        else dao.atualizar(g);
    }

    public List<Guiche> listarTodos() {
        return dao.listarTodos();
    }

    public void excluir(Long id) {
        dao.remover(dao.buscarPorId(id));
    }
}
