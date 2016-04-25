package com.austin.goaltracker.Model;

import java.util.Calendar;

/**
 * Created by austin on 4/24/16.
 */
public class PendingGoalNotification {
    private String id;
    private String associatedGoalId;
    private Calendar dateTimeNotified;

    public String getAssociatedGoalId() {
        return associatedGoalId;
    }

    public Calendar getDateTimeNotified() {
        return dateTimeNotified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAssociatedGoalId(String associatedGoalId) {
        this.associatedGoalId = associatedGoalId;
    }

    public void setDateTimeNotified(Calendar dateTimeNotified) {
        this.dateTimeNotified = dateTimeNotified;
    }
}
