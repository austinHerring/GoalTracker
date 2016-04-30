package com.austin.goaltracker.View.Goals;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
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

import com.austin.goaltracker.Controller.BaseActivitySelectorAdapter;
import com.austin.goaltracker.Controller.GoalListAdapter;
import com.austin.goaltracker.Controller.Util;
import com.austin.goaltracker.Model.CountdownCompleterGoal;
import com.austin.goaltracker.Model.Goal;
import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.Model.StreakSustainerGoal;
import com.austin.goaltracker.R;

import com.austin.goaltracker.View.Friends.FriendsBaseActivityListActivity;
import com.austin.goaltracker.View.LoginActivity;
import com.austin.goaltracker.View.PendingReminders.ReminderListActivity;
import com.austin.goaltracker.View.SettingsActivity;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;


public class GoalsBaseActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ListView listOfGoals;
    private static
    Spinner spinner;
    String[] activities = {"Goals", "Friends", "TEST", "Messages", "History"};
    private static Button buttonPending;
    private static Button buttonNewGoal;
    private static int mPendingCount = 0;
    private GoalListAdapter goalListAdapter;
    private Firebase mFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals_base);
        GoalTrackerApplication.INSTANCE.setCurrentActivity(this);
        setupWindowAnimations();
        setUpNotificationCountWithFirebaseListener();

        spinner = (Spinner) findViewById(R.id.spinnerSelectBase);
        spinner.setAdapter(new BaseActivitySelectorAdapter(this, R.layout.layout_spinner_dropdown, activities));
        spinner.setOnItemSelectedListener(this);

        buttonNewGoal = (Button) findViewById(R.id.buttonNewGoal);
        buttonNewGoal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), GoalsTypeSelectActivity.class);
                startActivity(i);
            }
        });

        listOfGoals = (ListView) findViewById(R.id.listOfGoals);
        goalListAdapter = new GoalListAdapter(this, android.R.layout.simple_list_item_1, Util.currentUser.goalsToList());
        listOfGoals.setAdapter(goalListAdapter);
        listOfGoals.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Pulling from the visible items, not the entire list
                Goal listItem =  ((GoalListAdapter)parent.getAdapter()).getItemFromFilteredList(position);
                goalListAdapter.getFilter().filter(listItem.getId());

                if (listItem.classification().equals(Goal.Classification.COUNTDOWN)) {
                    GoalCountdownGraphicFragment fragment = GoalCountdownGraphicFragment.newInstance((CountdownCompleterGoal)listItem);
                    getFragmentManager().beginTransaction().replace(R.id.goal_graphic, fragment).commit();
                } else {
                    GoalsStreakGraphicFragment fragment = GoalsStreakGraphicFragment.newInstance((StreakSustainerGoal) listItem);
                    getFragmentManager().beginTransaction().replace(R.id.goal_graphic, fragment).commit();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_goals_base, menu);
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
                    startActivity(i, ActivityOptions.makeSceneTransitionAnimation(GoalsBaseActivity.this).toBundle());
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
            i.putExtra("finish", true);
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
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (!parent.getItemAtPosition(pos).equals(this.toString())
                && parent.getItemAtPosition(pos).equals("Friends")) {
            Intent i = new Intent(getApplicationContext(), FriendsBaseActivityListActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public String toString() {
        return "Goals";
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
//        getWindow().setAllowEnterTransitionOverlap(false);
//        Transition fade = new Fade();
//        getWindow().setExitTransition(fade);
        Transition slideIn = new Slide(Gravity.BOTTOM);
        slideIn.setDuration(1000);
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
