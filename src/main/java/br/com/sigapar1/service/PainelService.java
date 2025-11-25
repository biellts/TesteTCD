package br.com.sigapar1.service;

import br.com.sigapar1.dao.AgendamentoDAO;
import br.com.sigapar1.entity.Agendamento;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class PainelService {

    @Inject
    private AgendamentoDAO dao;

    public List<Agendamento> listarFilaPainel() {
        return dao.findAll(); // para painel simples, lista todos; você pode filtrar conforme necessidade
    }

    public Agendamento ultimoChamado() {
        return dao.buscarUltimoChamado();
    }
}
