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
import android.widget.TextView;
import android.widget.Toast;

import com.example.thinkpaduser.myapplication.StaticMethod.NetworkUtil;
import com.example.thinkpaduser.myapplication.StaticMethod.ServerUrl;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangeNickNameActivity extends AppCompatActivity {
    private EditText nameView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_nick_name);

        nameView = (EditText) findViewById(R.id.activity_change_nick_name_tv_name);
        button = (Button)findViewById(R.id.activity_change_nick_name_but_finish);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUsername();
            }
        });
//        SharedPreferences sp = getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
//        nameView.setText(getString(R.string.name,sp.getString("nickname","")));
    }

    public void changeUsername(){
        String name = nameView.getText().toString();
        if (TextUtils.isEmpty(name)){
            nameView.setError("昵称不能为空");
            nameView.requestFocus();
            return;
        }
        Pattern pattern = Pattern.compile("^[a-zA-Z]{3,8}");
        Matcher matcher = pattern.matcher(name);
        if (!matcher.matches()) {
            nameView.setError("昵称格式不对");
            nameView.requestFocus();
            return;
        }
        SharedPreferences sp = getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        Log.v("看这里修改过的名字",name);
        Log.v("看这里服务器你的名字",sp.getString("nickname",""));
        if (nameView.getText().toString().equals(sp.getString("nickname",""))){
            nameView.setError("昵称没有修改，请重试");
            nameView.requestFocus();
            return;
        }
        new ChangeNicknameThread().start();
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0){
                Toast.makeText(ChangeNickNameActivity.this,"昵称修改失败",Toast.LENGTH_SHORT).show();
                return true;
            }else{
                Toast.makeText(ChangeNickNameActivity.this,"昵称修改成功",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ChangeNickNameActivity.this,PersonalActivity.class);
                startActivity(intent);
                return true;
            }
        }
    });
    private class ChangeNicknameThread extends Thread{
        @Override
        public void run() {
            Map<String,String> parmas = new HashMap<>();
            SharedPreferences sp = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            parmas.put("uid",sp.getString("id",""));
            parmas.put("userpass",sp.getString("userpass",""));
            parmas.put("nickname",nameView.getText().toString()); //上传的昵称就应该是修改过的昵称！
            String text = NetworkUtil.sendPostRequest(ServerUrl.CHANGE_NICKNAME,parmas);
            Log.v("看这里",text);
            if (TextUtils.isEmpty(text)){
                handler.sendEmptyMessage(0);
                return;
            }
            try {
                JSONObject obj = new JSONObject(text);
                Log.v("看这里msg",obj.getString("msg"));
                if (obj.getInt("result") == 1){
//                    JSONObject data = obj.getJSONObject("data");
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("nickname",nameView.getText().toString());
                    editor.commit();
                    handler.sendEmptyMessage(obj.getInt("result"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}
