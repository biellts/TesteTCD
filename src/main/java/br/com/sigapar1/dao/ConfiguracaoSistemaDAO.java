package br.com.sigapar1.dao;

import br.com.sigapar1.entity.ConfiguracaoSistema;
import jakarta.ejb.Stateless;

@Stateless
public class ConfiguracaoSistemaDAO extends GenericDAO<ConfiguracaoSistema> {

    public ConfiguracaoSistemaDAO() {
        super(ConfiguracaoSistema.class);
    }
}
