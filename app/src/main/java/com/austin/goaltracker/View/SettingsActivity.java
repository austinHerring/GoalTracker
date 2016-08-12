package com.austin.goaltracker.View;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.austin.goaltracker.Controller.Converter;
import com.austin.goaltracker.Controller.Util;
import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.R;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends PreferenceActivity {

    private static List<String> fragments = new ArrayList<>();

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
        fragments.clear();
        for (Header header : target) {
            fragments.add(header.fragment);
        }
    }

    @Override
    protected boolean isValidFragment(String fragmentName)
    {
        return fragments.contains(fragmentName);
    }

    public static class ProfilePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_profile);

            findPreference("password_change_text").setSummary(Util.currentUser.getPasswordDate());
            findPreference("select_image").setOnPreferenceClickListener (new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), GoalTrackerApplication.SELECT_REQUEST);
                    return true;
                }


            });
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            try {
                if (requestCode == GoalTrackerApplication.SELECT_REQUEST && resultCode == Activity.RESULT_OK) {
                    Uri pictureURI = data.getData();
                    cropPicture(pictureURI);
                } else if (requestCode == GoalTrackerApplication.PIC_CROP) {
                    Bitmap thePic = data.getExtras().getParcelable("data");
                    String pictureAsString = Converter.encodeToBase64(thePic, Bitmap.CompressFormat.PNG, 100);

                    Util.currentUser.setPictureData(pictureAsString);
                    Util.updateAccountPictureOnDB(Util.currentUser.getId(), pictureAsString);
                }
            } catch (Exception e) {
                Log.e("Settings", "Exception in onActivityResult : " + e.getMessage());
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            Drawable profilePic = Converter.makeDrawableFromBase64String(getActivity(), Util.currentUser.getPictureData());
            if (profilePic != null) {
                findPreference("select_image").setIcon(profilePic);
            }
        }

        private void cropPicture(Uri pictureURI) {
            try {
                Intent cropIntent = new Intent("com.android.camera.action.CROP");
                cropIntent.setDataAndType(pictureURI, "image/*");
                cropIntent.putExtra("crop", "true");
                cropIntent.putExtra("aspectX", 1);
                cropIntent.putExtra("aspectY", 1);
                cropIntent.putExtra("outputX", 256);
                cropIntent.putExtra("outputY", 256);
                cropIntent.putExtra("return-data", true);
                startActivityForResult(cropIntent, GoalTrackerApplication.PIC_CROP);
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            boolean notificationEligible = Util.currentUser.canReceiveNotificationsOnCurrentDevice();
            findPreference("notifications_enable_on_device").setDefaultValue(notificationEligible);
            //findPreference("notifications_enable_on_device").setOnPreferenceChangeListener();
        }
    }

}