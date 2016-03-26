package com.austin.goaltracker.View;

import com.austin.goaltracker.Controller.LoginMediator;
import com.austin.goaltracker.Controller.RegistrationIntentService;
import com.austin.goaltracker.Controller.ToastDisplayer;
import com.austin.goaltracker.Model.GCMPreferences;
import com.austin.goaltracker.R;
import com.austin.goaltracker.Controller.Util;
import com.austin.goaltracker.View.Goals.GoalsBaseActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class LoginActivity extends Activity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "LOGIN ACTIVITY";

    private boolean isReceiverRegistered;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private EditText usernameInput;
    private EditText passwordInput;
    private String errorMessage;
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        Firebase database = new Firebase("https://flickering-inferno-500.firebaseio.com/");
        Util.setDB(database);
        setTitle("Login");
        setContentView(R.layout.activity_login);

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        // Attempt to get registration token
        if (isConnected) {
            setUpGCM();
        }

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
                        Util.currentUser.addRegisteredDevice(LoginMediator.pasteDeviceRegID());
                        Util.updateAccountRegIdsOnDB(Util.currentUser);
                        Intent i = new Intent(getApplicationContext(), GoalsBaseActivity.class);
                        i.putExtra("TabNumber", 0);
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
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void setUpGCM() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(GCMPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    Log.i(TAG, "SENT!!!");
                } else {
                    Log.i(TAG, "NOT SENT");
                }
            }
        };

        // Registering BroadcastReceiver
        registerReceiver();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    private void registerReceiver() {
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(GCMPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}

