package com.example.thinkpaduser.myapplication.StaticMethod;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class  Method{
    private final  static String URL = "http://139.129.19.51/story/index.php/home/Interface/";
    private String  name;
    private String pass;

    public static String  getURL(){
        return URL;
    }

    public static boolean isname(String name){
        Pattern pattern = Pattern.compile("^[a-zA-Z]{1}\\w{5,11}");
        Matcher matcher = pattern.matcher(name);
        if(matcher.matches()){
            return true;
        }else{
            return false;
        }
    }

    public static boolean ispass(String pass){
        Pattern pattern = Pattern.compile("\\w{5,8}");
        Matcher matcher = pattern.matcher(pass);
        if(matcher.matches()){
            return true;
        }else{
            return false;

        }
    }
}
