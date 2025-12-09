package br.com.sigapar1.service;

import br.com.sigapar1.dao.AgendamentoDAO;
import br.com.sigapar1.entity.Agendamento;
import br.com.sigapar1.entity.StatusAgendamento;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Comparator;
import java.util.List;

@ApplicationScoped
public class FilaService {

    @Inject
    private AgendamentoDAO agendamentoDAO;

    public List<Agendamento> listarFila() {

        List<Agendamento> lista = agendamentoDAO.findAll();

        lista.removeIf(a ->
                a.getStatus() != StatusAgendamento.EM_FILA
        );

        lista.sort(
                Comparator
                        .comparing(Agendamento::getPrioridade, Comparator.nullsLast(String::compareTo))
                        .thenComparing(Agendamento::getHoraCheckin, Comparator.nullsLast((x,y) -> x.compareTo(y)))
        );

        return lista;
    }
}
