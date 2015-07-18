package sg.edu.nyp.backend;

/**
 * Created by Administrator on 7/17/15.
 */
public enum AchievementsENUM {
    UNIQUE_THREE(1 , "Helped 5 people"),
    UNIQUE_SEVEN(2 , "Helped 10 people"),
    UNIQUE_TEN(3, "Helped 15 people");

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
