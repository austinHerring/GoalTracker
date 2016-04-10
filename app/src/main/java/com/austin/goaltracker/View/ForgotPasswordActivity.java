package com.austin.goaltracker.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.austin.goaltracker.Controller.EmailDispatcher;
import com.austin.goaltracker.Controller.ToastDisplayer;
import com.austin.goaltracker.Controller.Util;
import com.austin.goaltracker.Model.Account;
import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.Model.NewPasswordEmail;
import com.austin.goaltracker.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;
import com.firebase.client.ValueEventListener;

public class ForgotPasswordActivity extends Activity {
    private EditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        GoalTrackerApplication.INSTANCE.setCurrentActivity(this);
        username = (EditText) findViewById(R.id.username);

        Button submitButton = (Button) findViewById(R.id.buttonSubmit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                attemptReset();
            }
        });

    }

    private void attemptReset() {
        Util.db.child("accounts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Util.retrieveUsers(snapshot);
                try {
                    Account changedAccount = Util.resetPassword(username.getText().toString(), null);
                    NewPasswordEmail e = new NewPasswordEmail(changedAccount);
                    EmailDispatcher dispatcher = new EmailDispatcher();
                    dispatcher.send(e);
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                } catch (FirebaseException e) {
                    ToastDisplayer.displayHint(e.getMessage(),
                            ToastDisplayer.MessageType.FAILURE,getApplicationContext());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }
}
