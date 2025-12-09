package br.com.sigapar1.dao;

import br.com.sigapar1.entity.AtendenteServicoEspaco;
import br.com.sigapar1.entity.Horario;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import java.util.Collections;
import java.util.List;

@Stateless
public class AtendenteServicoEspacoDAO extends GenericDAO<AtendenteServicoEspaco> {

    public AtendenteServicoEspacoDAO() {
        super(AtendenteServicoEspaco.class);
    }

    // ====================== MÉTODOS JÁ EXISTENTES ======================
    public void excluir(Long id) {
        AtendenteServicoEspaco v = buscarPorId(id);
        if (v != null) {
            remover(v);
        }
    }

    public List<AtendenteServicoEspaco> listarPorAtendente(Long id) {
        return getEntityManager()
                .createQuery("SELECT v FROM AtendenteServicoEspaco v WHERE v.atendente.id = :id", AtendenteServicoEspaco.class)
                .setParameter("id", id)
                .getResultList();
    }

    public List<AtendenteServicoEspaco> listarPorServico(Long id) {
        return getEntityManager()
                .createQuery("""
                SELECT v FROM AtendenteServicoEspaco v
                JOIN FETCH v.horario h
                JOIN FETCH v.servico s
                JOIN FETCH v.espaco e
                WHERE v.servico.id = :id
                AND h.ativo = true
                AND h.capacidadeAtual < h.capacidadeMax
            """, AtendenteServicoEspaco.class)
                .setParameter("id", id)
                .getResultList();
    }

    public List<AtendenteServicoEspaco> listarPorEspaco(Long id) {
        return getEntityManager()
                .createQuery("SELECT v FROM AtendenteServicoEspaco v WHERE v.espaco.id = :id", AtendenteServicoEspaco.class)
                .setParameter("id", id)
                .getResultList();
    }

    public List<AtendenteServicoEspaco> listarPorHorario(Long id) {
        return getEntityManager()
                .createQuery("SELECT v FROM AtendenteServicoEspaco v WHERE v.horario.id = :id", AtendenteServicoEspaco.class)
                .setParameter("id", id)
                .getResultList();
    }

    // ====================== MÉTODOS NOVOS E OBRIGATÓRIOS ======================
    /**
     * Verifica se já existe vínculo com a mesma combinação
     */
    public List<AtendenteServicoEspaco> buscarDuplicados(Long atendenteId, Long servicoId, Long espacoId, Long horarioId) {
        try {
            return getEntityManager()
                    .createQuery(
                            "SELECT v FROM AtendenteServicoEspaco v "
                            + "WHERE v.atendente.id = :a "
                            + "AND v.servico.id = :s "
                            + "AND v.espaco.id = :e "
                            + "AND v.horario.id = :h", AtendenteServicoEspaco.class)
                    .setParameter("a", atendenteId)
                    .setParameter("s", servicoId)
                    .setParameter("e", espacoId)
                    .setParameter("h", horarioId)
                    .getResultList();
        } catch (Exception e) {
            System.err.println("Erro ao buscar duplicados: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Lista todos os horários ativos (campo disponivel = true)
     */
    public List<Horario> listarHorariosAtivos() {
        try {
            return getEntityManager()
                    .createQuery("SELECT h FROM Horario h WHERE h.disponivel = true ORDER BY h.diaSemana, h.hora", Horario.class)
                    .getResultList();
        } catch (Exception e) {
            System.err.println("Erro ao listar horários ativos: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Busca um horário por ID
     */
    public Horario buscarHorarioPorId(Long id) {
        if (id == null) {
            return null;
        }
        try {
            return getEntityManager().find(Horario.class, id);
        } catch (Exception e) {
            System.err.println("Horário não encontrado: ID " + id);
            return null;
        }
    }

    // ====================== MÉTODOS AUXILIARES (OPCIONAIS MAS ÚTEIS) ======================
    /**
     * Lista todos os vínculos (usado na tela principal)
     */
    @Override
    public List<AtendenteServicoEspaco> listarTodos() {
        try {
            return getEntityManager()
                    .createQuery("SELECT v FROM AtendenteServicoEspaco v "
                            + "ORDER BY v.atendente.nome, v.servico.nome", AtendenteServicoEspaco.class)
                    .getResultList();
        } catch (Exception e) {
            System.err.println("Erro ao listar todos os vínculos: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Conta quantos vínculos um atendente tem
     */
    public Long contarPorAtendente(Long atendenteId) {
        try {
            return getEntityManager()
                    .createQuery("SELECT COUNT(v) FROM AtendenteServicoEspaco v WHERE v.atendente.id = :id", Long.class)
                    .setParameter("id", atendenteId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0L;
        }
    }

    public List<AtendenteServicoEspaco> listarTodosPorServico(Long idServico) {
        return em.createQuery(
                "select v from AtendenteServicoEspaco v where v.servico.id = :id",
                AtendenteServicoEspaco.class
        )
                .setParameter("id", idServico)
                .getResultList();
    }

}
