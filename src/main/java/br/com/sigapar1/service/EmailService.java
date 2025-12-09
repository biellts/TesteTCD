package br.com.sigapar1.service;

import br.com.sigapar1.entity.Agendamento;
import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

@Stateless
public class EmailService {

    @Resource(lookup = "java:jboss/mail/SigaparMail")
    private Session mailSession;

    public EmailService() {
    }

    // ============================================================
    // 1) EMAIL DE RECUPERAÇÃO DE SENHA
    // ============================================================
    public void enviarEmailRecuperacao(String emailDestino, String nome, String link) {
        try {
            MimeMessage mensagem = new MimeMessage(mailSession);
            mensagem.setRecipients(RecipientType.TO, InternetAddress.parse(emailDestino));
            mensagem.setSubject("Recuperação de Senha - SIGAPAR", "UTF-8");

            MimeBodyPart corpo = new MimeBodyPart();
            corpo.setContent(gerarTemplateHtmlRecuperacao(nome, link), "text/html; charset=UTF-8");

            MimeMultipart multi = new MimeMultipart();
            multi.addBodyPart(corpo);
            mensagem.setContent(multi);

            Transport.send(mensagem);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String gerarTemplateHtmlRecuperacao(String nome, String link) {
        return """
            <html><body style='background:#f4f4f4; font-family:Arial;'>
                <div style='width:600px; margin:auto; background:white; border-radius:8px;'>
                    <div style='background:#004AAD; padding:20px; text-align:center; color:white;'>
                        <h2>Recuperação de Senha - SIGAPAR</h2>
                    </div>

                    <div style='padding:30px; color:#333; font-size:16px;'>
                        <p>Olá, <b>%s</b>,</p>
                        <p>Você solicitou recuperar sua senha.</p>

                        <p style='text-align:center; margin:30px 0;'>
                            <a href='%s'
                               style='background:#004AAD; color:white; padding:12px 20px; text-decoration:none; border-radius:5px;'>
                               Recuperar Senha
                            </a>
                        </p>

                        <p>Se não foi você, apenas ignore este e-mail.</p>
                        <p style='font-size:12px; color:#777;'>Link válido por 1 hora.</p>
                    </div>
                </div>
            </body></html>
        """.formatted(nome, link);
    }

    // ============================================================
    // 2) EMAIL DE CONFIRMAÇÃO DE CADASTRO (AGORA LEVA DIRETO AO LOGIN)
    // ============================================================
    public void enviarEmailConfirmacao(String emailDestino, String nome, String linkIgnorado) {
        try {
            // O link agora sempre vai para o login
            String link = "http://localhost:8080/sigapar/usuarios/login_usuario.xhtml";

            MimeMessage mensagem = new MimeMessage(mailSession);
            mensagem.setRecipients(RecipientType.TO, InternetAddress.parse(emailDestino));
            mensagem.setSubject("Confirmação de Cadastro - SIGAPAR", "UTF-8");

            MimeBodyPart corpo = new MimeBodyPart();
            corpo.setContent(gerarTemplateHtmlConfirmacao(nome, link), "text/html; charset=UTF-8");

            MimeMultipart multi = new MimeMultipart();
            multi.addBodyPart(corpo);
            mensagem.setContent(multi);

            Transport.send(mensagem);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String gerarTemplateHtmlConfirmacao(String nome, String link) {
        return """
            <html><body style='background:#f4f4f4; font-family:Arial;'>
                <div style='width:600px; margin:auto; background:white; border-radius:8px;'>
                    <div style='background:#004AAD; padding:20px; text-align:center; color:white;'>
                        <h2>Confirmação de Cadastro - SIGAPAR</h2>
                    </div>

                    <div style='padding:30px; color:#333; font-size:16px;'>
                        <p>Olá, <b>%s</b>,</p>
                        <p>Obrigado por se cadastrar no sistema SIGAPAR.</p>
                        <p>Clique no botão abaixo para acessar o login:</p>

                        <p style='text-align:center; margin:30px 0;'>
                            <a href='%s'
                               style='background:#28a745; color:white; padding:12px 20px;
                               text-decoration:none; border-radius:5px;'>
                               Acessar Login
                            </a>
                        </p>

                        <p>Se você não fez este cadastro, ignore este e-mail.</p>
                    </div>
                </div>
            </body></html>
        """.formatted(nome, link);
    }

    // ============================================================
    // 3) EMAIL DE SENHA TEMPORÁRIA
    // ============================================================
    public void enviarSenhaTemporaria(String emailDestino, String nome, String senhaTemporaria) {
        try {
            MimeMessage mensagem = new MimeMessage(mailSession);
            mensagem.setRecipients(RecipientType.TO, InternetAddress.parse(emailDestino));
            mensagem.setSubject("Acesso ao Sistema - Senha Temporária", "UTF-8");

            MimeBodyPart corpo = new MimeBodyPart();
            corpo.setContent(
                    gerarTemplateHtmlSenhaTemporaria(nome, senhaTemporaria, emailDestino),
                    "text/html; charset=UTF-8"
            );

            MimeMultipart multi = new MimeMultipart();
            multi.addBodyPart(corpo);
            mensagem.setContent(multi);

            Transport.send(mensagem);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String gerarTemplateHtmlSenhaTemporaria(String nome, String senha, String emailDestino) {
        return """
            <html><body style='background:#f4f4f4; font-family:Arial;'>
                <div style='width:600px; margin:auto; background:white; border-radius:8px;'>
                    <div style='background:#004AAD; padding:20px; text-align:center; color:white;'>
                        <h2>Acesso ao Sistema - SIGAPAR</h2>
                    </div>

                    <div style='padding:30px; color:#333; font-size:16px;'>
                        <p>Olá, <b>%s</b>,</p>
                        <p>Sua conta foi criada/atualizada no sistema SIGAPAR. Abaixo estão as credenciais temporárias:</p>

                        <p style='background:#f7f7f7; padding:12px; border-radius:6px; text-align:center;'>
                            <strong>Usuário:</strong> %s<br/>
                            <strong>Senha temporária:</strong> <span style='font-family:monospace;'>%s</span>
                        </p>

                        <p>Por segurança, altere sua senha após o primeiro acesso.</p>
                        <p style='font-size:12px; color:#777;'>Se você não solicitou este acesso, procure o administrador.</p>
                    </div>
                </div>
            </body></html>
        """.formatted(nome, emailDestino, senha);
    }

}
