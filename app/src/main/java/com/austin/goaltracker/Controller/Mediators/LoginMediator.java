package com.austin.goaltracker.Controller.Mediators;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Class to help with text carry over in login
 */
public class LoginMediator {
    // These are used to copy over a username and password from login screen to registration screen
    public static String emailCarryOver;
    public static String passwordCarryOver;

    public static void copyLoginInfo(String email, String password) {
        emailCarryOver = email;
        passwordCarryOver = password;
    }

    public static String pasteEmail() {
        return emailCarryOver;
    }

    public static String pastePassword() {
        return passwordCarryOver;
    }

}
