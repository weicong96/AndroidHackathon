package sg.edu.nyp.backend;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 7/17/15.
 */
@Entity
public class User {
    @Id
    private String razerID;
    private String name;
    private String email;
    private boolean needy;
    private String level;
    private long points;
    private String profileUrl;

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    @Load
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private List<Ref<UserAchievement>> achievements;

    @Load
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private List<Ref<User>> helped;
    /*
    @Load
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private List<Ref<Post>> posts;

    public List<Ref<Post>> getPosts() {
        if(posts == null){
            posts = new ArrayList<Ref<Post>>();
        }
        return posts;
    }

    public void setPosts(List<Ref<Post>> posts) {
        this.posts = posts;
    }*/

    public List<Ref<UserAchievement>> getAchievements() {
        if(achievements == null){
            achievements = new ArrayList<Ref<UserAchievement>>();
        }
        return achievements;
    }

    public void setAchievements(List<Ref<UserAchievement>> achievements) {
        this.achievements = achievements;
    }

    public List<Ref<User>> getHelped() {
        return helped;
    }

    public void setHelped(List<Ref<User>> helped) {
        this.helped = helped;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isNeedy() {
        return needy;
    }

    public void setNeedy(boolean needy) {
        this.needy = needy;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }


    public String getRazerID() {
        return razerID;
    }

    public void setRazerID(String razerID) {
        this.razerID = razerID;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(long points) {
        this.points = points;
    }
}
