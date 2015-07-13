package com.example.admin.myapplication.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by Administrator on 7/12/15.
 */
@Entity
public class Achievements {
    @Id
    private Long achievementID;
    private String achievementTitle;

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
