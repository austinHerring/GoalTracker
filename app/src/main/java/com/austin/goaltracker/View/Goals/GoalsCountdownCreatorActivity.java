package com.austin.goaltracker.View.Goals;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.austin.goaltracker.Controller.GAEDatastoreController;
import com.austin.goaltracker.Controller.GoalMediator;
import com.austin.goaltracker.Controller.ToastDisplayer;
import com.austin.goaltracker.Controller.Util;
import com.austin.goaltracker.Model.Account;
import com.austin.goaltracker.Model.CountdownCompleterGoal;
import com.austin.goaltracker.Model.Goal;
import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.R;
import com.firebase.client.FirebaseException;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class GoalsCountdownCreatorActivity extends Activity implements TimePickerDialog.OnTimeSetListener {
    boolean firstCall = true;  // For setting up calendar one day ahead
    int year_x, month_x, day_x;
    static final int DIALOG_ID = 0;
    String goalTask;
    Goal.IncrementType type;
    Button setButtonDate;
    TextView dateFinish;
    int mPromptMinute;
    int mPromptHour;
    Button setButtonTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_goals_countdown_creator);
        setupWindowAnimations();
        GoalTrackerApplication.INSTANCE.setCurrentActivity(this);

        //SET CURRENT TIME
        mPromptMinute = Calendar.getInstance().get(Calendar.MINUTE);
        mPromptHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        setButtonTime = (Button) findViewById(R.id.promptTimeCountdown);
        setPromptTimeDisplay();

        setButtonTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dpd = TimePickerDialog.newInstance(
                        GoalsCountdownCreatorActivity.this, mPromptHour, mPromptMinute, false);
                dpd.show(getFragmentManager(), "Datepickerdialog");
                dpd.setAccentColor(getResources().getColor(R.color.primaryP));
                dpd.setTitle("Select a Prompt Time");
            }
        });

        //SET CURRENT DATE
        final Calendar c = Calendar.getInstance();
        year_x = c.get(Calendar.YEAR);
        month_x = c.get(Calendar.MONTH);
        day_x = c.get(Calendar.DAY_OF_MONTH) + 1;
        setButtonDate = (Button) findViewById(R.id.promptDate);
        setButtonDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_ID);
            }
        });
        dateFinish = (TextView) findViewById(R.id.textDateEnd);
        setPromptDateDisplay();

        Button createGoalButton = (Button) findViewById(R.id.buttonfinish);
        createGoalButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goalTask = ((EditText) findViewById(R.id.goaltask)).getText().toString();
                type = GoalMediator.convertUItoType(((Spinner) findViewById(R.id.goalincrement)).getSelectedItem().toString());
                if (!goalTask.equals("")) {
                    Account account = Util.currentUser;
                    Calendar endDate = new GregorianCalendar(year_x, month_x, day_x);
                    endDate.set(Calendar.HOUR_OF_DAY, mPromptHour);
                    endDate.set(Calendar.MINUTE, mPromptMinute);
                    CountdownCompleterGoal newGoal = new CountdownCompleterGoal(GoalMediator.pasteGoalTitle(), type, endDate);
                    newGoal.setTask(goalTask);
                    newGoal.setCronJobKey(GAEDatastoreController.createCronKey(newGoal));
                    try {
                        Util.updateAccountGoalOnDB(account.getId(), newGoal);
                        GAEDatastoreController.persistCron(newGoal, mPromptMinute, mPromptHour);
                        account.addGoal(newGoal.getId(), newGoal);
                        ToastDisplayer.displayHint("Goal Created",
                                ToastDisplayer.MessageType.SUCCESS, getApplicationContext());
                        Intent i = new Intent(getApplicationContext(), GoalsBaseActivity.class);
                        startActivity(i);
                    } catch (FirebaseException e) {
                        ToastDisplayer.displayHint("Could not connect to database",
                                ToastDisplayer.MessageType.FAILURE, getApplicationContext());
                    }
                } else {
                    ToastDisplayer.displayHint("Fill in all fields",
                            ToastDisplayer.MessageType.FAILURE, getApplicationContext());
                }
            }
        });
    }

    private void setPromptDateDisplay() {
        GregorianCalendar g = new GregorianCalendar(year_x, month_x, day_x);
        Calendar startDate = Calendar.getInstance();
        long dayDiff = 0;
        while (startDate.before(g)) {
            startDate.add(Calendar.DAY_OF_MONTH, 1);
            dayDiff++;
        }
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
        setButtonDate.setText(df.format(g.getTime()));
        dateFinish.setText(String.format("%d %s from now", dayDiff, (dayDiff != 1) ? "days" : "day"));
    }

    // Date picker listener variable used to create a dialog
    private DatePickerDialog.OnDateSetListener dPickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear;
            day_x = dayOfMonth;
            firstCall = false;
            setPromptDateDisplay();
        }
    };

    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_ID) {
            return new DatePickerDialog(this, dPickerListener, year_x, month_x, day_x);
        } else {
            return null;
        }
    }

    private void setupWindowAnimations() {
        Transition slideIn = new Slide(Gravity.RIGHT);
        slideIn.setDuration(600);
        getWindow().setEnterTransition(slideIn);
        Transition slideOut = new Slide(Gravity.RIGHT);
        slideOut.setDuration(600);
        getWindow().setExitTransition(slideOut);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(getApplicationContext(), GoalsTypeSelectActivity.class);
                startActivity(i, ActivityOptions.makeSceneTransitionAnimation(GoalsCountdownCreatorActivity.this).toBundle());
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
        setButtonTime.setText(result);
    }
}
