package br.com.sigapar1.service;

import br.com.sigapar1.dao.AgendamentoDAO;
import br.com.sigapar1.entity.Agendamento;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

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
            a.setCheckin(true);   // use setCheckin nome correto
            dao.atualizar(a);
        }
    }

    public void enviarParaFila(Long id) {
        Agendamento a = dao.buscarPorId(id);
        if (a != null) {
            // marcar como não chamado (ficará pronto para ser pego por buscarProximoFila)
            a.setChamado(false);
            dao.atualizar(a);
        }
    }

    public List<Agendamento> listarFila() {
        return dao.findAll().stream()
                .filter(x -> x.isCheckin() && !x.isChamado() && !x.isFinalizado())
                .toList();
    }
}
