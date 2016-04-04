package com.austin.goaltracker.gcm;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * A Servlet class that is called by cron.xml to check the datastaore for pending notifications.
 * Then it schedules a message task in the queue and updates the next runtime for the job
 *
 */
@SuppressWarnings("serial")
public class CronJobManager extends HttpServlet {
    static Logger Log = Logger.getLogger(CronListener.class.getName());
    public static Firebase db = new Firebase("https://flickering-inferno-500.firebaseio.com/cronJobs");

    private static final Logger log = Logger.getLogger(CronJobManager.class
            .getName());

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        log.info("Running cron loop");
        long currentTime = Calendar.getInstance().getTimeInMillis() / 1000L;
        log.info("Current time: " + currentTime);

        // FILTER OUT OF DATE NOTIFICATIONS
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();
        Query query = new Query("NotificationJob")
                .setFilter(new Query.FilterPredicate("next_run_ts",
                        Query.FilterOperator.LESS_THAN_OR_EQUAL, currentTime));
        PreparedQuery pq = datastore.prepare(query);

        for (Entity job : pq.asIterable()) {
            processJob(job);
        }
    }

    private void processJob(Entity job) {
        String key = KeyFactory.keyToString(job.getKey());
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        final CronData[] cronData = {null};
        db.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cronData[0] = dataSnapshot.getValue(CronData.class);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.severe("The read failed: " + firebaseError.getMessage());
            }
        });

        // ADD SENDING MESSAGE TO QUEUE
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(withUrl("/sender").param("rawMessage", constructRawMessage(cronData)));

        // UPDATE NEXT RUN DATE OR DELETE IF GOAL IS DONE
        String frequency = cronData[0].getFrequency();
        long lastRun = cronData[0].getlastRun();
        long previousNextRun = (Long) job.getProperty("next_run_ts");
        long futureNextRun = getNextRun(frequency, previousNextRun);

        if (lastRun != -1 && futureNextRun > lastRun) {
            datastore.delete(job.getKey());
        } else {
            job.setProperty("next_run_ts", futureNextRun);
            datastore.put(job);
        }
    }

    private long getNextRun(String frequency, long previousnextRun) {
        Calendar futureNextRun = Calendar.getInstance();
        futureNextRun.setTimeInMillis(previousnextRun);
        if (frequency == "HOURLY") {
            futureNextRun.add(Calendar.HOUR_OF_DAY, 1);
        } else if (frequency == "DAILY") {
            futureNextRun.add(Calendar.DATE, 1);
        } else if (frequency == "BIDAILY") {
            futureNextRun.add(Calendar.DATE, 2);
        } else if (frequency == "WEEKLY") {
            futureNextRun.add(Calendar.DATE, 7);
        } else if (frequency == "BIWEEKLY") {
            futureNextRun.add(Calendar.DATE, 1);
        } else if (frequency == "MONTHLY") {
            futureNextRun.add(Calendar.MONTH, 1);
        } else if (frequency == "YEARLY") {
            futureNextRun.add(Calendar.YEAR, 1);
        } else {
            Log.severe("WHEN FINDING NEXT RUN DATE A FREQUENCY WAS NOT DETECTED");
        }

        return futureNextRun.getTimeInMillis();
    }

    private String constructRawMessage(CronData[] cronData) {
        String messageRaw = cronData[0].getMessage();
        List<String> deviceIds = cronData[0].getRegisteredDevices();
        for(String id : deviceIds) {
            messageRaw += ";" + id;
        }

        return messageRaw;
    }
}
