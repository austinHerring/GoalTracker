package com.austin.goaltracker.Model;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Abstract class for an email
 */
public abstract class EmailAgent {
    private String recipientEmail;

    EmailAgent(Account account) {
        this.recipientEmail = account.getEmail();
    }

    public String getrecipientEmail() {
        return recipientEmail;
    }

    public abstract String getMessage();

    public abstract String getSubject();
}
