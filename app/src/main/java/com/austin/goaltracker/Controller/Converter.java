package com.austin.goaltracker.Controller;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import com.austin.goaltracker.Model.Enums.IncrementType;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Utility to convert information taken from firebase into interpretable types
 */
public class Converter {

    public static Calendar longToCalendar(long date) {
        if (date == 0) {
            return null;
        }
        Calendar c = new GregorianCalendar();
        c.setTimeInMillis(date);
        return c;
    }

    public static IncrementType stringToFrequency(String input) {
        if (input.equals("HOURLY")) {
            return IncrementType.HOURLY;
        } else if (input.equals("DAILY")) {
            return IncrementType.DAILY;
        } else if (input.equals("BIDAILY")) {
            return IncrementType.BIDAILY;
        } else if (input.equals("WEEKLY")) {
            return IncrementType.WEEKLY;
        } else if (input.equals("BIWEEKLY")) {
            return IncrementType.BIWEEKLY;
        } else if (input.equals("MONTHLY")) {
            return IncrementType.MONTHLY;
        } else if (input.equals("YEARLY")) {
            return IncrementType.YEARLY;
        } else {
            return null;
        }
    }

    public static Drawable makeDrawableFromBase64String(Activity activity, String base64String) {
        if (base64String != null && !base64String.equals("")) {
            byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            return new BitmapDrawable(activity.getResources(), decodedByte);
        }
        return null;
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }
}
