// EspacoAtendimento.java
package br.com.sigapar1.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "espaco_atendimento")
public class EspacoAtendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo; // identificador curto

    private String descricao;

    private boolean ativo = true;

    @OneToMany(mappedBy = "espaco", cascade = CascadeType.ALL)
    private List<Guiche> guiches;

    public EspacoAtendimento() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public List<Guiche> getGuiches() { return guiches; }
    public void setGuiches(List<Guiche> guiches) { this.guiches = guiches; }
}
