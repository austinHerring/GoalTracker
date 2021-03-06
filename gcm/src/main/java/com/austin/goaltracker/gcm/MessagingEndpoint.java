/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.austin.goaltracker.gcm;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;
import javax.inject.Named;

import static com.austin.goaltracker.gcm.OfyService.ofy;

/**
 * An endpoint to send messages to devices registered with the backend
 *
 * For more information, see
 * https://developers.google.com/appengine/docs/java/endpoints/
 *
 * NOTE: This endpoint does not use any form of authorization or
 * authentication! If this app is deployed, anyone can access this endpoint! If
 * you'd like to add authentication, take a look at the documentation.
 */
@Api(
  name = "messaging",
  version = "v1",
  namespace = @ApiNamespace(
    ownerDomain = "gcm.goaltracker.austin.com",
    ownerName = "gcm.goaltracker.austin.com",
    packagePath=""
  )
)
public class MessagingEndpoint {
    private static final Logger log = Logger.getLogger(MessagingEndpoint.class.getName());

    private static final String API_KEY = System.getProperty("gcm.api.key");

    /**
     * Send to the first 10 devices (You can modify this to send to any number of devices or a specific device)
     *
     * @param rawMessage The raw message with the note and reg ids
     */
    public void sendMessage(@Named("message") String rawMessage) throws IOException {
        if(rawMessage == null || rawMessage.trim().length() == 0) {
            log.warning("Not sending message because it is empty");
            return;
        }

        String message = parseMessageForNote(rawMessage);
        String accountId = parseMessageForAccountId(rawMessage);
        // crop longer messages
        if (message.length() > 1000) {
            message = message.substring(0, 1000) + "[...]";
        }
        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder()
                .addData("message", message)
                .addData("accountId", accountId)
                .addData("goalId", parseMessageForGoalId(rawMessage))
                .addData("dateTimeNotified", parseMessageForDateTimeNotified(rawMessage))
                .build();

        // GETS THE REGISTRATION RECORDS FOR A USER ACCOUNT
        RegistrationEndpoint registrationEndpoint = new RegistrationEndpoint();
        CollectionResponse<RegistrationRecord> records = registrationEndpoint.listDevices(accountId);

        for(RegistrationRecord record : records.getItems()) {
            Result result = sender.send(msg, record.getRegId(), 5); // With 5 retries
            if (result.getMessageId() != null) {
                log.info("Message sent to " + record.getRegId());
                String canonicalRegId = result.getCanonicalRegistrationId();
                if (canonicalRegId != null) {
                    // if the regId changed, we have to update the datastore
                    log.info("Registration Id changed for " + record.getRegId() + " updating to " + canonicalRegId);
                    record.setRegId(canonicalRegId);
                    ofy().save().entity(record).now();
                }
            } else {
                String error = result.getErrorCodeName();
                if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                    log.warning("Registration Id " + record.getRegId() + " no longer registered with GCM, removing from datastore");
                    // if the device is no longer registered with Gcm, remove it from the datastore
                    ofy().delete().entity(record).now();
                }
                else {
                    log.warning("Error when sending message : " + error);
                }
            }
        }
    }

    private String parseMessageForNote(String m) {
        return Arrays.asList(m.split(";")).get(0);
    }

    private String parseMessageForAccountId(String m) {
        return Arrays.asList(m.split(";")).get(1);
    }

    private String parseMessageForGoalId(String m) {
        return Arrays.asList(m.split(";")).get(2);
    }

    private String parseMessageForDateTimeNotified(String m) {
        return Arrays.asList(m.split(";")).get(3);
    }
}
