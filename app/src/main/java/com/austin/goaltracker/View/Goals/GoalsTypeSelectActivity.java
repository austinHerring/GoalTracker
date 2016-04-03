package com.austin.goaltracker.View.Goals;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Transition;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.austin.goaltracker.Controller.GoalMediator;
import com.austin.goaltracker.Controller.ToastDisplayer;
import com.austin.goaltracker.R;

public class GoalsTypeSelectActivity extends Activity {
    EditText goalTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_goals_type_select);
        setupWindowAnimations();

        //set up GUI components
        goalTitle = (EditText) findViewById(R.id.goaltitle);
        final Button countdown = (Button) findViewById(R.id.button_countdown_select);
        countdown.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (goalTitle.getText().toString().equals("")) {
                    ToastDisplayer.displayHint("Fill in all fields",
                            ToastDisplayer.MessageType.FAILURE, getApplicationContext());
                } else if(goalTitle.getText().toString().contains(";")) {
                    ToastDisplayer.displayHint("Semicolons are not permitted",
                            ToastDisplayer.MessageType.FAILURE, getApplicationContext());
                } else {
                    GoalMediator.copyInfo1(goalTitle.getText().toString());
                    Intent i = new Intent(getApplicationContext(), GoalsCountdownCreatorActivity.class);
                    startActivity(i,ActivityOptions.makeSceneTransitionAnimation(GoalsTypeSelectActivity.this).toBundle());
                }
            }
        });

        final Button streak = (Button) findViewById(R.id.button_streak_select);
        streak.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (goalTitle.getText().toString().equals("")) {
                    ToastDisplayer.displayHint("Fill in all fields",
                            ToastDisplayer.MessageType.FAILURE, getApplicationContext());
                } else {
                    GoalMediator.copyInfo1(goalTitle.getText().toString());
                    Intent i = new Intent(getApplicationContext(), GoalsStreakCreatorActivity.class);
                    startActivity(i,ActivityOptions.makeSceneTransitionAnimation(GoalsTypeSelectActivity.this).toBundle());
                }
            }
        });
    }

    private void setupWindowAnimations() {
        getWindow().setAllowEnterTransitionOverlap(false);
        Transition fade = new Fade();
        getWindow().setExitTransition(fade);
    }


}
