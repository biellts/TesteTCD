package br.com.sigapar1.service;

import br.com.sigapar1.dao.IntervaloBloqueadoDAO;
import br.com.sigapar1.entity.IntervaloBloqueado;
import br.com.sigapar1.util.BusinessException;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.time.LocalTime;
import java.util.List;

@Stateless
public class IntervaloBloqueadoService {

    @Inject
    private IntervaloBloqueadoDAO dao;

    public List<IntervaloBloqueado> listarPorDia(String dia) {
        return dao.listarPorDia(dia);
    }

    public void adicionar(String dia, LocalTime inicio, LocalTime fim) {

        if (inicio == null || fim == null)
            throw new BusinessException("Intervalo inválido.");

        if (!fim.isAfter(inicio))
            throw new BusinessException("Fim deve ser maior que início.");

        IntervaloBloqueado i = new IntervaloBloqueado();
        i.setDiaSemana(dia);
        i.setInicio(inicio);
        i.setFim(fim);

        dao.salvar(i);
    }

    public void remover(Long id) {
        dao.excluir(id);
    }
}
