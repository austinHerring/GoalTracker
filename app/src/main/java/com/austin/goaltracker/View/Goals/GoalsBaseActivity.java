package com.austin.goaltracker.View.Goals;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
import com.austin.goaltracker.Model.StreakSustainerGoal;
import com.austin.goaltracker.R;

import com.austin.goaltracker.View.Friends.FriendsBaseActivityListActivity;
import com.austin.goaltracker.View.LoginActivity;
import com.austin.goaltracker.View.SettingsActivity;


public class GoalsBaseActivity extends FragmentActivity implements AdapterView.OnItemSelectedListener {

    ListView listOfGoals;
    Spinner spinner;
    String[] activities = {"Goals", "Friends", "Messages", "History"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals_base);

        spinner = (Spinner) findViewById(R.id.spinnerSelectBase);
        spinner.setAdapter(new BaseActivityAdapter(this, R.layout.layout_spinner_dropdown, activities));
        spinner.setOnItemSelectedListener(this);

        Button buttonNewGoal = (Button) findViewById(R.id.buttonNewGoal);
        buttonNewGoal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), GoalsTypeSelectActivity.class);
                startActivity(i);
            }
        });

        listOfGoals = (ListView) findViewById(R.id.listOfGoals);
        GoalAdapter arrayAdapter = new GoalAdapter(this, android.R.layout.simple_list_item_1, Util.currentUser.getGoals());
        listOfGoals.setAdapter(arrayAdapter);
        listOfGoals.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Goal listItem = (Goal) listOfGoals.getItemAtPosition(position);
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
        return true;
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (!parent.getItemAtPosition(pos).equals(this.toString())) {
            if (parent.getItemAtPosition(pos).equals("Friends")) {
                Intent i = new Intent(getApplicationContext(), FriendsBaseActivityListActivity.class);
                startActivity(i);
            }
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
}
