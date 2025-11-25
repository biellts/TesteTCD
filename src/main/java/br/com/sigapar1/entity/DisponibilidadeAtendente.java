// DisponibilidadeAtendente.java
package br.com.sigapar1.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "disponibilidade_atendente")
public class DisponibilidadeAtendente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private StatusAtendente status;

    private LocalDateTime alteradoEm;

    private String motivo;

    @ManyToOne
    private Usuario atendente;

    public DisponibilidadeAtendente() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StatusAtendente getStatus() { return status; }
    public void setStatus(StatusAtendente status) { this.status = status; }

    public LocalDateTime getAlteradoEm() { return alteradoEm; }
    public void setAlteradoEm(LocalDateTime alteradoEm) { this.alteradoEm = alteradoEm; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Usuario getAtendente() { return atendente; }
    public void setAtendente(Usuario atendente) { this.atendente = atendente; }
}
