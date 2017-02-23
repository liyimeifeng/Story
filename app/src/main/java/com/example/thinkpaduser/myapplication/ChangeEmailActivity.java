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
import android.widget.Toast;

import com.example.thinkpaduser.myapplication.R;
import com.example.thinkpaduser.myapplication.StaticMethod.NetworkUtil;
import com.example.thinkpaduser.myapplication.StaticMethod.ServerUrl;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ChangeEmailActivity extends AppCompatActivity {
    private EditText emailView;
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        emailView = (EditText)findViewById(R.id.activity_change_email_et_email);
        button = (Button)findViewById(R.id.activity_change_email_but_finish);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEmail();
            }
        });
    }

    private void changeEmail(){
        String email = emailView.getText().toString();
        if (TextUtils.isEmpty(email)){
            emailView.setError("请输入正确的邮箱");
            emailView.requestFocus();
            return;
        }
        //一个判断邮箱格式的正则表达式
        new ChangeEmailThread().start();
    }
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0){
                Toast.makeText(ChangeEmailActivity.this,"修改邮箱失败",Toast.LENGTH_SHORT).show();
                return true;
            }else{
                Toast.makeText(ChangeEmailActivity.this,"修改邮箱成功",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ChangeEmailActivity.this,PersonalActivity.class);
                startActivity(intent);
                return true;
            }
        }
    });
    private class ChangeEmailThread extends Thread{
        @Override
        public void run() {
            Map<String,String> parmas = new HashMap<>();
            SharedPreferences sp = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            parmas.put("uid",sp.getString("id",""));
            parmas.put("userpass",sp.getString("userpass",""));
            Log.v("输入的userpass",sp.getString("userpass",""));
            parmas.put("useremail",emailView.getText().toString());
            Log.v("输入的邮箱",emailView.getText().toString());
            String text = NetworkUtil.sendPostRequest(ServerUrl.CHANGE_EMAIL,parmas);
            Log.v("看这里",text);
            if (TextUtils.isEmpty(text)){
                handler.sendEmptyMessage(0);
                return;
            }
            try {
                JSONObject object = new JSONObject(text);
                Log.v("看这里msg",object.getString("msg"));
                if (object.getInt("result") == 1){
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("useremail",emailView.getText().toString());
                    editor.commit();
                    handler.sendEmptyMessage(1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
