package com.austin.goaltracker.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.austin.goaltracker.Controller.Generators.QuoteGenerator;
import com.austin.goaltracker.Controller.Util;
import com.austin.goaltracker.R;
import com.austin.goaltracker.View.Goals.GoalsBaseActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends Activity {
    private static String TAG = SplashActivity.class.getName();
    private static long SLEEP_TIME = 5;
    private FirebaseAuth mAuth;
    private Intent intent;
    private Activity activity;

    @Override
    public void onStart() {
        super.onStart();
        IntentLauncher launcher = new IntentLauncher();
        launcher.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        activity = this;
        mAuth = FirebaseAuth.getInstance();

        TextView text = (TextView) findViewById(R.id.motivation);
        text.setText(QuoteGenerator.generateQuote());

    }

    private class IntentLauncher extends Thread {
        public void run() {
            try {
                Thread.sleep(SLEEP_TIME * 1000);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

            final FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            } else {
                Intent i = new Intent(getApplicationContext(), GoalsBaseActivity.class);
                Util.tryLoadUser(activity, i, currentUser.getUid());
            }
        }
    }
}
