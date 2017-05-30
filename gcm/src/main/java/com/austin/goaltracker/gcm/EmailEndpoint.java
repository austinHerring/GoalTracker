package com.austin.goaltracker.gcm;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import com.sun.mail.smtp.SMTPTransport;

/**
 * A email endpoint class we are exposing for automated email requests from the client
 */
@Api(
    name = "email",
    version = "v1",
    namespace = @ApiNamespace(
            ownerDomain = "gcm.goaltracker.austin.com",
            ownerName = "gcm.goaltracker.austin.com",
            packagePath=""
    )
)
public class EmailEndpoint {

    private static final Logger log = Logger.getLogger(EmailEndpoint.class.getName());
    private final String ADMIN_EMAIL = "noreply.goaltrackerbyaustin@gmail.com";
    private final String ADMIN_NAME = "noreply.goaltrackerbyaustin";
//    private final String PASSWORD = "goaltracker";
    private final String PASSWORD = "dnwiuuacukkicadx";
    private final String SSL_PORT = "587";
    private final String SSL_HOST = "smtp.gmail.com";

    /**
     * Send an email through the backend
     *
     * @param subject the subject of the email
     * @param body the content of the email
     * @param to the recipient of the email
     */
    @ApiMethod(name = "sendEmail")
    public void sendEmail(@Named("subject") String subject,
                            @Named("body") String body,
                            @Named("to") String to)
    {

        Properties props = System.getProperties();
        props.put("mail.smtp.port", SSL_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SSL_HOST);
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(ADMIN_EMAIL,
                        PASSWORD);
            }
        });

        Message msg = new MimeMessage(session);
        try {
            msg.setFrom(new InternetAddress(ADMIN_EMAIL));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to, false));
            msg.setSubject(subject);
            msg.setText(body);
            msg.setSentDate(new Date());

            SMTPTransport transporter = (SMTPTransport) session.getTransport("smtp");
            transporter.connect(SSL_HOST, ADMIN_NAME, PASSWORD);
            transporter.sendMessage(msg, msg.getAllRecipients());
            transporter.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }
}