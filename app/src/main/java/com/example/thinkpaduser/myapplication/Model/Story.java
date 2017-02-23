package com.example.thinkpaduser.myapplication.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ThinkPad User on 2016/7/11.
 */
public class Story  implements Parcelable{
    private String id;
    private String time;
    private String info;
    private ArrayList<String> pics;
    private String uid;
    private String lat;
    private String lng;
    private String city;
    private String readcount;
    private String comment;
    private User user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public ArrayList<String> getPics() {
        return pics;
    }

    public void setPics(ArrayList<String> pics) {
        this.pics = pics;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCount() {
        return readcount;
    }

    public void setReadcount(String readcount) {
        this.readcount = readcount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Story() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.time);
        dest.writeString(this.info);
        dest.writeStringList(this.pics);
        dest.writeString(this.uid);
        dest.writeString(this.lat);
        dest.writeString(this.lng);
        dest.writeString(this.city);
        dest.writeString(this.readcount);
        dest.writeString(this.comment);
        dest.writeParcelable(this.user, flags);
    }

    protected Story(Parcel in) {
        this.id = in.readString();
        this.time = in.readString();
        this.info = in.readString();
        this.pics = in.createStringArrayList();
        this.uid = in.readString();
        this.lat = in.readString();
        this.lng = in.readString();
        this.city = in.readString();
        this.readcount = in.readString();
        this.comment = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Story> CREATOR = new Creator<Story>() {
        @Override
        public Story createFromParcel(Parcel source) {
            return new Story(source);
        }

        @Override
        public Story[] newArray(int size) {
            return new Story[size];
        }
    };
}
