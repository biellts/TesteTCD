package br.com.sigapar1.service;

import br.com.sigapar1.dao.*;
import br.com.sigapar1.entity.*;
import br.com.sigapar1.util.BusinessException;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Stateless
public class AgendamentoService {

    @Inject
    private AgendamentoDAO dao;
    @Inject
    private HorarioDAO horarioDao;
    @Inject
    private FeriadoDAO feriadoDao;
    @Inject
    private ServicoDAO servicoDao;
    @Inject
    private EspacoAtendimentoDAO espacoDao;

    @PersistenceContext
    private EntityManager em;

// ===============================
// SERVIÇO / ESPAÇO
// ===============================
    public Servico buscarServicoPorId(Long id) {
        return servicoDao.buscarPorId(id);
    }

    public List<Servico> listarServicos() {
        return servicoDao.findAll();
    }

    public List<EspacoAtendimento> listarEspacos() {
        return espacoDao.findAll();
    }

// ===============================
// HORÁRIOS DISPONÍVEIS
// ===============================
    public List<Horario> horariosDisponiveis(Long idServico, LocalDate data) {

        if (idServico == null || data == null || data.isBefore(LocalDate.now())) {
            return List.of();
        }

        List<AtendenteServicoEspaco> vinculos = em.createQuery(
                "SELECT v FROM AtendenteServicoEspaco v JOIN FETCH v.horario h "
                + "WHERE v.servico.id = :idServico", AtendenteServicoEspaco.class)
                .setParameter("idServico", idServico)
                .getResultList();

        return vinculos.stream()
                .map(AtendenteServicoEspaco::getHorario)
                .filter(Horario::isAtivo)
                .filter(h -> h.getDiaSemana().equalsIgnoreCase(data.getDayOfWeek().name()))
                .filter(h -> {
                    int atual = h.getCapacidadeAtual() == null ? 0 : h.getCapacidadeAtual();
                    int max = h.getCapacidadeMax() == null ? Integer.MAX_VALUE : h.getCapacidadeMax();
                    return atual < max;
                })
                .sorted(Comparator.comparing(Horario::getHora))
                .distinct()
                .toList();
    }

// ===============================
// DIAS DISPONÍVEIS (NOVA LÓGICA)
// ===============================
    public List<LocalDate> buscarDiasDisponiveis(Long idServico, int diasFuturos) {
        if (idServico == null || diasFuturos <= 0) {
            return List.of();
        }

        return IntStream.rangeClosed(0, diasFuturos - 1)
                .mapToObj(i -> LocalDate.now().plusDays(i))
                .filter(d -> {
                    List<Horario> hs = horariosDisponiveis(idServico, d);
                    return hs != null && !hs.isEmpty();
                })
                .collect(Collectors.toList());
    }

// ===============================
// BUSCAS
// ===============================
    public Agendamento buscarPorId(Long id) {
        return dao.buscarPorId(id);
    }

    public Agendamento buscarProximo(Usuario u) {
        return dao.buscarProximo(u);
    }

    public List<Agendamento> listarPorUsuario(Usuario u) {
        return dao.listarPorUsuario(u);
    }

    public Agendamento buscarPorCpf(String cpf) {
        return dao.buscarPorCpf(cpf);
    }

    public Agendamento buscarPorProtocolo(String protocolo) {
        if (protocolo == null || protocolo.trim().isEmpty()) {
            return null;
        }
        return dao.buscarPorProtocolo(protocolo.trim().toUpperCase());
    }

    public Agendamento buscarPorNomeParcial(String nomeParcial, LocalDate data) {
        if (nomeParcial == null || nomeParcial.isBlank() || data == null) {
            return null;
        }
        return dao.buscarPorNomeParcial(nomeParcial.toUpperCase(), data);
    }

    public List<Agendamento> buscarPorCpfOuProtocolo(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return List.of();
        }
        return dao.buscarPorCpfOuProtocolo(valor.trim());
    }

// ===============================
// SALVAR
// ===============================
    public void salvar(Agendamento a) throws BusinessException {
        if (a == null) {
            throw new BusinessException("Agendamento inválido.");
        }

        Horario h = horarioDao.buscarPorId(a.getHorario().getId());
        if (h == null) {
            throw new BusinessException("Horário inválido.");
        }

        Horario hLocked = em.find(Horario.class, h.getId(), LockModeType.PESSIMISTIC_WRITE);

        int atual = hLocked.getCapacidadeAtual() == null ? 0 : hLocked.getCapacidadeAtual();
        int max = hLocked.getCapacidadeMax() == null ? Integer.MAX_VALUE : hLocked.getCapacidadeMax();

        if (atual >= max) {
            throw new BusinessException("Horário indisponível ou lotado.");
        }
        if (dao.existeConflito(a)) {
            throw new BusinessException("Já existe agendamento para esse horário.");
        }

        if (a.getProtocolo() == null) {
            a.setProtocolo(gerarProtocolo());
        }

        a.setDataHora(LocalDateTime.of(a.getData(), hLocked.getHora()));

        hLocked.setCapacidadeAtual(atual + 1);
        horarioDao.atualizar(hLocked);

        dao.salvar(a);
    }

// ===============================
// CANCELAR
// ===============================
    public void cancelar(Long idAgendamento, Usuario usuarioLogado) throws BusinessException {
        Agendamento ag = dao.buscarPorId(idAgendamento);
        if (ag == null) {
            throw new BusinessException("Agendamento não encontrado.");
        }

        if (!ag.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new BusinessException("Você não pode cancelar este agendamento.");
        }

        if (!podeCancelar(ag)) {
            throw new BusinessException("Faltam menos de 24h para o atendimento.");
        }

        Horario h = ag.getHorario();
        h.setCapacidadeAtual(Math.max(0, h.getCapacidadeAtual() - 1));
        horarioDao.atualizar(h);

        ag.setStatus(StatusAgendamento.CANCELADO);
        dao.atualizar(ag);
    }

    public boolean podeCancelar(Agendamento a) {
        return Duration.between(LocalDateTime.now(), a.getDataHora()).toHours() >= 24;
    }

// ===============================
// REAGENDAR
// ===============================
    public void reagendar(Long idAgendamento, Long idHorarioNovo, Usuario usuarioLogado) throws BusinessException {
        Agendamento antigo = dao.buscarPorId(idAgendamento);
        if (antigo == null) {
            throw new BusinessException("Agendamento não encontrado.");
        }
        if (!antigo.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new BusinessException("Você não pode reagendar este agendamento.");
        }
        if (!podeCancelar(antigo)) {
            throw new BusinessException("Faltam menos de 24h para reagendar.");
        }

        Horario novoHorario = horarioDao.buscarPorId(idHorarioNovo);
        if (novoHorario == null || novoHorario.getCapacidadeAtual() >= novoHorario.getCapacidadeMax()) {
            throw new BusinessException("Horário novo inválido ou lotado.");
        }

        Agendamento teste = new Agendamento();
        teste.setData(antigo.getData());
        teste.setHorario(novoHorario);
        teste.setServico(antigo.getServico());
        if (dao.existeConflito(teste)) {
            throw new BusinessException("Este horário já está ocupado.");
        }

        Horario hAntigo = antigo.getHorario();
        hAntigo.setCapacidadeAtual(Math.max(0, hAntigo.getCapacidadeAtual() - 1));
        horarioDao.atualizar(hAntigo);

        novoHorario.setCapacidadeAtual(novoHorario.getCapacidadeAtual() + 1);
        horarioDao.atualizar(novoHorario);

        antigo.setHorario(novoHorario);
        antigo.setDataHora(LocalDateTime.of(antigo.getData(), novoHorario.getHora()));
        antigo.setStatus(StatusAgendamento.REMARCADO);
        dao.atualizar(antigo);
    }

// ===============================
// CHECK-IN
// ===============================
    public void fazerCheckin(Agendamento a, Usuario usuarioLogado) throws BusinessException {
        if (!a.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new BusinessException("Você não pode fazer check-in deste agendamento.");
        }
        if (!a.getData().isEqual(LocalDate.now())) {
            throw new BusinessException("O check-in só é permitido no dia do atendimento.");
        }

        a.setStatus(StatusAgendamento.EM_FILA);
        a.setHoraCheckin(LocalTime.now());
        dao.atualizar(a);
    }

    public void realizarCheckin(Agendamento agendamento) throws BusinessException {
        if (agendamento == null || agendamento.getId() == null) {
            throw new BusinessException("Agendamento inválido.");
        }
        Agendamento ag = dao.buscarPorId(agendamento.getId());
        if (ag == null) {
            throw new BusinessException("Agendamento não encontrado.");
        }
        if (!ag.getData().isEqual(LocalDate.now())) {
            throw new BusinessException("Check-in só é permitido na data do atendimento.");
        }

        ag.setStatus(StatusAgendamento.EM_FILA);
        ag.setHoraCheckin(LocalTime.now());
        dao.atualizar(ag);
    }

// ===============================
// FILA
// ===============================
    public Agendamento buscarProximoFila() {
        return dao.buscarProximoFila();
    }

    public long contarAgendamentosDoDia(LocalDate data) {
        return dao.contarPorData(data);
    }

    public Agendamento buscarUltimoChamado() {
        return dao.buscarUltimoChamado();
    }

    public List<Agendamento> listarUltimasChamadas(int limite) {
        return dao.listarUltimasChamadas(limite);
    }

    public int contarFila() {
        return dao.contarFila();
    }

    public Agendamento chamarProximoCompativel(List<Long> servicosIds, Long guicheId) {
        return dao.chamarProximoCompativel(servicosIds, guicheId);
    }

    public void atualizar(Agendamento agendamento) throws BusinessException {
        if (agendamento == null || agendamento.getId() == null) {
            throw new BusinessException("Agendamento inválido para atualização.");
        }
        dao.atualizar(agendamento);
    }

    public List<Agendamento> listarFilaOrdenada() {
        return dao.buscarProximoFilaOrdenado();
    }

    public Horario buscarHorarioPorId(Long id) {
        return horarioDao.buscarPorId(id);
    }

// ===============================
// REENCAMINHAMENTO
// ===============================
    public Agendamento gerarReencaminhamento(Usuario usuario, Long idServicoDestino) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo.");
        }
        if (idServicoDestino == null) {
            throw new IllegalArgumentException("Serviço de destino não informado.");
        }

        Servico servico = servicoDao.buscarPorId(idServicoDestino);
        if (servico == null) {
            throw new IllegalArgumentException("Serviço de destino não encontrado.");
        }

        Agendamento novo = new Agendamento();
        novo.setUsuario(usuario);
        novo.setServico(servico);
        novo.setStatus(StatusAgendamento.EM_FILA);
        novo.setData(LocalDate.now());
        novo.setDataHora(LocalDateTime.now());
        novo.setPrioridade("NORMAL");
        novo.setProtocolo(gerarProtocolo());

        dao.salvar(novo);
        return novo;
    }

// ===============================
// AUXILIARES
// ===============================
    private String gerarProtocolo() {
        String data = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int numero = (int) (Math.random() * 90000) + 10000;
        return data + "-" + numero;
    }

    public String formatarDataHora(Agendamento a) {
        return a.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                + " às " + a.getHorario().getHora().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public boolean podeReagendar(Agendamento a) {
        return Duration.between(LocalDateTime.now(),
                LocalDateTime.of(a.getData(), a.getHorario().getHora()))
                .toHours() >= 24
                && a.getStatus().equals(StatusAgendamento.AGENDADO);
    }

    public List<DayOfWeek> diasDisponiveisPorServico(Long idServico) {
        if (idServico == null) {
            return List.of();
        }

        List<AtendenteServicoEspaco> vinculos = em.createQuery(
                "SELECT v FROM AtendenteServicoEspaco v JOIN FETCH v.horario h "
                + "WHERE v.servico.id = :idServico", AtendenteServicoEspaco.class)
                .setParameter("idServico", idServico)
                .getResultList();

        return vinculos.stream()
                .map(v -> v.getHorario().getDiaSemana())
                .map(d -> DayOfWeek.valueOf(d.toUpperCase()))
                .distinct()
                .sorted()
                .toList();
    }

    private List<Horario> listarHorariosPorServico(Long idServico) {
        return horarioDao.listarPorServico(idServico);
    }

    public List<LocalDate> buscarDiasDisponiveisSimples(Long idServico, int dias) {
        List<Horario> horarios = listarHorariosPorServico(idServico);
        if (horarios.isEmpty()) {
            return List.of();
        }

        LocalDate hoje = LocalDate.now();

        return IntStream.range(0, dias)
                .mapToObj(hoje::plusDays)
                .filter(data -> {
                    String diaSemana = data.getDayOfWeek().name();
                    return horarios.stream().anyMatch(h
                            -> h.getDiaSemana().equalsIgnoreCase(diaSemana)
                            && h.getCapacidadeAtual() < h.getCapacidadeMax()
                    );
                })
                .collect(Collectors.toList());
    }

    public List<Horario> buscarHorariosPorServicoEDia(Long idServico, String diaSemana) {
        return horarioDao.buscarPorServicoEDia(idServico, diaSemana);

    }

    public List<Horario> buscarHorariosPorServico(Long idServico) {
        try {
            return em.createQuery(
                    "SELECT h FROM Horario h "
                    + "WHERE h.servico.id = :idServico "
                    + "AND h.ativo = TRUE "
                    + "AND h.disponivel = TRUE "
                    + "ORDER BY h.diaSemana, h.hora",
                    Horario.class)
                    .setParameter("idServico", idServico)
                    .getResultList();

        } catch (Exception e) {
            throw new BusinessException("Erro ao buscar horários por serviço: " + e.getMessage());
        }
    }

    public List<LocalDate> buscarDiasDisponiveisPorServico(Long idServico) {
        String sql = "SELECT DISTINCT CAST(h.data_hora AS DATE) as dia "
                + "FROM atendente_servico_espaco ase "
                + "JOIN horario h ON ase.id_horario = h.id "
                + "WHERE ase.id_servico = :idServico "
                + "AND h.data_hora >= CURRENT_DATE "
                + "ORDER BY h.data_hora";

        List<Date> datas = em.createNativeQuery(sql)
                .setParameter("idServico", idServico)
                .getResultList();

        return datas.stream()
                .map(d -> new java.sql.Date(d.getTime()).toLocalDate())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Horario> buscarHorariosPorServicoEDia(Long idServico, LocalDate dia) {
        String sql = "SELECT h.* FROM horario h "
                + "JOIN atendente_servico_espaco ase ON ase.id_horario = h.id "
                + "WHERE ase.id_servico = :idServico "
                + "AND CAST(h.data_hora AS DATE) = :dia "
                + "AND h.disponivel = true "
                + "ORDER BY h.data_hora";

        return em.createNativeQuery(sql, Horario.class)
                .setParameter("idServico", idServico)
                .setParameter("dia", java.sql.Date.valueOf(dia))
                .getResultList();

    }

    public Agendamento buscarProximoAgendamento() {
        return dao.buscarProximoAgendamento();
    }

    public List<Agendamento> listarPorData(LocalDate data) {
        return em.createQuery(
                "SELECT a FROM Agendamento a WHERE a.data = :data ORDER BY a.horario.hora ASC",
                Agendamento.class)
                .setParameter("data", data)
                .getResultList();
    }
}
