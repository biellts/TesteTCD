package br.com.sigapar1.service;

import br.com.sigapar1.dao.FeriadoDAO;
import br.com.sigapar1.entity.Feriado;
import br.com.sigapar1.util.BusinessException;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.List;

@Stateless
public class FeriadoService {

    @Inject
    private FeriadoDAO dao;

    public List<Feriado> listarTodos() {
        return dao.listarTodos();
    }

    public void adicionar(LocalDate data) {

        if (data == null)
            throw new BusinessException("Data do feriado obrigatória.");

        if (dao.isFeriado(data))
            throw new BusinessException("Esse feriado já está cadastrado.");

        Feriado f = new Feriado();
        f.setData(data);
        dao.salvar(f);
    }

    public void remover(Long id) {
        if (id == null)
            throw new BusinessException("ID inválido para remoção.");

        dao.excluir(id);
    }
}
