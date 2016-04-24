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
import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.Model.NewMemberEmail;
import com.austin.goaltracker.R;
import com.austin.goaltracker.View.Goals.GoalsBaseActivity;

import com.firebase.client.ValueEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

public class RegistrationActivity extends Activity {

    String nameFirst;
    String nameLast;
    String username;
    String emailaddress;
    String password;
    String passwordConfirm;
    String errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setTitle("Registration");
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

                // Check that passwords match first
                if (password.equals(passwordConfirm)) {
                    attemptRegistration(nameFirst, nameLast, username, password, emailaddress);
                } else {
                    ToastDisplayer.displayHint("Passwords Do Not Match",
                            ToastDisplayer.MessageType.FAILURE, getApplicationContext());
                }
            }
        });
    }

    void attemptRegistration(String nameFirst, String nameLast, String username,
                             String password, String emailaddress) {
        final String nF = nameFirst;
        final String nL = nameLast;
        final String u = username;
        final String e = emailaddress;
        final String p = password;
        Util.db.child("accounts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Util.retrieveUsersToLocal(snapshot);
                errorMessage = Util.registerUserOnDB(nF, nL, u, p, e);
                if (errorMessage == null) {
                    EmailDispatchService dispatcher =
                            new EmailDispatchService(new NewMemberEmail(Util.currentUser));
                    errorMessage = dispatcher.send();
                    if (errorMessage == null) {
                        GAEDatastoreController.registerdeviceForCurrentUser();
                        Intent i = new Intent(getApplicationContext(), GoalsBaseActivity.class);
                        startActivity(i);
                        ToastDisplayer.displayHint("Registration Successful",
                                ToastDisplayer.MessageType.SUCCESS, getApplicationContext());
                    } else {
                        ToastDisplayer.displayHint(errorMessage,
                                ToastDisplayer.MessageType.FAILURE, getApplicationContext());
                    }
                } else {
                    ToastDisplayer.displayHint(errorMessage,
                            ToastDisplayer.MessageType.FAILURE, getApplicationContext());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
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
