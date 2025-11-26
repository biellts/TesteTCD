package br.com.sigapar1.dao;

import br.com.sigapar1.entity.Agendamento;
import jakarta.enterprise.context.Dependent;
import jakarta.persistence.NoResultException;
import java.time.LocalDate;
import java.util.List;

@Dependent
public class AgendamentoDAO extends GenericDAO<Agendamento> {

    public AgendamentoDAO() {
        super(Agendamento.class);
    }

    public Agendamento buscarPorProtocolo(String protocolo) {
        try {
            return getEntityManager()
                    .createQuery(
                        "SELECT a FROM Agendamento a WHERE a.protocolo = :p",
                        Agendamento.class
                    )
                    .setParameter("p", protocolo)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public Agendamento buscarPorCpf(String cpf) {
        try {
            return getEntityManager()
                    .createQuery(
                        "SELECT a FROM Agendamento a WHERE a.usuario.cpf = :cpf",
                        Agendamento.class
                    )
                    .setParameter("cpf", cpf)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public Agendamento buscarPorId(Long id) {
        return getEntityManager().find(Agendamento.class, id);
    }

    public List<Agendamento> listarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return getEntityManager()
                .createQuery(
                    "SELECT a FROM Agendamento a WHERE a.data BETWEEN :ini AND :fim ORDER BY a.data, a.horario.hora",
                    Agendamento.class
                )
                .setParameter("ini", inicio)
                .setParameter("fim", fim)
                .getResultList();
    }

    public List<Agendamento> findAll() {
        return listarTodos();
    }

    public Agendamento buscarProximoFila() {
        List<Agendamento> lista = getEntityManager()
                .createQuery(
                    """
                    SELECT a FROM Agendamento a 
                    WHERE a.checkin = true 
                      AND a.chamado = false 
                      AND a.emAtendimento = false 
                      AND a.finalizado = false
                    ORDER BY a.data, a.horario.hora
                    """,
                    Agendamento.class
                )
                .getResultList();

        return lista.isEmpty() ? null : lista.get(0);
    }

    public Agendamento buscarUltimoChamado() {
        List<Agendamento> lista = getEntityManager()
                .createQuery(
                    "SELECT a FROM Agendamento a WHERE a.chamado = true ORDER BY a.data DESC, a.horario.hora DESC",
                    Agendamento.class
                )
                .setMaxResults(1)
                .getResultList();

        return lista.isEmpty() ? null : lista.get(0);
    }

    public List<?> listarHorarios(LocalDate data) {
        return getEntityManager()
                .createQuery("SELECT h FROM Horario h ORDER BY h.hora")
                .getResultList();
    }

    public void save(Agendamento a) {
        if (a.getId() == null) {
            getEntityManager().persist(a);
        } else {
            getEntityManager().merge(a);
        }
    }
}
