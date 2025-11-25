package br.com.sigapar1.service;

import br.com.sigapar1.dao.AgendamentoDAO;
import br.com.sigapar1.entity.Agendamento;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class RelatorioService {

    @Inject
    private AgendamentoDAO agendamentoDAO;

    public List<Agendamento> atendimentosPorPeriodo(LocalDate inicio, LocalDate fim) {
        return agendamentoDAO.listarPorPeriodo(inicio, fim);
    }
}
