package br.com.sigapar1.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "guiche")
public class Guiche implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Número visível ao público (Guichê 01, 02, 03...)
    @Column(nullable = false)
    private Integer numero;

    // Nome opcional (Guichê Prioritário, Atendimento Geral...)
    private String nome;

    // Se o guichê está ativo ou não
    @Column(nullable = false)
    private boolean ativo = true;

    // Se quiser amarrar guichê a algum espaço
    @ManyToOne
    private EspacoAtendimento espaco;

    public Guiche() {}

    public Guiche(Integer numero, String nome) {
        this.numero = numero;
        this.nome = nome;
        this.ativo = true;
    }

    // ------------------ GETTERS E SETTERS ------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public EspacoAtendimento getEspaco() {
        return espaco;
    }

    public void setEspaco(EspacoAtendimento espaco) {
        this.espaco = espaco;
    }

    @Override
    public String toString() {
        return "Guiche{" +
                "id=" + id +
                ", numero=" + numero +
                ", nome='" + nome + '\'' +
                ", ativo=" + ativo +
                '}';
    }
}
