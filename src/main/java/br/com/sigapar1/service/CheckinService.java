package br.com.sigapar1.service;

import br.com.sigapar1.dao.AgendamentoDAO;
import br.com.sigapar1.dao.CheckinDao;
import br.com.sigapar1.entity.Agendamento;
import br.com.sigapar1.entity.Checkin;
import br.com.sigapar1.entity.StatusAgendamento;
import br.com.sigapar1.entity.Usuario;
import br.com.sigapar1.util.BusinessException;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Stateless
public class CheckinService {

    @Inject
    private CheckinDao checkinDao;

    @Inject
    private AgendamentoDAO agendamentoDao;

    @Inject
    private FilaAtendimentoService filaService;

    // ==============================================================
    // CHECK-IN FEITO PELO RECEPCIONISTA - UC08
    // ==============================================================
    public Checkin realizarCheckinRecepcao(Agendamento ag, Usuario recepcionista) {

        if (ag == null) {
            throw new BusinessException("Nenhum agendamento selecionado.");
        }

        // **FA03 - já tem check-in**
        Checkin existente = checkinDao.buscarPorAgendamento(ag);
        if (existente != null) {
            throw new BusinessException("Check-in já foi realizado para este usuário.");
        }

        // **FA02 - agendamento de outro dia**
        if (!ag.getData().isEqual(LocalDate.now())) {
            throw new BusinessException("Este agendamento não é para hoje.");
        }

        // =====================================================
        // Criar registro de check-in
        // =====================================================
        Checkin checkin = new Checkin();
        checkin.setAgendamento(ag);
        checkin.setUsuario(ag.getUsuario());         
        checkin.setDataHoraCheckin(new Date());
        checkin.setStatus("REALIZADO");

        checkinDao.salvar(checkin);

        // =====================================================
        // Atualizar agendamento
        // =====================================================
        ag.setCheckin(true);
        ag.setHoraCheckin(LocalTime.now());
        ag.setStatus(StatusAgendamento.EM_FILA);

        agendamentoDao.atualizar(ag);

        // =====================================================
        // Gerar senha
        // =====================================================
        String senha = "A" + ag.getId(); // sua regra atual

        // =====================================================
        // Enfileirar
        // =====================================================
        filaService.adicionar(ag, recepcionista, senha);

        return checkin;
    }
}
