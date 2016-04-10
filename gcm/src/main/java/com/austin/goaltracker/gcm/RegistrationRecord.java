package com.austin.goaltracker.gcm;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/** The Objectify object model for device registrations that are persisting */
@Entity
public class RegistrationRecord {

    @Id
    Long id; // Will automatically generate ID on save

    @Index private String regId;
    @Index private String accountId;

    public RegistrationRecord() {}

    public String getRegId() {
        return regId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}