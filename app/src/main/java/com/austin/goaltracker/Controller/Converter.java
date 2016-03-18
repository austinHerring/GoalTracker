package com.austin.goaltracker.Controller;

import com.austin.goaltracker.Model.Goal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Utility to convert information taken from firebase into interpretable types
 */
public class Converter {

    public static Calendar stringToCalendar(String date) {
        if (date.equals("NA")) {
            return null;
        }
        //PROBLEM HERE
        List<String> numbers = Arrays.asList(date.split(","));
        int[] ints = new int[3];
        int i = 0;
        for (String number : numbers) {
            ints[i] = Integer.parseInt(number);
            i++;
        }
        return new GregorianCalendar(ints[0], ints[1], ints[2]);
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
