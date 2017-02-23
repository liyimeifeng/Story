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
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class ChangeBirthdayActivity extends AppCompatActivity {
    private EditText birthdayView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_birthday);

        birthdayView = (EditText)findViewById(R.id.activity_change_birthday_et_birthday);
        button = (Button)findViewById(R.id.activity_change_birthday_but_finish);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        changeBirthday();
            }
        });
    }
    private void changeBirthday(){
        String birthday = birthdayView.getText().toString();
        new ChangeBirthdayThread().start();
    }
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0){
                Toast.makeText(ChangeBirthdayActivity.this,"修改生日失败",Toast.LENGTH_SHORT).show();
                return true;
            }else{
                Toast.makeText(ChangeBirthdayActivity.this,"修改生日成功",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ChangeBirthdayActivity.this,PersonalActivity.class);
                startActivity(intent);
                return true;
            }
        }
    });

    private class ChangeBirthdayThread extends  Thread{
        @Override
        public void run() {
            Map<String,String> parmas = new HashMap<>();
            SharedPreferences sp = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            parmas.put("uid",sp.getString("id",""));
            parmas.put("userpass",sp.getString("userpass",""));
            parmas.put("birthday",birthdayView.getText().toString());
            String text = NetworkUtil.sendPostRequest(ServerUrl.CHANGE_BIRTHDAY,parmas);
            Log.v("看这个服务器记录",text);
            if (TextUtils.isEmpty(text)){
                handler.sendEmptyMessage(0);
                return;
            }
            try {
                JSONObject object = new JSONObject(text);
                Log.v("看这里msg",object.getString("msg"));
                if (object.getInt("result") == 1){
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("birthday",birthdayView.getText().toString());
                    editor.commit();
                    handler.sendEmptyMessage(1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
