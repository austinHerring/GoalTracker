package com.austin.goaltracker.gcm;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import java.util.logging.Logger;
import javax.inject.Named;

import static com.austin.goaltracker.gcm.OfyService.ofy;

/**
 * A cron job endpoint class we are exposing for a users reminder notifications on the backend
 */
@Api(
        name = "cronJob",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "gcm.goaltracker.austin.com",
                ownerName = "gcm.goaltracker.austin.com",
                packagePath=""
        )
)
public class CronJobEndpoint {

    private static final Logger log = Logger.getLogger(CronJobEndpoint.class.getName());

    /**
     * Add a cron job to the backend to the backend
     *
     * @param cronKey key to identify a cron job
     * @param message message to send to user
     * @param accountId the accountID used to query the datastore for registered devices eligible
     *                  to receive push notification
     * @param frequency how often a cron job will process
     * @param nextRunTS the next time to send the cron job
     * @param lastRun if the goal isa countdown, this is the last time it will run the job
     */
    @ApiMethod(name = "persistCron")
    public void persistCron(@Named("cronKey") String cronKey,
                            @Named("message") String message,
                            @Named("accountId") String accountId,
                            @Named("frequency") String frequency,
                            @Named("nextRunTS") long nextRunTS,
                            @Named("lastRun") long lastRun)
    {
        CronData cronData = new CronData(cronKey, message, accountId, frequency, nextRunTS, lastRun);
        ofy().save().entity(cronData).now(); // async without adding .now()
    }

    /**
     * Remove a cron job from the backend
     *
     * @param cronKey TThe cron job key to remove
     */
    @ApiMethod(name = "removeCron")
    public void removeCron(@Named("cronKey") String cronKey) {
        CronData cronData = findCronJob(cronKey);
        if(cronData == null) {
            log.info("Data for key:  " + cronKey + " was not found");
            return;
        }
        ofy().delete().entity(cronData);
    }

    private CronData findCronJob(String cronKey) {
        return ofy().load().type(CronData.class).filter("cronKey", cronKey).first().now();
    }
}
