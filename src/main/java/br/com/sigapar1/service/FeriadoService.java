package br.com.sigapar1.service;

import br.com.sigapar1.dao.FeriadoDAO;
import br.com.sigapar1.entity.Feriado;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class FeriadoService {

    @Inject
    private FeriadoDAO dao;

    public void salvar(Feriado f) {
        if (f.getId() == null) dao.salvar(f);
        else dao.atualizar(f);
    }

    public void remover(Long id) {
        Feriado f = dao.buscarPorId(id);
        if (f != null) dao.remover(f);
    }

    public List<Feriado> listar() {
        return dao.listarTodos();
    }
}
