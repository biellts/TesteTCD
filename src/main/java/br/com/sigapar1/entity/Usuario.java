package br.com.sigapar1.entity;

import br.com.sigapar1.entity.EspacoAtendimento;
import br.com.sigapar1.entity.Guiche;
import br.com.sigapar1.entity.Role;
import br.com.sigapar1.entity.Servico;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true, nullable = false, length = 11)
    private String cpf;

    @Column(unique = true, nullable = false)
    private String email;

    private String telefone;
    private String senha;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "timestamp_status")
    private LocalDateTime timestampStatus;

    @Column(name = "motivo_pausa")
    private String motivoPausa;

    private boolean ativo = true;

    @Temporal(TemporalType.DATE)
    @Column(name = "data_nascimento")
    private Date dataNascimento;

    private String matricula;
    private String status;

    @ManyToOne
    @JoinColumn(name = "espaco_atendimento_id")
    private EspacoAtendimento espacoAtendimento;

    @ManyToOne
    @JoinColumn(name = "guiche_id")
    private Guiche guiche;

    @ManyToMany
    @JoinTable(
            name = "usuario_servico",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "servico_id")
    )
    private List<Servico> servicosAtendente;

    @Column(nullable = false)
    private boolean emailConfirmed = false;

    @Column(unique = true, length = 100)
    private String emailConfirmationToken;

    @Temporal(TemporalType.TIMESTAMP)
    private Date emailConfirmationExpires;

    public Usuario() {
    }

    public Usuario(Long id) {
        this.id = id;
    }

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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public EspacoAtendimento getEspacoAtendimento() {
        return espacoAtendimento;
    }

    public void setEspacoAtendimento(EspacoAtendimento espacoAtendimento) {
        this.espacoAtendimento = espacoAtendimento;
    }

    public Guiche getGuiche() {
        return guiche;
    }

    public void setGuiche(Guiche guiche) {
        this.guiche = guiche;
    }

    public List<Servico> getServicosAtendente() {
        return servicosAtendente;
    }

    public void setServicosAtendente(List<Servico> servicosAtendente) {
        this.servicosAtendente = servicosAtendente;
    }

    public boolean isEmailConfirmed() {
        return emailConfirmed;
    }

    public void setEmailConfirmed(boolean emailConfirmed) {
        this.emailConfirmed = emailConfirmed;
    }

    public String getEmailConfirmationToken() {
        return emailConfirmationToken;
    }

    public void setEmailConfirmationToken(String emailConfirmationToken) {
        this.emailConfirmationToken = emailConfirmationToken;
    }

    public Date getEmailConfirmationExpires() {
        return emailConfirmationExpires;
    }

    public void setEmailConfirmationExpires(Date emailConfirmationExpires) {
        this.emailConfirmationExpires = emailConfirmationExpires;
    }

    @Override
    public String toString() {
        return "Usuario{"
                + "id=" + id
                + ", nome='" + nome + '\''
                + ", email='" + email + '\''
                + '}';
    }

    public boolean isAtendente() {
        return role == Role.ROLE_ATTENDANT;
    }

    public LocalDateTime getTimestampStatus() {
        return timestampStatus;
    }

    public void setTimestampStatus(LocalDateTime timestampStatus) {
        this.timestampStatus = timestampStatus;
    }


    public String getMotivoPausa() {
        return motivoPausa;
    }

    public void setMotivoPausa(String motivoPausa) {
        this.motivoPausa = motivoPausa;
    }
    

}
