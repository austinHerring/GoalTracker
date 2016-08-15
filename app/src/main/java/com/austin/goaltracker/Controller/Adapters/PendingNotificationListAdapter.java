package com.austin.goaltracker.Controller.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.austin.goaltracker.Controller.Converter;
import com.austin.goaltracker.Controller.GAEDatastoreController;
import com.austin.goaltracker.Controller.Util;
import com.austin.goaltracker.Model.Goal.CountdownCompleterGoal;
import com.austin.goaltracker.Model.Goal.Goal;
import com.austin.goaltracker.Model.Enums.GoalClassification;
import com.austin.goaltracker.Model.Enums.IncrementType;
import com.austin.goaltracker.Model.PendingGoalNotification;
import com.austin.goaltracker.Model.Goal.StreakSustainerGoal;
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
    protected void populateView(final View view, final PendingGoalNotification pendingGoalNotification) {
        SwipeLayout swipeLayout =  (SwipeLayout) view;
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, view.findViewById(R.id.bottom_wrapper));

        final long timeStamp = pendingGoalNotification.getDateTimeNotified();
        ((TextView) view.findViewById(R.id.timeStamp)).setText(getNotificationTime(timeStamp));

        String message = getNotificatoinMessage(pendingGoalNotification.getAssociatedGoalId());
        ((TextView) view.findViewById(R.id.message)).setText(message);

        String info = getNotificatoinInfo(pendingGoalNotification.getAssociatedGoalId());
        ((TextView) view.findViewById(R.id.numericalInfo)).setText(info);

        positiveResponse = (Button) view.findViewById(R.id.positiveResponse);
        positiveResponse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Goal goal = getGoalFromId(pendingGoalNotification.getAssociatedGoalId());
                if (goal.classification().equals(GoalClassification.COUNTDOWN)) {
                    CountdownCompleterGoal cGoal = (CountdownCompleterGoal) goal;
                    cGoal.update();
                    if (cGoal.getPercentProgress() >= 100) {
                        // The goal was completed, flag it as terminated and notify user
                        notifyUserCompletion(view.getContext(), goal);
                        Util.currentUser.incrementCompletedGoals();
                        cGoal.setIsTerminated(true);
                        cGoal.setBrokenDate(cGoal.getDateDesiredFinish());
                        Util.addHistoryArtifactOnDB("GOAL", cGoal.getId(), cGoal.getDateDesiredFinish().getTimeInMillis(), true, null);
                    }
                    Util.updateAccountGoalOnDB(Util.currentUser.getId(), cGoal);
                } else {
                    StreakSustainerGoal sGoal = (StreakSustainerGoal) goal;
                    sGoal.update();
                    long streakInMillis = Calendar.getInstance().getTimeInMillis()
                                            - sGoal.getDateOfOrigin().getTimeInMillis();
                    if (Util.currentUser.getLongestStreak() < streakInMillis) {
                        Util.currentUser.setLongestStreak(streakInMillis);
                    }
                    Util.updateAccountGoalOnDB(Util.currentUser.getId(), sGoal);
                }
                Util.removePendingGoalNotificationFromDB(pendingGoalNotification.getId());
            }
        });

        negativeResponse = (Button) view.findViewById(R.id.negativeResponse);
        negativeResponse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String goalId = pendingGoalNotification.getAssociatedGoalId();
                Goal goal = getGoalFromId(goalId);

                if (goal.classification().equals(GoalClassification.COUNTDOWN)) {
                    CountdownCompleterGoal cGoal = (CountdownCompleterGoal) goal;
                    cGoal.setIsTerminated(true);
                    cGoal.setBrokenDate(Converter.longToCalendar(pendingGoalNotification.getDateTimeNotified()));
                    Util.updateAccountGoalOnDB(Util.currentUser.getId(), cGoal);
                    Util.addHistoryArtifactOnDB("GOAL", cGoal.getId(), pendingGoalNotification.getDateTimeNotified(), false, null);

                    Util.removeAssociatedPendingGoalNotificationsFromDB(goalId);
                    GAEDatastoreController.removeCron(Util.currentUser.getGoals().get(goalId));
                } else {
                    StreakSustainerGoal sGoal = (StreakSustainerGoal) goal;
                    if (sGoal.updateCheatNumber()) {
                        // The goal ran out of cheats, flag as terminated
                        sGoal.setIsTerminated(true);
                        sGoal.setBrokenDate(Converter.longToCalendar(pendingGoalNotification.getDateTimeNotified()));
                        Util.addHistoryArtifactOnDB("GOAL", sGoal.getId(), pendingGoalNotification.getDateTimeNotified(), false, null);
                        // Remove cron and other associated reminders
                        Util.removeAssociatedPendingGoalNotificationsFromDB(goalId);
                        GAEDatastoreController.removeCron(Util.currentUser.getGoals().get(goalId));
                    } else {
                        Util.removePendingGoalNotificationFromDB(goalId);
                    }
                    Util.updateAccountGoalOnDB(Util.currentUser.getId(), sGoal);
                }
                notifyUserTermination(view.getContext(), goal);
            }
        });
    }

    private String getNotificationTime(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        SimpleDateFormat format = new SimpleDateFormat("MMM, dd\nh:mm a");
        return format.format(calendar.getTime());
    }

    private void notifyUserCompletion(Context context, Goal goal) {
        SimpleDateFormat format = new SimpleDateFormat("MMMM, dd h:mm a");
        String date = format.format(goal.getDateOfOrigin().getTime());
        AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
        String message = "Congratulations!\n\nYou just finished your the goal to " + goal.getGoalName()
                + " that started " + date + ".\n\nYou'll be able to view this " +
                "in 'History' along with the other completed goals.\n\nHappy Goal Tracking!";
        alert.setMessage (message);
        Dialog dialog = alert.create();
        dialog.show();
    }

    private void notifyUserTermination(Context context, Goal goal) {
        GoalClassification type  = goal.classification();
        AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.AlertDialogStyle);

        String message;
        if (type == GoalClassification.COUNTDOWN) {
            message = "Goal Ended\n\nWe're sorry to see that you're quiting your goal to " + goal.getGoalName() +
                    ".\nYou will be able to look at the details in 'History'";

        } else {
            int cheatsRemaining = ((StreakSustainerGoal) goal).getCheatsRemaining();

            if (cheatsRemaining < 0) {
                long streak = ((StreakSustainerGoal) goal).getStreak();
                message = "Goal Ended\n\nYour goal to " + goal.getGoalName() + " stopped at a streak of " +
                        streak + ". You had a great run!.\n\nYou will be able to look at the " +
                        "details in 'History'";

            } else {
                message = "Using a cheat!\n\nYou just used a cheat so your streak won't stop.\n\nThere are "
                    + cheatsRemaining + " cheats remaining. Keep it up!";
            }
        }
        alert.setMessage(message);

        Dialog dialog = alert.create();
        dialog.show();
    }

    private String getNotificatoinMessage(String goalId) {
        Goal goal = getGoalFromId(goalId);
        IncrementType type = goal.getIncrementType();
        String message = "Did you " + goal.getTask() + " during the last ";

        if (type == IncrementType.HOURLY) {
            return message + "hour?";
        } else if (type == IncrementType.DAILY) {
            return message + "day?";
        } else if (type == IncrementType.BIDAILY) {
            return message + "two days?";
        } else if (type == IncrementType.WEEKLY) {
            return message + "week?";
        } else if (type == IncrementType.BIWEEKLY) {
            return message + "two weeks?";
        }  else if (type == IncrementType.MONTHLY) {
            return message + "month?";
        } else {
            return message + "year?";
        }
    }

    private String getNotificatoinInfo(String goalId) {
        Goal goal = getGoalFromId(goalId);
        if (goal.classification().equals(GoalClassification.STREAK)) {
            StreakSustainerGoal sGoal = (StreakSustainerGoal) goal;
            IncrementType type = sGoal.getIncrementType();
            String message = sGoal.getStreak() + "\n";
            if (type == IncrementType.HOURLY) {
                return message + "hours";
            } else if (type == IncrementType.DAILY) {
                return message + "days";
            } else if (type == IncrementType.BIDAILY) {
                return message + "bi-days";
            } else if (type == IncrementType.WEEKLY) {
                return message + "weeks";
            } else if (type == IncrementType.BIWEEKLY) {
                return message + "bi-weeks";
            }  else if (type == IncrementType.MONTHLY) {
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
