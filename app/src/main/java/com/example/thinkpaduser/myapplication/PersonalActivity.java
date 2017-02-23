package com.example.thinkpaduser.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActionMenuView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thinkpaduser.myapplication.StaticMethod.BitmapUtil;
import com.example.thinkpaduser.myapplication.StaticMethod.NetworkUtil;
import com.example.thinkpaduser.myapplication.StaticMethod.ServerUrl;
import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PersonalActivity extends AppCompatActivity {
    private ImageView avatarView;
    private TextView userID;
    private TextView nicknameView;
    private TextView sexView;
    private TextView emailView;
    private TextView birthdayView;
    private Button changepassword;
    private Button quit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        avatarView = (ImageView) findViewById(R.id.activity_personal_iv_avatar);
        userID = (TextView) findViewById(R.id.activity_personal_tv_userid);
        nicknameView = (TextView) findViewById(R.id.activity_personal_tv_nickname);
        sexView = (TextView) findViewById(R.id.activity_personal_tv_sex);
        emailView = (TextView) findViewById(R.id.activity_personal_tv_email);
        birthdayView = (TextView) findViewById(R.id.activity_personal_tv_birthday);
        changepassword = (Button) findViewById(R.id.activity_personal_but_changepassword);
        quit = (Button) findViewById(R.id.activity_personal_but_quit);


        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        nicknameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("要开始修改昵称了","!!!!!");
                Log.v("显示签名","" + nicknameView.getText().toString());
                 nicknameView.getText().toString();
                Intent intent = new Intent(PersonalActivity.this, ChangeNickNameActivity.class);
                startActivity(intent);
            }
        });

        sexView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalActivity.this, ChangeSexActivity.class);
                startActivity(intent);
            }
        });
        emailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalActivity.this, ChangeEmailActivity.class);
                startActivity(intent);
            }
        });
        birthdayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalActivity.this, ChangeBirthdayActivity.class);
                startActivity(intent);
            }
        });
        changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(PersonalActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });
        SharedPreferences sp = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);

        userID.setText("账号：" + sp.getString("username", ""));
//        nicknameView.setText(getString(R.string.nickname,sp.getString("nickname","")));
        nicknameView.setText(String.format(getResources().getString(R.string.nickname), sp.getString("nickname", "")));
        //占位符写法
//        String age = getResources().getString(R.string.sex);
//        String sex = String.format(age,"男");
        sexView.setText(String.format(getResources().getString(R.string.sex), changeSex()));
        emailView.setText("邮箱：" + sp.getString("useremail", ""));
        birthdayView.setText("生日：" + sp.getString("birthday", ""));
//        avatarView.setImageBitmap(sp.getString("portrait",""));
        Log.v("服务器里上传的头像", sp.getString("portrait", ""));

        if (sp.getString("portrait", "").equals("null")) {//这里面只能用这个方法，因为服务器返回回来的是一个"null"字符串
            Log.v("检查头像是不是为空,空了执行下一步", sp.getString("portrait", ""));
            avatarView.setImageResource(R.drawable.touxiang);
        } else {
            Log.v("头像检查不出来？", "???");
        }
        String url = ServerUrl.GET_PORTRAIT + sp.getString("portrait", "");
        new GetPortraitThread(url).start();
//        avatarView.setImageBitmap(new GetPortraitThread(url).start());
    }

    @Override
    protected void onStart() {  //这个方法里写的就是改了某个信息之后跳回到个人信息页面就应该改掉该信息
        super.onStart();
        SharedPreferences sp = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);

        userID.setText("账号：" + sp.getString("username", ""));
//        nicknameView.setText(getString(R.string.nickname,sp.getString("nickname","")));
        nicknameView.setText(String.format(getResources().getString(R.string.nickname), sp.getString("nickname", "")));
        //占位符写法
//        String age = getResources().getString(R.string.sex);
//        String sex = String.format(age,"男");
        sexView.setText(String.format(getResources().getString(R.string.sex), changeSex()));
        emailView.setText("邮箱：" + sp.getString("useremail", ""));
        birthdayView.setText("生日：" + sp.getString("birthday", ""));
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] items = {"拍照", "图库"};
        //设置对话框和监听器
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//拍照片动作，就是这么写！
                    //这是使用高清图，需要设置照片存储路径，如果不设置就只会返回一个照片的缩略图
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(getFilesDir().getAbsolutePath() + File.separator + "abc.jpg")));
                    startActivityForResult(intent, 1);//发送出去的是1，回调onActivityResult方法里也接受1
                } else {
                    Log.v("要开始传图片了","开始了");
                    //图库的图片在扩展存储区域中，因此需要读取权限，下面是检查权限
                    int permissionCheck = ContextCompat.checkSelfPermission(PersonalActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(PersonalActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
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
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
            } else {
                Toast.makeText(PersonalActivity.this, "授权被拒绝", Toast.LENGTH_SHORT).show();
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
                String path = getFilesDir().getAbsolutePath() + File.separator + "abc.jpg";
                Bitmap bitmap = BitmapUtil.decodeFile(path, size, size);
                if (avatarView != null) {
                    avatarView.setImageBitmap(bitmap);
                    Uri uri = Uri.fromFile(new File(path));
                    avatarView.setTag(uri);
                }
            } else {     //其他情况也就是从图库选择照片的动作，这里是为了修改头像
                Bitmap bitmap = BitmapUtil.decodeUri(this, data.getData(), size, size);
                if (avatarView != null) {
                    avatarView.setImageBitmap(bitmap);
                    avatarView.setTag(data.getData());
                    new ChangePortraitThread(bitmap).start();//修改头像完成后启动修改头像的线程开始上传参数
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String changeSex() {
        SharedPreferences sp = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        if (sp.getString("usersex", "").equals("0")) {
            return "男";
        } else if (sp.getString("usersex", "").equals("1")) {
            return "女";
        } else {
            return null;
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0) {
                Toast.makeText(PersonalActivity.this, "头像修改失败", Toast.LENGTH_SHORT).show();
                return true;
            } else if (msg.what == 2) {
                Toast.makeText(PersonalActivity.this, "头像显示成功", Toast.LENGTH_SHORT).show();
                Bitmap portrait = (Bitmap) msg.obj;
                avatarView.setImageBitmap(portrait); //这里是为了显示你注册时上传的头像
                return true;
            } else if (msg.what == 3){
                Toast.makeText(PersonalActivity.this, "头像修改成功", Toast.LENGTH_SHORT).show();
                //想一想修改完之后怎么上传修改后头像？？？？？？
//                SharedPreferences sp = getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sp.edit();
//                editor.putString("portrait", data.getString("portrait"));

                return true;
            }else return true;
        }
    });

    private class GetPortraitThread extends Thread { //这是为了显示注册时头像的线程
        private String url;
        public GetPortraitThread(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            Bitmap bitmap = getPortarit(url);
            Message msg = new Message();
            msg.obj = bitmap;//用message来封装对象和数字2
            msg.what = 2;
            handler.sendMessage(msg);
        }
    }

    private Bitmap getPortarit(String fileurl) {//这是以流的形式下载注册时上传头像的方法
        InputStream is = null;
        try {
            URL url = new URL(fileurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            is = conn.getInputStream();
            return BitmapFactory.decodeStream(is);//这里返回的就是一个一个bitmap形式的图
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private class ChangePortraitThread extends Thread {//修改头像线程
        private Bitmap bitmap;
        public ChangePortraitThread(Bitmap bitmap) {//构造方法传参
            this.bitmap = bitmap;
        }
        SharedPreferences sp = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        @Override
        public void run() {
            InputStream is = null;
            OutputStream os = null;
            try {
                URL url = new URL(ServerUrl.CHANGE_PORTRAIT);
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
                sb.append("Content-Disposition: form-data; name=\"uid\"\r\n");
                sb.append("\r\n");
                sb.append(sp.getString("id", ""));
                sb.append("\r\n");
                sb.append("------abcd\r\n");
                sb.append("Content-Disposition: form-data; name=\"userpass\"\r\n");
                sb.append("\r\n");
                sb.append(sp.getString("userpass", ""));
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
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);//数字越大精度越高
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
                    Log.v("打印text",""+text);
                    if (TextUtils.isEmpty(text)) {
                        handler.sendEmptyMessage(0);
                        return;
                    }
                        JSONObject obj = new JSONObject(text);
                        Log.v("打印msg",obj.getString("msg"));
                        Log.v("打印result","" + obj.getInt("result"));
                        Log.v("打印data信息",obj.getString("data"));
                    SharedPreferences sp = getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("portrait", obj.getString("data"));
                    editor.commit();
                    handler.sendEmptyMessage(3);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null)
                        is.close();
                    if (os != null)
                        os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_return) {//触发menu监听器的时间写在这里！
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.personal_information, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
