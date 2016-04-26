package com.austin.goaltracker.View.Goals;

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

import com.austin.goaltracker.Controller.BaseActivityAdapter;
import com.austin.goaltracker.Controller.GoalAdapter;
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


public class GoalsBaseActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ListView listOfGoals;
    private static
    Spinner spinner;
    String[] activities = {"Goals", "Friends", "TEST", "Messages", "History"};
    private static Button buttonPending;
    private static Button buttonNewGoal;
    private static int mPendingCount = 0;
    private GoalAdapter goalAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals_base);
        GoalTrackerApplication.INSTANCE.setCurrentActivity(this);
        setPendingGoalsCountInActionBar();
        setupWindowAnimations();

        spinner = (Spinner) findViewById(R.id.spinnerSelectBase);
        spinner.setAdapter(new BaseActivityAdapter(this, R.layout.layout_spinner_dropdown, activities));
        spinner.setOnItemSelectedListener(this);

        buttonNewGoal = (Button) findViewById(R.id.buttonNewGoal);
        buttonNewGoal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), GoalsTypeSelectActivity.class);
                startActivity(i);
            }
        });

        listOfGoals = (ListView) findViewById(R.id.listOfGoals);
        goalAdapter = new GoalAdapter(this, android.R.layout.simple_list_item_1, Util.currentUser.goalsToList());
        listOfGoals.setAdapter(goalAdapter);
        listOfGoals.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Pulling from the visible items, not the entire list
                Goal listItem =  ((GoalAdapter)parent.getAdapter()).getItemFromFilteredList(position);
                goalAdapter.getFilter().filter(listItem.getId());

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

        MenuItem item = menu.findItem(R.id.pending_goals);
        MenuItemCompat.setActionView(item, R.layout.feed_update_count);
        buttonPending = (Button) MenuItemCompat.getActionView(item);
        buttonPending.setText(String.valueOf(mPendingCount));
        buttonPending.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ReminderListActivity.class);
                //startActivity(i);
                //TODO Fix the transition. Buggy right now
                startActivity(i, ActivityOptions.makeSceneTransitionAnimation(GoalsBaseActivity.this).toBundle());
            }
        });
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

    private void setPendingGoalsCountInActionBar() {
        mPendingCount = 0;
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
}
