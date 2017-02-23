package com.example.thinkpaduser.myapplication.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thinkpaduser.myapplication.Model.Comment;
import com.example.thinkpaduser.myapplication.Model.Story;
import com.example.thinkpaduser.myapplication.Model.User;
import com.example.thinkpaduser.myapplication.MyStoryActivity;
import com.example.thinkpaduser.myapplication.R;
import com.example.thinkpaduser.myapplication.StaticMethod.NetworkUtil;
import com.example.thinkpaduser.myapplication.StaticMethod.ServerUrl;
import com.example.thinkpaduser.myapplication.StaticMethod.Util;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ThinkPad User on 2016/7/30.
 */
public class MyStoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<Story> myStory = new ArrayList<>();
    private Story story;
    private User user;
    Context context;
    private String newSignature;


    public MyStoryAdapter(User user) {
        this.user = user;
    }
    public void addAll(ArrayList<Story> sto){
        this.myStory.addAll(sto);
        notifyDataSetChanged();  //通知数据已更新,必须写！！！
    }
    public void clear(){
        this.myStory.clear();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       context= parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == 0) {
            return new HeaderViewHolder(inflater.inflate(R.layout.item_my_story_header, parent, false));
        } else  {
            return new ItemViewHolder(inflater.inflate(R.layout.item_my_story_info, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder){
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            Log.v("名字是","" + user.getNickname());
            headerViewHolder.nameView.setText(user.getNickname());
            Log.v("签名是",user.getSignature());
            headerViewHolder.signatureTextView.setText(user.getSignature());
            headerViewHolder.signatureEditView.setText(user.getSignature());
            Log.v("头像是",""+user.getPortrait());
            headerViewHolder.portraitView.setImageURI(ServerUrl.GET_PORTRAIT + user.getPortrait());
        }else{
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            final Story story1 = myStory.get(position - 1);
            Log.v("打印故事","故事是" + story1);
            //因为这个我的故事部分上面还有一个header部分，所以位置要减一
                itemViewHolder.storyView.setText(story1.getInfo());
            Log.v("打印我发出去的故事",story1.getInfo());
                itemViewHolder.favourView.setText(story1.getCount());
                itemViewHolder.commentView.setText(story1.getComment());
                itemViewHolder.timeView.setText(Util.getTime(Long.valueOf(story1.getTime())));

            ArrayList<String> pics = story1.getPics();//服务器上的图片是以字符串形式显示的
            Log.v("打印发出去的图片","图片是" + story1.getPics());
//            Log.v("打印图片URI",ServerUrl.IMAGE_ROOT + pics.get(0));
                int size = pics.size();
            Log.v("打印发出去的图片","几张"+size);
//            Log.v("打印图片数量", );
                if(size > 5){ //如果有六张图片，设置第六张图片可显示出来
                    itemViewHolder.imageView5.setVisibility(View.VISIBLE);
                    itemViewHolder.imageView5.setImageURI(ServerUrl.IMAGE_ROOT + pics.get(5));//获得图片的跟地址
                }else{
                    itemViewHolder.imageView5.setVisibility(View.GONE);//如果没有第六张，设置第六个位置gone，消失而且不占位置
                }
                if(size > 4){
                    itemViewHolder.imageView4.setVisibility(View.VISIBLE);
                    itemViewHolder.imageView4.setImageURI(ServerUrl.IMAGE_ROOT + pics.get(4));
                }else{
                    itemViewHolder.imageView4.setVisibility(View.GONE);
                }
                if(size > 3){
                    itemViewHolder.imageView3.setVisibility(View.VISIBLE);
                    itemViewHolder.imageView3.setImageURI(ServerUrl.IMAGE_ROOT + pics.get(3));
                }else{
                    itemViewHolder.imageView3.setVisibility(View.GONE);
                }
                if(size > 2){
                    itemViewHolder.imageView2.setVisibility(View.VISIBLE);
                    itemViewHolder.imageView2.setImageURI(ServerUrl.IMAGE_ROOT + pics.get(2));
                }else{
                    itemViewHolder.imageView2.setVisibility(View.GONE);
                }
                if(size > 1){
                    itemViewHolder.imageView1.setVisibility(View.VISIBLE);
                    itemViewHolder.imageView1.setImageURI(ServerUrl.IMAGE_ROOT + pics.get(1));
                }else{
                    itemViewHolder.imageView1.setVisibility(View.GONE);
                }
                if(size > 0){
                    itemViewHolder.imageView0.setVisibility(View.VISIBLE);
                    itemViewHolder.imageView0.setImageURI(ServerUrl.IMAGE_ROOT + pics.get(0));
                }
                else{
                    itemViewHolder.imageView0.setImageURI("");
//                    itemViewHolder.imageView0.
                    //如果一张图片都没有，获取到的URL地址是空，显示的即是加载失败应该显示的图片
                }
                if (size<=1)itemViewHolder.imageView0.setAspectRatio(16.0f/9.0f);//设置图片高宽比率
        }
    }

    @Override
    public int getItemCount() {
        return myStory.size()+1;
    }
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        }
         else return 1;//其他情况都属于我的故事页面
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder{
        TextView nameView,signatureTextView;
        EditText signatureEditView;
        SimpleDraweeView portraitView;
        Button button,finishButton;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            finishButton = (Button)itemView.findViewById(R.id.item_my_story_header_bu_finish);
            button = (Button)itemView.findViewById(R.id.item_my_story_header_bu_button);
            signatureTextView = (TextView)itemView.findViewById(R.id.item_my_story_header_tv_signature);
            signatureEditView = (EditText)itemView.findViewById(R.id.item_my_story_header_et_signature);
            button.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {//实现点几次按钮隐藏TextView和当前的button
                   signatureTextView.setVisibility(View.GONE);
                   signatureEditView.setVisibility(View.VISIBLE);
                   button.setVisibility(View.GONE);
                   finishButton.setVisibility(View.VISIBLE);
               }
           });
            finishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signatureTextView.setVisibility(View.VISIBLE);
                    signatureEditView.setVisibility(View.GONE);
                    finishButton.setVisibility(View.GONE);
                    button.setVisibility(View.VISIBLE);
                    newSignature = signatureEditView.getText().toString();

                    //把修改后的Edit里面的内容传给TextView
                    signatureTextView.setText(signatureEditView.getText().toString());
                    new ChangeSignatureThread().start();

                }
            });

            portraitView = (SimpleDraweeView)itemView.findViewById(R.id.item_my_story_header_portarit);
            nameView = (TextView)itemView.findViewById(R.id.item_my_story_header_name);

        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView  storyView, timeView, favourView, commentView;
        SimpleDraweeView imageView0, imageView1, imageView2, imageView3, imageView4, imageView5;
        public ItemViewHolder(View itemView) {
            super(itemView);
            timeView = (TextView) itemView.findViewById(R.id.item_my_story_info_tv_time);
            storyView = (TextView) itemView.findViewById(R.id.item_my_story_info_tv_mystory);
            commentView = (TextView) itemView.findViewById(R.id.item_my_story_info_tv__comment);
            favourView = (TextView) itemView.findViewById(R.id.item_my_story_info_tv__favour);
            imageView0 = (SimpleDraweeView) itemView.findViewById(R.id.item_my_story_info__image0);
            imageView1 = (SimpleDraweeView)itemView.findViewById(R.id.item_my_story_info__image1);
            imageView2 = (SimpleDraweeView)itemView.findViewById(R.id.item_my_story_info__image2);
            imageView3 = (SimpleDraweeView)itemView.findViewById(R.id.item_my_story_info__image3);
            imageView4 = (SimpleDraweeView)itemView.findViewById(R.id.item_my_story_info__image4);
            imageView5 = (SimpleDraweeView)itemView.findViewById(R.id.item_my_story_info__image5);
            imageView0.setAspectRatio(1);
            imageView1.setAspectRatio(1);
            imageView2.setAspectRatio(1);
            imageView3.setAspectRatio(1);
            imageView4.setAspectRatio(1);
            imageView5.setAspectRatio(1);
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0){
                Toast.makeText(context,"签名更新失败",Toast.LENGTH_SHORT).show();
                return true;
            }else {
                Toast.makeText(context,"签名更新成功",Toast.LENGTH_SHORT).show();
                return true;
            }

        }
    });
    private class ChangeSignatureThread extends Thread{//修改签名的线程
        @Override
        public void run() {
            Map<String,String> parmas = new HashMap<>();
            SharedPreferences sp = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            parmas.put("uid",sp.getString("id",""));
            parmas.put("userpass",sp.getString("userpass",""));
            parmas.put("signature",newSignature);
            String text = NetworkUtil.sendPostRequest(ServerUrl.CHANGE_SIGNATURE,parmas);
            Log.v("打印信息",text);
            if (TextUtils.isEmpty(text)){
                handler.sendEmptyMessage(0);
                return;
            }
            try {
                JSONObject object = new JSONObject(text);
                if (object.getInt("result") == 1){
                   SharedPreferences.Editor editor = sp.edit();
                    editor.putString("signature",newSignature);
                    editor.commit();
                    handler.sendEmptyMessage(1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
