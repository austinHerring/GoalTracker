package com.austin.goaltracker.Controller;

import android.content.Intent;

import com.austin.goaltracker.Controller.Services.CronJobIntentService;
import com.austin.goaltracker.Controller.Services.RegistrationIntentService;
import com.austin.goaltracker.Model.Goal.CountdownCompleterGoal;
import com.austin.goaltracker.Model.Goal.Goal;
import com.austin.goaltracker.Model.Enums.GoalClassification;
import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.Model.Enums.IncrementType;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.Calendar;
import java.util.Random;
import java.util.logging.Logger;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * A Controller that takes a goal from the UI and starts a service to add a cron job to the
 * GAE Data Store
 */
public class GAEDatastoreController {
    static Logger Log = Logger.getLogger(GAEDatastoreController.class.getName());

    /**
     * Starts and intent to use a registration endpoint that will create GCM regId for an account
     */
    public static void registerdeviceForCurrentUser() {
        String currentUserId = Util.currentUser.getId();
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(GoalTrackerApplication.INSTANCE, RegistrationIntentService.class);
            intent.putExtra("accountId", currentUserId);
            GoalTrackerApplication.INSTANCE.startService(intent);
        }
    }

    /**
     * Loops through the goals for an account and converts it from DB to Object
     *
     * @param goal the goal to create prompting for
     * @param promptMinute time to prompt the user with a reminder
     * @param promptHour time to prompt the user with a reminder
     */
    public static void persistCron(Goal goal, int promptMinute, int promptHour) {
        IncrementType frequency = goal.getIncrementType();
        boolean isHourly = frequency.equals(IncrementType.HOURLY);

        Intent intent = new Intent(GoalTrackerApplication.INSTANCE, CronJobIntentService.class);
        intent.putExtra("ACTION", "ADD");
        intent.putExtra("cronKey", goal.getCronJobKey());
        intent.putExtra("message", goal.toNotificationMessage());
        intent.putExtra("accountId", Util.currentUser.getId());
        intent.putExtra("goalId", goal.getId());
        intent.putExtra("frequency", frequency.toString());
        intent.putExtra("nextRunTS", calculateFirstNotificationDate(promptHour, promptMinute, isHourly) / 100000 * 100000);
        intent.putExtra("lastRun", determineLastRunDate(goal));
        GoalTrackerApplication.INSTANCE.startService(intent);
    }

    /**
     * Loops through the goals for an account and converts it from DB to Object
     *
     * @param goal the goal to create prompting for
     */
    public static void removeCron(Goal goal) {
        Intent intent = new Intent(GoalTrackerApplication.INSTANCE, CronJobIntentService.class);
        intent.putExtra("ACTION", "REMOVE");
        intent.putExtra("cronKey", goal.getCronJobKey());
        GoalTrackerApplication.INSTANCE.startService(intent);
    }

    /**
     * Create a key for a goals cron service
     *
     * @param goal the goal to create a cron for
     */
    public static String createCronKey(Goal goal) {
        StringBuilder sb = new StringBuilder();
        sb.append(System.currentTimeMillis());  // PART 1: APPEND TIME USER CREATED GOAL
        sb.append(goal.hashCode());             // PART 2: APPEND THE USERS CREATED GOAL HASHCODE
        sb.append(new Random().nextInt(10000)); // PART 3: APPEND SOME RANDOM BITS JUST IN THE CASE
        //         SOMEONE CREATES THE SAME EXACT GOAL AT
        //         THE SAME MILLISECOND IN TIME
        return sb.toString();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private static boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(GoalTrackerApplication.INSTANCE);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(GoalTrackerApplication.INSTANCE.getCurrentActivity(),
                        resultCode, GoalTrackerApplication.PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.info("This device is not supported.");
                return false;
            }
            return false;
        }
        return true;
    }

    /**
     * Handles the casting of different goals and returns a valid date for Countdown goals
     * and -1 for Streak Goals
     */
    private static long determineLastRunDate(Goal goal) {
        if (goal.classification().equals(GoalClassification.STREAK)) {
            return -1;
        } else {
            CountdownCompleterGoal g = (CountdownCompleterGoal) goal;
            return g.getDateDesiredFinish().getTimeInMillis() / 100000 * 100000;
        }
    }
    /**
     * Determines when the very first notification should be sent out
     */
    private static long calculateFirstNotificationDate(int hour, int min, boolean isHourly) {
        Calendar currentDateTime = Calendar.getInstance();
        Calendar potentialDateTime = Calendar.getInstance();
        potentialDateTime.set(Calendar.MINUTE, min);
        long present = currentDateTime.getTimeInMillis();

        if (isHourly) {
            long potential = potentialDateTime.getTimeInMillis();

            if (present < potential) {
                return potential;
            } else {
                potentialDateTime.add(Calendar.HOUR, 1);
                return potentialDateTime.getTimeInMillis();
            }

        } else {
            potentialDateTime.set(Calendar.HOUR_OF_DAY, hour);
            long potential = potentialDateTime.getTimeInMillis();

            if (present < potential) {
                return potential;
            } else {
                potentialDateTime.add(Calendar.DATE, 1);
                return potentialDateTime.getTimeInMillis();
            }
        }

    }
}
