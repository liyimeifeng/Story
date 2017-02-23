package com.example.thinkpaduser.myapplication.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.thinkpaduser.myapplication.Model.Comment;
import com.example.thinkpaduser.myapplication.Model.Story;

import com.example.thinkpaduser.myapplication.Model.User;
import com.example.thinkpaduser.myapplication.R;
import com.example.thinkpaduser.myapplication.StaticMethod.ServerUrl;
import com.example.thinkpaduser.myapplication.StaticMethod.Util;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

/**
 * Created by ThinkPad User on 2016/7/18.
 */
public class StoryDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int VIEW_TYPE_HEADER = 0;
    private final static int VIEW_TYPE_COMMENT = 1;
    private final static int VIEW_TYPE_MORE = 2;

    private ArrayList<Comment> comments = new ArrayList<>();
    private Story story;

    public StoryDetailAdapter(Story story) {
        this.story = story;
    }

    public void addAll(ArrayList<Comment> com) {
        this.comments.addAll(com);
        notifyDataSetChanged();  //通知数据已更新
    }

    public void clear() {
        this.comments.clear();//写一个评论列表清除的方法，下面要用
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_HEADER) {
            return new HeaderViewHolder(inflater.inflate(R.layout.item_story_detail_header, parent, false));
        } else if (viewType == VIEW_TYPE_COMMENT) {
            return new CommentViewHolder(inflater.inflate(R.layout.item_story_detail_comment, parent, false));
        } else {
            return new MoreViewHolder(inflater.inflate(R.layout.item_more, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
//      //下面这句话是用来判断传过来的viewHolder参数是不是属于ItemViewholder这个类，如果是，执行下一步
        if (viewHolder instanceof HeaderViewHolder) {
            HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
            //1，获得当前位置的数据
//            story = new Story();
            User user = story.getUser();
            holder.nameView.setText(user.getNickname());
            holder.cityView.setText(story.getCity());
            holder.contentView.setText(story.getInfo());
            holder.favourView.setText(story.getCount());
            holder.commentView.setText(story.getComment());
            holder.timeView.setText(Util.getTime(Long.valueOf(story.getTime())));
            holder.portraitView.setImageURI(ServerUrl.GET_PORTRAIT + user.getPortrait());

            ArrayList<String> pics = story.getPics();//服务器上的图片是以字符串形式显示的
            Log.v("打印发出去的图片", "图片是" + story.getPics());
            int size = pics.size();
            if (size > 5) { //如果有六张图片，设置第六张图片可显示出来
                holder.imageView5.setVisibility(View.VISIBLE);
                holder.imageView5.setImageURI(ServerUrl.IMAGE_ROOT + pics.get(5));//获得图片的跟地址
            } else {
                holder.imageView5.setVisibility(View.GONE);//如果没有第六张，设置第六个位置gone，消失而且不占位置
            }
            if (size > 4) {
                holder.imageView4.setVisibility(View.VISIBLE);
                holder.imageView4.setImageURI(ServerUrl.IMAGE_ROOT + pics.get(4));
            } else {
                holder.imageView4.setVisibility(View.GONE);
            }
            if (size > 3) {
                holder.imageView3.setVisibility(View.VISIBLE);
                holder.imageView3.setImageURI(ServerUrl.IMAGE_ROOT + pics.get(3));
            } else {
                holder.imageView3.setVisibility(View.GONE);
            }
            if (size > 2) {
                holder.imageView2.setVisibility(View.VISIBLE);
                holder.imageView2.setImageURI(ServerUrl.IMAGE_ROOT + pics.get(2));
            } else {
                holder.imageView2.setVisibility(View.GONE);
            }
            if (size > 1) {
                holder.imageView1.setVisibility(View.VISIBLE);
                holder.imageView1.setImageURI(ServerUrl.IMAGE_ROOT + pics.get(1));
            } else {
                holder.imageView1.setVisibility(View.GONE);
            }
            if (size > 0) {
                holder.imageView0.setVisibility(View.VISIBLE);
                holder.imageView0.setImageURI(ServerUrl.IMAGE_ROOT + pics.get(0));
            } else {
//                holder.imageView0.setImageURI("");
                holder.imageView0.setVisibility(View.GONE);
                //如果一张图片都没有，获取到的URL地址是空，显示的即是加载失败应该显示的图片
            }
            if (size <= 1) holder.imageView0.setAspectRatio(16.0f / 9.0f);//设置图片高宽比率

        } else if (viewHolder instanceof CommentViewHolder) {  //如果属于第二个页面
            CommentViewHolder holder = (CommentViewHolder) viewHolder;
            final Comment comment = comments.get(position - 1);
            //因为这个评论comment部分上面还有一个header部分，所以位置要减一
            holder.nameView.setText(comment.getUser().getNickname());
            holder.commentView.setText(comment.getComments());
            holder.timeView.setText(Util.getTime(Long.valueOf(comment.getTime())));
            holder.portraitView.setImageURI(ServerUrl.GET_PORTRAIT + comment.getUser().getPortrait());
        } else {      //如果属于第三个页面
            MoreViewHolder holder = (MoreViewHolder) viewHolder;
        }
    }

    @Override
    public int getItemCount() {
        return comments.size() + 2;//因为除了评论部分的view，还有其他两个view
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        } else if (position == comments.size() + 1) {
            //如果位置等于评论列表长度加上header页面，那么就显示最后一个more页面
            return VIEW_TYPE_MORE;
        } else return VIEW_TYPE_COMMENT;//其他情况都属于评论页面
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView nameView, timeView, commentView;
        SimpleDraweeView portraitView;

        public CommentViewHolder(View itemView) {
            super(itemView);
            nameView = (TextView) itemView.findViewById(R.id.item_story_detail__comment_name);
            timeView = (TextView) itemView.findViewById(R.id.item_story_detail__comment_time);
            commentView = (TextView) itemView.findViewById(R.id.item_story_detail__comment_comments);
            portraitView = (SimpleDraweeView) itemView.findViewById(R.id.item_story_detail__comment_portrait);
            portraitView.setAspectRatio(1);

        }
    }//有三种view，Item表示中间主体部分的view，也就是评论部分

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView nameView, cityView, contentView, timeView, favourView, commentView;
        SimpleDraweeView portraitView, imageView0, imageView1, imageView2, imageView3, imageView4, imageView5;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            portraitView = (SimpleDraweeView) itemView.findViewById(R.id.item_story_detail__header_portrait);
            nameView = (TextView) itemView.findViewById(R.id.item_story_detail_header_name);
            cityView = (TextView) itemView.findViewById(R.id.item_story_detail_header_city);
            timeView = (TextView) itemView.findViewById(R.id.item_storydetail_header_time);
            contentView = (TextView) itemView.findViewById(R.id.item_story_detail_header_content);
            favourView = (TextView) itemView.findViewById(R.id.item_story_detail_header_favour);
            commentView = (TextView) itemView.findViewById(R.id.item_story_detail_header_message);

            imageView0 = (SimpleDraweeView) itemView.findViewById(R.id.item_story_detail__header_image0);
            imageView1 = (SimpleDraweeView) itemView.findViewById(R.id.item_story_detail__header_image1);
            imageView2 = (SimpleDraweeView) itemView.findViewById(R.id.item_story_detail__header_image2);
            imageView3 = (SimpleDraweeView) itemView.findViewById(R.id.item_story_detail__header_image3);
            imageView4 = (SimpleDraweeView) itemView.findViewById(R.id.item_story_detail__header_image4);
            imageView5 = (SimpleDraweeView) itemView.findViewById(R.id.item_story_detail__header_image5);
            imageView0.setAspectRatio(1);
            imageView1.setAspectRatio(1);
            imageView2.setAspectRatio(1);
            imageView3.setAspectRatio(1);
            imageView4.setAspectRatio(1);
            imageView5.setAspectRatio(1);
        }
    }//这是故事体部分的view

    class MoreViewHolder extends RecyclerView.ViewHolder {
        public MoreViewHolder(View itemView) {
            super(itemView);
        }
    }//这是更多部分的view


}
