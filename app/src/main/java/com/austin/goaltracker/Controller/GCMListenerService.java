package com.austin.goaltracker.Controller;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.R;

import com.austin.goaltracker.View.LoginActivity;
import com.austin.goaltracker.View.PendingReminders.ReminderListActivity;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Service called when GAE issues a push notification to a device. This then formats the data to
 * display and starts a service to add the pending notification to the firebase
 */
public class GCMListenerService extends GcmListenerService {

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {

        // TODO THIS CRASHES WHEN PHONE TURNS ON
        Intent intent = new Intent(GoalTrackerApplication.INSTANCE, PendingNotificationIntentService.class);
        intent.putExtra("accountId",data.getString("accountId"));
        intent.putExtra("goalId", data.getString("goalId"));
        intent.putExtra("dateTimeNotified", Long.parseLong(data.getString("dateTimeNotified")));
        GoalTrackerApplication.INSTANCE.startService(intent);

        String message = data.getString("message");
        sendNotification(message);
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent;
        if (Util.currentUser != null) {
            intent = new Intent(this, ReminderListActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.goals_icon);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(GoalTrackerApplication.notificationId++, notificationBuilder.build());
    }


}