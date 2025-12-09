package br.com.sigapar1.service;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class DebugService {

    @PersistenceContext
    private EntityManager em;

    public void debugDados(Long idServico) {
        System.out.println("\n========== DEBUG DADOS ==========");

        // Check coluna correta
        try {
            List<?> colunas = em.createNativeQuery(
                "SELECT column_name FROM information_schema.columns WHERE table_name='atendente_servico_espaco'"
            ).getResultList();
            System.out.println("Colunas da tabela atendente_servico_espaco:");
            for (Object col : colunas) {
                System.out.println("  - " + col);
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar colunas: " + e.getMessage());
        }

        // Check Serviços
        try {
            List<?> servicos = em.createNativeQuery("SELECT COUNT(*) FROM servico WHERE ativo = TRUE").getResultList();
            System.out.println("Total de Serviços Ativos: " + servicos.get(0));
        } catch (Exception e) {
            System.out.println("Erro Serviços: " + e.getMessage());
        }

        // Check Vínculos
        try {
            List<?> vinculos = em.createNativeQuery("SELECT COUNT(*) FROM atendente_servico_espaco").getResultList();
            System.out.println("Total de Vínculos: " + vinculos.get(0));
        } catch (Exception e) {
            System.out.println("Erro Vínculos: " + e.getMessage());
        }

        // Check Horários
        try {
            List<?> horarios = em.createNativeQuery(
                "SELECT COUNT(*) FROM horario WHERE disponivel = TRUE AND ativo = TRUE"
            ).getResultList();
            System.out.println("Total de Horários Disponíveis: " + horarios.get(0));
        } catch (Exception e) {
            System.out.println("Erro Horários: " + e.getMessage());
        }

        System.out.println("================================\n");
    }
}