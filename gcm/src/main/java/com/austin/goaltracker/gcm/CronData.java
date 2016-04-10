package com.austin.goaltracker.gcm;

import com.google.appengine.api.datastore.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * A POJO class that that will be objectified to data store
 */
@com.googlecode.objectify.annotation.Entity
public class CronData {

    @Id Long id; // Will automatically generate ID on save

    @Index transient private String cronKey;
    private String message;
    private String accountId;
    private String frequency;
    @Index transient private long nextRunTS;
    private long lastRun;
    private Entity entity;

    public CronData() {}

    public CronData(Entity entity) {
        this.entity = entity;
        this.cronKey = (String) entity.getProperty("cronKey");
        this.message = (String) entity.getProperty("message");
        this.accountId = (String) entity.getProperty("accountId");
        this.frequency = (String) entity.getProperty("frequency");
        this.nextRunTS = (Long) entity.getProperty("nextRunTS");
        this.lastRun = (Long) entity.getProperty("lastRun");
    }

    public CronData(String cronKey, String message, String accountId,
                    String frequency, long nextRunTS, long lastRun) {
        this.cronKey = cronKey;
        this.message = message;
        this.accountId = accountId;
        this.frequency = frequency;
        this.nextRunTS = nextRunTS;
        this.lastRun = lastRun;
    }

    public String getCronKey() {
        return cronKey;
    }

    public String getMessage() {
        return message;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getFrequency() {
        return frequency;
    }

    public long getNextRunTS() {
        return nextRunTS;
    }

    public long getLastRun() {
        return lastRun;
    }

    public void setNextRunTS(long nextRunTS) {
        this.nextRunTS = nextRunTS;
        this.entity.setProperty("nextRunTS", nextRunTS);
    }

    public Entity toEntity() {
        return entity;
    }
}
