package com.austin.goaltracker.gcm;

import java.util.List;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * A POJO class that is hydrated from JSON when cron jobs from Firebase are queried
 */
public class CronData {

    private String message;
    private int promptMinute;
    private int promptHour;
    private List<String> registeredDevices;
    private long lastRun;
    private String frequency;

    public CronData() {
    }

    public String getMessage() {
        return message;
    }

    public int getPromptMinute() {
        return promptMinute;
    }

    public int getPromptHour() {
        return promptHour;
    }

    public List<String> getRegisteredDevices() {
        return registeredDevices;
    }

    public String getFrequency() {
        return frequency;
    }

    public long getlastRun() {
        return lastRun;
    }
}
