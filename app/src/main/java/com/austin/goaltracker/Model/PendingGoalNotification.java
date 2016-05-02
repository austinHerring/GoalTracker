package com.austin.goaltracker.Model;

import java.util.Calendar;

/**
 * Created by austin on 4/24/16.
 */
public class PendingGoalNotification {
    private String id;
    private String associatedGoalId;
    private long dateTimeNotified;
    private String name;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private PendingGoalNotification() {
    }

    public String getAssociatedGoalId() {
        return associatedGoalId;
    }

    public long getDateTimeNotified() {
        return dateTimeNotified;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAssociatedGoalId(String associatedGoalId) {
        this.associatedGoalId = associatedGoalId;
    }

    public void setDateTimeNotified(long dateTimeNotified) {
        this.dateTimeNotified = dateTimeNotified;
    }
}
