package sg.edu.nyp.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by admin on 19/7/15.
 */
@Entity
public class Event {
    @Id
    private long eventID;
    private String name;
    private String description;

    public long getEventID() {
        return eventID;
    }

    public void setEventID(long eventID) {
        this.eventID = eventID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
