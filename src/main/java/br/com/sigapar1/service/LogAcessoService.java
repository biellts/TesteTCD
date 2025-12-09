package br.com.sigapar1.service;

import br.com.sigapar1.dao.LogAcessoDAO;
import br.com.sigapar1.entity.LogAcesso;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class LogAcessoService {

    @Inject
    private LogAcessoDAO dao;

    public void registrar(String email, String role, String ip) {
        dao.salvar(new LogAcesso(email, role, ip));
    }
}
