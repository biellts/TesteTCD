package br.com.sigapar1.service;

import br.com.sigapar1.dao.AgendamentoDAO;
import br.com.sigapar1.dao.HorarioDAO;
import br.com.sigapar1.entity.Agendamento;
import br.com.sigapar1.entity.Horario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class AgendamentoService {

    @Inject
    private AgendamentoDAO dao;

    @Inject
    private HorarioDAO horarioDAO;

    public List<Horario> listarHorarios(LocalDate data) {
        return horarioDAO.findDisponiveis();
    }

    public void salvar(Agendamento a) {
        dao.save(a);
    }

    public Agendamento buscar(Long id) {
        return dao.buscarPorId(id);
    }

    public void atualizar(Agendamento a) {
        dao.atualizar(a);
    }

    public boolean existeConflito(Agendamento a) {
        if (a.getProtocolo() == null) return false;
        return dao.buscarPorProtocolo(a.getProtocolo()) != null;
    }

    public List<Agendamento> listarTodos() {
        return dao.findAll();
    }

    public List<Horario> horariosDisponiveis(Long idServico, LocalDate data) {
        return horarioDAO.findDisponiveis();
    }

    // nome usado em controllers: buscarProximoDaFila()
    public Agendamento buscarProximoDaFila() {
        return dao.buscarProximoFila();
    }
}
