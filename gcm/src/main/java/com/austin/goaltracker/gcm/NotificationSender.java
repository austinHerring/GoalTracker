package com.austin.goaltracker.gcm;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by austin on 4/1/16.
 */
@SuppressWarnings("serial")
public class NotificationSender extends HttpServlet {
    private static final Logger log = Logger.getLogger(NotificationSender.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String rawMessage = req.getParameter("rawMessage");
        log.info("EXECUTING TASK FROM QUEUE " + rawMessage);
        MessagingEndpoint messagingEndpoint = new MessagingEndpoint();
        try {
            messagingEndpoint.sendMessage(rawMessage);
        } catch (IOException e) {
            log.severe(e.toString());
        }
    }
}
