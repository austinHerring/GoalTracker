package com.austin.goaltracker.View;

import android.app.ListActivity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;
import android.widget.ListView;

import com.austin.goaltracker.Controller.PendingNotificationListAdapter;
import com.austin.goaltracker.Controller.ToastDisplayer;
import com.austin.goaltracker.Controller.Util;
import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class ReminderListActivity extends ListActivity {
    private PendingNotificationListAdapter mListAdapter;
    private Firebase mFirebaseListRef;
    private ValueEventListener mConnectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_list);
        setupWindowAnimations();
        mFirebaseListRef = new Firebase(GoalTrackerApplication.FIREBASE_URL).child("accounts")
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
        final ListView listView = getListView();
        mListAdapter = new PendingNotificationListAdapter(mFirebaseListRef.limit(50), this, R.layout.reminder_list_content);
        listView.setAdapter(mListAdapter);
        mListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mListAdapter.getCount() - 1);
            }
        });

        // Finally, a little indication of connection status
        mConnectedListener = mFirebaseListRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (!connected) {
                    ToastDisplayer.displayHint("Could not connect to Database",
                            ToastDisplayer.MessageType.FAILURE, getApplicationContext());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseListRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mListAdapter.cleanup();
    }
}
