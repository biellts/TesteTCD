package br.com.sigapar1.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "espaco_atendimento")
public class EspacoAtendimento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String descricao;

    @Column(nullable = false)
    private boolean ativo = true;

    // >>> Associação obrigatória RF024
    @ManyToMany
    @JoinTable(
            name = "espaco_servico",
            joinColumns = @JoinColumn(name = "espaco_id"),
            inverseJoinColumns = @JoinColumn(name = "servico_id")
    )
    private List<Servico> servicos;

    public EspacoAtendimento() {}
    public EspacoAtendimento(Long id) { this.id = id; }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public List<Servico> getServicos() { return servicos; }
    public void setServicos(List<Servico> servicos) { this.servicos = servicos; }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
