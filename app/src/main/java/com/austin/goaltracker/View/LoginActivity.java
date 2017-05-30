package com.austin.goaltracker.View;

import com.austin.goaltracker.Controller.Mediators.LoginMediator;
import com.austin.goaltracker.Controller.ToastDisplayer;
import com.austin.goaltracker.Model.Enums.ToastType;
import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.R;
import com.austin.goaltracker.Controller.Util;
import com.austin.goaltracker.View.Goals.GoalsBaseActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

    private EditText emailInput;
    private EditText passwordInput;
    private Activity activity;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoalTrackerApplication.INSTANCE.setCurrentActivity(this);
        setTitle("Login");
        setContentView(R.layout.activity_login);

        activity = this;
        mAuth = FirebaseAuth.getInstance();
        emailInput = (EditText) findViewById(R.id.email);
        passwordInput = (EditText) findViewById(R.id.password);

        Button loginButton = (Button) findViewById(R.id.buttonLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                signIn(emailInput.getText().toString(), passwordInput.getText().toString());
            }
        });

        Button registerButton = (Button) findViewById(R.id.buttonRegister);
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LoginMediator.copyLoginInfo(emailInput.getText().toString(),
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

    private void signIn(final String email, final String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent i = new Intent(getApplicationContext(), GoalsBaseActivity.class);
                            Util.tryLoadUser(activity, i, user.getUid());
                        } else {
                            ToastDisplayer.displayHint("Authentication failed.", ToastType.FAILURE, LoginActivity.this);
                            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
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

}

