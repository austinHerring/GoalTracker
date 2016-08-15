package com.austin.goaltracker.View.Friends;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.austin.goaltracker.Controller.Adapters.GetGoalListAdapter;
import com.austin.goaltracker.Controller.Converter;
import com.austin.goaltracker.Controller.Util;
import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.Model.RealTime.GetAccount;
import com.austin.goaltracker.Model.RealTime.GetGoals;
import com.austin.goaltracker.R;

import java.util.concurrent.TimeUnit;

public class FriendsDetailActivity extends Activity {
    private GetAccount mGetAccount;
    private Button removeFriendButton;
    private boolean isCurrentUserFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_detail);
        GoalTrackerApplication.INSTANCE.setCurrentActivity(this);
        mGetAccount = (GetAccount) getIntent().getSerializableExtra("com.austin.goaltracker.Model.RealTime.GetAccount");
        isCurrentUserFriend = getIntent().getBooleanExtra("isCurrentUserFriend", false);

        String fullName= mGetAccount.getNameFirst() + " " + mGetAccount.getNameLast();
        long friends = mGetAccount.getTotalFriends();
        if (mGetAccount.getFriends().containsKey(Util.currentUser.getId())) {
            friends--;
        }
        String friendsString = friends + "";
        
        String goals = mGetAccount.getTotalGoalsStarted() + " goals started";
        long goalsCompleted = mGetAccount.getTotalGoalsCompleted();
        String goalsCompletedFormatted = (goalsCompleted == 1) ? goalsCompleted + " goal" : goalsCompleted + " goals";
        String longestStreak = formatLongestStreak(mGetAccount.getLongestStreak());
        Drawable profilePicture = Converter.makeDrawableFromBase64String(this, mGetAccount.getPictureData());

        setTitle(mGetAccount.getUsername() + "'s profile");
        ((TextView) findViewById(R.id.profileName)).setText(fullName);
        ((TextView) findViewById(R.id.profileFriends)).setText(friendsString);
        ((TextView) findViewById(R.id.profileGoalsTotal)).setText(goals);
        ((TextView) findViewById(R.id.profileGoalsCompleted)).setText(goalsCompletedFormatted);
        ((TextView) findViewById(R.id.profileGoalsStreak)).setText(longestStreak);
        ((TextView) findViewById(R.id.profileGoalsStreak)).setText(longestStreak);
        if (profilePicture != null) {
            findViewById(R.id.detailProfilePic).setBackground(profilePicture);
        }

        GetGoals getGoals = mGetAccount.getGetGoals();
        if (getGoals != null && getGoals.Goals.size() > 0) {
            ListView listOfGoals = (ListView) findViewById(R.id.listGoals);
            GetGoalListAdapter goalListAdapter = new GetGoalListAdapter(this, R.layout.layout_getgoal_row, mGetAccount.getGetGoals());
            listOfGoals.setAdapter(goalListAdapter);
        }

        removeFriendButton = (Button) findViewById(R.id.removeFriendButton);
        if (isCurrentUserFriend) {
            removeFriendButton.setVisibility(View.VISIBLE);
            removeFriendButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Util.currentUser.removeFriend(mGetAccount);
                    Intent i = new Intent(getApplicationContext(), FriendsBaseActivity.class);
                    startActivity(i);
                }
            });
        }


        findViewById(R.id.friendsClickable).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), FriendsOfFriendsDetailActivity.class);
                i.putExtra("friends", mGetAccount.getFriends());
                i.putExtra("user", mGetAccount.getUsername());
                startActivity(i);
            }
        });

    }

    private String formatLongestStreak(long timeInMillis) {
        long days = TimeUnit.MILLISECONDS.toDays(timeInMillis);
        return (days == 1) ? days + " day" : days + " days";

    }
}
