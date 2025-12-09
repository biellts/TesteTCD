package br.com.sigapar1.service;

import br.com.sigapar1.dao.DisponibilidadeAtendenteDAO;
import br.com.sigapar1.entity.DisponibilidadeAtendente;
import br.com.sigapar1.entity.StatusAtendente;
import br.com.sigapar1.entity.Usuario;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RequestScoped
public class DisponibilidadeAtendenteService {

    @Inject
    private DisponibilidadeAtendenteDAO dao;

    // ==================================================
    // 1) ALTERAR STATUS (o que já existia na sua versão)
    // ==================================================
    public void alterarStatus(Usuario atendente, StatusAtendente status, String motivo) {
        DisponibilidadeAtendente d = new DisponibilidadeAtendente();
        d.setAtendente(atendente);
        d.setStatus(status);
        d.setMotivo(motivo);
        d.setAlteradoEm(LocalDateTime.now());

        dao.salvar(d);
    }

    // ==================================================
    // 2) SALVAR UMA DISPONIBILIDADE DE DIA + HORÁRIO
    // ==================================================
    public void salvarDisponibilidade(DisponibilidadeAtendente d) {
        d.setAlteradoEm(LocalDateTime.now());
        dao.salvar(d);
    }

    // ==================================================
    // 3) GERAR HORÁRIOS AUTOMÁTICOS (TCD exige)
    // ==================================================
    public List<LocalTime> gerarHorarios(LocalTime inicio, LocalTime fim, int duracaoMinutos) {

        List<LocalTime> horarios = new ArrayList<>();

        LocalTime atual = inicio;

        while (!atual.plusMinutes(duracaoMinutos).isAfter(fim)) {
            horarios.add(atual);
            atual = atual.plusMinutes(duracaoMinutos);
        }

        return horarios;
    }
}
