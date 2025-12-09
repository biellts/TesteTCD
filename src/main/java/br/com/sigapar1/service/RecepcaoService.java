package br.com.sigapar1.service;

import br.com.sigapar1.dao.AgendamentoDAO;
import br.com.sigapar1.entity.Agendamento;
import br.com.sigapar1.entity.StatusAgendamento;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalTime;
import java.util.List;

@ApplicationScoped
public class RecepcaoService {

    @Inject
    private AgendamentoDAO dao;

    public Agendamento buscarPorCpf(String cpf) {
        return dao.buscarPorCpf(cpf);
    }

    public void checkIn(Long id) {
        Agendamento a = dao.buscarPorId(id);
        if (a != null) {
            a.setStatus(StatusAgendamento.EM_FILA);
            a.setHoraCheckin(LocalTime.now());
            dao.atualizar(a);
        }
    }

    public void enviarParaFila(Long id) {
        Agendamento a = dao.buscarPorId(id);
        if (a != null) {
            a.setStatus(StatusAgendamento.EM_FILA);
            dao.atualizar(a);
        }
    }

    public List<Agendamento> listarFila() {

        return dao.findAll().stream()
                .filter(x -> x.getStatus() == StatusAgendamento.EM_FILA)
                .toList();
    }
}
