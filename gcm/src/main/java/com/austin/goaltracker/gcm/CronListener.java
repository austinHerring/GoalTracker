package com.austin.goaltracker.gcm;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import java.util.Calendar;
import java.util.logging.Logger;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Firebase Listener that adds jobs to the GAE datastore when a new cron is generated in firebase
 */
public class CronListener implements ChildEventListener{
    static Logger Log = Logger.getLogger(CronListener.class.getName());
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    @Override
    public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
        Log.info("Firebase Listener CHILD ADDED");

        CronData cronData = snapshot.getValue(CronData.class);
        String keyAsString = snapshot.getKey();
        Key key = KeyFactory.createKey("NotificationJob", keyAsString);
        Entity job = new Entity("NotificationJob", key);
        long firstRunDate = calculateFirstNotificationDate(cronData.getPromptHour(),
                cronData.getPromptMinute(), cronData.getFrequency().equals("HOURLY"));
        job.setProperty("next_run_ts", firstRunDate);

        datastore.put(job);
        Log.info("NEW JOB CREATED: " + key);
    }

    @Override
    public void onChildRemoved(DataSnapshot snapshot) {
        datastore.delete(KeyFactory.createKey("NotificationJob", snapshot.getKey()));
        Log.info(KeyFactory.stringToKey(snapshot.getKey()) + " WAS REMOVED");
    }

    @Override
    public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
        Log.info("Firebase Listener CHILD CHANGED");
    }

    @Override
    public void onChildMoved(DataSnapshot snapshot, String previousChildKey) {
        Log.info("Firebase Listener CHILD MOVED");
    }

    @Override
    public void onCancelled(FirebaseError error) {
        Log.info("Firebase Listener CANCELLED");
    }

    private long calculateFirstNotificationDate(int hour, int min, boolean isHourly) {
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
