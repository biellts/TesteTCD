package br.com.sigapar1.controller;

import br.com.sigapar1.entity.ConfiguracaoSistema;
import br.com.sigapar1.service.ConfiguracaoSistemaService;
import br.com.sigapar1.util.JsfUtil;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class ConfiguracaoSistemaController implements Serializable {

    @Inject
    private ConfiguracaoSistemaService service;

    private ConfiguracaoSistema cfg;

    @PostConstruct
    public void init() {
        cfg = service.carregar();
    }

    public void salvar() {
        service.salvar(cfg);
        JsfUtil.addInfoMessage("Configurações salvas!");
    }

    public ConfiguracaoSistema getCfg() {
        return cfg;
    }
}
