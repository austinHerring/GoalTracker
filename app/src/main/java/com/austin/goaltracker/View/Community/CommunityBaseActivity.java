package com.austin.goaltracker.View.Community;

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
import android.widget.Spinner;

import com.austin.goaltracker.Controller.Adapters.BaseActivitySelectorAdapter;
import com.austin.goaltracker.Controller.Util;
import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.R;
import com.austin.goaltracker.View.Friends.FriendsBaseActivity;
import com.austin.goaltracker.View.Goals.GoalsBaseActivity;
import com.austin.goaltracker.View.History.HistoryBaseActivity;
import com.austin.goaltracker.View.LoginActivity;
import com.austin.goaltracker.View.ReminderListActivity;
import com.austin.goaltracker.View.SettingsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CommunityBaseActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private static Button buttonPending;
    private static int mPendingCount = 0;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_base);
        GoalTrackerApplication.INSTANCE.setCurrentActivity(this);
        setupWindowAnimations();
        setUpNotificationCountWithFirebaseListener();

        spinner = (Spinner) findViewById(R.id.spinnerSelectBase);
        spinner.setAdapter(new BaseActivitySelectorAdapter(this, R.layout.layout_spinner_dropdown));
        spinner.setOnItemSelectedListener(this);

        //TODO Should be able to add friend from detail
        //TODO Where to store events and filter
//        ref.limitToLast(20).on("child_added", function(snapshot) {
//            // Add link to home page.
//        });
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
        } else if (!parent.getItemAtPosition(pos).toString().equals(this.toString())
                && parent.getItemAtPosition(pos).equals("History")) {
            Intent i = new Intent(getApplicationContext(), HistoryBaseActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public String toString() {
        return GoalTrackerApplication.ACTIVITIES[2];
    }

    @Override
    public void onStart() {
        super.onStart();
        spinner = (Spinner) findViewById(R.id.spinnerSelectBase);
        spinner.setAdapter(new BaseActivitySelectorAdapter(this, R.layout.layout_spinner_dropdown));
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(GoalTrackerApplication.COMMUNITY); // index in list
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
                    startActivity(i, ActivityOptions.makeSceneTransitionAnimation(CommunityBaseActivity.this).toBundle());
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
            FirebaseAuth.getInstance().signOut();
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
        mRootRef.child("accounts").child(Util.currentUser.getId())
                .child("pending goal notifications").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    setPendingGoalsCountInActionBar(0);
                } else {
                    setPendingGoalsCountInActionBar(((HashMap<String, Object>) snapshot.getValue()).size());
                }

            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

}

