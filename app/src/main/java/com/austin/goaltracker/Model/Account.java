package com.austin.goaltracker.Model;

import com.austin.goaltracker.Controller.Util;
import com.austin.goaltracker.Model.Goal.Goal;
import com.austin.goaltracker.Model.RealTime.GetAccount;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Account structure used to keep track of account information
 */
public class Account {
    private String id, nameFirst, nameLast, username, email, pictureData;
    private Password password;
    private long totalFriends, totalGoalsStarted, totalGoalsCompleted, longestStreak;
    private HashMap<String, Goal> goals;
    private HashMap<String, String> friends;
    private boolean receiveNotificationsOnCurrentDevice;

    /**
     * Create a new Account
     *
     * @param nameFirst the first name of the account
     * @param nameLast the last name of the account
     * @param username the username of the account
     * @param password the password of the account
     * @param email the email of the account
     */
    public Account(String nameFirst, String nameLast,String username, Password password, String
            email) {
        this.nameFirst = nameFirst;
        this.nameLast = nameLast;
        this.username = username;
        this.password = password;
        this.email = email;
        this.friends = new HashMap<>();
        this.goals = new HashMap<>();
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

    public void setPassword(Password password) {
        this.password = password;
    }

    public void setGoals(HashMap<String, Goal> goals) {
        this.goals = goals;
    }

    public String getPasswordDate() {
        return this.password.toDateString();
    }

    public String getPictureData() {
        return pictureData;
    }

    public boolean canReceiveNotificationsOnCurrentDevice() {
        return receiveNotificationsOnCurrentDevice;
    }

    public String getEmail() {
        return this.email;
    }

    public String getId() {
        return id;
    }

    public HashMap<String, String> getFriends() {
        return friends;
    }

    public long getTotalFriends() {
        return totalFriends;
    }

    public void setPictureData(String pictureData) {
        this.pictureData = pictureData;
    }

    public long getTotalGoalsCompleted() {
        return totalGoalsCompleted;
    }

    public long getLongestStreak() {
        return longestStreak;
    }

    public long getTotalGoalsStarted() {
        return totalGoalsStarted;
    }

    public void setId(String id) { this.id = id; }

    public void setTotalFriends(long totalFriends) {
        this.totalFriends = totalFriends;
    }

    public void setFriends(HashMap<String, String> friends) {
        this.friends = friends;
    }

    public void setTotalGoalsCompleted(long totalGoalsCompleted) {
        this.totalGoalsCompleted = totalGoalsCompleted;
    }

    public void setTotalGoalsStarted(long totalGoalsStarted) {
        this.totalGoalsStarted = totalGoalsStarted;
    }

    public void setReceiveNotificationsOnCurrentDevice(boolean canReceive) {
        receiveNotificationsOnCurrentDevice = canReceive;
    }

    public void setLongestStreak(long longestStreak) {
        this.longestStreak = longestStreak;
    }

    public HashMap<String, Goal> getGoals() {
        return goals;
    }

    public void addGoal(String id, Goal goal) {
        goals.put(id, goal);
        totalGoalsStarted++;
        Util.updateFriendsForAccountOnDB(this.id, friends, totalFriends);
    }

    public void addFriend(GetAccount getAccount) {
        friends.put(getAccount.getId(), getAccount.getId());
        totalFriends++;
        Util.updateFriendsForAccountOnDB(this.id, friends, totalFriends);
        Util.addHistoryArtifactOnDB("FRIEND", id, Calendar.getInstance().getTimeInMillis(), true, getAccount);
    }

    public void removeFriend(GetAccount getAccount) {
        friends.remove(getAccount.getId());
        totalFriends--;
        Util.updateFriendsForAccountOnDB(this.id, friends, totalFriends);
        Util.addHistoryArtifactOnDB("FRIEND", id, Calendar.getInstance().getTimeInMillis(), false, getAccount);
    }

    public void incrementCompletedGoals() {
        totalGoalsCompleted++;
    }

    public ArrayList<Goal> activeGoalsToList() {
        List<Goal> listOfAllGoals = new ArrayList<>(goals.values());
        ArrayList<Goal> activeGoals = new ArrayList<>();
        for (Goal goal : listOfAllGoals) {
            if (!goal.isTerminated()) {
                activeGoals.add(goal);
            }
        }
        return activeGoals;
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
