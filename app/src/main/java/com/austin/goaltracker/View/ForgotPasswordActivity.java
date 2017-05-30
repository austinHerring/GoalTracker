package com.austin.goaltracker.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.austin.goaltracker.Controller.Services.EmailDispatchService;
import com.austin.goaltracker.Controller.ToastDisplayer;
import com.austin.goaltracker.Controller.Util;
import com.austin.goaltracker.Model.Account;
import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.Model.Mail.NewPasswordEmail;
import com.austin.goaltracker.Model.Password;
import com.austin.goaltracker.Model.Enums.ToastType;
import com.austin.goaltracker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;

public class ForgotPasswordActivity extends Activity {
    private EditText usernameText;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        GoalTrackerApplication.INSTANCE.setCurrentActivity(this);
        usernameText = (EditText)findViewById(R.id.forgot_username);

        Button submitButton = (Button) findViewById(R.id.buttonSubmit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                attemptReset(usernameText.getText().toString());
            }
        });

    }

    private void attemptReset(final String username) {
        DatabaseReference firebaseRef = mRootRef.child("accounts");
        com.google.firebase.database.Query queryRef = firebaseRef.orderByChild("username").equalTo(username);
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    try {
                        HashMap<String, HashMap<String, Object>> accounts =
                                (HashMap<String, HashMap<String, Object>>) snapshot.getValue();
                        HashMap<String, Object> filteredAccount = accounts.entrySet().iterator().next().getValue();
                        Account account = new Account(
                                (String) filteredAccount.get("firstname"),
                                (String) filteredAccount.get("lastname"),
                                (String) filteredAccount.get("username"),
                                Util.retrieveUserPasswordToLocal(filteredAccount),
                                (String) filteredAccount.get("email"));
                        account.setId((String) filteredAccount.get("id"));

                        Password password = Util.updatePasswordForAccountOnDB(account, null);
                        account.setPassword(password);
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.updatePassword(password.toPasswordString());
                        NewPasswordEmail email = new NewPasswordEmail(account);
                        EmailDispatchService dispatcher = new EmailDispatchService(email);
                        dispatcher.send();
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);
                    } catch (DatabaseException e) {
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        ToastDisplayer.displayHint(e.getMessage(), ToastType.FAILURE, getApplicationContext());
                    }
                } else {
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    ToastDisplayer.displayHint("This Account Does Not Exist", ToastType.FAILURE, getApplicationContext());
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }
}
