package br.com.sigapar1.service;

import br.com.sigapar1.dao.ServicoDAO;
import br.com.sigapar1.dao.ServicoHistoricoDAO;
import br.com.sigapar1.entity.Servico;
import br.com.sigapar1.entity.ServicoHistorico;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@RequestScoped
public class ServicoService {

    @Inject
    private ServicoDAO dao;

    @Inject
    private ServicoHistoricoDAO historicoDao;

    public ServicoService() {}

    // ================================
    // SALVAR
    // ================================
    @Transactional
    public void salvar(Servico atual) {
        boolean novo = (atual.getId() == null);

        if (novo) {
            dao.salvar(atual);
            registrarCriacao(atual);
            return;
        }

        Servico antigo = dao.buscarPorId(atual.getId());
        if (antigo == null) {
            dao.salvar(atual);
            registrarCriacao(atual);
            return;
        }

        registrarAlteracoes(antigo, atual);
        dao.atualizar(atual);
    }

    // ================================
    // EXCLUIR (soft delete)
    // ================================
    @Transactional
    public void excluir(Long id) {
        Servico s = dao.buscarPorId(id);
        if (s == null) return;

        s.setAtivo(false);
        dao.atualizar(s);

        ServicoHistorico h = new ServicoHistorico();
        h.setServico(s);
        h.setCampo("EXCLUSAO");
        h.setValorAnterior("ATIVO");
        h.setValorNovo("INATIVO");
        h.setDataAlteracao(LocalDateTime.now());
        historicoDao.salvar(h);
    }

    // ================================
    // LISTAGENS
    // ================================
    public List<Servico> listarTodos() {
        return dao.findAll();
    }

    // ⚠️ AGORA LISTA APENAS SERVIÇOS ATIVOS
    public List<Servico> listarServicos() {
        return dao.listarAtivos();
    }

    public List<Servico> listarAtivos() {
        return dao.listarAtivos();
    }

    public Servico buscarPorId(Long id) {
        return dao.buscarPorId(id);
    }

    public Servico buscarPorNome(String nome) {
        return dao.buscarPorNome(nome);
    }

    public List<ServicoHistorico> listarHistorico(Long servicoId) {
        return historicoDao.listarPorServico(servicoId);
    }

    // ================================
    // HISTÓRICO
    // ================================
    private void registrarCriacao(Servico novo) {
        ServicoHistorico h = new ServicoHistorico();
        h.setServico(novo);
        h.setCampo("CRIACAO");
        h.setValorAnterior(null);
        h.setValorNovo(descreverServico(novo));
        h.setDataAlteracao(LocalDateTime.now());
        historicoDao.salvar(h);
    }

    private void registrarAlteracoes(Servico antigo, Servico atual) {
        LocalDateTime agora = LocalDateTime.now();

        comparar(antigo.getNome(), atual.getNome(), "nome", atual, agora);
        comparar(antigo.getDescricao(), atual.getDescricao(), "descricao", atual, agora);
        comparar(antigo.getDocumentosNecessarios(), atual.getDocumentosNecessarios(),
                "documentosNecessarios", atual, agora);

        if (antigo.getTempoEstimado() != atual.getTempoEstimado()) {
            persistir(atual, "tempoEstimado",
                    String.valueOf(antigo.getTempoEstimado()),
                    String.valueOf(atual.getTempoEstimado()),
                    agora);
        }

        if (antigo.isAtivo() != atual.isAtivo()) {
            persistir(atual, "ativo",
                    String.valueOf(antigo.isAtivo()),
                    String.valueOf(atual.isAtivo()),
                    agora);
        }
    }

    private void comparar(String antes, String depois, String campo,
                          Servico atual, LocalDateTime data) {

        if (!equalsSafe(antes, depois)) {
            persistir(atual, campo, safe(antes), safe(depois), data);
        }
    }

    private void persistir(Servico servico, String campo, String antes,
                           String depois, LocalDateTime data) {

        ServicoHistorico h = new ServicoHistorico();
        h.setServico(servico);
        h.setCampo(campo);
        h.setValorAnterior(antes);
        h.setValorNovo(depois);
        h.setDataAlteracao(data);
        historicoDao.salvar(h);
    }

    private boolean equalsSafe(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private String descreverServico(Servico s) {
        return "nome=" + safe(s.getNome()) +
                "; descricao=" + safe(s.getDescricao()) +
                "; tempo=" + s.getTempoEstimado() +
                "; docs=" + safe(s.getDocumentosNecessarios()) +
                "; ativo=" + s.isAtivo();
    }
}
