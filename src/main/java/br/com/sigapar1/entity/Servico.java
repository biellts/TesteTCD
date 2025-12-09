package br.com.sigapar1.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "servico")
public class Servico implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String nome;

    @Column(length = 1000)
    private String descricao;

    // Nome da coluna no banco é exatamente "tempoestimado"
    @Column(name = "tempoestimado", nullable = false)
    private int tempoestimado;

    @Column(name = "documentos_necessarios", length = 1000)
    private String documentosNecessarios;

    @Column(nullable = false)
    private boolean ativo = true;

    // ====================== CONSTRUTORES ======================
    public Servico() {}

    public Servico(Long id) {
        this.id = id;
    }

    // ====================== GETTERS E SETTERS ======================
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getTempoEstimado() {
        return tempoestimado;
    }

    public void setTempoEstimado(int tempoestimado) {
        this.tempoestimado = tempoestimado;
    }

    public String getDocumentosNecessarios() {
        return documentosNecessarios;
    }

    public void setDocumentosNecessarios(String documentosNecessarios) {
        this.documentosNecessarios = documentosNecessarios;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    // ====================== TO STRING ======================
    // Mostra somente o nome ao exibir em selects, tabelas, logs etc.
    @Override
    public String toString() {
        return nome != null ? nome : "Serviço sem nome";
    }
}
