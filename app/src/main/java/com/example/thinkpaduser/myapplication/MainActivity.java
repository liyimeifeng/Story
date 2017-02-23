package com.example.thinkpaduser.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thinkpaduser.myapplication.Adapter.FragmentAdapter;

import com.example.thinkpaduser.myapplication.Fragment.StoryFragment;
import com.example.thinkpaduser.myapplication.StaticMethod.ServerUrl;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //找到对象
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //创建对象
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        //同步状态
        toggle.syncState();
        //找到Nav
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        //设置其菜单项监听事件
        navigationView.setNavigationItemSelectedListener(this);
        //找到tablayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        //找到viewpager
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        //创建适配器对象
       FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        //viewpager设定适配器
        viewPager.setAdapter(fragmentAdapter);
        //把tablayout和viewpager绑定
       tabLayout.setupWithViewPager(viewPager);

        View headLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        SharedPreferences sp = getSharedPreferences("UserInfo",Context.MODE_PRIVATE);
        TextView nameView = (TextView)headLayout.findViewById(R.id.nav_header_main_name);
        SimpleDraweeView portraitView = (SimpleDraweeView)headLayout.findViewById(R.id.nav_header_main_portrait);
        portraitView.setImageURI(ServerUrl.GET_PORTRAIT + sp.getString("portrait",""));
        nameView.setText(sp.getString("nickname",""));

        //这里下面就是舍弃的方法了
//        tabLayout.addTab(tabLayout.newTab().setText("最新"));
//        tabLayout.addTab(tabLayout.newTab().setText("最热"));
//        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//               int position = tab.ge(pisition);
//                Fragment fragment = null;
//                if (position == 0){
//                    fragment = new StoryFragment();
//                }else if(position == 1){
//                    fragment = new BlankFragment();
//                }
//                //将fragmengt添加到容器中
//                //1.获得FragmentManager对象//显示Fragment，一下都是套路
//                FragmentManager fm = getSupportFragmentManager();
//                //2.启用事务
//                FragmentTransaction ft = fm.beginTransaction();
//                //3.调用方法
//                ft.replace(R.id.container,fragment);
//                //4.提交事务
//                ft.commit();
//            }
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//            }
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //创建菜单选项
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //菜单事件监听，点击菜单按钮后会调用该方法，本来应该设置按钮监听器来实现页面跳转，
        // 但是这个方法可以自动设置监听器，所以我们不用再写监听器
        int id = item.getItemId();
        if (id == R.id.action_creatstory) {//点击了创建新故事的页面，就相当于创建了按钮监听
            Toast.makeText(this,"发表你的故事",Toast.LENGTH_SHORT).show();//更新UI
            //发表新的故事首先需要登录，先要判断用户是否登录
            SharedPreferences sp = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            String uid = sp.getString("id","");
            if(TextUtils.isEmpty(uid)){ //如果检测到用户名为空，则跳转到登录页面
                Toast.makeText(this,"你还没登录，请先登录！",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this,LoginActivity.class);//跳转到登陆页面
                //这个是写在Activity自带的方法里面，自然不用创建context对象，并且也不存在内部类这个东西
                //所以就可以简写，因为this就表示当前的MainActivity.this
                startActivity(intent);
            }else{
                Intent intent = new Intent(this,WriteStoryActivity.class);
                startActivity(intent);
            }
          //从故事主页点击上方的按钮跳转到写故事页面
//            findViewById(R.id.action_creatstory).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(MainActivity.this,WriteStoryActivity.class);
//                    startActivity(intent);
//                }
//            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        //g根据ID确定选择哪一项执行功能
        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_my_story) {
            Intent intent = new Intent(this,MyStoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_personalInfo) {
            Intent intent = new Intent(this,PersonalActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
