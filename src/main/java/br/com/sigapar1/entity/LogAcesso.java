package br.com.sigapar1.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "log_acesso")
public class LogAcesso implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String role;

    private String ip;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataHora = new Date();

    public LogAcesso() {}

    public LogAcesso(String email, String role, String ip) {
        this.email = email;
        this.role = role;
        this.ip = ip;
    }

}
