package com.austin.goaltracker.Model;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Class for an email sent out for new users when they register. Implements EmailAgent
 */
public class NewMemberEmail extends EmailAgent{
    final private String subject = "Welcome to Goal Tracker!";
    private String message;

    public NewMemberEmail(Account account) {
        super(account);
        this.message = "Greetings " + account.getNameFirst() + "!\n\nYou are getting this email " +
        "because you have recently set up the new account '" + account.getUsername() +"' on Goal" +
                " Tracker. Hopefully, this tool will help you start AND finish all of your " +
                "great ambitions.\n\nHappy Goal Tracking!\nAustin";
    }

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
