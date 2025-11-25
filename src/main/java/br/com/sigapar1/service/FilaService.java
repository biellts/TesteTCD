package br.com.sigapar1.service;

import br.com.sigapar1.dao.AgendamentoDAO;
import br.com.sigapar1.entity.Agendamento;

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

        lista.removeIf(a -> !a.isCheckin() || a.isChamado());

        lista.sort(
                Comparator
                    .comparing(Agendamento::getPrioridade)
                    .thenComparing(Agendamento::getDataHora)
        );

        return lista;
    }
}
