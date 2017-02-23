package com.example.thinkpaduser.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Space;
import android.widget.Toast;

import com.example.thinkpaduser.myapplication.Fragment.StoryFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private Button   button;
    private LoginThread mLoginThread;
    private final static String LOG_TAG = "LoginActivity";
    //定义静态常量每个字母都要大写

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
//                LoginActivity.this.finish();//清除当前页面，返回到上一个页面
            } else {
                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.activity_login_et_username);
        //View是所有控件的父类，所以父类要转化为子类需要强制转换
        password = (EditText) findViewById(R.id.activity_login_et_password);
        button = (Button) findViewById(R.id.acticity_login_bu_button);

        findViewById(R.id.acticity_login_tv_regist).setOnClickListener(new View.OnClickListener() {
            @Override//匿名内部类跳转页面，
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegistActivity.class);
                startActivity(intent);
                //这个页面跳转写在new View.OnClickListener()这个匿名内部类里面，如果只写this就表示这个匿名内部类的实例
                //而且Activity到最后继承Context，这里是写在Activity里面的，所以也不写context
                }
        });

        button.setOnClickListener(new View.OnClickListener() {
            //登录按钮设置监听器，匿名内部内
            @Override
            public void onClick(View v) {
                if (mLoginThread != null) return;
                login();
            }
        });
    }

    public void login() {
        String name = username.getText().toString();
        String pass = password.getText().toString();
        if (TextUtils.isEmpty(name)) {
            username.setError("用户名不能为空");
            username.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            password.setError("密码不能为空");
            password.requestFocus();
            return;
        }
        Pattern pattern = Pattern.compile("^[a-zA-Z]{1}\\w{5,11}");
        Matcher matcher = pattern.matcher(name);
        if (!matcher.matches()) {
            username.setError("用户名格式不对");
            username.requestFocus();
            return;
        }
        pattern = Pattern.compile("\\w{5,8}");
        matcher = pattern.matcher(pass);
        if (!matcher.matches()) {
            password.setError("密码格式不对");
            password.requestFocus();
            return;
        }
        mLoginThread = new LoginThread(name, pass); //检验之后再启动线程！！！！！！
        mLoginThread.start();
    }

    public class LoginThread extends Thread {
        private String name;
        private String pass;

        public LoginThread(String name, String pass) {
            this.name = name;
            this.pass = pass;
        }

        @Override
        public void run() {
            InputStream is = null;
            OutputStream os = null;
            try {
                URL url = new URL("http://139.129.19.51/story/index.php/home/Interface/login");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setConnectTimeout(10 * 1000);
                conn.setReadTimeout(10 * 1000);
                StringBuilder sb = new StringBuilder();
                sb.append("username=");
                sb.append(name);
                sb.append("&");
                sb.append("password=");
                sb.append(pass);
                os = conn.getOutputStream();//打开输出流
                os.write(new String(sb).getBytes());
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
                    String text = new String (bos.toByteArray());
                    Log.v(LOG_TAG, "===>" + text);
                    if (TextUtils.isEmpty(text)) {
                        handler.sendEmptyMessage(0);
                        //写这句话的意思和Toast.makeText意思一样，这样发送出去的是0，就是登录失败
                    } else {
                        JSONObject obj = new JSONObject(text);
                        int rs = obj.getInt("result");
                        Log.v(LOG_TAG, obj.getString("msg"));
                        //2,创建message对象，利用msg.what=rs将要发送到主线程的消息封装，利用Handler发送到主线程。
                        //Message可以new一个msg对象出来，写这句话和谐上面这句话意思一样，只不过简单
                        // msg可以调用arg1.arg2.what,这三个属性可以用于保存整形数据，obj可以用来保存对象
                        //使用handler的方法把数据发送出去
                        if (rs == 1) {
                            JSONObject data = obj.getJSONObject("data");
                            //使用SharedPreferences存储数据
                            SharedPreferences sp = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                            //获得Editor对象
                            SharedPreferences.Editor editor = sp.edit();
                            //这里是添加到sp里存起来的过程，putString(key,value),key理论上可以随便写
                            //value是下面用getString(服务器的key值，不能改！！)方法再解析之后得到的
                            editor.putString("id", data.getString("id"));
                            editor.putString("username", data.getString("username"));
                            editor.putString("userpass", data.getString("userpass"));
                            editor.putString("usersex", data.getString("usersex"));
//                            editor.putString("usersex", "0");
                            editor.putString("useremail", data.getString("useremail"));
                            editor.putString("nickname", data.getString("nickname"));
                            editor.putString("birthday", data.getString("birthday"));
                            editor.putString("portrait", data.getString("portrait"));
                            editor.putString("signature", data.getString("signature"));
                            editor.commit();//调用commit方法提交
                        }
                        handler.sendEmptyMessage(rs);
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                handler.sendEmptyMessage(0);
                //这个样子的话，程序只要有异常都会发送Message然后Handler接受Toast显示登录失败
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
                mLoginThread = null;//一定要执行的话，就要写在finally里面
            }
        }
    }


}


