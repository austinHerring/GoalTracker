package com.austin.goaltracker.Model;

import java.util.ArrayList;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Holds fetched goals for a get account in friends list
 */
public class GetGoals {

    public ArrayList<GetGoal> Goals;

    public GetGoals() {
        Goals = new ArrayList<>();
    }

    public boolean AddGoal(GetGoal getGoal) {
        return Goals.add(getGoal);
    }
}
