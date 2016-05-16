package com.austin.goaltracker.Controller.Mediators;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Class to help with text carry over in login
 */
public class LoginMediator {
    // These are used to copy over a username and password from login screen to registration screen
    public static String usernameCarryOver;
    public static String passwordCarryOver;

    public static void copyLoginInfo(String username, String password) {
        usernameCarryOver = username;
        passwordCarryOver = password;
    }

    public static String pasteUsername() {
        return usernameCarryOver;
    }

    public static String pastePassword() {
        return passwordCarryOver;
    }

}
