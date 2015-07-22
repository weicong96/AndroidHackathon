package sg.edu.nyp.backend;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.OnLoad;

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

    private double lat;
    private double lng;

    public String getRegID() {
        return regID;
    }

    public void setRegID(String regID) {
        this.regID = regID;
    }

    private String regID;


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

    @Load
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private List<Ref<UserAchievement>> achievements;

    @Load
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private List<Ref<User>> helpedRef;

    @Load
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private List<Ref<UserReward>> rewardsRef;

    @Ignore
    private List<UserReward> rewards;

    public List<Ref<UserReward>> getRewardsRef() {
        return rewardsRef;
    }

    public void setRewardsRef(List<Ref<UserReward>> rewardsRef) {
        this.rewardsRef = rewardsRef;
    }

    public List<UserReward> getRewards() {
        return rewards;
    }

    public void setRewards(List<UserReward> rewards) {
        this.rewards = rewards;
    }

    @Ignore
    private ArrayList<User> helpeed;


    public ArrayList<User> getHelpeed() {
        return helpeed;
    }

    public void setHelpeed(ArrayList<User> helpeed) {
        this.helpeed = helpeed;
    }

    public List<Ref<UserAchievement>> getAchievements() {
        if(achievements == null){
            achievements = new ArrayList<Ref<UserAchievement>>();
        }
        return achievements;
    }

    public void setAchievements(List<Ref<UserAchievement>> achievements) {
        this.achievements = achievements;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
    public List<Ref<User>> getHelpedRef() {
        return helpedRef;
    }

    public void setHelpedRef(List<Ref<User>> helpedRef) {
        if(helpedRef != null){

            helpeed = new ArrayList<User>();
            for(Ref<User> refHelper : helpedRef){
                helpeed.add(refHelper.get());
            }
            this.helpedRef = helpedRef;
        }
    }
    @OnLoad
    void populateRefIgnore(){
        //Trigger to load the entity
        this.helpeed = new ArrayList<User>();
        this.rewards = new ArrayList<UserReward>();
        if(helpedRef != null)
            for(Ref<User> helpRef : helpedRef){
                this.helpeed.add(helpRef.get());
            }
        if(rewardsRef != null)
            for(Ref<UserReward> rewardRef : rewardsRef ){
                rewards.add(rewardRef.get());
            }
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
