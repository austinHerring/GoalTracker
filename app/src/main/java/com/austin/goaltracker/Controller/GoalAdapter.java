package com.austin.goaltracker.Controller;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.austin.goaltracker.Model.CountdownCompleterGoal;
import com.austin.goaltracker.Model.Goal;
import com.austin.goaltracker.Model.StreakSustainerGoal;
import com.austin.goaltracker.R;

import java.util.ArrayList;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * List adapter for the Goals activity. Also sets on click listeners for each type of goal
 * in the list so that the proper fragment displays
 */
public class GoalAdapter extends ArrayAdapter<Goal> {
    Activity activity;
    private ArrayList<Goal> listOfGoals;
    private static LayoutInflater inflater = null;
    // Colors used for the list of goals
    private int STREAK_RED;
    private int COUNTDOWN_BLUE;
    private int BLUE_BACKGROUND;
    private int RED_BACKGROUND;

    public GoalAdapter (Activity activity, int textViewResourceId,ArrayList<Goal> goals) {
        super(activity, textViewResourceId, goals);
        try {
            this.activity = activity;
            listOfGoals = goals;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            STREAK_RED = activity.getResources().getColor(R.color.text2);
            COUNTDOWN_BLUE = activity.getResources().getColor(R.color.text3);
            BLUE_BACKGROUND = activity.getResources().getColor(R.color.goal_list_blue_background);
            RED_BACKGROUND = activity.getResources().getColor(R.color.goal_list_red_background);

        } catch (Exception e) {
            Log.e("GoalAdapter", "Error constructing");
        }
    }

    @Override
    public int getCount() {
        return listOfGoals.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView display_name;
        public TextView display_info;
        public ImageView display_icon;

        public TextView getDisplayName() {
            return display_name;
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                v = inflater.inflate(R.layout.layout_row, null);
                holder = new ViewHolder();

                holder.display_name = (TextView) v.findViewById(R.id.goalNameList);
                holder.display_info = (TextView) v.findViewById(R.id.basicInfoList);
                holder.display_icon = (ImageView) v.findViewById(R.id.classificationIcon);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }

            holder.display_name.setText(listOfGoals.get(position).getGoalName());
            //holder.display_name.setSelected(true);
            if (listOfGoals.get(position).classification().equals(Goal.Classification.COUNTDOWN)) {
                holder.display_info.setText(((CountdownCompleterGoal) listOfGoals.get(position)).toBasicInfo());
                v.setBackgroundColor(BLUE_BACKGROUND);
                holder.display_name.setTextColor(COUNTDOWN_BLUE);
                holder.display_info.setTextColor(COUNTDOWN_BLUE);
                holder.display_icon.setImageResource(R.drawable.countdown_flag_small);

            } else {
                holder.display_info.setText(((StreakSustainerGoal) listOfGoals.get(position)).toBasicInfo());
                v.setBackgroundColor(RED_BACKGROUND);
                holder.display_name.setTextColor(STREAK_RED);
                holder.display_info.setTextColor(STREAK_RED);
                holder.display_icon.setImageResource(R.drawable.streak_flame_small);
            }

        } catch (Exception e) {
            Log.e("GoalAdapter", "Error constructing");
        }
        return v;
    }
}
