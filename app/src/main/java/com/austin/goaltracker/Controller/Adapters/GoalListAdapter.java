package com.austin.goaltracker.Controller.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.austin.goaltracker.Controller.GAEDatastoreController;
import com.austin.goaltracker.Controller.ToastDisplayer;
import com.austin.goaltracker.Controller.Util;
import com.austin.goaltracker.Model.Goal.CountdownCompleterGoal;
import com.austin.goaltracker.Model.Goal.Goal;
import com.austin.goaltracker.Model.Enums.GoalClassification;
import com.austin.goaltracker.Model.GoalTrackerApplication;
import com.austin.goaltracker.Model.Goal.StreakSustainerGoal;
import com.austin.goaltracker.Model.Enums.ToastType;
import com.austin.goaltracker.R;
import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * List adapter for the Goals activity. Also sets on click listeners for each type of goal
 * in the list so that the proper fragment displays
 */
public class GoalListAdapter extends ArrayAdapter<Goal> implements Filterable {
    Activity activity;
    private ArrayList<Goal> allListOfActiveGoals;
    private ArrayList<Goal> displayedListOfGoals;
    private static LayoutInflater inflater = null;
    // Colors used for the list of goals
    private int STREAK_RED, COUNTDOWN_BLUE, BLUE_BACKGROUND, RED_BACKGROUND;
    private int mtextViewResourceId;

    public GoalListAdapter(Activity activity, int textViewResourceId, ArrayList<Goal> goals) {
        super(activity, textViewResourceId, goals);
        try {
            this.activity = activity;
            mtextViewResourceId = textViewResourceId;
            displayedListOfGoals = goals;
            allListOfActiveGoals = goals;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            STREAK_RED = activity.getResources().getColor(R.color.text2);
            COUNTDOWN_BLUE = activity.getResources().getColor(R.color.text3);
            BLUE_BACKGROUND = activity.getResources().getColor(R.color.goal_list_blue_background);
            RED_BACKGROUND = activity.getResources().getColor(R.color.goal_list_red_background);

        } catch (Exception e) {
            Log.e("GoalListAdapter", "Error constructing");
        }
    }

    @Override
    public int getCount() {
        return displayedListOfGoals.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Goal getItemFromFilteredList(int position) {
        return displayedListOfGoals.get(position);
    }

    public static class ViewHolder {
        public TextView display_name;
        public TextView display_info;
        public ImageView display_icon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final ViewHolder holder;
        final Goal goal= displayedListOfGoals.get(position);
        try {
            if (convertView == null) {
                v = inflater.inflate(mtextViewResourceId, null);
                holder = new ViewHolder();

                holder.display_name = (TextView) v.findViewById(R.id.goalNameList);
                holder.display_info = (TextView) v.findViewById(R.id.basicInfoList);
                holder.display_icon = (ImageView) v.findViewById(R.id.classificationIcon);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }

            holder.display_name.setText(goal.getGoalName());
            if (goal.classification().equals(GoalClassification.COUNTDOWN)) {
                holder.display_info.setText(((CountdownCompleterGoal) goal).toBasicInfo());
                v.findViewById(R.id.goal_row_layout).setBackgroundColor(BLUE_BACKGROUND);
                holder.display_name.setTextColor(COUNTDOWN_BLUE);
                holder.display_info.setTextColor(COUNTDOWN_BLUE);
                holder.display_icon.setImageResource(R.drawable.countdown_flag_small);

            } else {
                holder.display_info.setText(((StreakSustainerGoal) goal).toBasicInfo());
                v.findViewById(R.id.goal_row_layout).setBackgroundColor(RED_BACKGROUND);
                holder.display_name.setTextColor(STREAK_RED);
                holder.display_info.setTextColor(STREAK_RED);
                holder.display_icon.setImageResource(R.drawable.streak_flame_small);
            }

        } catch (Exception e) {
            Log.e("GoalListAdapter", "Error constructing");
        }

        SwipeLayout swipeLayout =  (SwipeLayout) v;
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, v.findViewById(R.id.bottom_wrapper));

        Button trash = (Button) v.findViewById(R.id.trash_action);
        trash.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                destroyGoal(goal);
                ToastDisplayer.displayHint("Goal Removed", ToastType.SUCCESS, GoalTrackerApplication.INSTANCE);
            }
        });

        return v;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

                displayedListOfGoals = (ArrayList<Goal>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                List<Goal> FilteredArrList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    results.count = allListOfActiveGoals.size();
                    results.values = allListOfActiveGoals;

                } else {
                    constraint = constraint.toString();
                    for (int i = 0; i < allListOfActiveGoals.size(); i++) {
                        Goal data = allListOfActiveGoals.get(i);
                        if (!(data.getId().equals(constraint))) {
                            FilteredArrList.add(data);
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }

    private void destroyGoal(Goal goal) {
        // REMOVE TRACES FROM GAE
        GAEDatastoreController.removeCron(goal);

        // REMOVE TRACES FROM FIREBASE
        Util.removeGoalFromDB(goal.getId());

        // REMOVE TRACES LOCALLY
        HashMap<String, Goal> goals = Util.currentUser.getGoals();
        goals.remove(goal.getId());
    }
}
