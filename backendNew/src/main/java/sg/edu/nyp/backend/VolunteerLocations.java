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
 * Created by admin on 26/7/15.
 */
@Entity
public class VolunteerLocations {
    @Id
    private Long id;

    private double lat;
    private double lng;
    private double radius;

    @Load
    @Index
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Ref<User> userRef;

    @Ignore
    private User user;



    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Ref<User> getUserRef() {
        return userRef;
    }

    public void setUserRef(Ref<User> userRef) {
        this.userRef = userRef;
    }
    @OnLoad
    void populateNonRef(){
        this.user = userRef.get();

    }
}
