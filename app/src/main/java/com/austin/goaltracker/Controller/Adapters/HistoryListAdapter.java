package com.austin.goaltracker.Controller.Adapters;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.austin.goaltracker.Controller.Converter;
import com.austin.goaltracker.Controller.Util;
import com.austin.goaltracker.Model.Account;
import com.austin.goaltracker.Model.Enums.GoalClassification;
import com.austin.goaltracker.Model.Goal.CountdownCompleterGoal;
import com.austin.goaltracker.Model.Goal.Goal;
import com.austin.goaltracker.Model.Goal.StreakSustainerGoal;
import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.Model.RealTime.HistoryArtifact;
import com.austin.goaltracker.R;
import com.firebase.client.Firebase;
import com.firebase.client.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * List adapter for the History activity.
 */
public class HistoryListAdapter extends FirebaseListAdapter<HistoryArtifact> {

    private int STREAK_RED, COUNTDOWN_BLUE, BLUE_BACKGROUND, RED_BACKGROUND, GREEN_BORDER, RED_BORDER, FRIEND_PURPLE, PURPLE_BACKGROUND;

    public HistoryListAdapter(Query ref, Activity activity, int layout) {
        super(ref, HistoryArtifact.class, layout, activity);
        try {
            Resources resources = activity.getResources();
            STREAK_RED = resources.getColor(R.color.text2);
            COUNTDOWN_BLUE = resources.getColor(R.color.text3);
            FRIEND_PURPLE = resources.getColor(R.color.primaryP);
            BLUE_BACKGROUND = resources.getColor(R.color.goal_list_blue_background);
            RED_BACKGROUND = resources.getColor(R.color.goal_list_red_background);
            PURPLE_BACKGROUND = resources.getColor(R.color.spinner_background);
            GREEN_BORDER = resources.getColor(R.color.positive_action);
            RED_BORDER = resources.getColor(R.color.negative_action);

        } catch (Exception e) {
            Log.e("GoalListAdapter", "Error constructing");
        }
    }

    /**
     * Bind an instance of the PendingGoalNotification class to the view.
     *
     * @param view A view instance corresponding to the layout passed to the constructor.
     */
    @Override
    protected void populateView(final View view, final HistoryArtifact historyArtifact) {
        TextView dateText = ((TextView) view.findViewById(R.id.history_date));
        TextView subjectText = ((TextView) view.findViewById(R.id.subject));
        TextView statText = ((TextView) view.findViewById(R.id.history_stat));
        ImageView imageView = (ImageView) view.findViewById(R.id.history_icon);
        String subject;
        String stat = "";

        Calendar date = Converter.longToCalendar(historyArtifact.getDate());
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
            dateText.setText(df.format(date.getTime()));
        }

        if (historyArtifact.getType().equals("FRIEND")) {
            subject = historyArtifact.getUsername() + " as a friend";
            view.setBackgroundColor(PURPLE_BACKGROUND);
            subjectText.setTextColor(FRIEND_PURPLE);
            dateText.setTextColor(FRIEND_PURPLE);
            Drawable picture = Converter.makeDrawableFromBase64String((Activity) view.getContext(), historyArtifact.getUserPic());
            if (picture == null) {
                picture = ResourcesCompat.getDrawable(view.getContext().getResources(), R.drawable.default_user_icon, null);
            }
            imageView.setImageDrawable(picture);
            if (historyArtifact.getPositiveAction()) {
                subject = "Added " + subject;
                imageView.setBackgroundColor(GREEN_BORDER);
            } else {
                subject = "Removed " + subject;
                imageView.setBackgroundColor(RED_BORDER);
            }

        } else {
            Goal goal = getGoalFromId(historyArtifact.getAssociatedObject());
            subject = goal.getGoalName() + ": " + goal.getTask();
            if (goal.classification().equals(GoalClassification.COUNTDOWN)) {
                CountdownCompleterGoal cGoal = (CountdownCompleterGoal) goal;
                view.setBackgroundColor(BLUE_BACKGROUND);
                subjectText.setTextColor(COUNTDOWN_BLUE);
                dateText.setTextColor(COUNTDOWN_BLUE);
                stat = cGoal.getPercentProgress() + "%";

                if (historyArtifact.getPositiveAction()) {
                    imageView.setImageResource(R.drawable.countdown_flag_small_gold);
                } else {
                    imageView.setImageResource(R.drawable.countdown_flag_small);
                }
            } else {
                StreakSustainerGoal sGoal = (StreakSustainerGoal) goal;
                view.setBackgroundColor(RED_BACKGROUND);
                subjectText.setTextColor(STREAK_RED);
                dateText.setTextColor(STREAK_RED);
                imageView.setImageResource(R.drawable.streak_flame_small);
                stat = sGoal.getStreak() + "\n" + sGoal.getIncrementType();
            }

        }
        subjectText.setText(subject);
        statText.setText(stat);
    }


    private Goal getGoalFromId(String goalId) {
        return Util.currentUser.getGoals().get(goalId);
    }
}

