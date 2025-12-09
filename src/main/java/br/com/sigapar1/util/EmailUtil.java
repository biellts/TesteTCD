package br.com.sigapar1.util;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

public class EmailUtil {

    private final String username = "SEU_EMAIL@gmail.com";      // coloque o seu email real
    private final String senhaApp = "SUA_SENHA_DE_APP_AQUI";     // senha de aplicativo (gmail)

    public void enviar(String destino, String assunto, String corpoHtml) {

        Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, senhaApp);
                    }
                });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(username, "SIGAPAR - Sistema de Atendimento"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destino));
            msg.setSubject(assunto);

            msg.setContent(corpoHtml, "text/html; charset=UTF-8");

            Transport.send(msg);

            System.out.println("E-mail enviado com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao enviar email: " + e.getMessage());
        }
    }
}
