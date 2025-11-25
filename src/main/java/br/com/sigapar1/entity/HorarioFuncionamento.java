// HorarioFuncionamento.java
package br.com.sigapar1.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "horario_funcionamento")
public class HorarioFuncionamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1 = segunda ... 7 = domingo
    private int diaSemana;

    private LocalTime abertura;

    private LocalTime fechamento;

    // exemplo "12:00-13:00", pode guardar múltiplos separado por ;
    private String intervalosBloqueados;

    // capacidade máxima de atendimentos por slot (opcional)
    private Integer capacidade; // null = sem limite explícito

    public HorarioFuncionamento() {}

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getDiaSemana() { return diaSemana; }
    public void setDiaSemana(int diaSemana) { this.diaSemana = diaSemana; }

    public LocalTime getAbertura() { return abertura; }
    public void setAbertura(LocalTime abertura) { this.abertura = abertura; }

    public LocalTime getFechamento() { return fechamento; }
    public void setFechamento(LocalTime fechamento) { this.fechamento = fechamento; }

    public String getIntervalosBloqueados() { return intervalosBloqueados; }
    public void setIntervalosBloqueados(String intervalosBloqueados) { this.intervalosBloqueados = intervalosBloqueados; }

    public Integer getCapacidade() { return capacidade; }
    public void setCapacidade(Integer capacidade) { this.capacidade = capacidade; }
}
