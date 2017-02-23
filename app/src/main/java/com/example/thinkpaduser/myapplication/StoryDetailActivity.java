package com.example.thinkpaduser.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thinkpaduser.myapplication.Adapter.StoryDetailAdapter;
import com.example.thinkpaduser.myapplication.Model.Comment;
import com.example.thinkpaduser.myapplication.Model.Story;

import com.example.thinkpaduser.myapplication.Model.User;
import com.example.thinkpaduser.myapplication.StaticMethod.NetworkUtil;
import com.example.thinkpaduser.myapplication.StaticMethod.ServerUrl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StoryDetailActivity extends AppCompatActivity {
    private StoryDetailAdapter mStoryDetailAdapter;
    private final static String LOG_TAG = "StoryDetailActivity";
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int mPage = 1;
    private String sid;
    private StoryDetailThread mStoryDetailThread;
    private Story story;
    private TextView inputView;
    private SendCommentThread mSendCommentThread;
    private ProgressDialog mProgressDialog;

//    @Override
//    protected void onStart() {
//        mStoryDetailAdapter.clear();
//
//        mPage = 1;
//        mStoryDetailThread = new StoryDetailThread();
//        mStoryDetailThread.start();
//
//        super.onStart();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //返回上一个页面也就是相应的Adapter页面传递的Intent对象
        Intent intent = getIntent();
//        sid = intent.getStringExtra("sid");
//        story = (Story) intent.getSerializableExtra("story");
        story = intent.getParcelableExtra("story");

        mRecyclerView = (RecyclerView) findViewById(R.id.content_story_detail_recyclerview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.content_story_detail_refresh);
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        //为什么这里只写一个this，你点开LinearLayoutManager看，里面有两个构造方法，最开始的写法和this写法
        // this即表示最开始那个写法，省略后面部分，但是代表的只是Vertical，如果是Horizatal就要重写
        mRecyclerView.setLayoutManager(manager);
        mStoryDetailAdapter = new StoryDetailAdapter(story);//设置适配器，这是这么写！
        mRecyclerView.setAdapter(mStoryDetailAdapter);

        mStoryDetailThread = new StoryDetailThread();//启动线程
        mStoryDetailThread.start();

        mSwipeRefreshLayout.setColorSchemeColors(Color.BLACK);
        //注册新监听器
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {  //设置下拉刷新的方法，然后启动线程
                mStoryDetailAdapter.clear();
                //每次下拉刷新清楚原来的列表，再重新启动线程加入新列表
                mPage = 1;
                mStoryDetailThread = new StoryDetailThread();
                mStoryDetailThread.start();
            }
        });

        //监听类别滚动,判断屏幕上下滚动的方式
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {//滚动停止状态
                    // 判断滑动到第几个位置，也就是判断滑动是否停止，先用Layoutmanage对象找到最后一个列表位置
                    int pos = manager.findLastVisibleItemPosition();
                    if (pos == mRecyclerView.getAdapter().getItemCount() - 2) {
                        //如果这个pos等于适配器中getItemCount的数目，也就是等于Arr列表长度
                        //因为上面有个header页面下面有个查看更多页面，所以要减2
                        mPage++;    //page加一，下拉到到更多页面，就要加载项下一页
                        mStoryDetailThread = new StoryDetailThread();   //然后启动线程
                        mStoryDetailThread.start();
                    }
                }
            }
        });

        inputView = (EditText)findViewById(R.id.activity_story_detail_et_input);

        findViewById(R.id.activity_story_detail_but_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment();
            }
        });
    }

    public void sendComment() {
        String comment = inputView.getText().toString();
        if (TextUtils.isEmpty(comment)) {
            inputView.setError("评论不能为空");
            inputView.requestFocus();
            return;
        }
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("发布中，请稍后.....");
        mProgressDialog.setCancelable(false);  //显示状态不可取消
        mProgressDialog.show();

        mSendCommentThread = new SendCommentThread(comment);
        mSendCommentThread.start();
        //先是上面调用该方法触发监听器，然后调用该方法检验是否为空，然后启动子线程，接收数据
    }

    private class SendCommentThread extends Thread { //创建发送评论的线程
        private String content;

        public SendCommentThread(String content) {    //构造函数传参
            this.content = content;
        }

        @Override
        public void run() {
            Map<String, String> parmas = new HashMap<>();
            Story story = getIntent().getParcelableExtra("story");
            SharedPreferences sp = getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
            parmas.put("sid", story.getId());//这是需要两个参数，sid和page
            parmas.put("uid", sp.getString("id", ""));//我错在这个地方！！！！！
            parmas.put("userpass", sp.getString("userpass", ""));
            parmas.put("cid", "");//""表示为空，因为这里可以为空
            parmas.put("comments", content);
            String text = NetworkUtil.sendPostRequest(ServerUrl.SEND_COMMENT, parmas);//发送评论接口地址
            if (TextUtils.isEmpty(text)) {
                handler2.sendEmptyMessage(0);
                return;
            }else{
                Log.v(LOG_TAG,"+++++>"+text);
                try {
                    JSONObject obj = new JSONObject(text);
                    if(obj.getInt("result") == 1){
                        Message msg = new Message();
                        msg.what = 1;
                        handler2.sendMessage(msg);
                    }else{
                        handler2.sendEmptyMessage(0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private Handler handler2 = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            mProgressDialog.dismiss();  //取消对话框
            if(msg.what == 1){
                Toast.makeText(StoryDetailActivity.this,"评论发送成功",Toast.LENGTH_SHORT).show();
                inputView.setText("");
                InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputmanger.hideSoftInputFromWindow(inputView.getWindowToken(), 0);

                mStoryDetailAdapter.clear();
                mStoryDetailThread = new StoryDetailThread();
                mStoryDetailThread.start();
                return true;
            }else{
                Toast.makeText(StoryDetailActivity.this,"评论发送失败",Toast.LENGTH_SHORT).show();
                return true;
            }

        }
    });


    private class StoryDetailThread extends Thread { //下面是创建接受评论的子线程，接收服务器数据，然后解析过程
        @Override
        public void run() {
            Map<String, String> parmas = new HashMap<>();
            Story story = getIntent().getParcelableExtra("story");
            //这句话意思是带着这个故事内容跳转到故事详情页面！！！！！
            parmas.put("sid", story.getId());//这是需要两个参数，sid和page
            parmas.put("page", "" + mPage);//page是整形数据，现在要的是String类型，所以前面加上""!!!!!!
            String text = NetworkUtil.sendPostRequest(ServerUrl.GET_COMMENT, parmas);
            Log.v(LOG_TAG,"===========>>>>>" + text);
            if (TextUtils.isEmpty(text)) {
                handler.sendEmptyMessage(0);
                return;
            }
            Log.v(LOG_TAG, "============>" + text);
            try {
                JSONObject object = new JSONObject(text);
                if (object.getInt("result") == 1) {
                    //result等于1表示有评论，最后发送出去的也是1，else等于0表示没有评论，发送出去的是0
                    //解析数据
                    ArrayList<Comment> comments = new ArrayList<>();
                    JSONArray array = object.getJSONArray("data");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        Comment comment = new Comment();
                        comment.setComments(obj.getString("comments"));
                        comment.setTime(obj.getString("time"));
                        //解析用户数据
                        User user = new User();
                        JSONObject userobj = obj.getJSONObject("user");
                        user.setId(userobj.getString("id"));
                        user.setUsername(userobj.getString("username"));
                        user.setUsersex(userobj.getString("usersex"));
                        user.setBirthday(userobj.getString("birthday"));
                        user.setNickname(userobj.getString("nickname"));
                        user.setPortrait(userobj.getString("portrait"));
                        user.setSignature(userobj.getString("signature"));
                        comment.setUser(user);
                        comments.add(comment);
                    }
                    Message msg = new Message();
                    msg.obj = comments;
                    msg.what = 1;//msg.what默认情况下是0，所以要给个初值，不然发送出去的永远是零
                    handler.sendMessage(msg);
                } else {
                    handler.sendEmptyMessage(0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0) {
                Toast.makeText(StoryDetailActivity.this, "没有评论", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
                return true;
            } else {
                Toast.makeText(StoryDetailActivity.this, "评论已刷新",Toast.LENGTH_SHORT).show();
                ArrayList<Comment> storyDetails = (ArrayList<Comment>) msg.obj;//强制类型转换
                mStoryDetailAdapter.addAll(storyDetails);
                mSwipeRefreshLayout.setRefreshing(false);  //设置SwipeRefreshLayout为非刷新状态
                return true;
            }
        }
    });//创建一个handler对象重写其方法接受下面线程的参数
}