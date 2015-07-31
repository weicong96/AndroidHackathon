package sg.edu.nyp.backend;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Load;

/**
 * Created by admin on 26/7/15.
 */
@Entity
public class HelpRequest {
    @Id
    private Long requestID;

    public double getDistanceFromU() {
        return distanceFromU;
    }

    public void setDistanceFromU(double distanceFromU) {
        this.distanceFromU = distanceFromU;
    }

    @Load
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Ref<User> userRef;

    private boolean resolved;

    @Load
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Ref<User> needyUserRef;

    @Ignore
    private User needyUser;

    @Ignore
    private User user;

    @Ignore
    private double distanceFromU;

    private double lat;
    private double lng;

    public Long getRequestID() {
        return requestID;
    }

    public void setRequestID(Long requestID) {
        this.requestID = requestID;
    }

    public Ref<User> getUserRef() {
        return userRef;
    }

    public void setUserRef(Ref<User> userRef) {
        this.userRef = userRef;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public Ref<User> getNeedyUserRef() {
        return needyUserRef;
    }

    public void setNeedyUserRef(Ref<User> needyUserRef) {
        this.needyUserRef = needyUserRef;
    }

    public User getNeedyUser() {
        return needyUser;
    }

    public void setNeedyUser(User needyUser) {
        this.needyUser = needyUser;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
}
