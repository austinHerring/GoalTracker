package com.austin.goaltracker.View;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.austin.goaltracker.Controller.Adapters.PendingNotificationListAdapter;
import com.austin.goaltracker.Controller.ToastDisplayer;
import com.austin.goaltracker.Controller.Util;
import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.Model.Enums.ToastType;
import com.austin.goaltracker.R;
import com.austin.goaltracker.View.Goals.GoalsBaseActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReminderListActivity extends AppCompatActivity {
    private PendingNotificationListAdapter mListAdapter;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mFirebaseListRef;
    private ValueEventListener mConnectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_list);
        GoalTrackerApplication.INSTANCE.setCurrentActivity(this);
        setupWindowAnimations();
        mFirebaseListRef = mRootRef.child("accounts")
                .child((Util.currentUser.getId())).child("pending goal notifications");
    }

    private void setupWindowAnimations() {
        Transition slideIn = new Slide(Gravity.TOP);
        slideIn.setDuration(1000);
        getWindow().setEnterTransition(slideIn);
    }

    @Override
    public void onStart() {
        super.onStart();
        final ListView listView = (ListView) findViewById(R.id.list_of_reminders);
        mListAdapter = new PendingNotificationListAdapter(mFirebaseListRef.limitToFirst(50), this, R.layout.layout_reminder_row);
        listView.setAdapter(mListAdapter);
        mListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mListAdapter.getCount() - 1);
            }
        });

        // A little indication of connection status
        mConnectedListener = mFirebaseListRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (!connected) {
                    ToastDisplayer.displayHint("Could not connect to Database", ToastType.FAILURE, getApplicationContext());
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseListRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mListAdapter.cleanup();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reminder_activity, menu);
        setupWindowAnimations();
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
        } else if (id == R.id.action_home) {
            Intent i = new Intent(getApplicationContext(), GoalsBaseActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
