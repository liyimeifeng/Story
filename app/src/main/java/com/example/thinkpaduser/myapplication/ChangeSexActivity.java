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
import android.widget.TextView;
import android.widget.Toast;

import com.example.thinkpaduser.myapplication.StaticMethod.NetworkUtil;
import com.example.thinkpaduser.myapplication.StaticMethod.ServerUrl;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class ChangeSexActivity extends AppCompatActivity {
    private TextView maleView;
    private TextView femaleView;
    private Button button;
    private String sex;
//    private final static String male = "0";
//    private final static String female = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_sex);

        maleView = (TextView)findViewById(R.id.activity_change__sex_tv_male);
        femaleView = (TextView)findViewById(R.id.activity_change_sex_tv_female);
        button = (Button)findViewById(R.id.activity_change_sex_but_finish);

        Log.v("看这里",maleView.getText().toString());
        Log.v("看这里",femaleView.getText().toString());
        maleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               sex =  maleView.getText().toString();
                sendSex();
//                Log.v("点击性别",sex);
            }
        });

        femaleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               sex = femaleView.getText().toString();
                sendSex();
//                Log.v("点击性别",sex);
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSex();
            }
        });
    }

    private void changeSex(){
        SharedPreferences sp = getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        if (sendSex().equals(sp.getString("usersex",""))){
            Toast.makeText(ChangeSexActivity.this,"性别没有修改，请重新选择",Toast.LENGTH_SHORT).show();
//            maleView.setError("性别没有修改，请重新选择");
//            maleView.requestFocus();
            return;
        }
        new ChangeSexThread().start();
    }

    private String sendSex(){
        if (sex.equals("男")){
            return "0";
        }else if (sex.equals("女")){
            return "1";
        }else{
            return null;
        }
    }
    private void a(){

    }
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0){
                Toast.makeText(ChangeSexActivity.this,"修改性别失败",Toast.LENGTH_SHORT).show();
                return true;
            }else{
                Toast.makeText(ChangeSexActivity.this,"修改性别成功",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ChangeSexActivity.this,PersonalActivity.class);
                startActivity(intent);
                return true;
            }
        }
    });
    private class ChangeSexThread extends Thread{
        @Override
        public void run() {
            Map<String,String> parmas = new HashMap<>();
            SharedPreferences sp = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            Log.v("服务器里性别",sp.getString("usersex",""));
            parmas.put("uid",sp.getString("id",""));
            Log.v("id",sp.getString("id",""));
            parmas.put("userpass",sp.getString("userpass",""));
            Log.v("password",sp.getString("userpass",""));
            parmas.put("usersex",sendSex());
            Log.v("修改后的性别",sendSex());
            String text = NetworkUtil.sendPostRequest(ServerUrl.CHANGE_SEX,parmas);
            Log.v("服务器信息",text);
            if (TextUtils.isEmpty(text)){
                handler.sendEmptyMessage(0);
                return;
            }
            try {
                JSONObject object = new JSONObject(text);
                Log.v("msg信息", object.getString("msg"));
                if (object.getInt("result") == 1){
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("usersex",sendSex());
                    editor.commit();
                    handler.sendEmptyMessage(1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
