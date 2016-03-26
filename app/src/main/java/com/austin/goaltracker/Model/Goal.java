package com.austin.goaltracker.Model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Abstract class for a Goal to track
 */
public abstract class Goal {
    public enum IncrementType {YEARLY, MONTHLY, BIWEEKLY, WEEKLY, BIDAILY, DAILY, HOURLY}
    public enum Classification {COUNTDOWN, STREAK}

    private String goalName, task;
    private int indexInDB;
    protected Calendar dateOfOrigin;
    private Calendar dateBroken;
    protected IncrementType incrementType;
    private ArrayList<Account> supporters;
    private Classification classification;
    private String cronJobKey;

    Goal(String name, IncrementType type, Classification c) {
        goalName = name;
        incrementType = type;
        dateOfOrigin = Calendar.getInstance();
        classification = c;
    }

    Goal(Classification c) {
        classification = c;
    }


    public void terminateGoal(Calendar date) {
        dateBroken = date;
    }

    public Classification classification() {
        return classification;
    }

    public String getGoalName() {
        return goalName;
    }

    public String getTask() {
        return task;
    }

    public int getIndex() {
        return indexInDB;
    }

    public String getDateOfOrigin() {
        return dateOfOrigin.get(Calendar.YEAR) + "," + dateOfOrigin.get(Calendar.MONTH) + "," + dateOfOrigin.get(Calendar.DAY_OF_MONTH);
    }

    public String getBrokenDate() {
        if (dateBroken != null) {
            return dateBroken.get(Calendar.YEAR) + "," + dateBroken.get(Calendar.MONTH) + "," + dateBroken.get(Calendar.DAY_OF_MONTH);
        } else {
            return "NA";
        }
    }

    public String getCronJobKey() {
        return cronJobKey;
    }

    public String generateCron(int minuteToPrompt, int hourToPrompt) {
        //TODO: ADD HOUR/MINUTE to PROMPT to the UI
        //(Seconds: 0-59) (Minutes: 0-59) (Hours: 0-23) (Day of Month: 1-31) (Months: 0-11) (Day of Week: 0-6)
        switch (incrementType) {
            case HOURLY: return "0 " + minuteToPrompt + " * * * *";
            case DAILY: return "0 " + minuteToPrompt + " " + hourToPrompt + " * * *";
            case BIDAILY: return "0 " + minuteToPrompt + " " + hourToPrompt + " */2 * *";
            case WEEKLY: return "0 " + minuteToPrompt + " " + hourToPrompt + " */7 * *";
            case BIWEEKLY: return "0 " + minuteToPrompt + " " + hourToPrompt + " */14 * *";
            case MONTHLY: return "0 " + minuteToPrompt + " " + hourToPrompt + " "
                    + dateOfOrigin.get(Calendar.DAY_OF_MONTH) + " * *";
            case YEARLY: return "0 " + minuteToPrompt + " " + hourToPrompt + " "
                    + dateOfOrigin.get(Calendar.DAY_OF_MONTH) + " "
                    + dateOfOrigin.get(Calendar.MONTH) + " *";
        }
        return "*/10 * * * * *";
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

    public void setIndex(int index) {
        indexInDB = index;
    }

    public void setBrokenDate(Calendar date) {
            dateBroken = date;
    }

    public void setDateOfOrigin(Calendar date) {
        dateOfOrigin = date;
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

    abstract String toBasicInfo();

    abstract void update();
}
