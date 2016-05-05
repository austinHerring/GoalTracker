package com.austin.goaltracker.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.austin.goaltracker.Controller.EmailDispatchService;
import com.austin.goaltracker.Controller.GAEDatastoreController;
import com.austin.goaltracker.Controller.LoginMediator;
import com.austin.goaltracker.Controller.ToastDisplayer;
import com.austin.goaltracker.Controller.Util;
import com.austin.goaltracker.Model.Account;
import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.Model.NewMemberEmail;
import com.austin.goaltracker.Model.Password;
import com.austin.goaltracker.Model.ToastType;
import com.austin.goaltracker.R;
import com.austin.goaltracker.View.Goals.GoalsBaseActivity;

import com.firebase.client.ValueEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

public class RegistrationActivity extends Activity {

    private String nameFirst;
    private String nameLast;
    private String username;
    private String emailaddress;
    private String password;
    private String passwordConfirm;
    private String errorMessage;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setTitle("Registration");
        GoalTrackerApplication.INSTANCE.setCurrentActivity(this);
        activity = this;

        // Copy the carry over username and password from login screen
        ((EditText) findViewById(R.id.username)).setText(LoginMediator.pasteUsername());
        ((EditText) findViewById(R.id.password)).setText(LoginMediator.pastePassword());

        Button createAccountButton = (Button) findViewById(R.id.buttonCreateAccount);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                nameFirst = ((EditText) findViewById(R.id.namefirst)).getText().toString();
                nameLast = ((EditText) findViewById(R.id.namelast)).getText().toString();
                username = ((EditText) findViewById(R.id.username)).getText().toString();
                emailaddress = ((EditText) findViewById(R.id.emailaddress)).getText().toString();
                password = ((EditText) findViewById(R.id.password)).getText().toString();
                passwordConfirm = ((EditText) findViewById(R.id.passwordConfirm)).getText().toString();
                if (checkInputSyntaxt()) {
                    Intent intent = new Intent(getApplicationContext(), GoalsBaseActivity.class);
                    Util.registerUserAndLoad(
                            activity,
                            intent,
                            new Account(nameFirst, nameLast, username, new Password(password), emailaddress)
                    );
                }
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

    private boolean checkInputSyntaxt() {
        // Checks that all fields are filled in
        if (nameFirst.equals("") || nameLast.equals("") || username.equals("") || emailaddress.equals("")
                || password.equals("")) {
            ToastDisplayer.displayHint("Fill In All Fields", ToastType.FAILURE, getApplicationContext());
            return false;
        }
        // Checks for valid Password.. 6 characters, at least 1 number, lower and upper letter
        if (password.length() < 6 || !(password.matches(".*[a-z].*") && password.matches(".*[A-Z].*")
                && password.matches(".*[0-9].*"))) {
            ToastDisplayer.displayHint("Invalid Password", ToastType.FAILURE, getApplicationContext());
            return false;
        }
        // Checks for valid email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailaddress).matches()) {
            ToastDisplayer.displayHint("Invalid Email Address", ToastType.FAILURE, getApplicationContext());
            return false;
        }
        //Checks if passwords match
        if (!password.equals(passwordConfirm)) {
            ToastDisplayer.displayHint("Passwords Do Not Match", ToastType.FAILURE, getApplicationContext());
            return false;
        }
        return true;
    }
}
