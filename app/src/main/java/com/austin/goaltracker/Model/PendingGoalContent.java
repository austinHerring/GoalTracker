package com.austin.goaltracker.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class PendingGoalContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<PendingGoalNotification> GOALS = new ArrayList<>();

    /**
     * A map of pending goals by ID.
     */
    public static final Map<String, PendingGoalNotification> ITEM_MAP = new HashMap<>();

    public static void addItem(PendingGoalNotification item) {
        GOALS.add(item);
        ITEM_MAP.put(item.getId(), item);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }
}
