package com.example.thinkpaduser.myapplication.Model;

/**
 * Created by ThinkPad User on 2016/7/19.
 */
public class Comment {
    private String comments;
    private String time;
    private User user;

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
