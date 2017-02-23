package com.example.thinkpaduser.myapplication.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.thinkpaduser.myapplication.R;
import com.example.thinkpaduser.myapplication.Model.Story;
import com.example.thinkpaduser.myapplication.StaticMethod.DialogPhoto;
import com.example.thinkpaduser.myapplication.StaticMethod.Util;
import com.example.thinkpaduser.myapplication.StoryDetailActivity;
import com.example.thinkpaduser.myapplication.StaticMethod.ServerUrl;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

/**
 * Created by ThinkPad User on 2016/7/11.
 */
public class StoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    //上面<>里面的是个内部类！！！内部类！！！！
    private final static int VIEW_TYPE_NORMAL = 0;
    private final static int VIEW_TYPR_MORE = 1;
    private final static String LOG_TAG = "StoryAdapter";

    public  ArrayList<Story> getStories() {
        return stories;
}

    private ArrayList<Story> stories = new ArrayList<>();

    public void addAll(ArrayList<Story> sto ){
        this.stories.addAll(sto);
        notifyDataSetChanged();//通知数据已更新，必须写！！！！！！！
    }

//    public StoryAdapter(ArrayList<Story> stories) {
//        this.stories = stories;
//    }

    //按ctrl+i 调出下面这些抽象方法。
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //创建列表行视图时调用该方法，需要实现该方法以便创建列表行视图
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //LayoutInflater可以将布局转换为View对象
        if (viewType == VIEW_TYPR_MORE) {
            View itemView = inflater.inflate(R.layout.item_more, parent, false);
            return new MoreViewHolder(itemView);
        } else {
            View itemView = inflater.inflate(R.layout.item_story, parent, false);
            return new ItemViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
    //下面这句话是用来判断传过来的viewHolder参数是不是属于ItemViewholder这个类，如果是，执行下一步
        if (viewHolder instanceof ItemViewHolder){
            ItemViewHolder holder = (ItemViewHolder) viewHolder;
            //1，获得当前位置的数据
            final Story story = stories.get(position);
            //2.获得这一行的视图
//        View itemView = holder.itemView;
//          //3.找到子视图
//        TextView nameView = (TextView)itemView.findViewById(R.id.item_story_tv_name);
            //4.更新数据
//        nameView.setText(story.getName());
            holder.nameView.setText(story.getUser().getNickname());
            holder.cityView.setText(story.getCity());
            holder.contentView.setText(story.getInfo());
            holder.shareView.setText(story.getLat());
            holder.favourView.setText(story.getCount());
            holder.commentView.setText(story.getComment());
            holder.timeView.setText(Util.getTime(Long.valueOf(story.getTime())));
            //获取头像的URI，只有空间是SimpleDraweerView的时候才能这么写
            holder.portraitView.setImageURI(ServerUrl.GET_PORTRAIT + story.getUser().getPortrait());

            ArrayList<String> pics = story.getPics();//图片是以字符串形式显示的
            int size = pics.size();
            Log.v(LOG_TAG,story.getUser().getNickname()+"有几张图=========================>" + size);
            if(size > 5){ //如果有六张图片，设置第六张图片可显示出来
                holder.imageView5.setVisibility(View.VISIBLE);
                holder.imageView5.setImageURI(ServerUrl.IMAGE_ROOT + pics.get(5));//获得图片的跟地址
                DialogPhoto.originalPhoto(holder.imageView5,ServerUrl.IMAGE_ROOT + pics.get(5));
            }else{
                holder.imageView5.setVisibility(View.GONE);//如果没有第六张，设置第六个位置gone，消失而且不占位置
            }
            if(size > 4){
                holder.imageView4.setVisibility(View.VISIBLE);
                holder.imageView4.setImageURI(ServerUrl.IMAGE_ROOT + pics.get(4));
                DialogPhoto.originalPhoto(holder.imageView4,ServerUrl.IMAGE_ROOT + pics.get(4));
            }else{
                holder.imageView4.setVisibility(View.GONE);
            }
            if(size > 3){
                holder.imageView3.setVisibility(View.VISIBLE);
                holder.imageView3.setImageURI(ServerUrl.IMAGE_ROOT + pics.get(3));
                DialogPhoto.originalPhoto(holder.imageView3,ServerUrl.IMAGE_ROOT + pics.get(3));
            }else{
                holder.imageView3.setVisibility(View.GONE);
            }
            if(size > 2){
                holder.imageView2.setVisibility(View.VISIBLE);
                holder.imageView2.setImageURI(ServerUrl.IMAGE_ROOT + pics.get(2));
                DialogPhoto.originalPhoto(holder.imageView2,ServerUrl.IMAGE_ROOT + pics.get(2));
            }else{
                holder.imageView2.setVisibility(View.GONE);
            }
            if(size > 1){
                holder.imageView1.setVisibility(View.VISIBLE);
                holder.imageView1.setImageURI(ServerUrl.IMAGE_ROOT + pics.get(1));
                DialogPhoto.originalPhoto(holder.imageView1,ServerUrl.IMAGE_ROOT + pics.get(1));
            }else{
                holder.imageView1.setVisibility(View.GONE);
            }
            if(size > 0){
                holder.imageView0.setVisibility(View.VISIBLE);
                holder.imageView0.setImageURI(ServerUrl.IMAGE_ROOT + pics.get(0));
                DialogPhoto.originalPhoto(holder.imageView0,ServerUrl.IMAGE_ROOT + pics.get(0));
            }else{
//                holder.imageView0.setImageURI("");
                holder.imageView0.setVisibility(View.GONE);
                //如果一张图片都没有，获取到的URL地址是空，显示的即是加载失败应该显示的图片
            }
            if (size <= 1)holder.imageView0.setAspectRatio(16.0f/9.0f);//设置图片高宽比率
            if (size == 1){
//                holder.imageView0.setVisibility(View.GONE);
                holder.imageView1.setVisibility(View.GONE);
                holder.imageView2.setVisibility(View.GONE);
                holder.imageView3.setVisibility(View.GONE);
                holder.imageView4.setVisibility(View.GONE);
                holder.imageView5.setVisibility(View.GONE);
            }

        //设置监听器，实现故事主页跳转故事详情,如果要全点就用viewHolder.itemView
            holder.contentView.setOnClickListener(new View.OnClickListener() {
                //故事跳转详情页面在这里写在adapter里面，因为他是带着数据跳转过去的，要卸载适配器里面，
                //而登录跳转注册不带数据跳，是activity和activity之间的跳转
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, StoryDetailActivity.class);
                //这个页面跳转是写在Adapter里面，Adapter并不会继承context,所以要创建一个context对象再写入
                intent.putExtra("story",story);//这里运用的就是Parceable方法存入intent里面传入下一个页面
//               intent.putExtra("sid",story.getId());//获得故事ID，并存入intent里面，可以存入跳转下一个页面
//                 Bundle bundle = new Bundle();
////               bundle.putSerializable("story",story);//这里运用的是Serializable要这样写存入bundle里面
//                intent.putExtras(bundle);
                context.startActivity(intent); //必须要有context才能调用start方法，所以这里就要创建一个context对象
            }
        });
        }else{
            MoreViewHolder holder = (MoreViewHolder) viewHolder;
        }

        //holder.imageview.serImageResource(R.drawer.abc)
        //使用BitmapFactory
        //1.获取图片的高度宽度，而不获取图片数据
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        //获取图片的高度宽度
//        Resources resources = holder.itemView.getResources();
//        BitmapFactory.decodeResource(resources,R.drawable.mmmm,options);
//        //获取图片的宽度高度
//        int width = options.outWidth;
//        int height = options.outHeight;
//        int viewWidth = 200;
//        int sample = width/viewWidth;
//        options.inJustDecodeBounds = false;//是不是只加载边缘
//        options.inSampleSize = sample;
//        Bitmap bitmap = BitmapFactory.decodeResource(resources,R.drawable.mmmm,options);
//        holder.imageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == stories.size()){
            return VIEW_TYPR_MORE;    //more即返回的就是1
        }else{
            return VIEW_TYPE_NORMAL;    //normal即返回的就是0
        }
    }

    @Override
    public int getItemCount() {
        //返回列表中的条目数，现在是返回数组长度
        return stories.size() + 1;
    }

    //4.申明一个ViewHolder子类
    class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView nameView,cityView,timeView,contentView,commentView,favourView,shareView;
        SimpleDraweeView portraitView,imageView0,imageView1,imageView2,imageView3,imageView4,imageView5;
        public  ItemViewHolder(View itemView){
            super(itemView);
            portraitView = (SimpleDraweeView) itemView.findViewById(R.id.item_story_sdv__portrait);
            nameView = (TextView)itemView.findViewById(R.id.item_story_tv_name);
            cityView = (TextView) itemView.findViewById(R.id.item_story_tv__city);
            contentView = (TextView) itemView.findViewById(R.id.item_story_tv__content);
            timeView = (TextView) itemView.findViewById(R.id.item_story_tv_time);
            commentView = (TextView) itemView.findViewById(R.id.item_story_tv__comment);
            favourView = (TextView) itemView.findViewById(R.id.item_story_tv__favour);
            shareView = (TextView) itemView.findViewById(R.id.item_story_tv__share);
            imageView0 = (SimpleDraweeView) itemView.findViewById(R.id.item_story_iv__image0);
            imageView1 = (SimpleDraweeView) itemView.findViewById(R.id.item_story_iv__image1);
            imageView2 = (SimpleDraweeView) itemView.findViewById(R.id.item_story_iv__image2);
            imageView3 = (SimpleDraweeView) itemView.findViewById(R.id.item_story_iv__image3);
            imageView4 = (SimpleDraweeView) itemView.findViewById(R.id.item_story_iv__image4);
            imageView5 = (SimpleDraweeView) itemView.findViewById(R.id.item_story_iv__image5);
            imageView0.setAspectRatio(1);//就算高和宽的比率
            imageView1.setAspectRatio(1);
            imageView2.setAspectRatio(1);
            imageView3.setAspectRatio(1);
            imageView4.setAspectRatio(1);
            imageView5.setAspectRatio(1);
        }
    }

        class MoreViewHolder extends RecyclerView.ViewHolder{
            public MoreViewHolder(View itemView) {
                super(itemView);
            }
        }





}
