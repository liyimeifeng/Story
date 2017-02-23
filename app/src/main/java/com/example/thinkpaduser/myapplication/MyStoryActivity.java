package com.example.thinkpaduser.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thinkpaduser.myapplication.Adapter.MyStoryAdapter;
import com.example.thinkpaduser.myapplication.Adapter.StoryDetailAdapter;
import com.example.thinkpaduser.myapplication.Model.Comment;
import com.example.thinkpaduser.myapplication.Model.Story;
import com.example.thinkpaduser.myapplication.Model.User;
import com.example.thinkpaduser.myapplication.StaticMethod.NetworkUtil;
import com.example.thinkpaduser.myapplication.StaticMethod.ServerUrl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyStoryActivity extends AppCompatActivity {
    private TextView sigNature;
    private RecyclerView mRecyclerView;
    private ImageView portraitView;
    private TextView nameView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private MyStoryThread mMyStoryThread;
    private int mPage = 1;
    private Story story;
    private MyStoryAdapter mMyStoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_story);

        mRecyclerView = (RecyclerView)findViewById(R.id.activity_my_story_recyclerView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_my_story_refresh);
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);

        User user = new User();
        SharedPreferences sp = getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        user.setNickname(sp.getString("nickname",""));//先set然后适配器才能get！！！

        user.setSignature(sp.getString("signature",""));
        user.setPortrait(sp.getString("portrait",""));
        mMyStoryAdapter = new MyStoryAdapter(user);//构造方法传值啊啊啊啊！！！
        mRecyclerView.setAdapter(mMyStoryAdapter);

//        sigNature.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //一个修改签名的方法
//            }
//        });

        mSwipeRefreshLayout.setColorSchemeColors(Color.BLACK);
        //注册新监听器
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {  //设置下拉刷新的方法，然后启动线程
                mMyStoryAdapter.clear();
                //每次下拉刷新清楚原来的列表，再重新启动线程加入新列表
                mPage = 1;
                mMyStoryThread = new MyStoryThread();
                mMyStoryThread.start();
            }
        });

        mMyStoryThread = new MyStoryThread();
        mMyStoryThread.start();
    }
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0){
                Toast.makeText(MyStoryActivity.this,"获取数据失败！",Toast.LENGTH_SHORT).show();
                return true;
            }else{
                Toast.makeText(MyStoryActivity.this,"获取数据成功！",Toast.LENGTH_SHORT).show();
                ArrayList<Story> mySto = (ArrayList<Story>) msg.obj;//强制类型转换
                mMyStoryAdapter.addAll(mySto);
                mSwipeRefreshLayout.setRefreshing(false);
                return true;
            }
        }
    });

    private class MyStoryThread extends Thread{
        @Override
        public void run() {
            Map<String,String> parmas = new HashMap<>();
            SharedPreferences sp = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            parmas.put("uid",sp.getString("id",""));
            parmas.put("page","" + mPage);
            String text = NetworkUtil.sendPostRequest(ServerUrl.MY_STORY,parmas);

            if (TextUtils.isEmpty(text)){
                handler.sendEmptyMessage(0);
                return;
            }
            try {
                JSONObject object = new JSONObject(text);
                Log.v("打印msg信息",object.getString("msg"));
                if (object.getInt("result") == 1){
                    ArrayList<Story> myStory = new ArrayList<>();
                    JSONArray array = object.getJSONArray("data");
                    for (int i = 0;i < array.length();i ++){
                        JSONObject obj = array.getJSONObject(i);
                        Story story = new Story();
                        story.setId(obj.getString("id"));
                        story.setTime(obj.getString("story_time"));
                        story.setInfo(obj.getString("story_info"));
                        story.setUid(obj.getString("uid"));
                        story.setLng(obj.getString("lng"));
                        story.setLat(obj.getString("lat"));
                        story.setCity(obj.getString("city"));
                        story.setReadcount(obj.getString("readcount"));
                        story.setComment(obj.getString("comment"));

                        JSONArray arr = obj.optJSONArray("pics");
                        ArrayList<String> pics = new ArrayList<>();
                        //注意这里用的是optJSONArray不是getJSONArray！！！！！！！！！！
                        if(arr != null){
                            for(int j = 0;j < arr.length();j ++ ){
                                pics.add(arr.getString(j));
                            }
                        }
                        story.setPics(pics);
                        Log.v("显示",""+pics.size());

                        User user = new User();
                        JSONObject userObj = object.getJSONObject("user");
                        user.setId(userObj.getString("id"));
                        user.setUsername(userObj.getString("username"));
                        user.setUsersex(userObj.getString("usersex"));
                        user.setNickname(userObj.getString("nickname"));
                        user.setBirthday(userObj.getString("birthday"));
                        user.setPortrait(userObj.getString("portrait"));
                        user.setSignature(userObj.getString("signature"));
                        story.setUser(user);
                        myStory.add(story);
                    }
                    Message msg = new Message();
                    msg.obj = myStory;
                    msg.what = 1;
                    handler.sendMessage(msg);
                }else {
                    handler.sendEmptyMessage(0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}


