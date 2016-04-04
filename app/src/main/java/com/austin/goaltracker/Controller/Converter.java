package com.austin.goaltracker.Controller;

import com.austin.goaltracker.Model.Goal;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Utility to convert information taken from firebase into interpretable types
 */
public class Converter {

    public static Calendar longToCalendar(long date) {
        if (date == 0) {
            return null;
        }
        Calendar c = new GregorianCalendar();
        c.setTimeInMillis(date);
        return c;
    }

    public static Goal.IncrementType stringToFrequency(String input) {
        if (input.equals("HOURLY")) {
            return Goal.IncrementType.HOURLY;
        } else if (input.equals("DAILY")) {
            return Goal.IncrementType.DAILY;
        } else if (input.equals("BIDAILY")) {
            return Goal.IncrementType.BIDAILY;
        } else if (input.equals("WEEKLY")) {
            return Goal.IncrementType.WEEKLY;
        } else if (input.equals("BIWEEKLY")) {
            return Goal.IncrementType.BIWEEKLY;
        } else if (input.equals("MONTHLY")) {
            return Goal.IncrementType.MONTHLY;
        } else if (input.equals("YEARLY")) {
            return Goal.IncrementType.YEARLY;
        } else {
            return null;
        }
    }
}
