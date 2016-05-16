package com.austin.goaltracker.Controller.Services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.gcm.registration.Registration;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.austin.goaltracker.Model.GCMPreferences;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(GoalTrackerApplication.PROJECT_NUMBER,
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i(TAG, "GCM Registration Token: " + token);

            // Create a Registration record in datastore associated with an account
            sendRegistrationToServer(token, intent.getStringExtra("accountId"));

            // Subscribe to topic channels
            subscribeTopics(token);

            // Stored boolean that indicates whether the generated token has been
            // sent to server. If the boolean is false, send the token to the server,
            // otherwise the server should have already received the token.
            sharedPreferences.edit().putBoolean(GCMPreferences.SENT_TOKEN_TO_SERVER, true).apply();

        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh Did you change default version of deployment from 1?", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(GCMPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(GCMPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token, String accountId) throws IOException {
        Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null)
                .setRootUrl(GoalTrackerApplication.PROJECT_ADDRESS)
                .setApplicationName("Goal Tracker");

        Registration regService = builder.build();

        regService.register(token, accountId).execute();
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }

}
