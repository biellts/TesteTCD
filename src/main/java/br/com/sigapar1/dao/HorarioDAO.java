package br.com.sigapar1.dao;

import br.com.sigapar1.entity.Horario;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Stateless
public class HorarioDAO extends GenericDAO<Horario> {

    public HorarioDAO() {
        super(Horario.class);
    }

    // CRUD  --------------------------------------------------
    public void salvar(Horario h) {
        getEntityManager().persist(h);
    }

    public Horario atualizar(Horario h) {
        return getEntityManager().merge(h);
    }

    public void excluir(Long id) {
        Horario h = buscarPorId(id);
        if (h != null) {
            getEntityManager().remove(h);
        }
    }

    public Horario buscarPorId(Long id) {
        return id == null ? null : getEntityManager().find(Horario.class, id);
    }

    // LISTAGEM  ----------------------------------------------
    public List<Horario> listarTodos() {
        return getEntityManager()
                .createQuery(
                        "SELECT h FROM Horario h WHERE h.ativo = true ORDER BY h.diaSemana, h.hora",
                        Horario.class)
                .getResultList();
    }

    public List<Horario> listarPorDiaSemana(String diaSemana) {
        return getEntityManager()
                .createQuery(
                        "SELECT h FROM Horario h WHERE h.diaSemana = :d AND h.ativo = true AND h.disponivel = true ORDER BY h.hora",
                        Horario.class)
                .setParameter("d", diaSemana)
                .getResultList();
    }

    // BUSCAS ESPECÍFICAS -------------------------------------
    public Horario buscarPorDiaEHora(String dia, LocalTime hora) {
        try {
            return getEntityManager()
                    .createQuery(
                            "SELECT h FROM Horario h WHERE h.diaSemana = :d AND h.hora = :h AND h.ativo = true",
                            Horario.class)
                    .setParameter("d", dia)
                    .setParameter("h", hora)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean isFeriado(LocalDate data) {
        Long count = getEntityManager()
                .createQuery("SELECT COUNT(f) FROM Feriado f WHERE f.data = :data", Long.class)
                .setParameter("data", data)
                .getSingleResult();
        return count > 0;
    }

    public boolean existeHorario(String diaSemana, LocalTime hora) {
        Long count = getEntityManager()
                .createQuery(
                        "SELECT COUNT(h) FROM Horario h WHERE h.diaSemana = :d AND h.hora = :h AND h.ativo = true",
                        Long.class)
                .setParameter("d", diaSemana)
                .setParameter("h", hora)
                .getSingleResult();
        return count > 0;
    }

    // BUSCA POR SERVIÇO + DIA --------------------------------
    public List<Horario> buscarPorServicoEDia(Long servicoId, String diaSemana) {
        return getEntityManager()
                .createQuery(
                        "SELECT h FROM Horario h "
                        + "JOIN Vinculo v ON v.horario.id = h.id "
                        + "WHERE v.servico.id = :servicoId "
                        + "AND h.diaSemana = :dia "
                        + "AND h.ativo = true "
                        + "ORDER BY h.hora",
                        Horario.class)
                .setParameter("servicoId", servicoId)
                .setParameter("dia", diaSemana)
                .getResultList();
    }

    // MÉTODO NOVO OBRIGATÓRIO PARA O AGENDAMENTO ----------------
    public List<Horario> listarPorServico(Long idServico) {
        return getEntityManager()
                .createQuery(
                        "SELECT h FROM Horario h "
                        + "JOIN Vinculo v ON v.horario.id = h.id "
                        + "WHERE v.servico.id = :idServico "
                        + "AND h.ativo = true "
                        + "ORDER BY h.diaSemana, h.hora",
                        Horario.class)
                .setParameter("idServico", idServico)
                .getResultList();
    }
    
}
