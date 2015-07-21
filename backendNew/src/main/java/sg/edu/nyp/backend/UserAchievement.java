package sg.edu.nyp.backend;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.OnLoad;

/**
 * Created by Administrator on 7/17/15.
 */
@Entity
public class UserAchievement {
    @Id
    private Long id;

    @Load
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Ref<Achievements> achievementsRef;

    @Ignore
    private User User;

    @Load
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Ref<User> userRef;

    @Ignore
    private Achievements achievements;

    private long timeRecieved;

    @OnLoad
    void populateNonRef(){
        User = userRef.get();
        achievements = achievementsRef.get();
    }
    public User getUser() {
        return User;
    }

    public void setUser(User user) {
        User = user;
    }

    public Ref<User> getUserRef() {
        return userRef;
    }

    public void setUserRef(Ref<User> userRef) {
        this.userRef = userRef;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public long getTimeRecieved() {
        return timeRecieved;
    }

    public void setTimeRecieved(long timeRecieved) {
        this.timeRecieved = timeRecieved;
    }


    public Ref<Achievements> getAchievementsRef() {
        return achievementsRef;
    }

    public void setAchievementsRef(Ref<Achievements> achievementsRef) {
        this.achievementsRef = achievementsRef;
    }

    public Achievements getAchievements() {
        return achievements;
    }

    public void setAchievements(Achievements achievements) {
        this.achievements = achievements;
    }


}
