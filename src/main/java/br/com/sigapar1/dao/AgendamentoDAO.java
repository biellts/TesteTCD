package br.com.sigapar1.dao;

import br.com.sigapar1.entity.Agendamento;
import br.com.sigapar1.entity.Horario;
import br.com.sigapar1.entity.Usuario;
import br.com.sigapar1.entity.StatusAgendamento;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Stateless
public class AgendamentoDAO extends GenericDAO<Agendamento> {

    public AgendamentoDAO() {
        super(Agendamento.class);
    }

    // ==========================================================
    // PAINEL PÚBLICO (RF021)
    // ==========================================================
    public Agendamento buscarUltimoChamado() {
        try {
            return em.createQuery(
                    "SELECT a FROM Agendamento a WHERE a.status = :status ORDER BY a.horaChamado DESC",
                    Agendamento.class)
                    .setParameter("status", StatusAgendamento.EM_ATENDIMENTO)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Agendamento> listarUltimasChamadas(int limite) {
        try {
            return em.createQuery(
                    "SELECT a FROM Agendamento a WHERE a.status = :status ORDER BY a.horaChamado DESC",
                    Agendamento.class)
                    .setParameter("status", StatusAgendamento.EM_ATENDIMENTO)
                    .setMaxResults(limite)
                    .getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public int contarFila() {
        try {
            Long count = em.createQuery(
                    "SELECT COUNT(a) FROM Agendamento a WHERE a.status = :status",
                    Long.class)
                    .setParameter("status", StatusAgendamento.EM_FILA)
                    .getSingleResult();
            return count.intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    // ==========================================================
    // CHAMAR PRÓXIMO COMPATÍVEL (RF017)
    // ==========================================================
    public Agendamento chamarProximoCompativel(List<Long> servicosIds, Long espacoId) {
        if (servicosIds == null || servicosIds.isEmpty()) {
            return null;
        }

        try {
            return em.createQuery(
                    "SELECT a FROM Agendamento a "
                    + "WHERE a.status = :status "
                    + "AND a.servico.id IN :servicos "
                    + "ORDER BY a.horaCheckin ASC",
                    Agendamento.class)
                    .setParameter("status", StatusAgendamento.EM_FILA)
                    .setParameter("servicos", servicosIds)
                    .setMaxResults(1)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    // ==========================================================
    // CONFLITO
    // ==========================================================
    public boolean existeConflitoPorServicoEHorario(Long idServico, LocalDate data, Horario h) {
        try {
            Long count = em.createQuery(
                    "SELECT COUNT(a) FROM Agendamento a "
                    + "WHERE a.servico.id = :idServico "
                    + "AND a.data = :data "
                    + "AND a.horario.id = :idHorario "
                    + "AND a.status IN (:s1, :s2)",
                    Long.class)
                    .setParameter("idServico", idServico)
                    .setParameter("data", data)
                    .setParameter("idHorario", h.getId())
                    .setParameter("s1", StatusAgendamento.AGENDADO)
                    .setParameter("s2", StatusAgendamento.REMARCADO)
                    .getSingleResult();

            return count != null && count > 0;

        } catch (Exception e) {
            return true;
        }
    }

    // ==========================================================
    // MÉTODOS PADRÃO COM JOIN FETCH
    // ==========================================================
    public Agendamento buscarPorId(Long id) {
        if (id == null) {
            return null;
        }

        try {
            return em.createQuery(
                    "SELECT a FROM Agendamento a "
                    + "LEFT JOIN FETCH a.servico "
                    + "LEFT JOIN FETCH a.horario "
                    + "LEFT JOIN FETCH a.usuario "
                    + "LEFT JOIN FETCH a.atendente "
                    + "LEFT JOIN FETCH a.espaco "
                    + "WHERE a.id = :id",
                    Agendamento.class)
                    .setParameter("id", id)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Agendamento> findAll() {
        return em.createQuery(
                "SELECT a FROM Agendamento a ORDER BY a.dataHora DESC",
                Agendamento.class)
                .getResultList();
    }

    public List<Agendamento> listarPorUsuario(Usuario u) {
        return em.createQuery(
                "SELECT a FROM Agendamento a "
                + "LEFT JOIN FETCH a.servico "
                + "LEFT JOIN FETCH a.horario "
                + "LEFT JOIN FETCH a.usuario "
                + "LEFT JOIN FETCH a.atendente "
                + "LEFT JOIN FETCH a.espaco "
                + "WHERE a.usuario = :u "
                + "ORDER BY a.dataHora ASC",
                Agendamento.class)
                .setParameter("u", u)
                .getResultList();
    }

    public Agendamento buscarProximo(Usuario u) {
        try {
            return em.createQuery(
                    "SELECT a FROM Agendamento a "
                    + "LEFT JOIN FETCH a.servico "
                    + "LEFT JOIN FETCH a.horario "
                    + "LEFT JOIN FETCH a.usuario "
                    + "LEFT JOIN FETCH a.atendente "
                    + "LEFT JOIN FETCH a.espaco "
                    + "WHERE a.usuario = :u "
                    + "AND a.status = :status "
                    + "AND a.dataHora >= CURRENT_TIMESTAMP "
                    + "ORDER BY a.dataHora ASC",
                    Agendamento.class)
                    .setParameter("u", u)
                    .setParameter("status", StatusAgendamento.AGENDADO)
                    .setMaxResults(1)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    public Agendamento buscarPorCpf(String cpf) {
        try {
            return em.createQuery(
                    "SELECT a FROM Agendamento a "
                    + "JOIN FETCH a.servico "
                    + "JOIN FETCH a.horario "
                    + "JOIN FETCH a.usuario u "
                    + "LEFT JOIN FETCH a.atendente "
                    + "LEFT JOIN FETCH a.espaco "
                    + "WHERE u.cpf = :cpf "
                    + "AND a.data = CURRENT_DATE "
                    + "AND a.status NOT IN (:c1, :c2)",
                    Agendamento.class)
                    .setParameter("cpf", cpf)
                    .setParameter("c1", StatusAgendamento.CANCELADO)
                    .setParameter("c2", StatusAgendamento.CONCLUIDO)
                    .setMaxResults(1)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    public Agendamento buscarPorProtocolo(String protocolo) {
        try {
            return em.createQuery(
                    "SELECT a FROM Agendamento a "
                    + "LEFT JOIN FETCH a.servico "
                    + "LEFT JOIN FETCH a.horario "
                    + "LEFT JOIN FETCH a.usuario "
                    + "LEFT JOIN FETCH a.atendente "
                    + "LEFT JOIN FETCH a.espaco "
                    + "WHERE UPPER(a.protocolo) = :p",
                    Agendamento.class)
                    .setParameter("p", protocolo.toUpperCase())
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    public Agendamento buscarPorNomeParcial(String nomeParcial, LocalDate data) {
        try {
            String nome = "%" + nomeParcial.toUpperCase() + "%";

            return em.createQuery(
                    "SELECT a FROM Agendamento a "
                    + "JOIN FETCH a.usuario u "
                    + "JOIN FETCH a.horario "
                    + "JOIN FETCH a.servico "
                    + "WHERE UPPER(u.nome) LIKE :nome "
                    + "AND a.data = :data "
                    + "AND a.status = :status "
                    + "ORDER BY a.dataHora",
                    Agendamento.class)
                    .setParameter("nome", nome)
                    .setParameter("data", data)
                    .setParameter("status", StatusAgendamento.AGENDADO)
                    .setMaxResults(1)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    public Agendamento buscarProximoFila() {
        try {
            return em.createQuery(
                    "SELECT a FROM Agendamento a WHERE a.status = :status ORDER BY a.horaCheckin ASC",
                    Agendamento.class)
                    .setParameter("status", StatusAgendamento.EM_FILA)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public long contarPorData(LocalDate data) {
        return em.createQuery(
                "SELECT COUNT(a) FROM Agendamento a WHERE a.data = :data",
                Long.class)
                .setParameter("data", data)
                .getSingleResult();
    }

    public boolean existeConflito(Agendamento teste) {
        if (teste == null
                || teste.getServico() == null
                || teste.getHorario() == null
                || teste.getData() == null) {
            return false;
        }

        try {
            Long count = em.createQuery(
                    "SELECT COUNT(a) FROM Agendamento a "
                    + "WHERE a.servico.id = :idServico "
                    + "AND a.data = :data "
                    + "AND a.horario.id = :idHorario "
                    + "AND a.status IN (:s1, :s2)",
                    Long.class)
                    .setParameter("idServico", teste.getServico().getId())
                    .setParameter("data", teste.getData())
                    .setParameter("idHorario", teste.getHorario().getId())
                    .setParameter("s1", StatusAgendamento.AGENDADO)
                    .setParameter("s2", StatusAgendamento.REMARCADO)
                    .getSingleResult();

            return count != null && count > 0;

        } catch (Exception e) {
            return true;
        }
    }

    public List<Agendamento> buscarPorCpfOuProtocolo(String valor) {
        boolean ehCpf = valor.matches("\\d{11}");

        if (ehCpf) {
            return em.createQuery(
                    "SELECT a FROM Agendamento a "
                    + "LEFT JOIN FETCH a.usuario "
                    + "LEFT JOIN FETCH a.servico "
                    + "LEFT JOIN FETCH a.horario "
                    + "LEFT JOIN FETCH a.atendente "
                    + "LEFT JOIN FETCH a.espaco "
                    + "WHERE a.usuario.cpf = :cpf "
                    + "ORDER BY a.data DESC",
                    Agendamento.class)
                    .setParameter("cpf", valor)
                    .getResultList();
        } else {
            return em.createQuery(
                    "SELECT a FROM Agendamento a "
                    + "LEFT JOIN FETCH a.usuario "
                    + "LEFT JOIN FETCH a.servico "
                    + "LEFT JOIN FETCH a.horario "
                    + "LEFT JOIN FETCH a.atendente "
                    + "LEFT JOIN FETCH a.espaco "
                    + "WHERE a.protocolo = :p",
                    Agendamento.class)
                    .setParameter("p", valor)
                    .getResultList();
        }
    }

    public List<Agendamento> buscarPorData(LocalDate data) {
        return em.createQuery(
                "SELECT a FROM Agendamento a "
                + "LEFT JOIN FETCH a.usuario "
                + "LEFT JOIN FETCH a.servico "
                + "LEFT JOIN FETCH a.horario "
                + "LEFT JOIN FETCH a.atendente "
                + "LEFT JOIN FETCH a.espaco "
                + "WHERE a.data = :data "
                + "ORDER BY a.horario.hora ASC",
                Agendamento.class)
                .setParameter("data", data)
                .getResultList();
    }

    public List<Agendamento> buscarPorTermo(String termo, LocalDate data) {
        String t = "%" + termo.toLowerCase() + "%";

        return em.createQuery(
                "SELECT a FROM Agendamento a "
                + "JOIN a.usuario u "
                + "WHERE a.data = :data "
                + "AND (LOWER(u.nome) LIKE :t "
                + "     OR LOWER(u.cpf) LIKE :t "
                + "     OR LOWER(a.protocolo) LIKE :t) "
                + "ORDER BY a.dataHora ASC",
                Agendamento.class)
                .setParameter("data", data)
                .setParameter("t", t)
                .getResultList();
    }

    // ===============================
    // ÚLTIMOS 3 AGENDAMENTOS
    // ===============================
    public List<Agendamento> buscarUltimos3(Usuario u) {
        return em.createQuery(
                "SELECT a FROM Agendamento a "
                + "LEFT JOIN FETCH a.servico "
                + "LEFT JOIN FETCH a.horario "
                + "LEFT JOIN FETCH a.usuario "
                + "LEFT JOIN FETCH a.atendente "
                + "LEFT JOIN FETCH a.espaco "
                + "WHERE a.usuario = :u "
                + "ORDER BY a.dataHora DESC",
                Agendamento.class)
                .setParameter("u", u)
                .setMaxResults(3)
                .getResultList();
    }

    public List<Agendamento> buscarProximoFilaOrdenado() {
        return em.createQuery(
                "SELECT a FROM Agendamento a "
                + "LEFT JOIN FETCH a.servico "
                + "LEFT JOIN FETCH a.horario "
                + "LEFT JOIN FETCH a.usuario "
                + "LEFT JOIN FETCH a.atendente "
                + "LEFT JOIN FETCH a.espaco "
                + "WHERE a.status = :status "
                + "ORDER BY a.horaCheckin ASC",
                Agendamento.class)
                .setParameter("status", StatusAgendamento.EM_FILA)
                .getResultList();
    }

    public Agendamento buscarProximoAgendamento() {
        try {
            // Primeiro tenta achar quem está em fila (EM_FILA)
            List<Agendamento> fila = em.createQuery(
                "SELECT a FROM Agendamento a "
                + "LEFT JOIN FETCH a.servico "
                + "LEFT JOIN FETCH a.horario "
                + "LEFT JOIN FETCH a.usuario "
                + "LEFT JOIN FETCH a.atendente "
                + "LEFT JOIN FETCH a.espaco "
                + "WHERE a.status = :status "
                + "ORDER BY a.horaCheckin ASC",
                Agendamento.class)
                .setParameter("status", StatusAgendamento.EM_FILA)
                .setMaxResults(1)
                .getResultList();

            if (!fila.isEmpty()) {
            return fila.get(0);
            }

            // Se não houver ninguém em fila, retorna o próximo agendamento AGENDADO por dataHora
            java.time.LocalDateTime agora = java.time.LocalDateTime.now();
            List<Agendamento> proximos = em.createQuery(
                "SELECT a FROM Agendamento a "
                    + "LEFT JOIN FETCH a.servico "
                    + "LEFT JOIN FETCH a.horario "
                    + "LEFT JOIN FETCH a.usuario "
                    + "LEFT JOIN FETCH a.atendente "
                    + "LEFT JOIN FETCH a.espaco "
                    + "WHERE a.status = :status AND a.dataHora >= :now "
                    + "ORDER BY a.dataHora ASC",
                Agendamento.class)
                .setParameter("status", StatusAgendamento.AGENDADO)
                .setParameter("now", agora)
                .setMaxResults(1)
                .getResultList();

            return proximos.isEmpty() ? null : proximos.get(0);

        } catch (NoResultException e) {
            return null;
        }
    }

}
