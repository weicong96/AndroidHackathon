package sg.edu.nyp.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by Administrator on 7/17/15.
 */
@Entity
public class Achievements {
    @Id
    private Long achievementID;
    private String achievementTitle;
    private String description;
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getAchievementID() {
        return achievementID;
    }

    public void setAchievementID(Long achievementID) {
        this.achievementID = achievementID;

    }

    public String getAchievementTitle() {
        return achievementTitle;
    }

    public void setAchievementTitle(String achievementTitle) {
        this.achievementTitle = achievementTitle;
    }

}

