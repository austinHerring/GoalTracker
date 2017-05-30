package com.austin.goaltracker.Controller.Services;

import android.app.IntentService;
import android.content.Intent;

import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    public PendingNotificationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            final String accountId = intent.getStringExtra("accountId");
            final String goalId = intent.getStringExtra("goalId");
            final long dateTimeNotified = intent.getLongExtra("dateTimeNotified", 0);
            addPendingGoalNotificationOnDB(accountId, goalId, dateTimeNotified);
        } catch (Exception e) {
            throw new RuntimeException("Interrupted Thread:" + e.getMessage());
        }
    }

    private void addPendingGoalNotificationOnDB(String accountId, String associatedGoalId, long dateTimeNotified) throws DatabaseException
    {
//        Firebase firebaseRef = new Firebase(GoalTrackerApplication.FIREBASE_URL);
        DatabaseReference accountGoalsRef = mRootRef.child("accounts").child(accountId).child("pending goal notifications");
        DatabaseReference row = accountGoalsRef.push();
        HashMap<String,Object> newGoalAsEntry = new HashMap<>();
        newGoalAsEntry.put("id", row.getKey());
        newGoalAsEntry.put("associatedGoalId", associatedGoalId);
        newGoalAsEntry.put("dateTimeNotified", dateTimeNotified);
        row.setValue(newGoalAsEntry);
    }
}
