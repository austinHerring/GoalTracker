package com.austin.goaltracker.Controller;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Class to display hints to user with a Toast
 */
public class ToastDisplayer {
    public enum MessageType {FAILURE, SUCCESS}

    public static void displayHint(String message, MessageType type, Context appContext) {
        Toast toast = Toast.makeText(appContext, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        TextView view = (TextView) toast.getView().findViewById(android.R.id.message);
        if (type.equals(MessageType.FAILURE)) {
            view.setTextColor(Color.RED);
        } else if (type.equals(MessageType.SUCCESS)) {
            view.setTextColor(Color.GREEN);
        }
        toast.show();
    }
}
