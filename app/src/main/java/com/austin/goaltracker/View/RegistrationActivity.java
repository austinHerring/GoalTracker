package com.austin.goaltracker.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.austin.goaltracker.Controller.Mediators.LoginMediator;
import com.austin.goaltracker.Controller.ToastDisplayer;
import com.austin.goaltracker.Controller.Util;
import com.austin.goaltracker.Model.Account;
import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.Model.Password;
import com.austin.goaltracker.Model.Enums.ToastType;
import com.austin.goaltracker.R;
import com.austin.goaltracker.View.Goals.GoalsBaseActivity;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;


public class RegistrationActivity extends Activity {

    private String nameFirst;
    private String nameLast;
    private String username;
    private String emailaddress;
    private String password;
    private String passwordConfirm;
    private String errorMessage;
    private Activity activity;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setTitle("Registration");
        GoalTrackerApplication.INSTANCE.setCurrentActivity(this);
        activity = this;

        mAuth = FirebaseAuth.getInstance();

        // Copy the carry over username and password from login screen
        ((EditText) findViewById(R.id.emailaddress)).setText(LoginMediator.pasteEmail());
        ((EditText) findViewById(R.id.password)).setText(LoginMediator.pastePassword());

        final Button createAccountButton = (Button) findViewById(R.id.buttonCreateAccount);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                nameFirst = ((EditText) findViewById(R.id.namefirst)).getText().toString();
                nameLast = ((EditText) findViewById(R.id.namelast)).getText().toString();
                username = ((EditText) findViewById(R.id.email)).getText().toString();
                emailaddress = ((EditText) findViewById(R.id.emailaddress)).getText().toString();
                password = ((EditText) findViewById(R.id.password)).getText().toString();
                passwordConfirm = ((EditText) findViewById(R.id.passwordConfirm)).getText().toString();
                createAccount();
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

    private void createAccount() {
        if (checkInputSyntax()) {
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

            mAuth.createUserWithEmailAndPassword(emailaddress, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Account account = new Account(nameFirst, nameLast, username, new Password(password), emailaddress);
                            account.setId(mAuth.getCurrentUser().getUid());
                            Intent intent = new Intent(getApplicationContext(), GoalsBaseActivity.class);
                            Util.registerUserAndLoad(activity, intent, account);

                        } else {
                            ToastDisplayer.displayHint("Registration failed. Try a new email", ToastType.FAILURE, getApplicationContext());
                            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        }
                    }
                });
        }
    }

    private boolean checkInputSyntax() {
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
