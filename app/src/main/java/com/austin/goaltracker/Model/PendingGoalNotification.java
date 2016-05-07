package com.austin.goaltracker.Model;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Class that joins reminder notifications to an associated goal. Displays in the reminder
 * list activity
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
}
