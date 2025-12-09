package br.com.sigapar1.service;

import br.com.sigapar1.dao.ServicoHistoricoDAO;
import br.com.sigapar1.entity.Servico;
import br.com.sigapar1.entity.ServicoHistorico;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class ServicoHistoricoService {

    @Inject
    private ServicoHistoricoDAO dao;

    /**
     * Salva um registro de histórico
     */
    public void salvar(ServicoHistorico h) {
        dao.salvar(h);
    }

    /**
     * Lista histórico ordenado pela data (mais recente primeiro)
     */
    public List<ServicoHistorico> listarPorServico(Long servicoId) {
        return dao.listarPorServico(servicoId);
    }

    /**
     * Lista histórico completo sem ordenação adicional
     */
    public List<ServicoHistorico> listarTudoPorServico(Long servicoId) {
        return dao.listarTudoPorServico(servicoId);
    }

    /**
     * MÉTODO PRINCIPAL:
     * Compara o serviço antigo com o serviço atualizado
     * e registra todas as alterações encontradas.
     */
    public void registrarAlteracoes(Servico antigo, Servico novo) {

        if (antigo == null || novo == null) return;

        List<ServicoHistorico> registros = new ArrayList<>();

        // ========== CAMPO NOME ==========
        if (!isIgual(antigo.getNome(), novo.getNome())) {
            registros.add(criarRegistro(antigo, "Nome",
                    antigo.getNome(), novo.getNome()));
        }

        // ========== DESCRIÇÃO ==========
        if (!isIgual(antigo.getDescricao(), novo.getDescricao())) {
            registros.add(criarRegistro(antigo, "Descrição",
                    antigo.getDescricao(), novo.getDescricao()));
        }

        // ========== TEMPO ESTIMADO ==========
        if (antigo.getTempoEstimado() != novo.getTempoEstimado()) {
            registros.add(criarRegistro(antigo, "Tempo Estimado",
                    antigo.getTempoEstimado() + " min",
                    novo.getTempoEstimado() + " min"));
        }

        // ========== DOCUMENTOS ==========
        if (!isIgual(antigo.getDocumentosNecessarios(), novo.getDocumentosNecessarios())) {
            registros.add(criarRegistro(antigo, "Documentos Necessários",
                    antigo.getDocumentosNecessarios(), novo.getDocumentosNecessarios()));
        }

        // ========== STATUS ==========
        if (antigo.isAtivo() != novo.isAtivo()) {
            registros.add(criarRegistro(antigo, "Status",
                    antigo.isAtivo() ? "Ativo" : "Inativo",
                    novo.isAtivo() ? "Ativo" : "Inativo"));
        }

        // ========== SALVAR REGISTROS ==========
        for (ServicoHistorico h : registros) {
            dao.salvar(h);
        }
    }

    /**
     * Verifica igualdade de forma segura (null-safe)
     */
    private boolean isIgual(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    /**
     * Cria um registro preenchido corretamente
     */
    private ServicoHistorico criarRegistro(Servico servico, String campo,
                                           String valorAntigo, String valorNovo) {

        ServicoHistorico h = new ServicoHistorico();
        h.setServico(servico);
        h.setCampo(campo);
        h.setValorAnterior(valorAntigo == null ? "" : valorAntigo);
        h.setValorNovo(valorNovo == null ? "" : valorNovo);
        h.setDataAlteracao(LocalDateTime.now());

        return h;
    }
}
