package sg.edu.nyp.backend;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Load;

import java.util.List;

/**
 * Created by Administrator on 7/17/15.
 */
@Entity
public class UserAchievement {
    @Id
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private long timeRecieved;
    @Load
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Ref<Achievements> achievementsRef;

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

    @Ignore
    private Achievements achievements;


}
