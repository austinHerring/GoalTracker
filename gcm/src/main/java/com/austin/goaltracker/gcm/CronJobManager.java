package com.austin.goaltracker.gcm;

import com.google.appengine.api.ThreadManager;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;

import static com.austin.goaltracker.gcm.OfyService.ofy;
import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * A Servlet class that is called by cron.xml to check the datastore for pending notifications.
 * Then it schedules a message task in the queue and updates the next runtime for the job
 *
 */
@SuppressWarnings("serial")
public class CronJobManager extends HttpServlet {
    private static final Logger Log = Logger.getLogger(CronJobManager.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        long currentTime = Calendar.getInstance().getTimeInMillis();

        // FILTER OUT OF DATE NOTIFICATIONS
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Query query = new Query("CronData")
                .setFilter(new Query.FilterPredicate("nextRunTS",
                        Query.FilterOperator.LESS_THAN_OR_EQUAL, currentTime));
        PreparedQuery pq = datastore.prepare(query);

        for (Entity job : pq.asIterable()) {
            processJob(job);
        }
        resp.setContentType("text/plain");
        resp.getWriter().println("Entity processed");
    }

    private void processJob(Entity job) {
        final CronData cronData = new CronData(job);
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        String key = cronData.getCronKey();
        Log.info("PROCESSING A JOB FOR CRON KEY: " + key);

        // ADD SENDING MESSAGE TO QUEUE
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(withUrl("/sender").param("rawMessage", constructRawMessage(cronData)));

        // UPDATE NEXT RUN DATE OR DELETE IF GOAL IS DONE
        String frequency = cronData.getFrequency();
        long terminalRun = cronData.getLastRun();
        long previousNextRun = cronData.getNextRunTS();
        long futureNextRun = getNextRun(frequency, previousNextRun);

        if (terminalRun != -1 && futureNextRun > terminalRun) {
            datastore.delete(job.getKey());
        } else {
            cronData.setNextRunTS(futureNextRun);
            datastore.put(cronData.toEntity());
        }
    }

    private long getNextRun(String frequency, long previousnextRun) {
        Calendar futureNextRun = Calendar.getInstance();
        futureNextRun.setTimeInMillis(previousnextRun);
        if (frequency.equals("HOURLY")) {
            futureNextRun.add(Calendar.HOUR, 1);
        } else if (frequency.equals("DAILY")) {
            futureNextRun.add(Calendar.DATE, 1);
        } else if (frequency.equals("BIDAILY")) {
            futureNextRun.add(Calendar.DATE, 2);
        } else if (frequency.equals("WEEKLY")) {
            futureNextRun.add(Calendar.DATE, 7);
        } else if (frequency.equals("BIWEEKLY")) {
            futureNextRun.add(Calendar.DATE, 1);
        } else if (frequency.equals("MONTHLY")) {
            futureNextRun.add(Calendar.MONTH, 1);
        } else if (frequency.equals("YEARLY")) {
            futureNextRun.add(Calendar.YEAR, 1);
        } else {
            Log.severe("WHEN FINDING NEXT RUN DATE A FREQUENCY WAS NOT DETECTED");
        }

        return futureNextRun.getTimeInMillis();
    }

    private String constructRawMessage(CronData cronData) {
        return cronData.getMessage() + ";" + cronData.getAccountId() + ";" + cronData.getGoalId() + ";" + cronData.getNextRunTS();
    }

}