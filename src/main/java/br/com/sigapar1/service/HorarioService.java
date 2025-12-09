package br.com.sigapar1.service;

import br.com.sigapar1.dao.HorarioDAO;
import br.com.sigapar1.dao.IntervaloBloqueadoDAO;
import br.com.sigapar1.entity.Horario;
import br.com.sigapar1.entity.IntervaloBloqueado;
import br.com.sigapar1.util.BusinessException;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.time.LocalTime;
import java.util.List;

@Stateless
public class HorarioService {

    @Inject
    private HorarioDAO dao;

    @Inject
    private IntervaloBloqueadoDAO intervaloDAO;

    // --------------------------------------------------
    // LISTAGEM
    // --------------------------------------------------
    public List<Horario> listarTodos() {
        return dao.listarTodos();
    }

    public Horario buscarPorId(Long id) {
        return dao.buscarPorId(id);
    }

    /**
     * Lista horários por serviço + dia da semana.
     */
    public List<Horario> listarPorServicoEDia(Long servicoId, String diaSemana) {
        return dao.buscarPorServicoEDia(servicoId, diaSemana);
    }

    // --------------------------------------------------
    // GERAR HORÁRIOS
    // --------------------------------------------------
    public void gerarHorarios(String diaSemana,
                              LocalTime inicio,
                              LocalTime fim,
                              int intervalo,
                              int capacidade) {

        // VALIDAÇÕES
        if (diaSemana == null || diaSemana.isBlank()) {
            throw new BusinessException("O dia da semana é obrigatório.");
        }

        if (inicio == null || fim == null) {
            throw new BusinessException("Horário inicial e final são obrigatórios.");
        }

        if (!fim.isAfter(inicio)) {
            throw new BusinessException("O horário final deve ser maior que o inicial.");
        }

        if (intervalo <= 0) {
            throw new BusinessException("O intervalo deve ser maior que zero.");
        }

        if (capacidade <= 0) {
            throw new BusinessException("A capacidade deve ser maior que zero.");
        }

        // BLOQUEIOS
        List<IntervaloBloqueado> bloqueados = intervaloDAO.listarPorDia(diaSemana);

        LocalTime atual = inicio;

        // GERAÇÃO
        while (atual.isBefore(fim)) {

            LocalTime proximo = atual.plusMinutes(intervalo);

            boolean horarioBloqueado = false;

            if (bloqueados != null && !bloqueados.isEmpty()) {
                for (IntervaloBloqueado b : bloqueados) {

                    boolean intersecta =
                        !(proximo.isBefore(b.getInicio()) || atual.isAfter(b.getFim()));

                    if (intersecta) {
                        horarioBloqueado = true;
                        break;
                    }
                }
            }

            if (!horarioBloqueado) {

                boolean existe = dao.existeHorario(diaSemana, atual);

                if (!existe) {
                    Horario h = new Horario();
                    h.setDiaSemana(diaSemana);
                    h.setHora(atual);

                    h.setAtivo(true);
                    h.setDisponivel(true);

                    h.setCapacidadeMax(capacidade);
                    h.setCapacidadeAtual(0);

                    dao.salvar(h);
                }
            }

            atual = proximo;
        }
    }

    // --------------------------------------------------
    // ALTERAR STATUS
    // --------------------------------------------------
    public void alterarStatus(Long id) {
        Horario h = dao.buscarPorId(id);
        if (h == null) return;

        h.setDisponivel(!h.isDisponivel());
        dao.atualizar(h);
    }

    // --------------------------------------------------
    // REMOVER
    // --------------------------------------------------
    public void remover(Long id) {
        dao.excluir(id);
    }
}
