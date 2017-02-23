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

import com.example.thinkpaduser.myapplication.StaticMethod.NetworkUtil;
import com.example.thinkpaduser.myapplication.StaticMethod.ServerUrl;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText oldPassView;
    private EditText newPassView;
    private EditText reWritepassView;
    private Button button;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        oldPassView = (EditText)findViewById(R.id.activity_change_userpass_et_oldpass);
        newPassView = (EditText)findViewById(R.id.activity_change_userpass_et_newpass);
        reWritepassView = (EditText)findViewById(R.id.activity_change_userpass_et_rewritenewpass);
        button = (Button)findViewById(R.id.activity_change_userpass_but_finish);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  changeUserpass();
            }
        });

    }
    private void changeUserpass(){
        String newPass = newPassView.getText().toString();
        String oldPass = oldPassView.getText().toString();
        String reWritePass = reWritepassView.getText().toString();
        SharedPreferences sp = getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        Log.v("原始密码",sp.getString("password",""));
        Log.v("现在输入的密码",oldPass);
//        if (!oldPass.equals(sp.getString("userpass",""))){
//            oldPassView.setError("输入密码错误，请重新输入");
//            oldPassView.requestFocus();
//            return;
//        }
        if (newPass.equals(oldPass)){
            newPassView.setError("修改的密码和原密码一样，请重新输入");
            newPassView.requestFocus();
            return;
        }
         Pattern pattern = Pattern.compile("\\w{5,8}");
         Matcher matcher = pattern.matcher(newPass);
        if (!matcher.matches()) {
            newPassView.setError("密码格式不对，请重新输入");
            newPassView.requestFocus();
            return;
        }
        if (!newPass.equals(reWritePass)){
            reWritepassView.setError("请保持和修改后的密码一样");
            reWritepassView.requestFocus();
            return;
        }
        new ChangeUserpassThread().start();
    }
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0){
                Toast.makeText(ChangePasswordActivity.this,"修改密码失败",Toast.LENGTH_SHORT).show();
                return true;
            }else{
                Toast.makeText(ChangePasswordActivity.this,"修改密码成功",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ChangePasswordActivity.this,PersonalActivity.class);
                startActivity(intent);
                return true;
            }
        }
    });
    private class ChangeUserpassThread extends Thread{
        @Override
        public void run() {
            Map<String,String> parmas = new HashMap<>();
            SharedPreferences sp = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            parmas.put("uid",sp.getString("id",""));
            parmas.put("oldpass",oldPassView.getText().toString());
            parmas.put("newpass",newPassView.getText().toString());
            String text = NetworkUtil.sendPostRequest(ServerUrl.CHANGE_PASSWORD,parmas);
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
                    editor.putString("password",newPassView.getText().toString());
                    editor.commit();
                    handler.sendEmptyMessage(1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
