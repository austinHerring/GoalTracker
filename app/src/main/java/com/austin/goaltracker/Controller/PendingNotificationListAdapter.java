package com.austin.goaltracker.Controller;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.austin.goaltracker.Model.PendingGoalNotification;
import com.firebase.client.Query;
import com.austin.goaltracker.R;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * List adapter for the Pending Notification that is bound to a the firebase activity.
 */
public class PendingNotificationListAdapter extends FirebaseListAdapter<PendingGoalNotification>{

    public PendingNotificationListAdapter(Query ref, Activity activity, int layout) {
        super(ref, PendingGoalNotification.class, layout, activity);
    }

    /**
     * Bind an instance of the PendingGoalNotification class to the view.
     *
     * @param view A view instance corresponding to the layout passed to the constructor.
     */
    @Override
    protected void populateView(View view, PendingGoalNotification pendingGoalNotification) {
        ((TextView) view.findViewById(R.id.idTEST)).setText(pendingGoalNotification.getId());
        ((TextView) view.findViewById(R.id.contentTEST)).setText(pendingGoalNotification.getName());
    }
}
