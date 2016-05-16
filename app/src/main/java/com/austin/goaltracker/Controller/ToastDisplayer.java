package com.austin.goaltracker.Controller;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.austin.goaltracker.Model.Enums.ToastType;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Class to display hints to user with a Toast
 */
public class ToastDisplayer {

    public static void displayHint(String message, ToastType type, Context appContext) {
        Toast toast = Toast.makeText(appContext, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        TextView view = (TextView) toast.getView().findViewById(android.R.id.message);
        if (type.equals(ToastType.FAILURE)) {
            view.setTextColor(Color.RED);
        } else if (type.equals(ToastType.SUCCESS)) {
            view.setTextColor(Color.GREEN);
        }
        toast.show();
    }
}
