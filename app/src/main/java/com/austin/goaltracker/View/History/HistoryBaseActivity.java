package com.austin.goaltracker.View.History;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.austin.goaltracker.Controller.Adapters.BaseActivitySelectorAdapter;
import com.austin.goaltracker.Controller.Adapters.GoalListAdapter;
import com.austin.goaltracker.Controller.Util;
import com.austin.goaltracker.Model.Enums.GoalClassification;
import com.austin.goaltracker.Model.Goal.CountdownCompleterGoal;
import com.austin.goaltracker.Model.Goal.Goal;
import com.austin.goaltracker.Model.Goal.StreakSustainerGoal;
import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.R;
import com.austin.goaltracker.View.Friends.FriendsBaseActivity;
import com.austin.goaltracker.View.Goals.GoalCountdownGraphicFragment;
import com.austin.goaltracker.View.Goals.GoalsBaseActivity;
import com.austin.goaltracker.View.Goals.GoalsStreakGraphicFragment;
import com.austin.goaltracker.View.Goals.GoalsTypeSelectActivity;
import com.austin.goaltracker.View.LoginActivity;
import com.austin.goaltracker.View.ReminderListActivity;
import com.austin.goaltracker.View.SettingsActivity;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;

public class HistoryBaseActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ListView listOfHistory;
    private Spinner spinner;
    private static Button buttonPending;
    private static int mPendingCount = 0;
    private Firebase mFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_base);
        GoalTrackerApplication.INSTANCE.setCurrentActivity(this);
        setupWindowAnimations();
        setUpNotificationCountWithFirebaseListener();

        spinner = (Spinner) findViewById(R.id.spinnerSelectBase);
        spinner.setAdapter(new BaseActivitySelectorAdapter(this, R.layout.layout_spinner_dropdown));
        spinner.setOnItemSelectedListener(this);

        listOfHistory = (ListView) findViewById(R.id.listOfHistory);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (!parent.getItemAtPosition(pos).toString().equals(this.toString())
                && parent.getItemAtPosition(pos).equals("Goals")) {
            Intent i = new Intent(getApplicationContext(), GoalsBaseActivity.class);
            startActivity(i);
        } else if (!parent.getItemAtPosition(pos).toString().equals(this.toString())
                && parent.getItemAtPosition(pos).equals("Friends")) {
            Intent i = new Intent(getApplicationContext(), FriendsBaseActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public String toString() {
        return "History";
    }

    @Override
    public void onStart() {
        super.onStart();
        spinner = (Spinner) findViewById(R.id.spinnerSelectBase);
        spinner.setAdapter(new BaseActivitySelectorAdapter(this, R.layout.layout_spinner_dropdown));
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(3); // index in list
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);
        setupWindowAnimations();

        MenuItem countItem = menu.findItem(R.id.pending_goals);
        MenuItemCompat.setActionView(countItem, R.layout.feed_update_count);
        if (mPendingCount > 0) {
            countItem.setVisible(true);
            buttonPending = (Button) MenuItemCompat.getActionView(countItem);
            buttonPending.setText(String.valueOf(mPendingCount));
            buttonPending.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), ReminderListActivity.class);
                    startActivity(i, ActivityOptions.makeSceneTransitionAnimation(HistoryBaseActivity.this).toBundle());
                }
            });
        } else {
            countItem.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(i);
            return true;
        } else if (id == R.id.action_logout) {
            Util.currentUser = null;    // Clear out the current user
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clean up all activities
            startActivity(i);
            finish();
            return true;
        } else if (id == R.id.pending_goals) {
            Intent i = new Intent(getApplicationContext(), ReminderListActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GoalTrackerApplication.INSTANCE.registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(GoalTrackerApplication.INSTANCE)
                .unregisterReceiver(GoalTrackerApplication.INSTANCE.mRegistrationBroadcastReceiver);
        GoalTrackerApplication.INSTANCE.isReceiverRegistered = false;
        super.onPause();
    }

    private void setPendingGoalsCountInActionBar(int count) {
        mPendingCount = count;
        invalidateOptionsMenu();
    }

    private void setupWindowAnimations() {
        getWindow().setAllowEnterTransitionOverlap(false);
        Transition fade = new Fade();
        fade.setDuration(400);
        getWindow().setExitTransition(fade);
        Transition slideIn = new Slide(Gravity.BOTTOM);
        slideIn.setDuration(400);
        getWindow().setExitTransition(slideIn);
    }

    private void setUpNotificationCountWithFirebaseListener() {
        mFirebaseRef = new Firebase(GoalTrackerApplication.FIREBASE_URL)
                .child("accounts")
                .child(Util.currentUser.getId())
                .child("pending goal notifications");
        mFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    setPendingGoalsCountInActionBar(0);
                } else {
                    setPendingGoalsCountInActionBar(((HashMap<String, Object>) snapshot.getValue()).size());
                }

            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }
}