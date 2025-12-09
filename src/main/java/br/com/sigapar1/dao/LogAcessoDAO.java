package br.com.sigapar1.dao;

import br.com.sigapar1.entity.LogAcesso;
import jakarta.ejb.Stateless;

@Stateless
public class LogAcessoDAO extends GenericDAO<LogAcesso> {

    public LogAcessoDAO() {
        super(LogAcesso.class);
    }
}
