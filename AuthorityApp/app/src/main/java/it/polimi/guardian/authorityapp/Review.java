package it.polimi.guardian.authorityapp;

/**
 * Created by Nemanja on 09/04/2015.
 */
public class Review {
    private int eventId;
    private String comment;

    public Review(int event_id, String comment) {
        this.eventId = event_id;
        this.comment = comment;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
