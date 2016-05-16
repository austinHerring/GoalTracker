package com.austin.goaltracker.Controller;

import com.austin.goaltracker.Model.Enums.IncrementType;

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

    public static IncrementType stringToFrequency(String input) {
        if (input.equals("HOURLY")) {
            return IncrementType.HOURLY;
        } else if (input.equals("DAILY")) {
            return IncrementType.DAILY;
        } else if (input.equals("BIDAILY")) {
            return IncrementType.BIDAILY;
        } else if (input.equals("WEEKLY")) {
            return IncrementType.WEEKLY;
        } else if (input.equals("BIWEEKLY")) {
            return IncrementType.BIWEEKLY;
        } else if (input.equals("MONTHLY")) {
            return IncrementType.MONTHLY;
        } else if (input.equals("YEARLY")) {
            return IncrementType.YEARLY;
        } else {
            return null;
        }
    }
}
