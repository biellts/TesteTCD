package br.com.sigapar1.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "servico_historico")
public class ServicoHistorico implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // referência para o serviço alterado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico;

    @Column(nullable = false, length = 120)
    private String campo;

    @Column(nullable = true, length = 1000)
    private String valorAnterior;

    @Column(nullable = true, length = 1000)
    private String valorNovo;

    @Column(name = "data_alteracao", nullable = false)
    private LocalDateTime dataAlteracao;

    // quem fez a alteração (opcional — se quiser adicionar depois)
    @Column(name = "usuario_alteracao", length = 100)
    private String usuarioAlteracao;

    public ServicoHistorico() {}

    public ServicoHistorico(Servico servico, String campo, String valorAnterior, String valorNovo, LocalDateTime dataAlteracao) {
        this.servico = servico;
        this.campo = campo;
        this.valorAnterior = valorAnterior;
        this.valorNovo = valorNovo;
        this.dataAlteracao = dataAlteracao;
    }

    // getters / setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Servico getServico() { return servico; }
    public void setServico(Servico servico) { this.servico = servico; }

    public String getCampo() { return campo; }
    public void setCampo(String campo) { this.campo = campo; }

    public String getValorAnterior() { return valorAnterior; }
    public void setValorAnterior(String valorAnterior) { this.valorAnterior = valorAnterior; }

    public String getValorNovo() { return valorNovo; }
    public void setValorNovo(String valorNovo) { this.valorNovo = valorNovo; }

    public LocalDateTime getDataAlteracao() { return dataAlteracao; }
    public void setDataAlteracao(LocalDateTime dataAlteracao) { this.dataAlteracao = dataAlteracao; }

    public String getUsuarioAlteracao() { return usuarioAlteracao; }
    public void setUsuarioAlteracao(String usuarioAlteracao) { this.usuarioAlteracao = usuarioAlteracao; }

    @Override
    public String toString() {
        return "ServicoHistorico{" +
                "id=" + id +
                ", servicoId=" + (servico != null ? servico.getId() : null) +
                ", campo='" + campo + '\'' +
                ", valorAnterior='" + valorAnterior + '\'' +
                ", valorNovo='" + valorNovo + '\'' +
                ", dataAlteracao=" + dataAlteracao +
                '}';
    }
}
