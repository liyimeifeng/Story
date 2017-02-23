package com.example.thinkpaduser.myapplication;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by ThinkPad User on 2016/7/14.
 */
public class StoryApp extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);//这个步骤是APP初始化，一定要实现！！！！！！！！
    }

    @Override
    protected void attachBaseContext(Context base) {//如果manifest里面已经有了application name，那么这里就要重写这个方法
        MultiDex.install(this);  //然后就调用MultiDex.install(this)以便于允许multidex
        super.attachBaseContext(base);
    }
}
