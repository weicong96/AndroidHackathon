package com.example.admin.myapplication.backend;

/**
 * Created by Administrator on 7/12/15.
 */
public enum AchievementsENUM {
    UNIQUE_ONE(1 , "Helped one people"),
    UNIQUE_THREE(2 , "Helped three people"),
    UNIQUE_SEVEN(3 , "Helped seven people"),
    UNIQUE_TEN(4, "Helped ten people");

    public long id;
    public String title;
    AchievementsENUM(long id, String title){
        this.id = id;
        this.title = title;
    }
    public static Achievements getAchivements(AchievementsENUM _enum){
        Achievements ach = new Achievements();
        ach.setAchievementID(_enum.id);
        ach.setAchievementTitle(_enum.title);
        return ach;
    }
}
