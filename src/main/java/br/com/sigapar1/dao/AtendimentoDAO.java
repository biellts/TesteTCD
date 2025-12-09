package br.com.sigapar1.dao;

import br.com.sigapar1.entity.Atendimento;
import br.com.sigapar1.entity.Usuario;
import br.com.sigapar1.entity.Agendamento;
import br.com.sigapar1.entity.StatusAgendamento;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class AtendimentoDAO extends GenericDAO<Atendimento> {

    public AtendimentoDAO() {
        super(Atendimento.class);
    }

    // ===========================================
    // LISTAR TODOS OS ATENDIMENTOS
    // ===========================================
    public List<Atendimento> listarTodos() {
        return getEntityManager()
                .createQuery("SELECT a FROM Atendimento a ORDER BY a.inicio DESC", Atendimento.class)
                .getResultList();
    }

    // ===========================================
    // LISTAR POR ATENDENTE
    // ===========================================
    public List<Atendimento> listarPorAtendente(Usuario atendente) {
        return getEntityManager()
                .createQuery("SELECT a FROM Atendimento a WHERE a.atendente = :u ORDER BY a.inicio DESC", Atendimento.class)
                .setParameter("u", atendente)
                .getResultList();
    }

    // ===========================================
    // LISTAR ATENDIMENTOS DO DIA
    // ===========================================
    public List<Atendimento> listarDoDia(LocalDate data) {
        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.plusDays(1).atStartOfDay();
        return getEntityManager()
                .createQuery("SELECT a FROM Atendimento a WHERE a.inicio >= :inicio AND a.inicio < :fim ORDER BY a.inicio", Atendimento.class)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getResultList();
    }

    // ===========================================
    // BUSCAR ATENDIMENTO EM ANDAMENTO POR ID DO AGENDAMENTO
    // ===========================================
    public Atendimento buscarEmAndamentoPorIdAgendamento(Long idAgendamento) {
        try {
            return getEntityManager()
                    .createQuery("SELECT a FROM Atendimento a WHERE a.agendamento.id = :id AND a.fim IS NULL", Atendimento.class)
                    .setParameter("id", idAgendamento)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    // ===========================================
    // CONTAR ATENDIMENTOS DO DIA POR ATENDENTE
    // ===========================================
    public long contarDoDiaPorAtendente(Usuario atendente, LocalDate data) {
        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.plusDays(1).atStartOfDay();
        return getEntityManager()
                .createQuery("SELECT COUNT(a) FROM Atendimento a WHERE a.atendente = :u AND a.inicio >= :inicio AND a.inicio < :fim", Long.class)
                .setParameter("u", atendente)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getSingleResult();
    }

    // ===========================================
    // LISTAR ATENDIMENTOS FINALIZADOS DO DIA
    // ===========================================
    public List<Atendimento> listarFinalizadosDoDia(LocalDate data) {
        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.plusDays(1).atStartOfDay();
        return getEntityManager()
                .createQuery("SELECT a FROM Atendimento a WHERE a.fim >= :inicio AND a.fim < :fim ORDER BY a.fim DESC", Atendimento.class)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getResultList();
    }

    // ===========================================
    // CHAMAR PRÓXIMO AGENDAMENTO COMPATÍVEL POR ESPAÇO
    // ===========================================
    public Agendamento chamarProximoPorEspaco(List<Long> servicosIds, Long espacoId) {
        try {
            return getEntityManager()
                    .createQuery(
                            "SELECT a FROM Agendamento a " +
                            "WHERE a.servico.id IN :servicos " +
                            "AND a.espaco.id = :espacoId " +
                            "AND a.status = :status " +
                            "ORDER BY a.prioridade DESC, a.dataHora ASC",
                            Agendamento.class)
                    .setParameter("servicos", servicosIds)
                    .setParameter("espacoId", espacoId)
                    .setParameter("status", StatusAgendamento.EM_FILA)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
