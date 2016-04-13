package com.austin.goaltracker.Controller;

import android.os.AsyncTask;
import android.util.Log;

import com.sun.mail.smtp.SMTPTransport;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.austin.goaltracker.Model.EmailAgent;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Class to send out automated emails
 */
public class EmailDispatcher {
    EmailAgent email;
    Session session;
    final String userName = "noreply.goaltrackerbyaustin@gmail.com";
    final String password = "goaltracker";
    final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    final String SSL_PORT = "587";
    final String SSL_HOST = "smtp.gmail.com";

    public String send(EmailAgent email) {
        try {
            InternetAddress test = new InternetAddress(email.getrecipientEmail()); //Test email for validity
            test.validate();
        } catch (AddressException e) {
            return "Invalid Email Address";
        }

        // Get a Properties object
        this.email = email;
        Properties props = System.getProperties();
        props.setProperty("mail.smtp.user", userName);
        props.setProperty("mail.smtps.host", SSL_HOST);
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", SSL_PORT);
        props.setProperty("mail.smtp.socketFactory.port", SSL_PORT);
        props.setProperty("mail.smtps.auth", "true");
        props.put("mail.smtps.quitwait", "false");
        session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName,
                        password);
            }
        });
        ReceiveFeedTask task = new ReceiveFeedTask();
        //TODO LOOK INTO WHY IT CRASHES
        try {
            task.execute();
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    class ReceiveFeedTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                final MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(userName));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(email.getrecipientEmail(), false));
                message.setSubject(email.getSubject());
                message.setText(email.getMessage(), "utf-8");
                message.setSentDate(new Date());

                SMTPTransport transporter = (SMTPTransport) session.getTransport("smtps");
                transporter.connect(SSL_HOST, userName, password);
                transporter.sendMessage(message, message.getAllRecipients());
                transporter.close();
            } catch (AddressException e) {
                e.printStackTrace();
                System.out.println("Caught an AddressException, which means one or more of your " +
                        "addresses are improperly formatted.");
                Log.e("EmailDispatcher:", "Address Exception Thrown");
            } catch (MessagingException e) {
                e.printStackTrace();
                System.out.println("Caught a MessagingException, which means that there was a " +
                        "problem sending your message to Amazon's E-mail Service check the " +
                        "stack trace for more information.");
                Log.e("EmailDispatcher:", "Messaging Exception Thrown");
            } catch (Exception e) {
                Log.e("EmailDispatcher:", "SOMETHING IS WORKING ----V");
                e.printStackTrace();
                Log.e("EmailDispatcher:", "SOMETHING IS WORKING ----^");
            }
            return null;
        }
    }
}