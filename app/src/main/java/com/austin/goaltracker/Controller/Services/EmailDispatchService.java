package com.austin.goaltracker.Controller.Services;

import android.os.AsyncTask;

import java.io.IOException;

import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.gcm.email.Email;

import com.austin.goaltracker.Model.Mail.EmailAgent;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

/**
 * @author Austin Herring
 * @version 1.1
 *
 * Class to send out automated emails
 */
public class EmailDispatchService {

    private EmailAgent emailAgent;

    public EmailDispatchService(EmailAgent emailAgent) {
        this.emailAgent = emailAgent;
    }

    public String send() {
        try {
            DispatchTask dispatchTask = new DispatchTask();
            dispatchTask.execute();
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    class DispatchTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            Email.Builder builder = new Email.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    .setRootUrl(GoalTrackerApplication.PROJECT_ADDRESS)
                    .setApplicationName(GoalTrackerApplication.APPLICATION_NAME);
            Email emailService = builder.build();
            try {
                emailService.sendEmail(
                        emailAgent.getSubject(),
                        emailAgent.getMessage(),
                        emailAgent.getrecipientEmail())
                        .execute();
                return null;
            } catch (IOException e) {
                return e.getMessage();
            }
        }
    }



}
