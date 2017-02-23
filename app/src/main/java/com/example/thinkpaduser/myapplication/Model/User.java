package com.example.thinkpaduser.myapplication.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by ThinkPad User on 2016/7/13.
 */
public class User implements Parcelable {
    private String id;
    private String username;
    private String usersex;
    private String userpass;
    private String useremail;
    private String nickname;
    private String birthday;
    private String portrait;
    private String signature;

    public String getUserpass() {
        return userpass;
    }

    public void setUserpass(String userpass) {
        this.userpass = userpass;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsersex() {
        return usersex;
    }

    public void setUsersex(String usersex) {
        this.usersex = usersex;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.username);
        dest.writeString(this.usersex);
        dest.writeString(this.useremail);
        dest.writeString(this.nickname);
        dest.writeString(this.birthday);
        dest.writeString(this.portrait);
        dest.writeString(this.signature);
        dest.writeString(this.userpass);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.id = in.readString();
        this.username = in.readString();
        this.usersex = in.readString();
        this.useremail = in.readString();
        this.nickname = in.readString();
        this.birthday = in.readString();
        this.portrait = in.readString();
        this.signature = in.readString();
        this.userpass = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
