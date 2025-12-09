package br.com.sigapar1.controller;

import br.com.sigapar1.entity.Agendamento;
import br.com.sigapar1.service.AgendamentoSimplificadoService;
import br.com.sigapar1.util.JsfUtil;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ComponentSystemEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

@Named("sucessoController")
@ViewScoped
public class SucessoController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private AgendamentoSimplificadoService agendamentoService;

    private String protocolo;
    private Agendamento agendamento;

    @PostConstruct
    public void init() {
        carregarProtocoloDaURL();
    }

    /**
     * Necessário para funcionar com:
     * <f:event type="preRenderView" listener="#{sucessoController.carregarDados}" />
     */
    public void carregarDados(ComponentSystemEvent event) {
        carregarDados();
    }

    /** Método real que executa a lógica */
    public void carregarDados() {
        try {
            if (protocolo == null || protocolo.isBlank()) {
                carregarProtocoloDaURL();
            }

            if (protocolo != null && !protocolo.isBlank()) {
                agendamento = agendamentoService.buscarPorProtocolo(protocolo);
            } else {
                JsfUtil.addWarn("Nenhum protocolo recebido.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JsfUtil.addError("Erro ao carregar dados do agendamento: " + e.getMessage());
        }
    }

    /** Lê o protocolo passado por parâmetro na URL */
    private void carregarProtocoloDaURL() {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        Map<String, String> params = ec.getRequestParameterMap();
        protocolo = params.get("protocolo");
    }

    /** Baixar PDF */
    public void baixarPdf() {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();

            externalContext.setResponseContentType("application/pdf");
            externalContext.setResponseHeader(
                    "Content-Disposition",
                    "attachment; filename=Agendamento_" + protocolo + ".pdf"
            );

            OutputStream out = externalContext.getResponseOutputStream();

            Document pdf = new Document();
            PdfWriter writer = PdfWriter.getInstance(pdf, out);

            pdf.open();

            Paragraph titulo = new Paragraph("Comprovante de Agendamento",
                    new Font(Font.HELVETICA, 22, Font.BOLD));
            titulo.setAlignment(Element.ALIGN_CENTER);
            pdf.add(titulo);

            pdf.add(new Paragraph("\n"));

            Paragraph pProtocolo = new Paragraph("Protocolo: " + protocolo,
                    new Font(Font.HELVETICA, 18, Font.BOLD));
            pProtocolo.setAlignment(Element.ALIGN_CENTER);
            pdf.add(pProtocolo);

            pdf.add(new Paragraph("\n\n"));

            // QR CODE
            String qrUrl = "https://chart.googleapis.com/chart?cht=qr&chs=300x300&chl=" + protocolo;
            java.awt.Image awtImage = javax.imageio.ImageIO.read(new java.net.URL(qrUrl));
            Image qrImage = Image.getInstance(awtImage, null);
            qrImage.scaleAbsolute(170, 170);
            qrImage.setAlignment(Image.ALIGN_CENTER);
            pdf.add(qrImage);

            pdf.add(new Paragraph("\n"));

            if (agendamento != null) {
                pdf.add(new Paragraph("Serviço: " + agendamento.getServico().getNome(), new Font(Font.HELVETICA, 12)));
                pdf.add(new Paragraph("Data: " + agendamento.getData().toString(), new Font(Font.HELVETICA, 12)));
                pdf.add(new Paragraph("Horário: " + agendamento.getHorario().getHora().toString(), new Font(Font.HELVETICA, 12)));
                pdf.add(new Paragraph("Usuário: " + agendamento.getUsuario().getNome(), new Font(Font.HELVETICA, 12)));
                pdf.add(new Paragraph("\n"));
            }

            pdf.add(new Paragraph("Comprovante gerado automaticamente pelo sistema SIGAPAR.",
                    new Font(Font.HELVETICA, 10)));

            pdf.close();
            writer.close();

            facesContext.responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
            JsfUtil.addErrorMessage("Erro ao gerar PDF: " + e.getMessage());
        }
    }

    // GETTERS E SETTERS
    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public Agendamento getAgendamento() {
        return agendamento;
    }
}
