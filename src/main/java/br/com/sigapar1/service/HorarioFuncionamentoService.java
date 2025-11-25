package br.com.sigapar1.service;

import br.com.sigapar1.dao.HorarioFuncionamentoDAO;
import br.com.sigapar1.entity.HorarioFuncionamento;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.util.List;

@RequestScoped
public class HorarioFuncionamentoService {

    @Inject
    private HorarioFuncionamentoDAO dao;

    public void salvar(HorarioFuncionamento h) {
        dao.salvar(h);
    }

    public List<HorarioFuncionamento> listar() {
        return dao.listarTodos();
    }
}
