package br.com.sigapar1.service;

import br.com.sigapar1.dao.AgendamentoDAO;
import br.com.sigapar1.dao.AtendimentoDAO;
import br.com.sigapar1.dao.ServicoDAO;
import br.com.sigapar1.dao.FilaAtendimentoDAO;
import br.com.sigapar1.entity.*;
import br.com.sigapar1.util.BusinessException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@ApplicationScoped
public class AtendimentoService {

    @Inject
    private AgendamentoDAO agendamentoDAO;

    @Inject
    private AtendimentoDAO atendimentoDAO;

    @Inject
    private ServicoDAO servicoDAO;

    @Inject
    private FilaAtendimentoDAO filaDAO;

    @PersistenceContext(unitName = "sigaparPU")
    private EntityManager em;

    public Agendamento chamarProximoPorEspaco(List<Long> servicosIds, Long espacoId) {
        if (servicosIds == null || servicosIds.isEmpty() || espacoId == null) return null;

        List<FilaAtendimento> fila = filaDAO.buscarPorEspacoOrderByEntrada(espacoId);

        return fila.stream()
                .filter(f -> servicosIds.contains(f.getAgendamento().getServico().getId()))
                .map(FilaAtendimento::getAgendamento)
                .findFirst()
                .orElse(null);
    }

    public List<FilaAtendimento> buscarProximosPorEspaco(List<Long> servicosIds, Long espacoId, int limite) {
        if (servicosIds == null || servicosIds.isEmpty() || espacoId == null) return List.of();

        List<FilaAtendimento> fila = filaDAO.buscarPorEspacoOrderByEntrada(espacoId);

        return fila.stream()
                .filter(f -> servicosIds.contains(f.getAgendamento().getServico().getId()))
                .limit(limite)
                .toList();
    }

    @Transactional
    public void iniciarAtendimento(Agendamento agendamento, Usuario atendente) {
        if (agendamento == null) return;

        agendamento.setAtendente(atendente);
        agendamento.setStatus(StatusAgendamento.EM_ATENDIMENTO);
        agendamento.setHoraChamado(LocalTime.now());
        agendamentoDAO.atualizar(agendamento);

        Atendimento at = new Atendimento();
        at.setAgendamento(agendamento);
        at.setAtendente(atendente);
        at.setInicio(LocalDateTime.now());

        atendimentoDAO.salvar(at);
    }

    @Transactional
    public void finalizarAtendimento(Long idAgendamento, String observacao) {
        if (idAgendamento == null) return;

        Agendamento agendamento = agendamentoDAO.buscarPorId(idAgendamento);
        if (agendamento != null) {
            agendamento.setStatus(StatusAgendamento.CONCLUIDO);
            agendamentoDAO.atualizar(agendamento);
        }

        Atendimento at = atendimentoDAO.buscarEmAndamentoPorIdAgendamento(idAgendamento);
        if (at != null) {
            at.setFim(LocalDateTime.now());
            at.setObservacao(observacao);
            atendimentoDAO.atualizar(at);
        }
    }

    @Transactional
    public Agendamento gerarReencaminhamento(Usuario usuario, Long idServicoDestino) {
        if (usuario == null) throw new BusinessException("Usuário não pode ser nulo.");
        if (idServicoDestino == null) throw new BusinessException("Serviço de destino não informado.");

        Servico servico = servicoDAO.buscarPorId(idServicoDestino);
        if (servico == null) throw new BusinessException("Serviço de destino não encontrado.");

        Agendamento novo = new Agendamento();
        novo.setUsuario(usuario);
        novo.setServico(servico);
        novo.setStatus(StatusAgendamento.EM_FILA);
        novo.setData(LocalDate.now());
        novo.setDataHora(LocalDateTime.now());
        novo.setPrioridade("NORMAL");
        novo.setProtocolo(gerarProtocolo());

        agendamentoDAO.salvar(novo);
        return novo;
    }

    private String gerarProtocolo() {
        return "AG-" + System.currentTimeMillis();
    }

    public List<Atendimento> listarPorCliente(Long idCliente) {
        return em.createQuery(
                        "SELECT a FROM Atendimento a WHERE a.agendamento.usuario.id = :id ORDER BY a.inicio DESC",
                        Atendimento.class)
                .setParameter("id", idCliente)
                .getResultList();
    }
}
