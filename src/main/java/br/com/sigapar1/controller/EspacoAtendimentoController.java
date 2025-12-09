package br.com.sigapar1.controller;

import br.com.sigapar1.entity.EspacoAtendimento;
import br.com.sigapar1.service.EspacoAtendimentoService;
import br.com.sigapar1.util.JsfUtil;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class EspacoAtendimentoController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private EspacoAtendimentoService service;

    private EspacoAtendimento espaco;

    private List<EspacoAtendimento> lista; // ← compatível com o XHTML

    @PostConstruct
    public void init() {
        espaco = new EspacoAtendimento();
        listar();
    }

    /**
     * LISTAR — usado no XHTML
     */
    public void listar() {
        lista = service.listarTodos(); // já vem com FETCH JOIN
    }

    /**
     * BOTÃO NOVO / LIMPAR
     */
    public void novo() {
        espaco = new EspacoAtendimento();
    }

    /**
     * SALVAR
     */
    public void salvar() {
        try {
            service.salvar(espaco);
            JsfUtil.addSuccessMessage("Espaço salvo com sucesso!");
            novo();     // limpa o formulário
            listar();   // atualiza tabela
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Erro ao salvar espaço: " + ex.getMessage());
        }
    }

    /**
     * EDITAR
     */
    public void editar(EspacoAtendimento e) {
        this.espaco = e;
    }

    /**
     * EXCLUIR DEFINITIVO (botão vermelho do XHTML)
     */
    public void excluirFisico(EspacoAtendimento e) {
        try {
            service.excluirFisicamente(e.getId());
            JsfUtil.addSuccessMessage("Espaço excluído DEFINITIVAMENTE!");
            listar();
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Erro ao excluir: " + ex.getMessage());
        }
    }

    /**
     * EXCLUIR NORMAL (soft delete = inativar)
     */
    public void excluir(EspacoAtendimento e) {
        try {
            service.excluir(e.getId());
            JsfUtil.addSuccessMessage("Espaço inativado com sucesso!");
            listar();
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Erro ao excluir: " + ex.getMessage());
        }
    }

    /**
     * ATIVAR / INATIVAR
     */
    public void toggleAtivo(EspacoAtendimento e) {
        try {
            e.setAtivo(!e.isAtivo());
            service.salvar(e);
            JsfUtil.addSuccessMessage("Status atualizado!");
            listar();
        } catch (Exception ex) {
            JsfUtil.addErrorMessage("Erro ao alterar status: " + ex.getMessage());
        }
    }

    // GETTERS E SETTERS

    public EspacoAtendimento getEspaco() {
        return espaco;
    }

    public void setEspaco(EspacoAtendimento espaco) {
        this.espaco = espaco;
    }

    public List<EspacoAtendimento> getLista() {
        return lista;
    }

    public void setLista(List<EspacoAtendimento> lista) {
        this.lista = lista;
    }
}
