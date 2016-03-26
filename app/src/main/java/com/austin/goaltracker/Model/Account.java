package com.austin.goaltracker.Model;

import java.util.ArrayList;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Account structure used to keep track of account information
 */
public class Account {
    private String nameFirst;
    private String nameLast;
    private String username;
    private Password password;
    private String email;
    private ArrayList<Goal> goals = new ArrayList<>();
    private ArrayList<String> registedGCMDevices = new ArrayList<>();
    private ArrayList<Account> friends = new ArrayList<>();
    private String id;

    /**
     * Create a new Account
     *
     * @param nameFirst the first name of the account
     * @param nameLast the last name of the account
     * @param username the username of the account
     * @param password the password of the account
     * @param email the email of the account
     */
    public Account(String nameFirst, String nameLast,String username, String password, String
            email) {
        this.nameFirst = nameFirst;
        this.nameLast = nameLast;
        this.username = username;
        this.password = new Password(password);
        this.email = email;
    }

    /**
     * Create a new Account with a given ID
     *
     * @param nameFirst the first name of the account
     * @param nameLast the last name of the account
     * @param username the username of the account
     * @param password the password of the account
     * @param email the email of the account
     */
    public Account(String nameFirst, String nameLast,String username, Password password, String email,
                   String id) {
        this.nameFirst = nameFirst;
        this.nameLast = nameLast;
        this.username = username;
        this.password = password;
        this.email = email;
        this.id = id;
    }

    public String getNameFirst() {
        return this.nameFirst;
    }

    public String getNameLast() {
        return this.nameLast;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Password getPasswordObject() {
        return this.password;
    }

    public String getPassword() {
        return this.password.toPasswordString();
    }

    public void setPassword(String password) {
        this.password = new Password(password);
    }

    public String getPasswordDate() {
        return this.password.toDateString();
    }

    public String getEmail() {
        return this.email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) { this.id = id; }

    public ArrayList<Goal> getGoals() {
        return goals;
    }

    public boolean addGoal(Goal g) {
        return goals.add(g);
    }

    public boolean addRegisteredDevice(String deviceID) {
        return deviceID != null && !registedGCMDevices.contains(deviceID) && registedGCMDevices.add(deviceID);
    }

    public ArrayList<String> getRegistedGCMDevices() {
        return registedGCMDevices;
    }

    /**
     * toString of the account
     *
     * @return string of the account content
     */
    public String toString() {
        return "Account username: " + username + ", password: "
                + password + ",  First name: " + nameFirst + ",  Last name: " + nameLast +
                ", email: " + email;
    }

    /**
     * Equals method to check if accounts are equal.
     * Accounts are equal if they have the username and email
     *
     * @param other the object to compare to
     * @return if the accounts are equal or not
     */
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (null == other) {
            return false;
        }
        if (!(other instanceof Account)) {
            return false;
        }
        Account that = (Account) other;
        return this.username.equals(that.username) && this.email.equals(that.email);
    }
}
