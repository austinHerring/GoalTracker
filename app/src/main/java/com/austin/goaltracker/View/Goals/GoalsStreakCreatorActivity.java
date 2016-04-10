package com.austin.goaltracker.View.Goals;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.austin.goaltracker.Controller.GAEDatastoreController;
import com.austin.goaltracker.Controller.GoalMediator;
import com.austin.goaltracker.Controller.ToastDisplayer;
import com.austin.goaltracker.Controller.Util;
import com.austin.goaltracker.Model.Account;
import com.austin.goaltracker.Model.Goal;
import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.Model.StreakSustainerGoal;
import com.austin.goaltracker.R;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class GoalsStreakCreatorActivity extends Activity implements TimePickerDialog.OnTimeSetListener {
    String goalTask;
    Goal.IncrementType type;
    int skipNumber;
    int mPromptMinute;
    int mPromptHour;
    Button setButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_goals_streak_creator);
        setupWindowAnimations();
        GoalTrackerApplication.INSTANCE.setCurrentActivity(this);

        //SET CURRENT TIME
        mPromptMinute = Calendar.getInstance().get(Calendar.MINUTE);
        mPromptHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        setButton = (Button) findViewById(R.id.promptTimeStreak);
        setPromptTimeDisplay();

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dpd = TimePickerDialog.newInstance(
                        GoalsStreakCreatorActivity.this, mPromptHour, mPromptMinute, false);
                dpd.show(getFragmentManager(), "Datepickerdialog");
                dpd.setAccentColor(getResources().getColor(R.color.primaryP));
                dpd.setTitle("Select a Prompt Time");
            }
        });

        Button createGoalButton = (Button) findViewById(R.id.buttonfinish);
        createGoalButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goalTask = ((EditText) findViewById(R.id.goaltaskStreak)).getText().toString();
                type = GoalMediator.convertUItoType(((Spinner) findViewById(R.id.goalincrementStreak)).getSelectedItem().toString());
                skipNumber = Integer.parseInt(((EditText) findViewById(R.id.skipNumber)).getText().toString());
                if (!goalTask.equals("")) {
                    Account user = Util.currentUser;
                    StreakSustainerGoal newGoal = new StreakSustainerGoal(GoalMediator.pasteGoalTitle(), type);
                    newGoal.setTask(goalTask);
                    newGoal.setCheatNumber(skipNumber);
                    newGoal.setCheatsRemaining(skipNumber);
                    String cronKey = GAEDatastoreController.persistCron(newGoal, mPromptMinute, mPromptHour);
                    newGoal.setCronJobKey(cronKey);
                    user.addGoal(newGoal);
                    Util.updateAccountOnDB(user);
                    ToastDisplayer.displayHint("Goal Created",
                            ToastDisplayer.MessageType.SUCCESS, getApplicationContext());
                    Intent i = new Intent(getApplicationContext(), GoalsBaseActivity.class);
                    startActivity(i);
                } else {
                    ToastDisplayer.displayHint("Fill in all fields",
                            ToastDisplayer.MessageType.FAILURE, getApplicationContext());
                }
            }
        });
    }

    private void setupWindowAnimations() {
        Transition slideIn = new Slide(Gravity.LEFT);
        slideIn.setDuration(600);
        getWindow().setEnterTransition(slideIn);
        Transition slideOut = new Slide(Gravity.LEFT);
        slideOut.setDuration(600);
        getWindow().setExitTransition(slideOut);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(getApplicationContext(), GoalsTypeSelectActivity.class);
                startActivity(i, ActivityOptions.makeSceneTransitionAnimation(GoalsStreakCreatorActivity.this).toBundle());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int day) {
        mPromptHour = hourOfDay;
        mPromptMinute = minute;
        setPromptTimeDisplay();
    }

    private void setPromptTimeDisplay() {
        int h = mPromptHour % 12 == 0 ? 12 : mPromptHour % 12;
        String m = String.format("%02d", mPromptMinute);
        String partOfDay = mPromptHour > 11 ? " PM" : " AM";
        String result = h + ":" + m + partOfDay;
        setButton.setText(result);
    }
}