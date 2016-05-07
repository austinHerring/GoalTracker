package com.austin.goaltracker.View.Goals;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.austin.goaltracker.Model.StreakSustainerGoal;
import com.austin.goaltracker.R;

import xyz.hanks.library.SmallBang;
import me.alexrs.wavedrawable.WaveDrawable;

public class GoalsStreakGraphicFragment extends Fragment  implements View.OnClickListener {
    private static final String GOAL_NAME = "goal name";
    private static final String GOAL_TASK = "goal task";
    private static final String GOAL_CHEAT_NUMBER = "goal cheat number";
    private static final String GOAL_CHEATS_REMAINING = "goal cheats used";
    private static final String GOAL_STREAK = "goal streak";
    private static final String GOAL_START = "goal start";
    private static final String GOAL_INFO = "goal info";

    private String mGoalName;
    private String mGoalTask;
    private int mGoalCheatNumber;
    private int mGoalCheatsremaining;
    private String mGoalStreak;
    private String mGoalStart;
    private String mGoalInfo;

    /**
     * Factory method to create a new instance of
     * a Streak graphic fragment
     *
     * @param goal the Streak goal to create a custom fragment for
     * @return A new instance of fragment GoalCountdownGraphicFragment.
     */
    public static GoalsStreakGraphicFragment newInstance(StreakSustainerGoal goal) {
        GoalsStreakGraphicFragment fragment = new GoalsStreakGraphicFragment();
        Bundle args = new Bundle();
        args.putString(GOAL_NAME, goal.getGoalName());
        args.putString(GOAL_TASK, goal.getTask());
        args.putInt(GOAL_CHEAT_NUMBER, goal.getCheatNumber());
        args.putInt(GOAL_CHEATS_REMAINING, goal.getCheatsRemaining());
        args.putString(GOAL_STREAK, Integer.toString(goal.getStreak()));
        args.putString(GOAL_START, goal.originDateToString());
        args.putString(GOAL_INFO, goal.currentStreakToString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGoalName = getArguments().getString(GOAL_NAME);
            mGoalTask = getArguments().getString(GOAL_TASK);
            mGoalCheatNumber = getArguments().getInt(GOAL_CHEAT_NUMBER);
            mGoalCheatsremaining = getArguments().getInt(GOAL_CHEATS_REMAINING);
            mGoalStreak = getArguments().getString(GOAL_STREAK);
            mGoalStart = getArguments().getString(GOAL_START);
            mGoalInfo = getArguments().getString(GOAL_INFO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView =  inflater.inflate(R.layout.fragment_goals_streak_graphic, container, false);
        rootView.setOnClickListener(this);
        ((TextView) rootView.findViewById(R.id.goal_title)).setText(mGoalName);
        rootView.findViewById(R.id.goal_title).setSelected(true);
        ((TextView) rootView.findViewById(R.id.goal_task)).setText(mGoalTask);
        rootView.findViewById(R.id.goal_task).setSelected(true);
        if (mGoalCheatNumber == 0) {
            rootView.findViewById(R.id.remainingCheats).setVisibility(View.GONE);
            rootView.findViewById(R.id.goal_cheat_ratio).setVisibility(View.GONE);
        } else {
            ((ProgressBar) rootView.findViewById(R.id.remainingCheats)).setProgress((int)((double)mGoalCheatsremaining / (double)mGoalCheatNumber * 100));
            ((ProgressBar) rootView.findViewById(R.id.remainingCheats)).getProgressDrawable().setColorFilter(getResources().getColor(R.color.primaryP), android.graphics.PorterDuff.Mode.SRC_IN);
            ((TextView) rootView.findViewById(R.id.goal_cheat_ratio)).setText(mGoalCheatsremaining + "/" + mGoalCheatNumber);
        }
        ((TextView) rootView.findViewById(R.id.streak_number)).setText(mGoalStreak);
        ((TextView) rootView.findViewById(R.id.goal_started_date)).setText(mGoalStart);
        ((TextView) rootView.findViewById(R.id.goal_units_completed)).setText(mGoalInfo);

        // Sets up the explosion animation
        final SmallBang explosion = new SmallBang(getContext());
        ((ViewGroup)rootView.findViewById(R.id.streak_graphic)).addView(explosion, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        int[] x = {getResources().getColor(R.color.primary_dark),
                getResources().getColor(R.color.primaryP),
                getResources().getColor(R.color.text2),
                getResources().getColor(R.color.text3),
                getResources().getColor(R.color.primaryW)};
        explosion.setColors(x);
        explosion.postDelayed(new Runnable() {  //delay button
            public void run() {
                explosion.bang(rootView.findViewById(R.id.streak_number), 230, null);
                explosion.performClick();
            }
        }, 10);
        WaveDrawable waveDrawable = new WaveDrawable(getResources().getColor(R.color.spinner_background), 225);
        waveDrawable.setWaveInterpolator(new BounceInterpolator());
        waveDrawable.startAnimation();
        rootView.findViewById(R.id.streak_graphic).setBackgroundDrawable(waveDrawable);
        return rootView;
    }


    @Override
    public void onClick(View v) {
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        ((GoalsBaseActivity) getActivity()).getGoalListAdapter().getFilter().filter(null);

    }

}
