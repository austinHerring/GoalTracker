package com.austin.goaltracker.Controller;

import android.app.IntentService;
import android.content.Intent;

import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Service that persists a notification to the firebase when gcm sends a push notification
 */
public class PendingNotificationIntentService extends IntentService {
    private static final String TAG = "PendingNotificationIntentService";
    static Logger Log = Logger.getLogger(PendingNotificationIntentService.class.getName());

    public PendingNotificationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            final String accountId = intent.getStringExtra("accountId");
            final String goalId = intent.getStringExtra("goalId");
            final long dateTimeNotified = intent.getLongExtra("dateTimeNotified", 0);
            final Firebase firebaseRef = new Firebase(GoalTrackerApplication.FIREBASE_URL);
            final Firebase queriedGoalRef = firebaseRef
                    .child("accounts").child(accountId)
                    .child("goals").child(goalId);
            queriedGoalRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    HashMap<String, Object> goalData = (HashMap<String, Object>)snapshot.getValue();
                    addPendingGoalNotificationOnDB(
                            firebaseRef,
                            accountId,
                            goalId,
                            dateTimeNotified,
                            (String) goalData.get("name"));
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.info("Firebase error");
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Interrupted Thread:" + e.getMessage());
        }
    }

    private void addPendingGoalNotificationOnDB(
            Firebase firebaseRef,
            String accountId,
            String associatedGoalId,
            long dateTimeNotified,
            String name) throws FirebaseException
    {
        Firebase accountGoalsRef = firebaseRef.child("accounts").child(accountId).child("pending goal notifications");
        Firebase row = accountGoalsRef.push();
        HashMap<String,Object> newGoalAsEntry = new HashMap<>();
        newGoalAsEntry.put("id", row.getKey());
        newGoalAsEntry.put("associatedGoalId", associatedGoalId);
        newGoalAsEntry.put("dateTimeNotified", dateTimeNotified);
        newGoalAsEntry.put("name", name);
        row.setValue(newGoalAsEntry, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    System.out.println("Data could not be saved. " + firebaseError.getMessage());
                    throw new FirebaseException(firebaseError.getMessage());
                } else {
                    System.out.println("Data saved successfully.");
                }
            }
        });
    }
}
