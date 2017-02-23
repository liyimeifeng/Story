package com.example.thinkpaduser.myapplication;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.thinkpaduser.myapplication.Fragment.StoryFragment;
import com.example.thinkpaduser.myapplication.Model.Comment;
import com.example.thinkpaduser.myapplication.Model.Story;
import com.example.thinkpaduser.myapplication.Model.User;
import com.example.thinkpaduser.myapplication.StaticMethod.BitmapUtil;
import com.example.thinkpaduser.myapplication.StaticMethod.NetworkUtil;
import com.example.thinkpaduser.myapplication.StaticMethod.ServerUrl;
import com.example.thinkpaduser.myapplication.StaticMethod.Util;
import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WriteStoryActivity extends AppCompatActivity {
    private FlexboxLayout mFlexboxLayout;
    private EditText mStory;
    private final static String LOG_TAG = "WriteStoryActivity";
    private SendStoryThread mSendStoryThread;
    private ImageView mImageView;
    private TextInputLayout mTextInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_story);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFlexboxLayout = (FlexboxLayout) findViewById(R.id.content_write_story_flexBoxLayout);
//        设置flex视图监听事件，实现点击一个图片之后自动生成下一个图标以供你点击
        mFlexboxLayout.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击该视图后我们就把它绑定到全局的Imageview,以便于后面给其添加图片内容。
                mImageView = (ImageView) mFlexboxLayout.getChildAt(0);
                showDialog();
            }
        });
        mTextInputLayout = (TextInputLayout) findViewById(R.id.content_write_story_textInputLayout);

//        mTextInputLayout.setOnClickListener(new View.OnClickListener() {
//            @Override //用TextInputLayout好一点
//            public void onClick(View v) {
//                SendStory();
//                Log.v("kankankn","kkkkkkkk");
//            }
//        });
        mStory = (EditText) findViewById(R.id.content_write_story_et_writestory);
    }

    public void showDialog() {
       AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] items = {"拍照","图库"};
        //设置对话框和监听器
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {//items数组从0开始，如果是0，就是拍照
                   //这是使用高清图，需要设置照片存储路径，如果不设置就只会返回一个照片的缩略图
                  //  intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(getFilesDir().getAbsolutePath()+ File.separator+"abc.jpg")));
                    String status= Environment.getExternalStorageState();
                    if(status.equals(Environment.MEDIA_MOUNTED)) {
                        File dir = new File(Environment.getExternalStorageDirectory() + "/" + "localTempImgDir");
                        if (!dir.exists()) dir.mkdirs();
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//拍照片动作，就是这么写！
                        File f=new File(dir,"localTempImgFileName");//localTempImgDir和localTempImageFileName是自己定义的名字
                        Uri u = Uri.fromFile(f);
                        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
                        // 启动拍照APP
                        startActivityForResult(intent, 1);//发送出去的是1，回调onActivityResult方法里也接受1
                    }

//                    startActivityForResult(intent, 1);//发送出去的是1，回调onActivityResult方法里也接受1
                } else {
                    //图库的图片在扩展存储区域中，因此需要读取权限
                    //首先要检查权限，权限名字是写在manifests里面
                    int permissionCheck = ContextCompat.checkSelfPermission(WriteStoryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {  //如果数字对不上，就是说用户没有授权
                        //那就要向用户申请权限
                        ActivityCompat.requestPermissions(WriteStoryActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                        //申请权限结果会出现在回调onRequestPermissionsResult方法里
                    } else {//用户已经授权，直接跳转获取数据
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 2);
                    }
                }

            }
        });
        builder.create().show();//显示生成对话框
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          //权限被授权，可以执行相关操作，然后就会跳转到图库选择照片
           Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,2);
                //如果用户授权，直接跳到对话框，如果没有授权，显示出拒绝对话框
            } else {
                Toast.makeText(WriteStoryActivity.this, "授权被拒绝", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        float density = getResources().getDisplayMetrics().density;
        int size = (int) (96 * density);
        if (resultCode == RESULT_OK) { //表示正常返回结果
            if (requestCode == 1) {
                //接受从点击拍照动作发出去的1信号,然后使用指定的照片存储路径加载原图
//                String path = getFilesDir().getAbsolutePath()+ File.separator + "abc.jpg";
//                Bitmap bitmap = BitmapUtil.decodeFile(path,size,size);
//                if (mImageView != null){
//                    mImageView.setImageBitmap(bitmap);
//                    Uri uri = Uri.fromFile(new File(path));
//                    mImageView.setTag(uri);
//                    addImageView();

                try { //接下来就是尝试实现控件上绑定拍出的照片的步骤
                    File f = new File(Environment.getExternalStorageDirectory()
                            +"/localTempImgDir"+"/localTempImgFileName");
                    InputStream is = null;
                    Uri u =
                            Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getContentResolver(),
                                    f.getAbsolutePath(), null, null));
//                    ContentResolver contentResolver = getContentResolver();
//                    is = contentResolver.openInputStream(u);
//                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    Bitmap bitmap = BitmapUtil.decodeUri(this,u,size,size);//用uri里面的方法进行压缩图片
                    if (mImageView != null) {//如果控件不为空
                        mImageView.setImageBitmap(bitmap);
                        mImageView.setTag(u);
                        addImageView();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {     //其他情况也就是从图库选择照片的动作
                Bitmap bitmap = BitmapUtil.decodeUri(this,data.getData(),size,size);
                if (mImageView != null){
                    mImageView.setImageBitmap(bitmap);
                    mImageView.setTag(data.getData());
                    addImageView();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void addImageView(){ //添加照片的方法
        if(mFlexboxLayout.getChildCount() == 6)  return; //如果flexbox视图达到六个，就返回
       //创建一个Imageview，这种写法和在布局中申明添加一个是一样的
        final ImageView imageView = new ImageView(WriteStoryActivity.this);
        //设置Imageview默认显示图片
        imageView.setImageResource(R.drawable.ic_add_black_24dp);
                //96dp在不同的手机是包括不同的像素点,获取当前设备上1dp代表多少个像素点
                float density = getResources().getDisplayMetrics().density;
                int size = (int) (96 * density);
                //定义LayoutParams用于定义Imageview的大小，指定高宽为上面计算出的像素数
                FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(size, size);
//              //把Imageview添加到布局中
                mFlexboxLayout.addView(imageView,params);
                //这里也可以写成（imageView,0）
               imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       //点击哪个视图我们就把其交付给全局的Imageview,以便于后面给其添加图片
                        mImageView = imageView;
                        showDialog();
                    }
                });
    }

    public void SendStory() {
        String storyContent = mStory.getText().toString();
        if (TextUtils.isEmpty(storyContent)) {
            mStory.setError("故事内容不能为空");
            mStory.requestFocus();
            return;
        }
        mSendStoryThread = new SendStoryThread(storyContent);//启动线程
        mSendStoryThread.start();
    }


    private class SendStoryThread extends Thread { //创建发送评论的线程
        private String content;

        public SendStoryThread(String content) {    //构造函数传参
            this.content = content;
        }

        @Override
        public void run() {
            InputStream is = null;
            OutputStream os = null;
            try {
                URL url = new URL(ServerUrl.SEND_STORY);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setConnectTimeout(10 * 1000);
                conn.setReadTimeout(10 * 1000);
                conn.setRequestProperty("Content-Type",
                        "multipart/form-data;"
                                + "boundary=----abcd");
                StringBuilder sb = new StringBuilder();
                sb.append("------abcd\r\n");                                        //分隔符
                sb.append("Content-Disposition: form-data; name=\"uid\"\r\n");  //服务器所需数据
                sb.append("\r\n");                                                  //拼字符
                sb.append(Util.userRead(WriteStoryActivity.this).getId());
                sb.append("\r\n");
                sb.append("------abcd\r\n");
                sb.append("Content-Disposition: form-data; name=\"story_info\"\r\n");
                sb.append("\r\n");
                sb.append(content);
                sb.append("\r\n");
                sb.append("------abcd\r\n");
                sb.append("Content-Disposition:form-data; name=\"userpass\"\r\n");
                sb.append("\r\n");
                sb.append(Util.userRead(WriteStoryActivity.this).getUserpass());
                sb.append("\r\n");
                sb.append("------abcd\r\n");
                sb.append("Content-Disposition: form-data; name=\"lng\"\r\n");
                sb.append("\r\n");
                sb.append("0.00");
                sb.append("\r\n");
                sb.append("------abcd\r\n");
                sb.append("Content-Disposition: form-data; name=\"lat\"\r\n");
                sb.append("\r\n");
                sb.append("0.00");
                sb.append("\r\n");
                sb.append("------abcd\r\n");
                sb.append("Content-Disposition: form-data; name=\"city\"\r\n");
                sb.append("\r\n");
                sb.append("汉中");
                sb.append("\r\n");

                os = conn.getOutputStream();//打开输出流
                os.write(new String(sb).getBytes());

                //传输图片文件内容
                int count = mFlexboxLayout.getChildCount();//一共6个框框
                for (int i = 0;i < count; i ++){
                    sb = new StringBuilder();
                    sb.append("------abcd\r\n"); //这里的图片都来自图库
                    sb.append("Content-Disposition: form-data; name=\"photo[]\";filename=\"abc");
                    sb.append(i);   //这里就在abc后面填数字，变成abc1，abc2，abc3.。。。。。
                    sb.append(".jpg\"\r\n");
                    sb.append("Content-Type: image/jpeg\r\n\r\n");
                    os.write(new String(sb).getBytes());
                    Uri uri = (Uri)mFlexboxLayout.getChildAt(i).getTag();//和前面的setTag对应
                    if (uri != null){
                        is = getContentResolver().openInputStream(uri);
                        int len = 0;
                        byte[]buf = new byte[1024];
                        while((len = is.read(buf)) != -1){
                            os.write(buf,0,len);
                        }
                    }
                    os.write("\r\n".getBytes());//换行符！！一定要写换行！！！！！！！！！！！！！！！！
                }
                //输出结束符
                os.write("------abcd".getBytes());//上面已经写了\r\n,这里只写后面部分！！！！！！！！
                os.flush();
                //获取结果
                if (conn.getResponseCode() == 200) {
                    is = conn.getInputStream();//打开输入流
                    int len = 0;
                    byte[] buf = new byte[1024];
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    while ((len = is.read(buf)) != -1) {
                        //等于-1就是没有读的了，不是-1就是说有的读，读多少写多少
                        bos.write(buf, 0, len);
                    }
                    //将读取出来的数据转换字符串
                    String text = new String(bos.toByteArray());
                    Log.v(LOG_TAG, "===>" + text);
                    Message msg = new Message();
                    if (TextUtils.isEmpty(text)) {
                        handler.sendEmptyMessage(0);
                    } else {
                        JSONObject obj = new JSONObject(text);
                        int rs = obj.getInt("result");
                        Log.v(LOG_TAG, obj.getString("msg"));
                        msg.what = rs;
                        //2,创建message对象，将要发送到主线程的消息封装，利用Handler发送到主线程。
                        //arg1.arg2.what,这三个属性可以用于保存整形数据，obj可以用来保存对象
                    }
                    handler.sendMessage(msg); //使用handler的方法把数据发送出去
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                handler.sendEmptyMessage(0);
            } catch (IOException e) {
                e.printStackTrace();
                handler.sendEmptyMessage(0);
            } catch (JSONException e) {
                e.printStackTrace();
                handler.sendEmptyMessage(0);
            } finally {
                try {
                    if (is != null)
                        is.close();
                    if (os != null)
                        os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //任务完成以后不管成功还是失败，都置线程对象为空
                mSendStoryThread = null;//一定要执行的话，就要写在finally里面
            }
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0) {
                Toast.makeText(WriteStoryActivity.this, "故事发送失败", Toast.LENGTH_SHORT).show();
                return true;
            } else if (msg.what == 2) {
                Toast.makeText(WriteStoryActivity.this, "JSON异常", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                Toast.makeText(WriteStoryActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                WriteStoryActivity.this.finish();
                return true;
            }
        }
    });

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sendstory){//触发menu监听器的时间写在这里！
            SendStory();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.wirte_story, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
