package com.austin.goaltracker.Controller.Services;

import android.app.IntentService;
import android.content.Intent;

import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.gcm.cronJob.CronJob;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.util.logging.Logger;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Service that adds a cron Job to the Datastore
 */
public class CronJobIntentService extends IntentService {
    private static final String TAG = "CronJobService";
    static Logger Log = Logger.getLogger(CronJobIntentService.class.getName());

    public CronJobIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            String action = intent.getStringExtra("ACTION");
            CronJob.Builder builder = new CronJob.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    .setRootUrl(GoalTrackerApplication.PROJECT_ADDRESS)
                    .setApplicationName(GoalTrackerApplication.APPLICATION_NAME);
            CronJob cronJobService = builder.build();

            if (action.equals("ADD")) {
                cronJobService.persistCron(
                        intent.getStringExtra("cronKey"),
                        intent.getStringExtra("message"),
                        intent.getStringExtra("accountId"),
                        intent.getStringExtra("goalId"),
                        intent.getStringExtra("frequency"),
                        intent.getLongExtra("nextRunTS", -1),
                        intent.getLongExtra("lastRun", -1))
                .execute();

                Log.info("ENDPOINT SHOULD HAVE ADDED A JOB");
            } else if (action.equals("REMOVE")) {
                cronJobService.removeCron(intent.getStringExtra("cronKey")).execute();
                Log.info("ENDPOINT SHOULD HAVE REMOVED A JOB");
            } else {
                Log.info("WHEN INTERACTING WITH DATASTORE THERE WAS AN INVALID ACTION: " + action);
            }

        } catch (Exception e) {
            Log.info("FAILED TO SEND CRON JOB TO DATA STORE: " + e);
        }
    }
}
