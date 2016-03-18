package com.austin.goaltracker.Controller;

import com.austin.goaltracker.Model.Goal;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Class to help with information carry over through the process of creating a goal
 */
public class GoalMediator {
    public static String goalTitleCarryOver;

    public static void copyInfo1(String title) {
        goalTitleCarryOver = title;
    }

    public static String pasteGoalTitle() {
        return goalTitleCarryOver;
    }

    public static Goal.IncrementType convertUItoType(String input) {
        if (input.equals("Every Hour")) {
            return Goal.IncrementType.HOURLY;
        } else if (input.equals("Every Day")) {
            return Goal.IncrementType.DAILY;
        } else if (input.equals("Every Other Day")) {
            return Goal.IncrementType.BIDAILY;
        } else if (input.equals("Every Week")) {
            return Goal.IncrementType.WEEKLY;
        } else if (input.equals("Every Other Week")) {
            return Goal.IncrementType.BIWEEKLY;
        } else if (input.equals("Every Month")) {
            return Goal.IncrementType.MONTHLY;
        } else if (input.equals("Every Year")) {
            return Goal.IncrementType.YEARLY;
        } else {
            return null;
        }
    }
}
