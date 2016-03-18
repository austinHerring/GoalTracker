package com.austin.goaltracker.View.Goals;

import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.austin.goaltracker.Model.CountdownCompleterGoal;
import com.austin.goaltracker.R;
import me.itangqi.waveloadingview.WaveLoadingView;


public class GoalCountdownGraphicFragment extends Fragment {
    private static final String GOAL_NAME = "goal name";
    private static final String GOAL_TASK = "goal task";
    private static final String GOAL_PERCENT = "goal percent";
    private static final String GOAL_UNITS_LEFT = "goal units left";
    private static final String GOAL_START = "goal start";
    private static final String GOAL_END = "goal end";

    private String mGoalName;
    private String mGoalTask;
    private int mGoalPercent;
    private String mGoalUnitsLeft;
    private String mGoalStart;
    private String mGoalEnd;

    /**
     * Factory method to create a new instance of
     * a Countdown fragment using the provided parameters.
     *
     * @param goal the countdown goal to create a custom fragment for
     * @return A new instance of fragment GoalCountdownGraphicFragment.
     */
    public static GoalCountdownGraphicFragment newInstance(CountdownCompleterGoal goal) {
        GoalCountdownGraphicFragment fragment = new GoalCountdownGraphicFragment();
        Bundle args = new Bundle();
        args.putString(GOAL_NAME, goal.getGoalName());
        args.putString(GOAL_TASK, goal.getTask());
        args.putInt(GOAL_PERCENT, goal.getPercentProgress());
        args.putString(GOAL_UNITS_LEFT, goal.getUnitsRemaining());
        args.putString(GOAL_START, goal.originDateToString());
        args.putString(GOAL_END, goal.desiredFinishDateToString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGoalName = getArguments().getString(GOAL_NAME);
            mGoalTask = getArguments().getString(GOAL_TASK);
            mGoalPercent = getArguments().getInt(GOAL_PERCENT);
            mGoalUnitsLeft = getArguments().getString(GOAL_UNITS_LEFT);
            mGoalStart = getArguments().getString(GOAL_START);
            mGoalEnd = getArguments().getString(GOAL_END);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_goal_countdown_graphic, container, false);
        ((TextView) rootView.findViewById(R.id.goal_title)).setText(mGoalName);
        rootView.findViewById(R.id.goal_title).setSelected(true);
        ((TextView) rootView.findViewById(R.id.goal_task)).setText(mGoalTask);
        rootView.findViewById(R.id.goal_task).setSelected(true);
        ((WaveLoadingView) rootView.findViewById(R.id.percent_complete_icon)).setProgressValue(100 - mGoalPercent);
        ((WaveLoadingView) rootView.findViewById(R.id.percent_complete_icon)).setCenterTitle(mGoalPercent+"%");
        ((TextView) rootView.findViewById(R.id.goal_remaining_units)).setText(mGoalUnitsLeft);
        ((TextView) rootView.findViewById(R.id.goal_start)).setText(mGoalStart);
        ((TextView) rootView.findViewById(R.id.goal_end)).setText(mGoalEnd);
        return rootView;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
}
