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

    public StreakSustainerGoal(String goalName, IncrementType type) {
        super(goalName, type, GoalClassification.STREAK);
    }

    public StreakSustainerGoal() {
        super(GoalClassification.STREAK);
    }

    public void update() {
        streak++;
    }

    public boolean updateCheatNumber() {
        // return true if out of cheats
        cheatsRemaining--;
        return cheatsRemaining < 0;
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
        return "Current streak is " + streak + unitToString(streak);
    }

    public String currentStreakToString() {
        String ret = "You streaked " + streak + unitToString(streak);

        if (cheatNumber > 0) {
            ret += " with " + cheatsRemaining + ((cheatsRemaining!=1) ? " cheats " : " cheat ") + "left";
        } else {
            ret += "without cheating";
        }
        return ret;
    }


}
