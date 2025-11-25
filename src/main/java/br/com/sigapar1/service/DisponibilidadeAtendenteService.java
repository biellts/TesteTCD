package br.com.sigapar1.service;

import br.com.sigapar1.dao.DisponibilidadeAtendenteDAO;
import br.com.sigapar1.entity.DisponibilidadeAtendente;
import br.com.sigapar1.entity.StatusAtendente;
import br.com.sigapar1.entity.Usuario;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;

@RequestScoped
public class DisponibilidadeAtendenteService {

    @Inject
    private DisponibilidadeAtendenteDAO dao;

    public void alterarStatus(Usuario atendente, StatusAtendente status, String motivo) {
        DisponibilidadeAtendente d = new DisponibilidadeAtendente();
        d.setAtendente(atendente);
        d.setStatus(status);
        d.setMotivo(motivo);
        d.setAlteradoEm(LocalDateTime.now());

        dao.salvar(d);
    }
}
