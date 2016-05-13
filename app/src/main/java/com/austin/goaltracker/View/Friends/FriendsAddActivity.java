package com.austin.goaltracker.View.Friends;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;

import com.austin.goaltracker.Controller.UserListAdapter;
import com.austin.goaltracker.Controller.Util;
import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.R;

public class FriendsAddActivity extends Activity {
    public UserListAdapter ListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_add);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        GoalTrackerApplication.INSTANCE.setCurrentActivity(this);
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        Util.GetAccounts(this, false);
    }

}
