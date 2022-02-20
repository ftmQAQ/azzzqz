package com.example.azzzqz;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.azzzqz.Fragment.DynamicFragment;
import com.example.azzzqz.Fragment.FriendFragment;
import com.example.azzzqz.Fragment.MsgFragment;
import com.example.azzzqz.logreg.LoginActivity;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    //viewpager对应的一些对象
    private MyPageAdapter adapter;
    TextView main_title;
    ViewPager viewPager;
    NavigationView navView;
    private int cur=0;
    private Fragment msgFragment, dynamicFragment, friendFragment;
    private RadioGroup dotsLayout;
    private RadioButton[] dots=new RadioButton[3];
    private ArrayList<String> tab_title_list = new ArrayList<>();//存放标签页标题
    private ArrayList<Fragment> fragment_list = new ArrayList<>();//存放ViewPager容器下的Fragment
    private KillBroadcastReceiver killBroadcastReceiver=new KillBroadcastReceiver();
    //加载布局的代码
    CircleImageView tx;
    DrawerLayout drawer;
    ImageView min_main;
    //小菜单控件
    ImageView im_exit;
    TextView minmain_username,minmain_account;
    CircleImageView userimage;
    //spf数据库
    SharedPreferences spf;
    SharedPreferences.Editor editor;
    //个人数据
    String account;
    String portrait;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spf= getSharedPreferences("user", Context.MODE_PRIVATE);;//打开本地存储的spf数据
        account=spf.getString("account","");
        portrait=spf.getString("portrait","test");
        minmain_username= findViewById(R.id.minmain_username);
        minmain_account=findViewById(R.id.minmain_account);
        main_title=findViewById(R.id.main_title);
        minmain_username.setText(spf.getString("username",""));
        minmain_account.setText("蹦蹦号: "+account);
        userimage=findViewById(R.id.userImage);
        im_exit=findViewById(R.id.im_exit);
        tx=findViewById(R.id.test);
        if(portrait.equals("test")){
            userimage.setImageResource(R.drawable.test);
            tx.setImageResource(R.drawable.test);
        }else if(portrait.equals("test2")){
            userimage.setImageResource(R.drawable.test2);
            tx.setImageResource(R.drawable.test2);
        }else if(portrait.equals("test3")){
            userimage.setImageResource(R.drawable.test3);
            tx.setImageResource(R.drawable.test3);
        }
        drawer=findViewById(R.id.drawer);
        //动态注册广播接收器
        IntentFilter intentFilter=new IntentFilter("com.example.azzzqz.Kill");
        registerReceiver(killBroadcastReceiver,intentFilter);
        /**
         * 主菜单
         */
        //点击头像跳出个人信息
        tx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        //小菜单点击事件
        navView=findViewById(R.id.navView);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navself:
                        Intent intent=new Intent(MainActivity.this, UserinfoActivity.class);
                        intent.putExtra("usertype",1);
                        startActivity(intent);
                        break;
                    case R.id.navmoney:
                        break;
                    case R.id.navLocation:
                        break;
                    case R.id.navcollect:
                        break;
                    case R.id.navPhoto:
                        break;
                    case R.id.navset:
                        break;
                }
                drawer.closeDrawers();
                return true;
            }
        });
        viewPager = findViewById(R.id.viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTitle(position);
                setCurDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //点击下拉菜单
        min_main=findViewById(R.id.min_main);
        min_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu=new PopupMenu(MainActivity.this,v);
                MenuInflater menuInflater=popupMenu.getMenuInflater();
                popupMenu.setOnMenuItemClickListener((item) -> {
                    switch (item.getItemId()){
                        case R.id.addfriend:
                            Intent intent=new Intent(MainActivity.this,AddFriendActivity.class);
                            startActivity(intent);
                            break;
                        case R.id.addground:
                            Toast.makeText(MainActivity.this, "功能研发中", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.saoyisao:
                            Toast.makeText(MainActivity.this, "功能研发中", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.exit:
                            AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("提示")
                                    .setMessage("确定要退出吗？")
                                    .setNegativeButton("取消",null)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            editor=spf.edit();
                                            editor.putBoolean("is_login", false);
                                            editor.commit();
                                            Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                            builder.create();
                            builder.show();
                            break;
                    }
                    return false;
                });
                menuInflater.inflate(R.menu.main_popmenu,popupMenu.getMenu());
                popupMenu.show();
            }
        });
        //下标题
        tab_title_list.add("消息");
        tab_title_list.add("好友");
        tab_title_list.add("动态");

        //三个fragment对象
        msgFragment= new MsgFragment();
        friendFragment = new FriendFragment();
        dynamicFragment =  new DynamicFragment();

        //往fragment列表添加内容
        fragment_list.add(msgFragment);
        fragment_list.add(friendFragment);
        fragment_list.add(dynamicFragment);

        adapter = new MyPageAdapter(getSupportFragmentManager(), tab_title_list, fragment_list);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(3);
        dotsLayout=(RadioGroup) findViewById(R.id.dotsRadio);
        for (int i=0;i<dots.length;i++){//分别获取三个Radiobutton
            dots[i]= (RadioButton) dotsLayout.getChildAt(i);
            dots[i].setTag(i);
            dots[i].setEnabled(true);
            dots[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index= (int) v.getTag();
                    viewPager.setCurrentItem(index);
                }
            });
        }
        dots[0].setEnabled(false);//设置默认开启消息界面
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        /**
         * 小菜单事件
         * im_exit 退出按钮
         * minmain_username 用户名textview
         * minmain_account 账号textview
         */
        im_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("提示")
                        .setMessage("确定要退出吗？")
                        .setNegativeButton("取消",null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editor=spf.edit();
                                editor.putBoolean("is_login", false);
                                editor.commit();
                                Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                builder.create();
                builder.show();
            }
        });
    }

    public class KillBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(killBroadcastReceiver);
    }

    public void setCurDot(int index) {
        dots[index].setEnabled(false);
        dots[cur].setEnabled(true);
        cur=index;
    }

    public void setTitle(int index) {
        main_title.setText(tab_title_list.get(index));
    }


}