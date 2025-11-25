// Servico.java
package br.com.sigapar1.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "servico")
public class Servico implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nome;

    private String descricao;

    private boolean ativo = true;

    // tempo estimado em minutos
    private int tempoEstimado;

    public Servico() {}

    public Servico(Long id) { this.id = id; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public int getTempoEstimado() { return tempoEstimado; }
    public void setTempoEstimado(int tempoEstimado) { this.tempoEstimado = tempoEstimado; }

    @Override
    public String toString() { return nome; }
}
