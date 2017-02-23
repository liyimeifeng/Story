package com.example.thinkpaduser.myapplication.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ThinkPad User on 2016/7/21.
 */
public class WriteStoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 2;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        public ItemViewHolder(View itemView) {
            super(itemView);
        }
    }
}
