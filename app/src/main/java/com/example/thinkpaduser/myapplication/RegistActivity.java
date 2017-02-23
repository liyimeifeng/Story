package com.example.thinkpaduser.myapplication;


import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContentResolverCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ThinkPad User on 2016/7/6.
 */
public class RegistActivity extends AppCompatActivity {
    private EditText username;
    private EditText nickname;
    private EditText password;
    private Button registbutton;
    private RegistThread mRegistThread;
    private final static String LOG_TAG = "RegistActivity";
    private ImageView mImageView;
    private Bitmap bitmap;

    //1,在主线程里面声明一个Handler
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            //消息发送出去后，该方法会被调用，也就是handler的sendMessage（）方法发送出去的消息都会发送到这里
            //该方法的Message对象就是我们发送出去的消息的Message对象
            //该消息是从子线程发送过来
            int result = msg.what;
            if (result == 1) {
                Toast.makeText(RegistActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegistActivity.this,LoginActivity.class);
                startActivity(intent);
//                RegistActivity.this.finish();
            } else {
                Toast.makeText(RegistActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        username = (EditText) findViewById(R.id.activity_regist_et_username);
        nickname = (EditText) findViewById(R.id.activity_regist_et_nickname);
        password = (EditText) findViewById(R.id.activity_regist_et_password);
        registbutton = (Button) findViewById(R.id.activity_regist_but_regist);

        registbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRegistThread != null) return;
                regist();
            }

        });

        mImageView = (ImageView)findViewById(R.id.activity_regist_iv_avatar);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private final static int REQUEST_CODE_CAPTURE = 0;
    private final static int REQUEST_CODE_GALLERY = 1;
    public void showDialog(){
        //1、创建一个AlertDialog.Budlier，Builder就是所谓的建造者模式，不需要中间过程
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //2、根据需要添加内容
        String[] items = new String[]{"拍照","图库"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){ //下面是设置拍照存储照片过程
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //设置照片存储路径，如果不设置，则只会返回一个照片的缩略图。
//                  intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(getFilesDir().getAbsolutePath()+ File.separator+"abc.jpg")));
                    //getFilesDir后面一大串就表示设置照片的绝对路径。
                    //启动Activity并获得一个结果
                    startActivityForResult(intent,REQUEST_CODE_CAPTURE);
                    //写到这里就会回调下面的onActivityResult方法,前提是你要用的就是这种方法。
                }else{//下面是从图库选择图片
                    //检查权限
                    int permissionCheck = ContextCompat.checkSelfPermission(RegistActivity.this,
                           Manifest.permission.READ_EXTERNAL_STORAGE);
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED){//如果码数不等于，说明用户没有授权
                       //申请权限
                        ActivityCompat.requestPermissions(RegistActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
                    //这里之后申请权限的结果会出现在onRequestPermissionsResult回调方法
                    }else{
                        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent,REQUEST_CODE_GALLERY);
                    }
                }
            }
        });
        builder.setCancelable(true);
        //设置成true就是点击页面其他部分都能取消这个对话框，false不能
        //3、显示对话框
        builder.create().show();
//       builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//           @Override
//           public void onClick(DialogInterface dialog, int which) {
//
//           }
//       });
//        builder.setNegativeButton("取消",null);//消极按钮就不逍遥设置监听器!!!!!
        //这两个写法是显示对话框右下角的“确定”“取消”按钮,这里可以不写，这里只是示范。
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 0){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //上面这句话表示权限被授权，可以执行相关操作，然后跳转到下面的图库选择图片
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,REQUEST_CODE_GALLERY);
            }else{   //申请被拒绝的情况
                Toast.makeText(RegistActivity.this,"授权被拒绝",Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){//ok表示正常返回结果
        if(requestCode == REQUEST_CODE_CAPTURE){
                //如果想要获得缩略图
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap)bundle.get("data");
            mImageView.setImageBitmap(bitmap);
                //如果想要获得原图
                //使用拍照时指定的照片存储路径加载原图
//                String path = getFilesDir().getAbsolutePath()+ File.separator + "abc.jpg";
//                bitmap = BitmapFactory.decodeFile(path);
            }else{//获得从图库选择的图片来源
           Uri uri = data.getData();
            //通过指定的uri加载图片
            InputStream is = null;
            try {
                //1、通过getContentResolver()获得ContentResolver对象
              ContentResolver cr = getContentResolver();
                //2、调用其openInputStream的方法打开指定的输入流
                is = cr.openInputStream(uri);
                //3、通过输入流入加载bitmap
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                mImageView.setImageBitmap(bitmap);//设置图片绑定到控件上
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }finally {
                try { if (is != null)
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void regist() {
        String uname = username.getText().toString();
        String nname = nickname.getText().toString();
        String pass = password.getText().toString();
        if (TextUtils.isEmpty(uname)) {
            username.setError("用户名不能为空");
            username.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(nname)) {
            nickname.setError("昵称不能为空");
            nickname.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            password.setError("密码不能为空");
            password.requestFocus();
            return;
        }

        Pattern pattern = Pattern.compile("^[a-zA-Z]{1}\\w{5,11}");//第一个字必须是字母不论大小写，后面有5到11位数字
        Matcher matcher = pattern.matcher(uname);
        if (!matcher.matches()) {
            username.setError("用户名格式不对");
            username.requestFocus();
            return;
        }
        pattern = Pattern.compile("^[a-zA-Z]{3,11}");//字母5到11位
        matcher = pattern.matcher(nname);
        if (!matcher.matches()) {
            nickname.setError("昵称格式不对");
            nickname.requestFocus();
            return;
        }
        pattern = Pattern.compile("\\w{5,8}");//数字5到8位
        matcher = pattern.matcher(pass);
        if (!matcher.matches()) {
            password.setError("密码格式不对");
            password.requestFocus();
            return;
        }
        //bitmap缩略图只能经过下列转化先转化成BitmapDrawable再转化成Bitmap
        BitmapDrawable bitmapDrawable = (BitmapDrawable) mImageView.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        Log.v("看控件上的图片",bitmap.toString());
        mRegistThread = new RegistThread(uname, nname, pass, bitmap);
        mRegistThread.start();
    }

    private class RegistThread extends Thread {
        private String uname;
        private String nname;
        private String pass;
        private Bitmap bitmap; //Bitmap形式的图片，也就是缩略图形式的图片

        public RegistThread(String uname, String nname, String pass,Bitmap bitmap) {
            this.uname = uname;
            this.nname = nname;
            this.pass = pass;
            this.bitmap = bitmap;
        }

        @Override
        public void run() {
            InputStream is = null;
            OutputStream os = null;
            try {
                URL url = new URL("http://139.129.19.51/story/index.php/home/Interface/regist");
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
                sb.append("------abcd\r\n");
                sb.append("Content-Disposition: form-data; name=\"nikename\"\r\n");
                sb.append("\r\n");
                sb.append(nname);
                sb.append("\r\n");
                sb.append("------abcd\r\n");
                sb.append("Content-Disposition: form-data; name=\"username\"\r\n");
                sb.append("\r\n");
                sb.append(uname);
                sb.append("\r\n");
                sb.append("------abcd\r\n");
                sb.append("Content-Disposition:form-data; name=\"password\"\r\n");
                sb.append("\r\n");
                sb.append(pass);
                sb.append("\r\n");
                sb.append("------abcd\r\n");
                sb.append("Content-Disposition: form-data; name=\"portrait\";filename=\"abc.jpg\"\r\n");
                //服务器上要上传的参数有问题，要加上这一句话filename="abc.jpg
                sb.append("Content-Type: image/jpeg\r\n\r\n");
                //打开输出流
                System.out.println(new String(sb));
                os = conn.getOutputStream();//打开输出流
                os.write(new String(sb).getBytes());
                //输出文件数据图片，
                // 把图片转化成byte数组并使用输出流输出
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,os);//数字越大精度越高
                os.flush();
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
//                byte[] data = bos.toByteArray();
                //输出结束符
                os.write("\r\n------abcd".getBytes());
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
                mRegistThread = null;//一定要执行的话，就要写在finally里面
            }
        }
    }
}
