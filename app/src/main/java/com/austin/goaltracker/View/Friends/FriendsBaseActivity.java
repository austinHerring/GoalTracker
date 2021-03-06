package com.austin.goaltracker.View.Friends;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.austin.goaltracker.Controller.Adapters.BaseActivitySelectorAdapter;
import com.austin.goaltracker.Controller.Adapters.GetAccountListAdapter;
import com.austin.goaltracker.Controller.Util;
import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.R;

import com.austin.goaltracker.View.Community.CommunityBaseActivity;
import com.austin.goaltracker.View.Goals.GoalsBaseActivity;
import com.austin.goaltracker.View.History.HistoryBaseActivity;
import com.austin.goaltracker.View.LoginActivity;
import com.austin.goaltracker.View.ReminderListActivity;
import com.austin.goaltracker.View.SettingsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class FriendsBaseActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private static Button buttonPending;
    private static FloatingActionButton buttonNewFriend;
    private static int mPendingCount = 0;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mFirebaseRef;
    public GetAccountListAdapter ListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_base);
        GoalTrackerApplication.INSTANCE.setCurrentActivity(this);
        setUpNotificationCountWithFirebaseListener();
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

        buttonNewFriend = (FloatingActionButton) findViewById(R.id.buttonNewFriend);
        buttonNewFriend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), FriendsAddActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        spinner = (Spinner) findViewById(R.id.spinnerSelectBase);
        spinner.setAdapter(new BaseActivitySelectorAdapter(this, R.layout.layout_spinner_dropdown));
        spinner.setSelection(GoalTrackerApplication.FRIENDS); // index in list
        spinner.setOnItemSelectedListener(this);
        Util.GetAccounts(this, Util.currentUser.getFriends(), false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);

        MenuItem countItem = menu.findItem(R.id.pending_goals);
        MenuItemCompat.setActionView(countItem, R.layout.feed_update_count);
        if (mPendingCount > 0) {
            countItem.setVisible(true);
            buttonPending = (Button) MenuItemCompat.getActionView(countItem);
            buttonPending.setText(String.valueOf(mPendingCount));
            buttonPending.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), ReminderListActivity.class);
                    startActivity(i, ActivityOptions.makeSceneTransitionAnimation(FriendsBaseActivity.this).toBundle());
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
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (!parent.getItemAtPosition(pos).equals(this.toString())
                && parent.getItemAtPosition(pos).toString().equals("Goals")) {
            Intent i = new Intent(getApplicationContext(), GoalsBaseActivity.class);
            startActivity(i);
        } else if (!parent.getItemAtPosition(pos).toString().equals(this.toString())
                && parent.getItemAtPosition(pos).equals("History")) {
            Intent i = new Intent(getApplicationContext(), HistoryBaseActivity.class);
            startActivity(i);
        } else if (!parent.getItemAtPosition(pos).toString().equals(this.toString())
                && parent.getItemAtPosition(pos).equals("Community")) {
            Intent i = new Intent(getApplicationContext(), CommunityBaseActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public String toString() {
        return GoalTrackerApplication.ACTIVITIES[1];
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

    private void setUpNotificationCountWithFirebaseListener() {
        mFirebaseRef = mRootRef
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
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }
}
