package com.austin.goaltracker.View;

import com.austin.goaltracker.Controller.GAEDatastoreController;
import com.austin.goaltracker.Controller.LoginMediator;
import com.austin.goaltracker.Controller.ToastDisplayer;
import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.R;
import com.austin.goaltracker.Controller.Util;
import com.austin.goaltracker.View.Goals.GoalsBaseActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class LoginActivity extends Activity {

    private EditText usernameInput;
    private EditText passwordInput;
    private String errorMessage;
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoalTrackerApplication.INSTANCE.setCurrentActivity(this);
        Firebase.setAndroidContext(this);
        Firebase database = new Firebase(GoalTrackerApplication.FIREBASE_URL);
        Util.setDB(database);
        setTitle("Login");
        setContentView(R.layout.activity_login);

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        //set up GUI components
        usernameInput = (EditText) findViewById(R.id.username);
        passwordInput = (EditText) findViewById(R.id.password);
        Button loginButton = (Button) findViewById(R.id.buttonLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String usernameInfo = usernameInput.getText().toString();
                String passwordInfo = passwordInput.getText().toString();
                validateLoginAndSignIn(usernameInfo, passwordInfo);
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

    /**
     * Validates a username and password provided by the user.
     * @param username The input username.
     * @param password The input password.
     */
    void validateLoginAndSignIn(String username, String password) {
        final String usernameInput = username;
        final String passwordInput = password;
        Util.db.child("accounts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Util.retrieveUsers(snapshot);
                errorMessage = Util.authenticate(usernameInput, passwordInput);
                if (errorMessage == null) {
                    if (isConnected) {
                        GAEDatastoreController.registerdeviceForCurrentUser();
                        Intent i = new Intent(getApplicationContext(), GoalsBaseActivity.class);
                        startActivity(i);
                    } else {
                        ToastDisplayer.displayHint("Not Connected to Internet",
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

