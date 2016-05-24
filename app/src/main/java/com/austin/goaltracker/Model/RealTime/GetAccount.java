package com.austin.goaltracker.Model.RealTime;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Account POJO for friends list
 */
public class GetAccount implements Serializable {

    private String nameFirst, nameLast, username, id, pictureData;
    private long totalFriends, totalGoalsStarted, totalGoalsCompleted, longestStreak;
    private GetGoals getGoals;
    private HashMap<String, String> friends = new HashMap<>();

    public HashMap<String, String> getFriends() {
        return friends;
    }

    public GetGoals getGetGoals() {
        return getGoals;
    }

    public long getTotalFriends() {
        return totalFriends;
    }

    public long getTotalGoalsCompleted() {
        return totalGoalsCompleted;
    }

    public long getTotalGoalsStarted() {
        return totalGoalsStarted;
    }

    public String getId() {
        return id;
    }

    public long getLongestStreak() {
        return longestStreak;
    }

    public String getNameFirst() {
        return nameFirst;
    }

    public String getNameLast() {
        return nameLast;
    }

    public String getPictureData() {
        return pictureData;
    }

    public String getUsername() {
        return username;
    }

    public void setFriends(HashMap<String, String> friends) {
        this.friends = friends;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNameFirst(String nameFirst) {
        this.nameFirst = nameFirst;
    }

    public void setGetGoals(GetGoals getGoals) {
        this.getGoals = getGoals;
    }

    public void setLongestStreak(long longestStreak) {
        this.longestStreak = longestStreak;
    }

    public void setNameLast(String nameLast) {
        this.nameLast = nameLast;
    }

    public void setPictureData(String pictureData) {
        this.pictureData = pictureData;
    }

    public void setTotalFriends(long totalFriends) {
        this.totalFriends = totalFriends;
    }

    public void setTotalGoalsCompleted(long totalGoalsCompleted) {
        this.totalGoalsCompleted = totalGoalsCompleted;
    }

    public void setTotalGoalsStarted(long totalGoalsStarted) {
        this.totalGoalsStarted = totalGoalsStarted;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
