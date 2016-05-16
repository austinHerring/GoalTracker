package com.austin.goaltracker.Model.RealTime;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Holds fetched goals for a get account in friends list
 */
public class GetGoals implements Serializable {

    public ArrayList<GetGoal> Goals;

    public GetGoals() {
        Goals = new ArrayList<>();
    }

    public boolean AddGoal(GetGoal getGoal) {
        return Goals.add(getGoal);
    }
}
