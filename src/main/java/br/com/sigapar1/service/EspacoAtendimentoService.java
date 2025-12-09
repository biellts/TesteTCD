package br.com.sigapar1.service;

import br.com.sigapar1.dao.EspacoAtendimentoDAO;
import br.com.sigapar1.entity.EspacoAtendimento;
import br.com.sigapar1.util.BusinessException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@RequestScoped
public class EspacoAtendimentoService {

    @Inject
    private EspacoAtendimentoDAO dao;

    public EspacoAtendimentoService() {
    }

    /**
     * Salvar ou atualizar
     */
    @Transactional
    public void salvar(EspacoAtendimento e) {
        if (e == null) return;

        if (e.getId() == null) {
            dao.salvar(e);
        } else {
            dao.atualizar(e);
        }
    }

    /**
     * Lista todos — usado pelas telas
     */
    public List<EspacoAtendimento> listarTodos() {
        return dao.listarTodos();
    }

    /**
     * Compatibilidade com controllers antigas
     */
    public List<EspacoAtendimento> findAll() {
        return dao.listarTodos();
    }

    public EspacoAtendimento buscarPorId(Long id) {
        return dao.buscarPorId(id);
    }

    /**
     * Exclusão padrão → SOFT DELETE (inativar)
     */
    @Transactional
    public void excluir(Long id) {
        if (id == null) return;

        if (dao.possuiAgendamentosVinculados(id)) {
            throw new BusinessException("Não é possível excluir: existem agendamentos vinculados.");
        }

        EspacoAtendimento e = dao.buscarPorId(id);
        if (e == null) return;

        // Soft delete
        e.setAtivo(false);
        dao.atualizar(e);
    }

    /**
     * Excluir de verdade (caso botão “Excluir Definitivo”)
     */
    @Transactional
    public void excluirFisicamente(Long id) {
        dao.excluir(id);
    }
}
