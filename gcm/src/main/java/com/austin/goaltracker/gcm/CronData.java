package com.austin.goaltracker.gcm;

import java.util.List;

/**
 * Created by austin on 4/2/16.
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
