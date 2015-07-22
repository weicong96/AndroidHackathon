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
 * Created by admin on 21/7/15.
 */
@Entity
public class UserReward{
    @Id
    private Long id;
    @Load
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Ref<Reward> rewardRef;

    @Load
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Ref<User> userRef;

    @Ignore
    private Reward reward;
    @Ignore
    private User user;

    @OnLoad
    void populateRefs(){
        this.reward = rewardRef.get();
        //this.user = userRef.get();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ref<Reward> getRewardRef() {
        return rewardRef;
    }

    public void setRewardRef(Ref<Reward> rewardRef) {
        this.rewardRef = rewardRef;
    }

    public Ref<User> getUserRef() {
        return userRef;
    }

    public void setUserRef(Ref<User> userRef) {
        this.userRef = userRef;
    }

    public Reward getReward() {
        return reward;
    }

    public void setReward(Reward reward) {
        this.reward = reward;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
