package br.com.sigapar1.dao;

import br.com.sigapar1.entity.Agendamento;
import br.com.sigapar1.entity.FilaAtendimento;
import jakarta.ejb.Stateless;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class FilaAtendimentoDAO extends GenericDAO<FilaAtendimento> {

    public FilaAtendimentoDAO() {
        super(FilaAtendimento.class);
    }

    // LISTAR TODA FILA
    public List<FilaAtendimento> listarFila() {
        return getEntityManager()
                .createQuery("SELECT f FROM FilaAtendimento f ORDER BY f.timestampEntrada ASC",
                        FilaAtendimento.class)
                .getResultList();
    }

    // BUSCAR POR ESPAÇO
    public List<FilaAtendimento> buscarPorEspacoOrderByEntrada(Long espacoId) {
        if (espacoId == null) return List.of();

        return getEntityManager()
                .createQuery(
                        "SELECT f FROM FilaAtendimento f " +
                        "WHERE f.agendamento.espaco.id = :espacoId " +
                        "ORDER BY f.timestampEntrada ASC",
                        FilaAtendimento.class)
                .setParameter("espacoId", espacoId)
                .getResultList();
    }

    // BUSCAR AGENDAMENTOS DO DIA
    public List<Agendamento> buscarDoDia(LocalDate data) {
        if (data == null) return List.of();
        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.plusDays(1).atStartOfDay();
        return getEntityManager()
                .createQuery("SELECT a FROM Agendamento a WHERE a.dataHora >= :inicio AND a.dataHora < :fim ORDER BY a.horario.hora ASC",
                        Agendamento.class)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getResultList();
    }

    // BUSCAR POR TERMO
    public List<Agendamento> buscarPorTermo(String termo) {
        if (termo == null || termo.isBlank()) return List.of();
        String t = "%" + termo.toLowerCase() + "%";
        return getEntityManager()
                .createQuery(
                        "SELECT a FROM Agendamento a WHERE LOWER(a.usuario.nome) LIKE :t OR LOWER(a.usuario.cpf) LIKE :t OR LOWER(a.protocolo) LIKE :t",
                        Agendamento.class)
                .setParameter("t", t)
                .getResultList();
    }

    // BUSCAR POR USUÁRIO
    public List<FilaAtendimento> buscarPorUsuario(Long usuarioId) {
        if (usuarioId == null) return List.of();
        return getEntityManager()
                .createQuery(
                        "SELECT f FROM FilaAtendimento f WHERE f.agendamento.usuario.id = :usuarioId ORDER BY f.timestampEntrada ASC",
                        FilaAtendimento.class)
                .setParameter("usuarioId", usuarioId)
                .getResultList();
    }

    // BUSCAR POR SERVIÇO
    public List<FilaAtendimento> buscarPorServico(Long servicoId) {
        if (servicoId == null) return List.of();
        return getEntityManager()
                .createQuery(
                        "SELECT f FROM FilaAtendimento f WHERE f.agendamento.servico.id = :servicoId ORDER BY f.timestampEntrada ASC",
                        FilaAtendimento.class)
                .setParameter("servicoId", servicoId)
                .getResultList();
    }

    // REMOVER DA FILA
    public void removerDaFila(FilaAtendimento f) {
        if (f == null) return;
        getEntityManager().remove(getEntityManager().contains(f) ? f : getEntityManager().merge(f));
    }
}
