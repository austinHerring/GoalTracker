package com.austin.goaltracker.View.Friends;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.austin.goaltracker.Controller.Adapters.GetAccountListAdapter;
import com.austin.goaltracker.Controller.Util;
import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.R;

import java.util.HashMap;

public class FriendsOfFriendsDetailActivity extends Activity {
    private HashMap<String, String> mFriends;
    private String mUserName;
    public GetAccountListAdapter ListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_of_friends_detail);
        GoalTrackerApplication.INSTANCE.setCurrentActivity(this);
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        mFriends = (HashMap<String, String>) getIntent().getSerializableExtra("friends");
        mUserName = getIntent().getStringExtra("user");
        setTitle(mUserName + "'s friends");
    }

    @Override
    public void onStart() {
        super.onStart();
        Util.GetAccounts(this, mFriends, true);
    }

    @Override
    public String toString() {
        return "Friends of Friends";
    }
}
