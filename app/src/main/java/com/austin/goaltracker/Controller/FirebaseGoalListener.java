package com.austin.goaltracker.Controller;

import android.app.Activity;
import android.util.Log;

import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

import java.util.HashMap;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Listener class to update goals displayed in goal base activity and loads the changes to local
 */
public class FirebaseGoalListener implements ChildEventListener {

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
        if (Util.currentUser != null) {
            updateLocalGoals(dataSnapshot);
            updateList();
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        if (Util.currentUser != null) {
            updateLocalGoals(dataSnapshot);
            updateList();
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        if (Util.currentUser != null) {
            Util.currentUser.getGoals().remove(dataSnapshot.getKey());
            updateList();
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
        Log.e("Goal Listener", "Listen was cancelled, no more updates will occur");
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        Log.e("Goal Listener", "Listen was cancelled, no more updates will occur");
    }

    private void updateList() {
        Activity activity = GoalTrackerApplication.INSTANCE.getCurrentActivity();
        if (activity.toString().equals("Goals")) {
            activity.recreate();
        }
    }

    private void updateLocalGoals(DataSnapshot dataSnapshot) {
        if (Util.currentUser != null) {
            Util.loadUserGoal((HashMap<String, String>) dataSnapshot.getValue());
        }

    }

}
