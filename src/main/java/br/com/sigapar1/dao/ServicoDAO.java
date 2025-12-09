package br.com.sigapar1.dao;

import br.com.sigapar1.entity.Servico;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import java.util.List;

@Stateless
public class ServicoDAO extends GenericDAO<Servico> {

    public ServicoDAO() {
        super(Servico.class);
    }

    public Servico buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) return null;
        try {
            return createQuery("SELECT s FROM Servico s WHERE LOWER(s.nome) = LOWER(:nome)")
                    .setParameter("nome", nome.trim())
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Servico> findAll() {
        return listarTodos();
    }

    public List<Servico> listarAtivos() {
        return createQuery("SELECT s FROM Servico s WHERE s.ativo = true ORDER BY s.nome")
                .getResultList();
    }
}