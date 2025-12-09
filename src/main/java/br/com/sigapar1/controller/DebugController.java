package br.com.sigapar1.controller;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.List;

@Named
@ViewScoped
public class DebugController implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext
    private EntityManager em;

    private String resultado = "";

    public void debugQuery() {
        StringBuilder sb = new StringBuilder();
        
        try {
            // 1. Buscar horários para SATURDAY (sábado)
            List<?> horariosServico7 = em.createNativeQuery(
                "SELECT h.id, h.dia_semana, h.hora, h.disponivel, h.ativo " +
                "FROM horario h " +
                "INNER JOIN atendente_servico_espaco ase ON ase.horario_id = h.id " +
                "WHERE ase.servico_id = 7 " +
                "AND UPPER(h.dia_semana) = 'SATURDAY'"
            ).getResultList();
            
            sb.append("=== HORÁRIOS PARA SERVIÇO 7 NO SÁBADO ===\n");
            for (Object obj : horariosServico7) {
                Object[] row = (Object[]) obj;
                sb.append("ID: ").append(row[0])
                  .append(" | DIA: ").append(row[1])
                  .append(" | HORA: ").append(row[2])
                  .append(" | DISP: ").append(row[3])
                  .append(" | ATIVO: ").append(row[4])
                  .append("\n");
            }
            sb.append("Total: ").append(horariosServico7.size()).append("\n\n");

            // 2. Buscar usando JPA/Hibernate
            List<?> comHibernate = em.createQuery(
                "SELECT h FROM Horario h " +
                "INNER JOIN AtendenteServicoEspaco ase ON ase.horario = h " +
                "WHERE ase.servico.id = 7 " +
                "AND h.disponivel = TRUE " +
                "AND h.ativo = TRUE"
            ).getResultList();
            
            sb.append("=== HORÁRIOS VIA HIBERNATE ===\n");
            for (Object obj : comHibernate) {
                if (obj != null) {
                    sb.append("Classe: ").append(obj.getClass().getSimpleName()).append("\n");
                    sb.append("Object: ").append(obj).append("\n");
                }
            }
            sb.append("Total: ").append(comHibernate.size()).append("\n");

        } catch (Exception e) {
            sb.append("ERRO: ").append(e.getMessage()).append("\n");
            e.printStackTrace();
        }

        resultado = sb.toString();
        System.out.println(resultado);
    }

    public String getResultado() {
        return resultado;
    }
}