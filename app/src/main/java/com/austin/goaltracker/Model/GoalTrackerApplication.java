package com.austin.goaltracker.Model;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.firebase.client.Firebase;

import java.util.logging.Logger;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Class to keep track of application context for intent services
 */
public class GoalTrackerApplication extends Application {
    public static GoalTrackerApplication INSTANCE;
    public static final String APPLICATION_NAME = "Goal Tracker";
    public static final String PROJECT_NUMBER = "226374478657";
    public static final String PROJECT_ADDRESS = "https://goal-tracker-1235.appspot.com/_ah/api/";
    public static final String FIREBASE_URL = "https://flickering-inferno-500.firebaseio.com/";
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public boolean isReceiverRegistered;
    public BroadcastReceiver mRegistrationBroadcastReceiver;
    private Activity mCurrentActivity = null;
    static Logger Log = Logger.getLogger(GoalTrackerApplication.class.getName());
    public static int notificationId = 0;
    public static String[] ACTIVITIES = {"Goals", "Friends", "Messages", "History"};
    public static final int SELECT_REQUEST = 2;
    public static final int PIC_CROP = 3;

    public void onCreate(){
        super.onCreate();
        INSTANCE = this;
        Firebase.setAndroidContext(this);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(GCMPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    Log.info("SENT!!");
                } else {
                    Log.info("NOT SENT :(");
                }
            }
        };

        // Registering BroadcastReceiver
        registerReceiver();
    }

    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }

    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }

    public void registerReceiver() {
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(GCMPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }
}
