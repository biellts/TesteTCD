package br.com.sigapar1.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true, nullable = false)
    private String cpf;

    @Column(unique = true, nullable = false)
    private String email;

    private String telefone;

    private String senha;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean ativo = true;

    @Column(name = "data_nascimento") // ⚠ correção aqui
    private LocalDate dataNascimento;

    private String matricula;

    @ManyToOne
    private Guiche guiche;

    @ManyToMany
    @JoinTable(
        name = "usuario_servico",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "servico_id")
    )
    private List<Servico> servicosAtendente;

    public Usuario() {}

    public Usuario(Long id) {
        this.id = id;
    }

    // Getters e setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public Guiche getGuiche() { return guiche; }
    public void setGuiche(Guiche guiche) { this.guiche = guiche; }

    public List<Servico> getServicosAtendente() { return servicosAtendente; }
    public void setServicosAtendente(List<Servico> servicosAtendente) { this.servicosAtendente = servicosAtendente; }

    @Override
    public String toString() { return nome; }

    public boolean isAtendente() { return role == Role.ROLE_ATTENDANT; }
}
