package com.austin.goaltracker.Model;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Class for an email sent out with a new temporary email when someone forgets their email.
 */
public class NewPasswordEmail extends EmailAgent {
    final private String subject = "Goal Tracker: Password Reset";
    private String message;

    public NewPasswordEmail(Account account) {
        super(account);
        this.message = "Please disregard this email if you are not " + account.getNameFirst() + " " +
                account.getNameLast() + ".\nYour new temporary password is '" + account.getPassword()
        + "'. You may use this to log in once and change it in 'Settings' if you desire.\n\nHappy Goal Tracking!\nAustin";
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
