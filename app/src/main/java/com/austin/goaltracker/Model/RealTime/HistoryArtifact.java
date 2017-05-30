package com.austin.goaltracker.Model.RealTime;

/**
 * @author Austin Herring
 * @version 1.0
 *
 * Class that joins friends additions and goals to a history object. Displays in the history
 * list activity
 */
public class HistoryArtifact {
    private String id, associatedObject, type, username, userPic;
    private long date, sort;
    private boolean positiveAction;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    public HistoryArtifact() {
    }

    public HistoryArtifact(String associatedObject, String type, long date, boolean positiveAction) {
        this.associatedObject = associatedObject;
        this.type = type;
        this.date = date;
        this.sort = date * -1;
        this.positiveAction = positiveAction;
    }

    public String getId() {
        return id;
    }

    public String getAssociatedObject() {
        return associatedObject;
    }

    public String getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public String getUserPic() {
        return userPic;
    }

    public long getDate() {
        return date;
    }

    public long getSort() {
        return sort;
    }

    public boolean getPositiveAction() {
        return positiveAction;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }
}

