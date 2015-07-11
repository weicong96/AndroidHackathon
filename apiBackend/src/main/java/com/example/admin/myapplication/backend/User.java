package com.example.admin.myapplication.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by admin on 10/7/15.
 */
@Entity
public class User {
    @Id
    private long userID;
    private String razerID;
    private long points;

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
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
