package com.austin.goaltracker.Controller.Adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.austin.goaltracker.Model.Enums.GoalClassification;
import com.austin.goaltracker.Model.Enums.IncrementType;
import com.austin.goaltracker.Model.RealTime.GetGoal;
import com.austin.goaltracker.Model.RealTime.GetGoals;
import com.austin.goaltracker.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Simple list adapter for user get Goals
 */
public class GetGoalListAdapter extends ArrayAdapter<GetGoal> {
    protected Activity activity;
    private GetGoals getGoals;
    private LayoutInflater inflater;
    private int mLayout;
    // Colors used for the list of goals
    private int STREAK_RED;
    private int COUNTDOWN_BLUE;
    private int BLUE_BACKGROUND;
    private int RED_BACKGROUND;

    public GetGoalListAdapter(Activity activity, int textViewResourceId, GetGoals getGoals) {
        super(activity, textViewResourceId, getGoals.Goals);
        try {
            this.activity = activity;
            this.inflater = activity.getLayoutInflater();
            this.getGoals = getGoals;
            this.mLayout = textViewResourceId;
            STREAK_RED = activity.getResources().getColor(R.color.text2);
            COUNTDOWN_BLUE = activity.getResources().getColor(R.color.text3);
            BLUE_BACKGROUND = activity.getResources().getColor(R.color.goal_list_blue_background);
            RED_BACKGROUND = activity.getResources().getColor(R.color.goal_list_red_background);

        } catch (Exception e) {
            Log.e("GetGoalListAdapter", "Error constructing");
        }
    }

    @Override
    public int getCount() {
        return getGoals.Goals.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView goal_name;
        public TextView goal_task;
        public TextView goal_start_info;
        public TextView goal_done_flag;
        public TextView goal_progress;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View v = view;
        final ViewHolder holder;
        final GetGoal getGoal = getGoals.Goals.get(position);

        if (view == null) {
            v = inflater.inflate(mLayout, null);
            holder = new ViewHolder();

            holder.goal_name = (TextView) v.findViewById(R.id.goalName);
            holder.goal_task = (TextView) v.findViewById(R.id.goalTask);
            holder.goal_start_info = (TextView) v.findViewById(R.id.goalStartInfo);
            holder.goal_done_flag = (TextView) v.findViewById(R.id.terminatedFlag);
            holder.goal_progress = (TextView) v.findViewById(R.id.goalProgressFriend);

            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        holder.goal_name.setText(getGoal.getGoalName());
        holder.goal_task.setText(getGoal.getTask());
        String startInfo = formatGoalStartInfo(getGoal.getIncrementType(), getGoal.getDateOfOrigin());
        holder.goal_start_info.setText(startInfo);

        if (getGoal.getClassification().equals(GoalClassification.COUNTDOWN)) {
            v.findViewById(R.id.goal_row_layout).setBackgroundColor(BLUE_BACKGROUND);
            setColorOfRow(holder, COUNTDOWN_BLUE);
            holder.goal_progress.setText(getGoal.getPercentProgress() + "%");

        } else {
            v.findViewById(R.id.goal_row_layout).setBackgroundColor(RED_BACKGROUND);
            setColorOfRow(holder, STREAK_RED);
            holder.goal_progress.setText(getGoal.getPercentProgress() + "");
        }

        if (getGoal.getIsTerminated()) {
            holder.goal_done_flag.setVisibility(View.VISIBLE);
        } else {
            holder.goal_done_flag.setVisibility(View.GONE);
        }

        return v;
    }

    private void setColorOfRow(ViewHolder holder, int color) {
        holder.goal_name.setTextColor(color);
        holder.goal_task.setTextColor(color);
        holder.goal_start_info.setTextColor(color);
        holder.goal_progress.setTextColor(color);
    }

    private String formatGoalStartInfo(IncrementType type, Calendar startDate) {
        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy h:mm a");
        String date =  format.format(startDate.getTime());

        if (type == IncrementType.HOURLY) {
            return "Hourly since " + date;
        } else if (type == IncrementType.DAILY) {
            return "Daily since " + date;
        } else if (type == IncrementType.BIDAILY) {
            return "Bi-daily since " + date;
        } else if (type == IncrementType.WEEKLY) {
            return "Weekly since " + date;
        } else if (type == IncrementType.BIWEEKLY) {
            return "Bi-weekly since " + date;
        }  else if (type == IncrementType.MONTHLY) {
            return "Monthly since " + date;
        } else {
            return "Yearly since " + date;
        }
    }
}
