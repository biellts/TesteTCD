package br.com.sigapar1.dao;

import br.com.sigapar1.entity.HorarioFuncionamento;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HorarioFuncionamentoDAO extends GenericDAO<HorarioFuncionamento> {

    public HorarioFuncionamentoDAO() {
        super(HorarioFuncionamento.class);
    }
}
