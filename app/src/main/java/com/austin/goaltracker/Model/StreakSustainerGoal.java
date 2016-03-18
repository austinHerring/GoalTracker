package com.austin.goaltracker.Model;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Class used for goals that count upward indefinitely in regards to time or completions.
 * The idea is to keep a streak going
 */
public class StreakSustainerGoal extends Goal {

    int cheatNumber;
    int cheatsRemaining;
    int streak;

    public StreakSustainerGoal(String goalName, Goal.IncrementType type) {
        super(goalName, type, Classification.STREAK);
    }

    public StreakSustainerGoal() {
        super(Classification.STREAK);
    }

    public void update() {
        streak++;
    }

    public int getStreak() {
        return streak;
    }

    public int getCheatNumber() {
        return cheatNumber;
    }

    public int getCheatsRemaining() {
        return cheatsRemaining;
    }

    public void setCheatNumber(int number) {
        cheatNumber = number;
    }

    public void setCheatsRemaining(int number) {
        cheatsRemaining = number;
    }

    public void setStreak(int number) {
        streak = number;
    }

    public String toBasicInfo() {
        return "Current streak is " + streak + ((streak!=1) ? " checkpoints" : " checkpoint");
    }

    public String currentStreakToString() {
        String ret = "";
        if (incrementType.equals(IncrementType.HOURLY)) {
            ret += "You streaked " + streak + ((streak!=1) ? " hours " : " hour ");
        } else if (incrementType.equals(IncrementType.DAILY)) {
            ret += "You streaked " + streak + ((streak!=1) ? " days " : " day ");
        } else if (incrementType.equals(IncrementType.BIDAILY)) {
            ret += "You streaked " + streak + ((streak!=1) ? " bi-days " : " bi-day ");
        } else if (incrementType.equals(IncrementType.WEEKLY)) {
            ret += "You streaked " + streak + ((streak!=1) ? " weeks " : " week ");
        } else if (incrementType.equals(IncrementType.BIWEEKLY)) {
            ret += "You streaked " + streak + ((streak!=1) ? " bi-weeks " : " bi-week ");
        } else if (incrementType.equals(IncrementType.MONTHLY)) {
            ret += "You streaked " + streak + ((streak!=1) ? " months " : " month ");
        } else if (incrementType.equals(IncrementType.YEARLY)) {
            ret += "You streaked " + streak + ((streak!=1) ? " years " : " year ");
        } else {
            //TODO: implement custom
            return ret;
        }

        if (cheatNumber > 0) {
            ret += "with " + cheatsRemaining + ((cheatsRemaining!=1) ? " cheats " : " cheat ") + "left";
        } else {
            ret += "without cheating";
        }
        return ret;
    }
}
