package com.austin.goaltracker.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.austin.goaltracker.Controller.EmailDispatchService;
import com.austin.goaltracker.Controller.ToastDisplayer;
import com.austin.goaltracker.Controller.Util;
import com.austin.goaltracker.Model.Account;
import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.Model.NewPasswordEmail;
import com.austin.goaltracker.Model.Password;
import com.austin.goaltracker.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;
import com.firebase.client.ValueEventListener;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

import java.util.Map;

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
                Util.retrieveUsersToLocal(snapshot);
                try {
                    String accountId = getAccountIdFromUsername(username.getText().toString());
                    Account account = Util.registeredUsers.get(accountId);
                    Password password = Util.updatePasswordForAccountOnDB(account, null);
                    account.setPassword(password);
                    NewPasswordEmail email = new NewPasswordEmail(account);
                    EmailDispatchService dispatcher = new EmailDispatchService(email);
                    dispatcher.send();
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                } catch (FirebaseException e) {
                    ToastDisplayer.displayHint(e.getMessage(),
                            ToastDisplayer.MessageType.FAILURE, getApplicationContext());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    private String getAccountIdFromUsername(final String username) {
        Predicate<Account> accountFilter = new Predicate<Account>() {
            public boolean apply(Account account) {
                return (account.getUsername().equals(username));
            }
        };
        Map<String, Account> filteredAccount = Maps.filterValues(Util.registeredUsers, accountFilter);
        if (filteredAccount.size() == 1) {
            Account foundAccount = (Account) filteredAccount.values().toArray()[0];
            return foundAccount.getId();
        }
        return null;
    }
}
