package sg.edu.nyp.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;

/**
 * Created by admin on 19/7/15.
 */
@Entity
public class Event {
    @Id
    private Long eventID;
    private String name;
    private String description;
    private long date;

    public Long getEventID() {
        return eventID;
    }

    public void setEventID(Long eventID) {
        this.eventID = eventID;
    }

    private Integer attending;
    private String contactInfo;
    private String contactPerson;

    @Ignore
    private int alreadyAttending;

    public int getAlreadyAttending() {
        return alreadyAttending;
    }

    public void setAlreadyAttending(int alreadyAttending) {
        this.alreadyAttending = alreadyAttending;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public Integer getAttending() {
        return attending;
    }

    public void setAttending(Integer attending) {
        this.attending = attending;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
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
