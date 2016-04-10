package com.austin.goaltracker.gcm;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;

import java.util.List;
import java.util.logging.Logger;
import javax.inject.Named;

import static com.austin.goaltracker.gcm.OfyService.ofy;

/**
 * A registration endpoint class we are exposing for a device's GCM registration id on the backend
 */
@Api(
  name = "registration",
  version = "v1",
  namespace = @ApiNamespace(
    ownerDomain = "gcm.goaltracker.austin.com",
    ownerName = "gcm.goaltracker.austin.com",
    packagePath=""
  )
)
public class RegistrationEndpoint {

    private static final Logger log = Logger.getLogger(RegistrationEndpoint.class.getName());

    /**
     * Register a device to the backend
     *
     * @param regId The Google Cloud Messaging registration Id to add
     * @param accountId the account Id that has the associated registered device
     */
    @ApiMethod(name = "register")
    public void registerDevice(@Named("regId") String regId, @Named("accountId") String accountId) {
        if(findRecord(regId, accountId) != null) {
            log.info("Device " + regId + " already registered for account " + accountId);
            return;
        }
        RegistrationRecord record = new RegistrationRecord();
        record.setRegId(regId);
        record.setAccountId(accountId);
        ofy().save().entity(record).now(); // async without adding .now()
    }

    /**
     * Unregister a device from the backend
     *
     * @param regId The Google Cloud Messaging registration Id to remove
     * @param accountId the account Id that has the associated registered device
     */
    @ApiMethod(name = "unregister")
    public void unregisterDevice(@Named("regId") String regId, @Named("accountId") String accountId) {
        RegistrationRecord record = findRecord(regId, accountId);
        if(record == null) {
            log.info("Device " + regId + " not registered for account " + accountId);
            return;
        }
        ofy().delete().entity(record).now();
    }

    /**
     * Return a collection of registered devices for a user account
     *
     * @param accountId The user to list the devices he is registered with
     * @return a list of Google Cloud Messaging registration Ids
     */
    @ApiMethod(name = "listDevices")
    public CollectionResponse<RegistrationRecord> listDevices(@Named("accountId") String accountId) {
        List<RegistrationRecord> records = ofy().load().type(RegistrationRecord.class).filter("accountId", accountId).list();
        return CollectionResponse.<RegistrationRecord>builder().setItems(records).build();
    }

    private RegistrationRecord findRecord(String regId, String accountId) {
        return ofy().load().type(RegistrationRecord.class)
                .filter("accountId", accountId)
                .filter("regId", regId)
                .first().now();
    }

}
