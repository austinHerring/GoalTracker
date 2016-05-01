package com.austin.goaltracker.Controller;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.austin.goaltracker.Model.CountdownCompleterGoal;
import com.austin.goaltracker.Model.Goal;
import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.Model.PendingGoalNotification;
import com.austin.goaltracker.Model.StreakSustainerGoal;
import com.daimajia.swipe.SwipeLayout;
import com.firebase.client.Query;
import com.austin.goaltracker.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * List adapter for the Pending Notification that is bound to a the firebase activity.
 */
public class PendingNotificationListAdapter extends FirebaseListAdapter<PendingGoalNotification>{
    Button positiveResponse;
    Button negativeResponse;


    public PendingNotificationListAdapter(Query ref, Activity activity, int layout) {
        super(ref, PendingGoalNotification.class, layout, activity);
    }

    /**
     * Bind an instance of the PendingGoalNotification class to the view.
     *
     * @param view A view instance corresponding to the layout passed to the constructor.
     */
    @Override
    protected void populateView(final View view, PendingGoalNotification pendingGoalNotification) {
        SwipeLayout swipeLayout =  (SwipeLayout) view;
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, view.findViewById(R.id.bottom_wrapper));

        long timeStamp = pendingGoalNotification.getDateTimeNotified();
        ((TextView) view.findViewById(R.id.timeStamp)).setText(getNotificationTime(timeStamp));

        String message = getNotificatoinMessage(pendingGoalNotification.getAssociatedGoalId());
        ((TextView) view.findViewById(R.id.message)).setText(message);

        String info = getNotificatoinInfo(pendingGoalNotification.getAssociatedGoalId());
        ((TextView) view.findViewById(R.id.numericalInfo)).setText(info);

        positiveResponse = (Button) view.findViewById(R.id.positiveResponse);
        positiveResponse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ToastDisplayer.displayHint("POSITIVE",
                        ToastDisplayer.MessageType.SUCCESS, GoalTrackerApplication.INSTANCE);
            }
        });

        negativeResponse = (Button) view.findViewById(R.id.negativeResponse);
        negativeResponse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ToastDisplayer.displayHint("NEGATIVE",
                        ToastDisplayer.MessageType.FAILURE, GoalTrackerApplication.INSTANCE);
            }
        });
    }

    private String getNotificationTime(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        SimpleDateFormat format = new SimpleDateFormat("MMM, dd\nh:mm a");
        return format.format(calendar.getTime());
    }

    private String getNotificatoinMessage(String goalId) {
        Goal goal = getGoalFromId(goalId);
        Goal.IncrementType type = goal.getIncrementType();
        String message = "Did you " + goal.getTask() + " during the last ";

        if (type == Goal.IncrementType.HOURLY) {
            return message + "hour?";
        } else if (type == Goal.IncrementType.DAILY) {
            return message + "day?";
        } else if (type == Goal.IncrementType.BIDAILY) {
            return message + "two days?";
        } else if (type == Goal.IncrementType.WEEKLY) {
            return message + "week?";
        } else if (type == Goal.IncrementType.BIWEEKLY) {
            return message + "two weeks?";
        }  else if (type == Goal.IncrementType.MONTHLY) {
            return message + "month?";
        } else {
            return message + "year?";
        }
    }

    private String getNotificatoinInfo(String goalId) {
        Goal goal = getGoalFromId(goalId);
        if (goal.classification().equals(Goal.Classification.STREAK)) {
            StreakSustainerGoal sGoal = (StreakSustainerGoal) goal;
            Goal.IncrementType type = sGoal.getIncrementType();
            String message = sGoal.getStreak() + "\n";
            if (type == Goal.IncrementType.HOURLY) {
                return message + "hours";
            } else if (type == Goal.IncrementType.DAILY) {
                return message + "days";
            } else if (type == Goal.IncrementType.BIDAILY) {
                return message + "bi-days";
            } else if (type == Goal.IncrementType.WEEKLY) {
                return message + "weeks";
            } else if (type == Goal.IncrementType.BIWEEKLY) {
                return message + "bi-weeks";
            }  else if (type == Goal.IncrementType.MONTHLY) {
                return message + "months";
            } else {
                return message + "years";
            }
        } else {
            CountdownCompleterGoal cGoal = (CountdownCompleterGoal) goal;
            return cGoal.getPercentProgress() +"%";
        }
    }

    private Goal getGoalFromId(String goalId) {
        return Util.currentUser.getGoals().get(goalId);
    }
}
