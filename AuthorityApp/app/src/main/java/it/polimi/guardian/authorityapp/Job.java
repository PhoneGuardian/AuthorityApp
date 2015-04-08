package it.polimi.guardian.authorityapp;

/**
 * Created by Nemanja on 07/04/2015.
 */
public class Job {
    private Event event;
    private String authUsername;
    private String type;
    private String status;

    public Job(Event event) {
        this.event = event;
        authUsername = User.getInstance().getUsername();
        type = User.getInstance().getType();
        status = "taken";
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getAuthUsername() {
        return authUsername;
    }

    public void setAuthUsername(String authUsername) {
        this.authUsername = authUsername;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
