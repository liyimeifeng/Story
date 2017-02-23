package com.example.thinkpaduser.myapplication.StaticMethod;

import android.app.Dialog;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.example.thinkpaduser.myapplication.R;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by ThinkPad on 2016/8/5.
 */
public class DialogPhoto {  //点击故事里图片跳出对话框显示高清大图的方法
    public static void originalPhoto (SimpleDraweeView simpleDraweeView,final String uri){
        simpleDraweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(v.getContext());
                View diaView = layoutInflater.inflate(R.layout.show_real_pic,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setView(diaView);
                SimpleDraweeView mSimpleDraweeView1 = (SimpleDraweeView) diaView.findViewById(R.id.show_real_pic_id);
                mSimpleDraweeView1.setImageURI(uri);
                mSimpleDraweeView1.setScaleType(ImageView.ScaleType.FIT_CENTER);

                final Dialog dialog =builder.create();
                dialog.show();
                diaView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();//实现点击高清大图后对话框显示的方法
                    }
                });
            }

        });
    }
}
