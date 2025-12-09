package br.com.sigapar1.service;

import br.com.sigapar1.dao.AgendamentoDAO;
import br.com.sigapar1.entity.Agendamento;
import br.com.sigapar1.entity.AtendenteServicoEspaco;
import br.com.sigapar1.entity.Horario;
import br.com.sigapar1.entity.Servico;
import br.com.sigapar1.entity.StatusAgendamento;
import br.com.sigapar1.entity.Usuario;
import br.com.sigapar1.util.BusinessException;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class AgendamentoSimplificadoService {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private AgendamentoDAO agendamentoDAO;

    // ================================================================
    // DIAS DISPONÍVEIS
    // ================================================================
    public List<LocalDate> buscarDiasDisponiveisParaServico(Long idServico) {
        try {
            String sql = "SELECT DISTINCT h.dia_semana FROM horario h "
                    + "INNER JOIN atendente_servico_espaco ase ON ase.horario_id = h.id "
                    + "WHERE ase.servico_id = ? AND h.disponivel = TRUE AND h.ativo = TRUE ORDER BY h.dia_semana";

            List<String> diasSemana = em.createNativeQuery(sql)
                    .setParameter(1, idServico)
                    .getResultList();

            return diasSemana.stream()
                    .map(this::proximasDatasDoDiaSemanaBuscando)
                    .flatMap(List::stream)
                    .distinct()
                    .sorted()
                    .limit(30)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    private List<LocalDate> proximasDatasDoDiaSemanaBuscando(String diaSemanaString) {
        var dayOfWeek = java.time.DayOfWeek.valueOf(diaSemanaString.toUpperCase());
        var hoje = LocalDate.now();

        List<LocalDate> datas = new java.util.ArrayList<>();
        LocalDate proxima = hoje;

        while (datas.size() < 5) {
            if (proxima.getDayOfWeek() == dayOfWeek) {
                datas.add(proxima);
            }
            proxima = proxima.plusDays(1);
        }
        return datas;
    }

    // ================================================================
    // HORÁRIOS
    // ================================================================
    public List<Horario> buscarHorariosPorServicoEData(Long idServico, LocalDate data) {
        if (idServico == null || data == null) {
            return List.of();
        }

        try {
            String sql = "SELECT h.* FROM horario h "
                    + "INNER JOIN atendente_servico_espaco ase ON ase.horario_id = h.id "
                    + "WHERE ase.servico_id = ? AND h.dia_semana = ? "
                    + "AND h.disponivel = TRUE AND h.ativo = TRUE ORDER BY h.hora ASC";

            String diaSemana = data.getDayOfWeek().toString();

            return em.createNativeQuery(sql, Horario.class)
                    .setParameter(1, idServico)
                    .setParameter(2, diaSemana)
                    .getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // ================================================================
    // SERVIÇOS
    // ================================================================
    public List<Servico> listarServicosAtivos() {
        try {
            return em.createQuery(
                    "SELECT s FROM Servico s WHERE s.ativo = TRUE ORDER BY s.nome",
                    Servico.class
            ).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // ================================================================
    // CONFIRMAR AGENDAMENTO
    // ================================================================
    public Agendamento confirmarAgendamento(Long idServico, Long idHorario, LocalDate data, Usuario usuario)
            throws BusinessException {

        if (usuario == null) {
            throw new BusinessException("Usuário não pode ser nulo");
        }

        try {
            Servico servico = em.find(Servico.class, idServico);
            Horario horario = em.find(Horario.class, idHorario);

            if (servico == null) {
                throw new BusinessException("Serviço não encontrado");
            }
            if (horario == null) {
                throw new BusinessException("Horário não encontrado");
            }
            if (!horario.isDisponivel()) {
                throw new BusinessException("Horário indisponível");
            }
            if (!horario.isAtivo()) {
                throw new BusinessException("Horário inativo");
            }

            // Criar agendamento
            Agendamento ag = new Agendamento();
            ag.setServico(servico);
            ag.setHorario(horario);
            ag.setUsuario(usuario);
            ag.setData(data);

            // >>> Associa automaticamente o espaço e atendente
            AtendenteServicoEspaco ase = buscarAtendenteServicoEspaco(idServico, idHorario);
            if (ase != null) {
                ag.setEspaco(ase.getEspaco());
                ag.setAtendente(ase.getAtendente());
            }

            ag.setChamado(false);
            ag.setCheckin(false);
            ag.setFinalizado(false);
            ag.setAtivo(true);
            ag.setObservacoes(null);
            ag.setHoraChamado(null);
            ag.setHoraCheckin(null);
            ag.setStatus(StatusAgendamento.AGENDADO);
            ag.setProtocolo(gerarProtocolo());
            ag.setDataHora(data.atTime(horario.getHora()));

            em.persist(ag);
            em.flush();

            return ag;

        } catch (BusinessException e) {
            throw e;

        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("Erro ao confirmar agendamento: " + e.getMessage());
        }
    }

    // ================================================================
    // MÉTODO AUXILIAR: BUSCAR ATENDENTE-SERVIÇO-ESPAÇO
    // ================================================================
    private AtendenteServicoEspaco buscarAtendenteServicoEspaco(Long idServico, Long idHorario) {
        try {
            return em.createQuery(
                    "SELECT ase FROM AtendenteServicoEspaco ase "
                    + "WHERE ase.servico.id = :idServico "
                    + "AND ase.horario.id = :idHorario "
                    + "AND ase.espaco.ativo = TRUE",
                    AtendenteServicoEspaco.class)
                    .setParameter("idServico", idServico)
                    .setParameter("idHorario", idHorario)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    private String gerarProtocolo() {
        return "AGD" + System.currentTimeMillis();
    }

    // ================================================================
    // BUSCAR POR ID (para reagendamento)
    // ================================================================
    public Agendamento buscarPorId(Long id) {
        if (id == null) {
            return null;
        }
        try {
            return em.find(Agendamento.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ================================================================
    // BUSCAR POR USUÁRIO
    // ================================================================
    public List<Agendamento> listarAgendamentosDoUsuario(Usuario usuario) {
        try {
            return em.createQuery(
                    "SELECT a FROM Agendamento a "
                    + "LEFT JOIN FETCH a.servico "
                    + "LEFT JOIN FETCH a.horario "
                    + "LEFT JOIN FETCH a.espaco "
                    + "WHERE a.usuario = :usuario "
                    + "ORDER BY a.dataHora DESC",
                    Agendamento.class
            ).setParameter("usuario", usuario)
                    .getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // ================================================================
    // BUSCAR POR PROTOCOLO
    // ================================================================
    public Agendamento buscarPorProtocolo(String protocolo) {
        if (protocolo == null || protocolo.isBlank()) {
            return null;
        }
        return agendamentoDAO.buscarPorProtocolo(protocolo);
    }

    // ================================================================
    // BUSCAR POR CPF
    // ================================================================
    public List<Agendamento> listarAgendamentosPorCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return List.of();
        }

        try {
            return em.createQuery(
                    "SELECT a FROM Agendamento a "
                    + "LEFT JOIN FETCH a.servico "
                    + "LEFT JOIN FETCH a.horario "
                    + "LEFT JOIN FETCH a.espaco "
                    + "LEFT JOIN FETCH a.usuario "
                    + "WHERE a.usuario.cpf = :cpf "
                    + "ORDER BY a.dataHora DESC",
                    Agendamento.class
            ).setParameter("cpf", cpf)
                    .getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // ================================================================
    // SALVAR / ATUALIZAR AGENDAMENTO
    // ================================================================
    public Agendamento salvar(Agendamento ag) {
        try {
            if (ag.getId() == null) {
                em.persist(ag);
            } else {
                em.merge(ag);
            }

            em.flush();
            return ag;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar agendamento", e);
        }
    }

    public Agendamento buscarAgendamentoComDetalhes(Long id) {
        try {
            List<Agendamento> resultado = em.createQuery(
                    "SELECT a FROM Agendamento a "
                    + "LEFT JOIN FETCH a.servico "
                    + "LEFT JOIN FETCH a.horario "
                    + "LEFT JOIN FETCH a.espaco "
                    + "WHERE a.id = :id", Agendamento.class)
                    .setParameter("id", id)
                    .getResultList();

            // Retorna o primeiro ou null se não existir
            return resultado.isEmpty() ? null : resultado.get(0);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
