package com.austin.goaltracker.Model.RealTime;

import com.austin.goaltracker.Model.Enums.GoalClassification;
import com.austin.goaltracker.Model.Enums.IncrementType;

import java.io.Serializable;
import java.util.Calendar;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Goal POJO used in GetAccounts for friends list
 */
public class GetGoal implements Serializable {
    private String goalName, task, id;
    protected Calendar dateOfOrigin, dateBroken;
    protected IncrementType incrementType;
    //private ArrayList<Account> supporters;
    private GoalClassification classification;
    private boolean isTerminated;

    // STREAK VARIABLES
    int streak;

    // COUNTDOWN VARIABLES
    private Calendar dateDesiredFinish;
    private int percentProgress;

    public Calendar getDateBroken() {
        return dateBroken;
    }

    public Calendar getDateDesiredFinish() {
        return dateDesiredFinish;
    }

    public Calendar getDateOfOrigin() {
        return dateOfOrigin;
    }

    public GoalClassification getClassification() {
        return classification;
    }

    public IncrementType getIncrementType() {
        return incrementType;
    }

    public int getPercentProgress() {
        return percentProgress;
    }

    public int getStreak() {
        return streak;
    }

    public String getGoalName() {
        return goalName;
    }

    public String getId() {
        return id;
    }

    public boolean getIsTerminated() {
        return isTerminated;
    }

    public String getTask() {
        return task;
    }

    public void setClassification(GoalClassification classification) {
        this.classification = classification;
    }

    public void setDateBroken(Calendar dateBroken) {
        this.dateBroken = dateBroken;
    }

    public void setDateDesiredFinish(Calendar dateDesiredFinish) {
        this.dateDesiredFinish = dateDesiredFinish;
    }

    public void setDateOfOrigin(Calendar dateOfOrigin) {
        this.dateOfOrigin = dateOfOrigin;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIsTerminated(boolean isTerminated) {
        this.isTerminated = isTerminated;
    }

    public void setIncrementType(IncrementType incrementType) {
        this.incrementType = incrementType;
    }

    public void setPercentProgress(int percentProgress) {
        this.percentProgress = percentProgress;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setTerminated(boolean terminated) {
        isTerminated = terminated;
    }
}
