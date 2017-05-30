package com.austin.goaltracker.Model.RealTime;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * POJO class that holds a news feed item to be displayed in community
 */
public class FeedItem {
    private String id, userName, userPic;
    private long timestamp;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    public FeedItem() {
    }

    public FeedItem(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
