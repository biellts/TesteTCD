package br.com.sigapar1.dao;

import br.com.sigapar1.entity.ConfiguracaoSistema;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConfiguracaoSistemaDAO extends GenericDAO<ConfiguracaoSistema> {

    public ConfiguracaoSistemaDAO() {
        super(ConfiguracaoSistema.class);
    }
}
