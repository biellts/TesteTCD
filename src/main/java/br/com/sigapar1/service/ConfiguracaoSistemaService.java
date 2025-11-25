package br.com.sigapar1.service;

import br.com.sigapar1.dao.ConfiguracaoSistemaDAO;
import br.com.sigapar1.entity.ConfiguracaoSistema;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ConfiguracaoSistemaService {

    @Inject
    private ConfiguracaoSistemaDAO dao;

    public ConfiguracaoSistema carregar() {
        ConfiguracaoSistema c = dao.buscarPorId(1L);
        if (c == null) {
            c = new ConfiguracaoSistema();
            dao.salvar(c);
        }
        return c;
    }

    public void salvar(ConfiguracaoSistema cfg) {
        dao.atualizar(cfg);
    }
}
