package com.austin.goaltracker.Model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Password structure used to keep track of Password information
 */
public class Password {
    private String password;
    private String dateLastChanged;

    public Password(String password) {
        this.password = password;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy");
        dateLastChanged = df.format(c.getTime());
    }

    public Password(String password, String date) {
        this.password = password;
        dateLastChanged = date;
    }

    public String toPasswordString() {
        return password;
    }

    public String toDateString() {
        return dateLastChanged;
    }
}
