package br.com.sigapar1.service;

import br.com.sigapar1.dao.AgendamentoDAO;
import br.com.sigapar1.dao.AtendimentoDAO;
import br.com.sigapar1.entity.Agendamento;
import br.com.sigapar1.entity.Atendimento;
import br.com.sigapar1.entity.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class AtendimentoService {

    @Inject
    private AgendamentoDAO agendamentoDAO;

    @Inject
    private AtendimentoDAO atendimentoDAO;

    // retorna próximo agendamento (delegando ao dao)
    public Agendamento buscarProximoDaFila() {
        return agendamentoDAO.buscarProximoFila();
    }

    public void iniciarAtendimento(Agendamento a, Usuario atendente) {
        if (a == null) return;
        a.setAtendente(atendente);
        a.setEmAtendimento(true);
        a.setChamado(true);
        agendamentoDAO.atualizar(a);

        Atendimento at = new Atendimento();
        at.setAgendamento(a);
        at.setAtendente(atendente);
        at.setInicio(LocalDateTime.now());
        atendimentoDAO.salvar(at);
    }

    public void finalizarAtendimento(Long idAgendamento, String observacao) {
        // finalizar o agendamento e o atendimento relacionado
        Agendamento a = agendamentoDAO.buscarPorId(idAgendamento);
        if (a != null) {
            a.setFinalizado(true);
            a.setEmAtendimento(false);
            agendamentoDAO.atualizar(a);
        }

        // localizar atendimento aberto e atualizar fim
        List<Atendimento> list = atendimentoDAO.listarPorAtendente(null); // listar todos e filtrar
        for (Atendimento at : list) {
            if (at.getAgendamento() != null && at.getAgendamento().getId().equals(idAgendamento) && at.getFim() == null) {
                at.setFim(LocalDateTime.now());
                if (observacao != null) at.setObservacao(observacao);
                atendimentoDAO.atualizar(at);
                break;
            }
        }
    }
}
