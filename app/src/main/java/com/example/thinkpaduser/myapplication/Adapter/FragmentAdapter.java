package com.example.thinkpaduser.myapplication.Adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import com.example.thinkpaduser.myapplication.Fragment.StoryFragment;

/**
 * Created by ThinkPad User on 2016/7/13.
 */
public class FragmentAdapter extends FragmentPagerAdapter {

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0){
            //构建一个Bundler对象,存入数据也就是字符串
           Bundle bundle = new Bundle();
            bundle.putString("type","new");
            //创建一个Fragment
            Fragment fragment = new StoryFragment();
            //调用setArguments
            fragment.setArguments(bundle);
            return fragment;
        }else if (position == 1){
            //构建一个Bundler对象
            Bundle bundle = new Bundle();
            bundle.putString("type","hot");
            //创建一个Fragment
            Fragment fragment = new StoryFragment();
            //调用setArguments
            fragment.setArguments(bundle);
            return fragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0){
            return "最新";
        }else if (position == 1){
            return "最热";
        }
        return super.getPageTitle(position);
    }
}
