// Guiche.java
package br.com.sigapar1.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "guiche")
public class Guiche {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String identificador;

    private String descricao;

    private boolean ativo = true;

    @ManyToOne
    private EspacoAtendimento espaco;

    @OneToMany(mappedBy = "guiche")
    private List<Usuario> atendentes; // usuários com role ATTENDANT

    public Guiche() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIdentificador() { return identificador; }
    public void setIdentificador(String identificador) { this.identificador = identificador; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public EspacoAtendimento getEspaco() { return espaco; }
    public void setEspaco(EspacoAtendimento espaco) { this.espaco = espaco; }

    public List<Usuario> getAtendentes() { return atendentes; }
    public void setAtendentes(List<Usuario> atendentes) { this.atendentes = atendentes; }
}
