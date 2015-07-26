package sg.edu.nyp.backend;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.OnLoad;

/**
 * Created by admin on 19/7/15.
 */
@Entity
public class UserEvent {
    @Id
    private Long id;

    @Load
    @Index
    @ApiResourceProperty(ignored= AnnotationBoolean.TRUE)
    private Ref<User> userRef;

    @Ignore
    private User user;

    @Load
    @Index
    @ApiResourceProperty(ignored= AnnotationBoolean.TRUE)
    private Ref<Event> eventRef;

    @Ignore
    private Event event;

    @Index
    private Boolean attending;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getAttending() {
        return attending;
    }

    public void setAttending(Boolean attending) {
        this.attending = attending;
    }

    public Ref<User> getUserRef() {
        return userRef;
    }

    public void setUserRef(Ref<User> userRef) {
        this.userRef = userRef;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Ref<Event> getEventRef() {
        return eventRef;
    }

    public void setEventRef(Ref<Event> eventRef) {
        this.eventRef = eventRef;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @OnLoad
    void populateNonRef(){
        this.user = userRef.get();
        this.event = eventRef.get();
    }
}
