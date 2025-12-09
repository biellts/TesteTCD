package br.com.sigapar1.controller;

import br.com.sigapar1.entity.*;
import br.com.sigapar1.entity.Role;
import br.com.sigapar1.service.*;
import br.com.sigapar1.util.JsfUtil;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named("vinculoController")
@ViewScoped
public class AtendenteServicoEspacoController implements Serializable {

    @Inject
    private AtendenteServicoEspacoService service;
    @Inject
    private UsuarioService usuarioService;
    @Inject
    private ServicoService servicoService;
    @Inject
    private EspacoAtendimentoService espacoService;

    private AtendenteServicoEspaco vinculo;
    private List<AtendenteServicoEspaco> lista;

    private Long atendenteId;
    private Long servicoId;
    private Long espacoId;
    private Long horarioId;

    @PostConstruct
    public void init() {
        vinculo = new AtendenteServicoEspaco();
        carregarLista();
    }

    private void carregarLista() {
        lista = service.listar();
    }

    public void salvar() {
        try {
            if (atendenteId == null || servicoId == null || espacoId == null || horarioId == null) {
                JsfUtil.addError("Preencha todos os campos obrigatórios.");
                return;
            }

            List<AtendenteServicoEspaco> duplicados = service.buscarDuplicados(
                    atendenteId, servicoId, espacoId, horarioId);

            if (!duplicados.isEmpty()) {
                JsfUtil.addError("Este vínculo já existe!");
                return;
            }

            AtendenteServicoEspaco novo = new AtendenteServicoEspaco();

            Usuario atendente = usuarioService.buscarPorId(atendenteId);
            Servico servico = servicoService.buscarPorId(servicoId);
            EspacoAtendimento espaco = espacoService.buscarPorId(espacoId);
            Horario horario = service.buscarHorarioPorId(horarioId);

            if (atendente == null || servico == null || espaco == null || horario == null) {
                JsfUtil.addError("Um dos itens selecionados não foi encontrado.");
                return;
            }

            novo.setAtendente(atendente);
            novo.setServico(servico);
            novo.setEspaco(espaco);
            novo.setHorario(horario);

            service.salvar(novo);

            JsfUtil.addSuccess("Vínculo criado com sucesso!");
            limparCampos();
            carregarLista();

        } catch (Exception e) {
            JsfUtil.addError("Erro ao salvar vínculo: " + e.getMessage());
        }
    }

    public void excluir(Long id) {
        try {
            service.excluir(id);
            JsfUtil.addSuccess("Vínculo excluído com sucesso!");
            carregarLista();
        } catch (Exception e) {
            JsfUtil.addError("Erro ao excluir: " + e.getMessage());
        }
    }

    private void limparCampos() {
        atendenteId = null;
        servicoId = null;
        espacoId = null;
        horarioId = null;
    }

    public List<Usuario> getAtendentes() {
        return usuarioService.listarPorRole(Role.ROLE_ATTENDANT);
    }

    public List<Servico> getServicosAtivos() {
        return servicoService.listarAtivos();
    }

    public List<EspacoAtendimento> getEspacosAtivos() {
        return espacoService.listarTodos();
    }

    public List<Horario> getHorariosAtivos() {
        return service.listarHorariosAtivos();
    }

    public AtendenteServicoEspaco getVinculo() {
        return vinculo;
    }

    public void setVinculo(AtendenteServicoEspaco vinculo) {
        this.vinculo = vinculo;
    }

    public List<AtendenteServicoEspaco> getLista() {
        return lista;
    }

    public Long getAtendenteId() {
        return atendenteId;
    }

    public void setAtendenteId(Long atendenteId) {
        this.atendenteId = atendenteId;
    }

    public Long getServicoId() {
        return servicoId;
    }

    public void setServicoId(Long servicoId) {
        this.servicoId = servicoId;
    }

    public Long getEspacoId() {
        return espacoId;
    }

    public void setEspacoId(Long espacoId) {
        this.espacoId = espacoId;
    }

    public Long getHorarioId() {
        return horarioId;
    }

    public void setHorarioId(Long horarioId) {
        this.horarioId = horarioId;
    }

    public String getDiaSemanaFormatado(String dia) {
        return switch (dia) {
            case "MONDAY" ->
                "Segunda-feira";
            case "TUESDAY" ->
                "Terça-feira";
            case "WEDNESDAY" ->
                "Quarta-feira";
            case "THURSDAY" ->
                "Quinta-feira";
            case "FRIDAY" ->
                "Sexta-feira";
            case "SATURDAY" ->
                "Sábado";
            case "SUNDAY" ->
                "Domingo";
            default ->
                dia;
        };
    }

    // ===================== NOVO MÉTODO =====================
    public List<AtendenteServicoEspaco> buscarVinculosDisponiveis(Long idServico, Long idHorario) {
        if (idServico == null || idHorario == null) {
            return List.of();
        }
        return service.listar().stream()
                .filter(v -> v.getServico().getId().equals(idServico))
                .filter(v -> v.getHorario().getId().equals(idHorario))
                .filter(v -> v.getHorario().getCapacidadeAtual() < v.getHorario().getCapacidadeMax())
                .toList();
    }

    public String getEspacoDoAtendente(Usuario u) {
        if (u.getGuiche() != null) {
            return u.getGuiche().getNome();
        }

        List<EspacoAtendimento> espacos = service.listarEspacosPorAtendente(u.getId());
        if (!espacos.isEmpty()) {
            return espacos.get(0).getDescricao(); // ou getNome(), se tiver
        }

        return "-";
    }

    public List<Horario> getHorariosPorServico() {
        if (servicoId == null) {
            return List.of();
        }

        return service.buscarHorariosPorServico(servicoId);
    }

}
