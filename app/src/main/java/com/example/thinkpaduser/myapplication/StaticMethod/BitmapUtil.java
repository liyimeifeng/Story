package com.example.thinkpaduser.myapplication.StaticMethod;


import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ThinkPad User on 2016/7/26.
 */
public class BitmapUtil {

    //通过拍出照片的路径path加载图片
    public static Bitmap decodeFile(String path, int viewWidth, int viewHeight) {
        //1、先加载图片的基本数据
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);//这地方用的decodeFile

        //2、获得图片的宽度和高度
        int width = options.outWidth;
        int height = options.outHeight;
        //3、根据视图的宽度和高度计算一个比率
        int factor_w = width / viewWidth;
        int factor_h = height / viewHeight;
        int factor = Math.max(factor_w, factor_h);  //取最大值
        if (factor < 1) factor = 1;

        options.inJustDecodeBounds = false;
        options.inSampleSize = factor;

        return BitmapFactory.decodeFile(path,options);//实现图片的压缩处理
    }

    public static Bitmap decodeUri(Context context, Uri uri, int viewWidght, int viewHeight) {
        //通过指定的Uri加载图片，是以流的形式加载手机里的图片,这是为了上传手机图库里的图片
        InputStream is = null;
        ContentResolver cr = context.getContentResolver();//通过getContentResolver()获得ContentResolver对象
        try {
            is = cr.openInputStream(uri);//调用openInputStream方法打开指定的输入流
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //1、先加载图片的基本信息
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
//            BitmapFactory.decodeStream(is);  //上传图库里的图片是以流的形式加载
          BitmapFactory.decodeStream(is, null, options);//必须要写！！！！！！
//        2、获得原图片的宽度和高度，以实现图片的压缩
        int width = options.outWidth;
        int height = options.outHeight;
        //3、根据视图的宽度高度计算一个比率
        int factor_w = width / viewWidght;
        int factor_h = height / viewHeight;
        int factor = Math.max(factor_w, factor_h);//取最大值
        if (factor < 1) factor = 1;
        if (is != null) {
            try {
                is.close();
                is = cr.openInputStream(uri);
                options.inJustDecodeBounds = false;
                options.inSampleSize = factor;
                Bitmap bitmap2 = BitmapFactory.decodeStream(is, null, options);//这就是正式压缩！！！！
                return bitmap2;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (is != null)
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
        return null; //记得返回空
    }


}
