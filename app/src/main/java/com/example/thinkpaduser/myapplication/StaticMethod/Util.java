package com.example.thinkpaduser.myapplication.StaticMethod;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.ContactsContract;

import com.example.thinkpaduser.myapplication.Model.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ThinkPad User on 2016/7/20.
 */
public class Util {
        public static String getTime(long time){//服务器传来的时间是长整型的
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String data = formatter.format(new Date(time*1000));
            return data;
//        return formatter.format(Long.parseLong(time));
            //format方法不能传入String类型的参数，所以要用包装类先装换成长整型
    }

        public static User userRead(Context context){
        SharedPreferences sp = context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        User user = new User();
        user.setId(sp.getString("id",""));
        user.setUsername(sp.getString("username",""));
        user.setUserpass(sp.getString("userpass",""));
        user.setNickname(sp.getString("nickname",""));
        user.setSignature(sp.getString("signature",""));
        user.setPortrait(sp.getString("portrait",""));
        user.setBirthday(sp.getString("birthday",""));
        user.setUseremail(sp.getString("useremail",""));
        user.setUsersex(sp.getString("usersex",""));
        return user;


    }

//    public static String getTime(String str){
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        long time = Long.parseLong(str);
//        Date date = new Date(time);
//        return dateFormat.format(date);
//    }

//    public static Date getDate(){
//        Date currentTime = new Date();
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String dateString  = formatter.format(currentTime);
//        ParsePosition position = new ParsePosition(8);
//        Date currentTime2 = formatter.parse(dateString,position);
//        return currentTime2;
//    }

}
