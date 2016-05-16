package com.austin.goaltracker.Controller.Mediators;

import com.austin.goaltracker.Model.Enums.IncrementType;

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

    public static IncrementType convertUItoType(String input) {
        if (input.equals("Every Hour")) {
            return IncrementType.HOURLY;
        } else if (input.equals("Every Day")) {
            return IncrementType.DAILY;
        } else if (input.equals("Every Other Day")) {
            return IncrementType.BIDAILY;
        } else if (input.equals("Every Week")) {
            return IncrementType.WEEKLY;
        } else if (input.equals("Every Other Week")) {
            return IncrementType.BIWEEKLY;
        } else if (input.equals("Every Month")) {
            return IncrementType.MONTHLY;
        } else if (input.equals("Every Year")) {
            return IncrementType.YEARLY;
        } else {
            return null;
        }
    }
}
