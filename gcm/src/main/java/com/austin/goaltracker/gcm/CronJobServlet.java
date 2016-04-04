package com.austin.goaltracker.gcm;

import com.firebase.client.Firebase;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Servlet that is initiated when the app engine starts. It listens to Cron Jobs being added
 * to Firebase
 */
public class CronJobServlet extends HttpServlet {
    static Logger Log = Logger.getLogger(CronJobServlet.class.getName());
    private static final String REF = "https://flickering-inferno-500.firebaseio.com/cronJobs";

    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        //Create a new Firebase instance and subscribe on child events.
        Firebase firebase = new Firebase(REF);
        firebase.addChildEventListener(new CronListener());
        Log.info("Initiating Servlet and Starting Firebase Listener");
    }
}