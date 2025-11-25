package br.com.sigapar1.dao;

import br.com.sigapar1.entity.Servico;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ServicoDAO extends GenericDAO<Servico> {

    public ServicoDAO() {
        super(Servico.class);
    }

    public Servico findById(Long id) {
        return buscarPorId(id);
    }

    public void create(Servico s) {
        salvar(s);
    }

    public void update(Servico s) {
        atualizar(s);
    }

    public void delete(Servico s) {
        remover(s);
    }

    public java.util.List<Servico> findAll() {
        return listarTodos();
    }
}
