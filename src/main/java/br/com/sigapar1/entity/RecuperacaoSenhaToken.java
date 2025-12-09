package br.com.sigapar1.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
public class RecuperacaoSenhaToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String token;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiracao;

    @OneToOne
    private Usuario usuario;

    public RecuperacaoSenhaToken() {}

    public RecuperacaoSenhaToken(Usuario usuario) {
        this.usuario = usuario;
        this.token = UUID.randomUUID().toString();
        this.expiracao = new Date(System.currentTimeMillis() + 3600000); // 1h
    }

    public boolean isValido() {
        return new Date().before(expiracao);
    }

    public Long getId() { return id; }
    public String getToken() { return token; }
    public Date getExpiracao() { return expiracao; }
    public Usuario getUsuario() { return usuario; }

    public void setId(Long id) { this.id = id; }
    public void setToken(String token) { this.token = token; }
    public void setExpiracao(Date expiracao) { this.expiracao = expiracao; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
