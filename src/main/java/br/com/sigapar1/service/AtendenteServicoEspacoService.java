package br.com.sigapar1.service;

import br.com.sigapar1.dao.AtendenteServicoEspacoDAO;
import br.com.sigapar1.entity.Agendamento;
import br.com.sigapar1.entity.AtendenteServicoEspaco;
import br.com.sigapar1.entity.EspacoAtendimento;
import br.com.sigapar1.entity.Horario;
import br.com.sigapar1.entity.Servico;
import br.com.sigapar1.entity.Usuario;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class AtendenteServicoEspacoService {
    @Inject
    private AtendenteServicoEspacoDAO dao;

    // ====================== CRUD ======================
    public void salvar(AtendenteServicoEspaco v) {
        List<AtendenteServicoEspaco> duplicados = dao.buscarDuplicados(
                v.getAtendente().getId(),
                v.getServico().getId(),
                v.getEspaco().getId(),
                v.getHorario().getId()
        );

        boolean isEdicao = v.getId() != null;
        boolean jaExiste = duplicados.stream()
                .anyMatch(d -> !d.getId().equals(v.getId()));

        if (jaExiste && !isEdicao) {
            throw new RuntimeException("Este vínculo já existe no sistema.");
        }

        if (isEdicao) {
            dao.atualizar(v);
        } else {
            dao.salvar(v);
        }
    }

    public void excluir(Long id) {
        dao.excluir(id);
    }

    public AtendenteServicoEspaco buscarPorId(Long id) {
        return dao.buscarPorId(id);
    }

    public List<AtendenteServicoEspaco> listar() {
        return dao.listarTodos();
    }

    // ====================== CONSULTAS ======================
    public List<AtendenteServicoEspaco> listarPorAtendente(Long id) {
        return dao.listarPorAtendente(id);
    }

    public List<AtendenteServicoEspaco> listarPorServico(Long id) {
        return dao.listarPorServico(id);
    }

    public List<AtendenteServicoEspaco> listarPorEspaco(Long id) {
        return dao.listarPorEspaco(id);
    }

    public List<AtendenteServicoEspaco> listarPorHorario(Long id) {
        return dao.listarPorHorario(id);
    }

    public List<AtendenteServicoEspaco> buscarDuplicados(Long atendenteId, Long servicoId, Long espacoId, Long horarioId) {
        return dao.buscarDuplicados(atendenteId, servicoId, espacoId, horarioId);
    }

    // ====================== HORÁRIOS ======================
    public Horario buscarHorarioPorId(Long id) {
        if (id == null) {
            return null;
        }
        return dao.buscarHorarioPorId(id);
    }

    public List<Horario> listarHorariosAtivos() {
        return dao.listarHorariosAtivos();
    }

    /**
     * FORMA MAIS IDIOTA, MAIS SIMPLES E 100% FUNCIONAL: Pega os horários direto
     * da tabela de vínculos.
     */
    public List<Horario> buscarHorariosPorServico(Long idServico) {

        List<AtendenteServicoEspaco> vinculos
                = dao.listarTodosPorServico(idServico); // <-- use o DAO

        List<Horario> horarios = new ArrayList<>();

        for (AtendenteServicoEspaco v : vinculos) {
            if (v.getHorario() != null) {
                horarios.add(v.getHorario());
            }
        }

        return horarios;
    }

    public boolean existeVinculo(Long atendenteId, Long servicoId, Long espacoId, Long horarioId) {
        return !buscarDuplicados(atendenteId, servicoId, espacoId, horarioId).isEmpty();
    }

    public List<EspacoAtendimento> listarEspacosPorAtendente(Long atendenteId) {
        if (atendenteId == null) {
            return List.of();
        }
        return dao.listarPorAtendente(atendenteId)
                .stream()
                .map(AtendenteServicoEspaco::getEspaco)
                .distinct()
                .toList();
    }

    // ====================== DISPONIBILIDADE ======================
    public List<String> diasDisponiveisPorServico(Long servicoId) {
        return dao.listarTodos().stream()
                .filter(v -> v.getServico().getId().equals(servicoId))
                .map(v -> v.getHorario().getDiaSemana())
                .distinct()
                .toList();
    }

    public List<Horario> horariosPorServicoEDia(Long servicoId, String dia) {
        return dao.listarTodos().stream()
                .filter(v -> v.getServico().getId().equals(servicoId))
                .filter(v -> v.getHorario().getDiaSemana().equalsIgnoreCase(dia))
                .filter(v -> v.getHorario().isAtivo())
                .filter(v -> v.getHorario().getCapacidadeAtual() < v.getHorario().getCapacidadeMax())
                .map(AtendenteServicoEspaco::getHorario)
                .distinct()
                .toList();
    }


}
