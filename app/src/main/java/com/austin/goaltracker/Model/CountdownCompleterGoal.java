package com.austin.goaltracker.Model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Class used for goals that count downward a fixed number times. The person sets a goal
 * for some discrete time or number of executions and the goal completes when it is done.
 */
public class CountdownCompleterGoal extends Goal {
    private Calendar dateDesiredFinish;
    private long remainingCheckpoints, totalCheckpoints;
    private int percentProgress;

    public CountdownCompleterGoal(String goalName, IncrementType type, Calendar dateDesiredFinish) {
        super(goalName, type, GoalClassification.COUNTDOWN);
        this.dateDesiredFinish = dateDesiredFinish;
        totalCheckpoints = calculateRemainingCheckpoints();
        remainingCheckpoints = totalCheckpoints;
        percentProgress = 0;
    }

    public CountdownCompleterGoal() {
        super(GoalClassification.COUNTDOWN);
    }

    public void update() {
        remainingCheckpoints--;
        percentProgress = (int) ((double)(totalCheckpoints - remainingCheckpoints) / totalCheckpoints * 100);
    }

    private long calculateRemainingCheckpoints() {
        long checkpointDiff = 0;
        // Create a new date to figure out checkpoint difference
        Calendar start = Calendar.getInstance();

        if (incrementType.equals(IncrementType.HOURLY)) {
            while (start.before(dateDesiredFinish)) {
                start.add(Calendar.HOUR, 1);
                checkpointDiff++;
            }
        } else if (incrementType.equals(IncrementType.DAILY)) {
            while (start.before(dateDesiredFinish)) {
                start.add(Calendar.DAY_OF_MONTH, 1);
                checkpointDiff++;
            }
        } else if (incrementType.equals(IncrementType.BIDAILY)) {
            while (start.before(dateDesiredFinish)) {
                start.add(Calendar.DAY_OF_MONTH, 2);
                checkpointDiff++;
            }
        } else if (incrementType.equals(IncrementType.WEEKLY)) {
            while (start.before(dateDesiredFinish)) {
                start.add(Calendar.WEEK_OF_MONTH, 1);
                checkpointDiff++;
            }
        } else if (incrementType.equals(IncrementType.BIWEEKLY)) {
            while (start.before(dateDesiredFinish)) {
                start.add(Calendar.DAY_OF_MONTH, 2);
                checkpointDiff++;
            }
        } else if (incrementType.equals(IncrementType.MONTHLY)) {
            while (start.before(dateDesiredFinish)) {
                start.add(Calendar.MONTH, 1);
                checkpointDiff++;
            }
        } else {
            while (start.before(dateDesiredFinish)) {
                start.add(Calendar.YEAR, 1);
                checkpointDiff++;
            }
        }
        return checkpointDiff;
    }

    public String toBasicInfo() {
        return remainingCheckpoints + unitToString(remainingCheckpoints) + " left until completion";
    }

    public String desiredFinishDateToString() {
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
        return "To Be Finished:\n" + df.format(dateDesiredFinish.getTime());
    }

    public String unitsRemainingToString() {
        return remainingCheckpoints + unitToString(remainingCheckpoints) + " remaining";
    }

    public Calendar getDateDesiredFinish() {
        return dateDesiredFinish;
    }

    public int getPercentProgress() {
        return percentProgress;
    }

    public long getRemainingCheckpoints() {
        return remainingCheckpoints;
    }

    public long getTotalCheckpoints() {
        return totalCheckpoints;
    }

    public void setDateDesiredFinish(Calendar date) {
        dateDesiredFinish = date;
    }

    public void setRemainingCheckpoints(int checkpoints) {
        remainingCheckpoints = checkpoints;
    }

    public void setTotalCheckpoints(int checkpoints) {
        totalCheckpoints = checkpoints;
    }

    public void setPercentProgress(int percent) {
        percentProgress = percent;
    }
}
