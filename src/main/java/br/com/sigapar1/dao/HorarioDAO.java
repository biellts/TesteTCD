package br.com.sigapar1.dao;

import br.com.sigapar1.entity.Horario;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class HorarioDAO extends GenericDAO<Horario> {

    public HorarioDAO() {
        super(Horario.class);
    }

    public List<Horario> findAll() {
        return listarTodos();
    }

    public List<Horario> findDisponiveis() {
        return getEntityManager()
                .createQuery(
                        "SELECT h FROM Horario h WHERE h.disponivel = true ORDER BY h.hora",
                        Horario.class
                )
                .getResultList();
    }

    public Horario findById(Long id) {
        return buscarPorId(id);
    }

    public void save(Horario h) {
        if (h.getId() == null) {
            salvar(h);
        } else {
            atualizar(h);
        }
    }

    public void delete(Long id) {
        Horario h = findById(id);
        if (h != null) {
            remover(h);
        }
    }
}
