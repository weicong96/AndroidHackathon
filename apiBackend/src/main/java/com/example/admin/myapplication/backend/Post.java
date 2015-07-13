package com.example.admin.myapplication.backend;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;

/**
 * Created by Administrator on 7/13/15.
 */
@Entity
public class Post {
    @Id
    private String postID;
    private String title;
    private String facebookPostID;
    private long timePosted;

    public long getTimePosted() {
        return timePosted;
    }

    public void setTimePosted(long timePosted) {
        this.timePosted = timePosted;
    }

    @Load
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Ref<User> user;

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFacebookPostID() {
        return facebookPostID;
    }

    public void setFacebookPostID(String facebookPostID) {
        this.facebookPostID = facebookPostID;
    }

    public Ref<User> getUser() {
        return user;
    }

    public void setUser(Ref<User> user) {
        this.user = user;
    }
}
