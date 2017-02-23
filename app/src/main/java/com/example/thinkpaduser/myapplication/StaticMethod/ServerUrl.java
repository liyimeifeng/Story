package com.example.thinkpaduser.myapplication.StaticMethod;

/**
 * Created by ThinkPad User on 2016/7/13.
 */
public class ServerUrl {
    public final static String API_ROOT = "http://139.129.19.51/story/index.php/home/Interface/";
    public final static String IMAGE_ROOT ="http://139.129.19.51/story/Uploads/"; //获取图片的跟地址
    public final static String GET_STORY = API_ROOT +"getStorys";     //获取故事的地址
    public final static String READ_STORY = API_ROOT +"readStorys";   //读取故事的地址
    public final static String GET_COMMENT = API_ROOT +"getComments";
    public final static String SEND_COMMENT = API_ROOT +"sendComment";
    public final static String SEND_STORY = API_ROOT +"sendStory";
    public final static String CHANGE_NICKNAME = API_ROOT + "changeNickName";
    public final static String CHANGE_SEX = API_ROOT + "changeSex";
    public final static String CHANGE_EMAIL = API_ROOT + "changeEmail";
    public final static String CHANGE_BIRTHDAY = API_ROOT + "changeBirthday";
    public final static String CHANGE_SIGNATURE = API_ROOT +"changeSignature";
    public final static String CHANGE_PORTRAIT = API_ROOT + "changePortrait";
    public final static String CHANGE_PASSWORD = API_ROOT + "changePassword";
    public final static String GET_PORTRAIT = "http://139.129.19.51/story/Uploads/portrait/";
    public final static String MY_STORY = API_ROOT +"myStorys";
}
