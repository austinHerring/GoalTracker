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

    abstract String toBasicInfo();

    abstract void update();
}
