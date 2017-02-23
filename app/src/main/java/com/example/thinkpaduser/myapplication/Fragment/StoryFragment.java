package com.example.thinkpaduser.myapplication.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.thinkpaduser.myapplication.MainActivity;
import com.example.thinkpaduser.myapplication.Model.User;
import com.example.thinkpaduser.myapplication.R;
import com.example.thinkpaduser.myapplication.Model.Story;
import com.example.thinkpaduser.myapplication.Adapter.StoryAdapter;
import com.example.thinkpaduser.myapplication.StaticMethod.NetworkUtil;
import com.example.thinkpaduser.myapplication.StaticMethod.ServerUrl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ThinkPad User on 2016/7/12.
 */
public class StoryFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private StoryThread mStoryThread;
    private final static String LOG_TAG = "StoryFragment";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int mPage = 1;
    private StoryAdapter mAdapter;

    @Nullable
    @Override   //该方法用于创建Fragment的视图,返回一个View类型
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_story,container,false);
    }

    @Override
    public void onStart() {
        mAdapter.getStories().clear();  //这个必须要写！！！要让页面清除之后重新刷一遍才会有刷新的感觉！！！！！！
        mPage = 1;
        mStoryThread  = new StoryThread();
        mStoryThread.start();
        super.onStart();
    }

    @Override  //Fragment创建完成时会调用该方法
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //在该fragment视图创建完成后会调用该方法
//        1.声明一个RecycleView并找到
        mRecyclerView = (RecyclerView)view.findViewById(R.id.fragment_story_rv_recycler);
        //2.创建其LayoutManager对象
        final LinearLayoutManager manager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        //3.设置其LayoutManager
        mRecyclerView.setLayoutManager(manager);
        //4.创建适配器类转至StoryAdapter
        mAdapter = new StoryAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_story_spf_refresh);
        mSwipeRefreshLayout.setColorSchemeColors(Color.BLACK,Color.BLUE,Color.RED);
        //注册新监听器
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {  //下拉刷新设置方法，然后启动线程
                mAdapter.getStories().clear();  //这个必须要写！！！要让页面清除之后重新刷一遍才会有刷新的感觉！！！！！！
                mPage = 1;
                Toast.makeText(getActivity(),"刷新成功",Toast.LENGTH_SHORT).show();
                mStoryThread  = new StoryThread();
                mStoryThread.start();
            }
        });

        //监听类别滚动,判断屏幕上下滚动的方式
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                RecyclerView.SCROLL_STATE_DRAGGING   按住屏幕滚动
//                RecyclerView.SCROLL_STATE_SETTLING   快速滚动的状态
//                 RecyclerView.SCROLL_STATE_IDLE       滚动停止状态
                if(newState == RecyclerView.SCROLL_STATE_DRAGGING){
                    //判断滑动到第几个位置，也就是判断滑动是否停止，先用Layoutmanage对象找到最后一个列表位置
                    int pos = manager.findLastVisibleItemPosition();
                    if(pos == mRecyclerView.getAdapter().getItemCount() - 1){
                        //如果这个pos等于适配器中getItemCount的数目，也就是等于Arr列表长度
                        //因为最后还有一个查看更多的页面，所有要减一
                        mPage ++;    //page加一
                        mStoryThread = new StoryThread();   //启动线程
                        mStoryThread.start();
                    }
                }
            }
        });
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == 0){
                Toast.makeText(getActivity(),"获取数据失败！",Toast.LENGTH_SHORT).show();
                return true;
            }else{
                Toast.makeText(getActivity(),"获取数据成功啦！！",Toast.LENGTH_SHORT).show();
                ArrayList<Story> stories = (ArrayList<Story>) msg.obj;//强制类型转换
                mAdapter.addAll(stories);
               // mRecyclerView.setAdapter(mAdapter);   //设置适配器一定要写！！！！！
                mSwipeRefreshLayout.setRefreshing(false);//设置SwipeRefreshLayout为非刷新状态
                return true;
            }
        }
    });

    @Override
    //activity创建完成后就调用该方法，但只能用一次
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mStoryThread = new StoryThread();
        mStoryThread.start();
    }

    public class StoryThread extends  Thread{
        @Override
        public void run() {
            Map<String,String> parmas = new HashMap<>();
            Bundle bundle = getArguments();
            parmas.put("type",bundle.getString("type"));
            parmas.put("page","" + mPage);
            String text = NetworkUtil.sendPostRequest(ServerUrl.GET_STORY,parmas);
            if (TextUtils.isEmpty(text) ){
                handler.sendEmptyMessage(0);
                return;
            }
            Log.v(LOG_TAG, "===>" + text);
            try {
                JSONObject object = new JSONObject(text);
                if (object.getInt( "result") == 1){
                    //解析数据
                    ArrayList<Story> stories = new ArrayList<>();
                    JSONArray array = object.getJSONArray("data");
                    for(int i = 0;i < array.length(); i++){
                        JSONObject obj = array.getJSONObject(i);
                        Story story = new Story();
                        story.setId(obj.getString("id"));
                        story.setCity(obj.getString("city"));
                        story.setComment(obj.getString("comment"));
                        story.setReadcount(obj.getString("readcount"));
                        story.setInfo(obj.getString("story_info"));
                        story.setLat(obj.getString("lat"));
                        story.setLng(obj.getString("lng"));
                        story.setTime(obj.getString("story_time"));
                        story.setUid(obj.getString("uid"));
                        //解析图片数据，因为图片是一个数组
                        JSONArray arr = obj.optJSONArray("pics");
                        //注意这里用的是optJSONArray不是getJSONArray！！！！！！！！！！
                        ArrayList<String> pics = new ArrayList<>();
                        if(arr != null){
                            for(int j = 0;j < arr.length();j ++ ){
                                pics.add(arr.getString(j));
                            }
                        }
                        story.setPics(pics);
                        //解析用户user数据
                        User user = new User();
                        JSONObject userobj = obj.getJSONObject("user");
                        user.setId(userobj.getString("id"));
                        user.setUsername(userobj.getString("username"));
                        user.setUsersex(userobj.getString("usersex"));
                        user.setBirthday(userobj.getString("birthday"));
                        user.setNickname(userobj.getString("nickname"));
                        user.setPortrait(userobj.getString("portrait"));
                        user.setSignature(userobj.getString("signature"));
                        story.setUser(user);
                        stories.add(story);
                    }
                    Message msg = new Message();
                    msg.obj = stories;
                    msg.what = 1;//msg.what默认情况下是0，所以要给个初值，不然发送出去的永远是零
                    handler.sendMessage(msg);
                }else{
                    handler.sendEmptyMessage(0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
