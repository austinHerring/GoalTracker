package com.austin.goaltracker.View;

import com.austin.goaltracker.Controller.Mediators.LoginMediator;
import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.R;
import com.austin.goaltracker.Controller.Util;
import com.austin.goaltracker.View.Goals.GoalsBaseActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {

    private EditText usernameInput;
    private EditText passwordInput;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoalTrackerApplication.INSTANCE.setCurrentActivity(this);
        setTitle("Login");
        activity = this;
        setContentView(R.layout.activity_login);

        //set up GUI components
        usernameInput = (EditText) findViewById(R.id.username);
        passwordInput = (EditText) findViewById(R.id.password);
        Button loginButton = (Button) findViewById(R.id.buttonLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                String usernameInfo = usernameInput.getText().toString();
                String passwordInfo = passwordInput.getText().toString();
                Intent i = new Intent(getApplicationContext(), GoalsBaseActivity.class);
                Util.authenticateUserAndLoad(activity, i, usernameInfo, passwordInfo);
            }
        });

        Button registerButton = (Button) findViewById(R.id.buttonRegister);
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LoginMediator.copyLoginInfo(usernameInput.getText().toString(),
                        passwordInput.getText().toString());
                Intent i = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(i);
            }
        });

        Button forgotPasswordButton = (Button) findViewById(R.id.buttonForgotPassword);
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivity(i);
            }
        });
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

}

