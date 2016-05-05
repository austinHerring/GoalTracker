package com.austin.goaltracker.View.Preferences;

import android.content.Context;
import android.preference.DialogPreference;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.austin.goaltracker.Controller.ToastDisplayer;
import com.austin.goaltracker.Controller.Util;
import com.austin.goaltracker.Model.Password;
import com.austin.goaltracker.Model.ToastType;
import com.firebase.client.FirebaseException;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Preference to change account password
 */
public class ChangePasswordPreference extends DialogPreference {

    private EditText passwordOld;
    private EditText passwordNew1;
    private EditText passwordNew2;
    private String passwordInDB;

    public ChangePasswordPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View onCreateDialogView() {
        passwordInDB = Util.currentUser.getPassword();
        LinearLayout ll = new LinearLayout(getContext());
        ll.setOrientation(LinearLayout.VERTICAL);
        passwordOld = new EditText(getContext());
        passwordOld.setHint("Current Password");
        passwordOld.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        ll.addView(passwordOld);

        passwordNew1 = new EditText(getContext());
        passwordNew1.setHint("New Password");
        passwordNew1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        ll.addView(passwordNew1);

        passwordNew2 = new EditText(getContext());
        passwordNew2.setHint("Confirm Password");
        passwordNew2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        ll.addView(passwordNew2);
        setSummary(Util.currentUser.getPasswordDate());
        return ll;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            if (passwordInDB.equals(passwordOld.getText().toString())) {
                if (passwordNew1.getText().toString().equals(passwordNew2.getText().toString())) {
                    try {
                        Password password = Util.updatePasswordForAccountOnDB(
                                                    Util.currentUser,
                                                    passwordNew1.getText().toString());
                        Util.currentUser.setPassword(password);
                        setSummary(Util.currentUser.getPasswordDate());
                        ToastDisplayer.displayHint("Change Successful", ToastType.SUCCESS, getContext());
                    } catch (FirebaseException e) {
                        ToastDisplayer.displayHint("Could not connect to database", ToastType.FAILURE, getContext());
                    }
                } else {
                    ToastDisplayer.displayHint("New Passwords Do Not Match", ToastType.FAILURE, getContext());
                }
            } else {
                ToastDisplayer.displayHint("Current Password Incorrect", ToastType.FAILURE, getContext());
            }
        }
    }

    public void setPassword(String passwordNew) {
        if (passwordNew.length() < 6 || !(passwordNew.matches(".*[a-z].*") && passwordNew.matches(".*[A-Z].*")
                && passwordNew.matches(".*[0-9].*"))) {
            ToastDisplayer.displayHint("Password Does Not Meet Security Standard", ToastType.FAILURE, getContext());
        } else {
            this.passwordInDB = passwordNew;
        }
    }
}
