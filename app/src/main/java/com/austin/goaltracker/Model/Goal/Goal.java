package com.austin.goaltracker.Model.Goal;

import com.austin.goaltracker.Model.Enums.GoalClassification;
import com.austin.goaltracker.Model.Enums.IncrementType;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Abstract class for a Goal to track
 */
public abstract class Goal {

    private String goalName, task, id, cronJobKey;
    protected Calendar dateOfOrigin, dateBroken;
    protected IncrementType incrementType;
    //private ArrayList<Account> supporters;
    private GoalClassification classification;
    private boolean isTerminated;

    Goal(String name, IncrementType type, GoalClassification c) {
        goalName = name;
        isTerminated = false;
        incrementType = type;
        dateOfOrigin = Calendar.getInstance();
        classification = c;
        isTerminated = false;
    }

    Goal(GoalClassification c) {
        classification = c;
    }


    public void terminateGoal(Calendar date) {
        dateBroken = date;
    }

    public GoalClassification classification() {
        return classification;
    }

    public String getGoalName() {
        return goalName;
    }

    public String getTask() {
        return task;
    }

    public String getId() {
        return id;
    }

    public boolean isTerminated() {
        return isTerminated;
    }

    public Calendar getDateOfOrigin() {
        return dateOfOrigin;
    }

    public Calendar getBrokenDate() {
        if (dateBroken != null) {
            return dateBroken;
        } else {
            return null;
        }
    }

    public String getCronJobKey() {
        return cronJobKey;
    }

    public String originDateToString() {
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
        return "Was Established:\n" + df.format(dateOfOrigin.getTime());
    }

    public String brokenDateToString() {
        if (dateBroken == null) {
            return "NA";
        } else {
            SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy");
            return df.format(dateBroken.getTime());
        }
    }

    public IncrementType getIncrementType() {
        return incrementType;
    }

    public void setName(String name) {
        this.goalName = name;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setIncrementType(IncrementType type) {
        this.incrementType = type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBrokenDate(Calendar date) {
            dateBroken = date;
    }

    public void setDateOfOrigin(Calendar date) {
        dateOfOrigin = date;
    }

    public void setIsTerminated(boolean isTerminated) {
        this.isTerminated = isTerminated;
    }

    public void setCronJobKey(String key) {
        cronJobKey = key;
    }

    public String toNotificationMessage() {
        String str = goalName +": Did you make progress ";
        if (incrementType == IncrementType.HOURLY) {
            return str + "last hour?";
        } else if (incrementType == IncrementType.DAILY || incrementType == IncrementType.BIDAILY) {
            return str + "today?";
        } else if (incrementType == IncrementType.WEEKLY || incrementType == IncrementType.BIWEEKLY) {
            return str + "last week?";
        } else if (incrementType == IncrementType.MONTHLY) {
            return str + "last month?";
        } else {
            return str + "last year";
        }
    }

    protected String unitToString(long number) {
        if (incrementType.equals(IncrementType.HOURLY)) {
            return ((number!=1) ? " hours" : " hour");
        } else if (incrementType.equals(IncrementType.DAILY)) {
            return ((number!=1) ? " days" : " day");
        } else if (incrementType.equals(IncrementType.BIDAILY)) {
            return ((number!=1) ? " bi-days" : " bi-day");
        } else if (incrementType.equals(IncrementType.WEEKLY)) {
            return ((number!=1) ? " weeks" : " week");
        } else if (incrementType.equals(IncrementType.BIWEEKLY)) {
            return ((number!=1) ? " bi-weeks" : " bi-week");
        } else if (incrementType.equals(IncrementType.MONTHLY)) {
            return ((number!=1) ? " months" : " month");
        } else if (incrementType.equals(IncrementType.YEARLY)) {
            return ((number!=1) ? " years" : " year");
        } else {
            return "";
        }
    }
}
