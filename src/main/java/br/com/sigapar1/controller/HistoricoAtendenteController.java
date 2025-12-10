
import br.com.sigapar1.controller.LoginController;
import br.com.sigapar1.entity.Agendamento;
import br.com.sigapar1.entity.Horario;
import br.com.sigapar1.entity.Usuario;
import br.com.sigapar1.service.AgendamentoSimplificadoService;
import br.com.sigapar1.util.JsfUtil;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Named("historicoAtendenteController")
@ViewScoped
public class HistoricoAtendenteController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private AgendamentoSimplificadoService service;

    @Inject
    private LoginController loginController;

    private List<Agendamento> agendamentosDoAtendente = new ArrayList<>();
    private String protocoloBusca; // Campo para busca por protocolo
    private Agendamento agendamentoSelecionado;

    @PostConstruct
    public void init() {
        carregarAgendamentosDoAtendente();
    }

    // ==========================
    // CARREGAR AGENDAMENTOS DO ATENDENTE
    // ==========================
    public void carregarAgendamentosDoAtendente() {
        try {
            Usuario atendente = loginController.getUsuarioLogado();
            if (atendente != null && atendente.isAtendente()) { // assumindo que tem um método isAtendente()
                agendamentosDoAtendente = service.listarAgendamentosDoAtendente(atendente);
            } else {
                agendamentosDoAtendente = new ArrayList<>();
                JsfUtil.addErrorMessage("Usuário não é atendente ou não está logado.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            agendamentosDoAtendente = new ArrayList<>();
        }
    }

    // ==========================
    // BUSCA POR PROTOCOLO
    // ==========================
    public void buscarPorProtocolo() {
        if (protocoloBusca == null || protocoloBusca.trim().isEmpty()) {
            carregarAgendamentosDoAtendente(); // recarrega todos
            JsfUtil.addInfoMessage("Digite um protocolo para buscar.");
            return;
        }

        Agendamento encontrado = service.buscarPorProtocolo(protocoloBusca.trim());
        if (encontrado != null) {
            // Verifica se pertence ao atendente logado
            if (encontrado.getAtendente() != null
                    && encontrado.getAtendente().getId().equals(loginController.getUsuarioLogado().getId())) {
                agendamentosDoAtendente = List.of(encontrado);
            } else {
                agendamentosDoAtendente = new ArrayList<>();
                JsfUtil.addErrorMessage("Protocolo não encontrado ou não pertence a você.");
            }
        } else {
            agendamentosDoAtendente = new ArrayList<>();
            JsfUtil.addErrorMessage("Protocolo não encontrado.");
        }
    }
    public String formatarData(LocalDate data) {
        if (data == null) {
            return "";
        }
        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy (EEEE)", new Locale("pt", "BR")));
    }

    public String formatarHora(Horario horario) {
        if (horario == null || horario.getHora() == null) {
            return "";
        }
        return horario.getHora().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    // Getters e Setters
    public List<Agendamento> getAgendamentosDoAtendente() {
        return agendamentosDoAtendente;
    }

    public String getProtocoloBusca() {
        return protocoloBusca;
    }

    public void setProtocoloBusca(String protocoloBusca) {
        this.protocoloBusca = protocoloBusca;
    }

    public Agendamento getAgendamentoSelecionado() {
        return agendamentoSelecionado;
    }
}
