package com.example.azzzqz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.azzzqz.Adapter.AddFriendAdapter;
import com.example.azzzqz.Javabean.User;
import com.example.azzzqz.Task.AddFriendTask;

import java.util.ArrayList;

public class AddFriendActivity extends AppCompatActivity {
    //导入控件
    private ListView lv_addfriend;
    private ImageView im_search,im_return;
    private EditText et_search;
    private Boolean isLoading=true;
    //请求访问地址
    private String url="http://friends.ftmqaq.cn/search.php?";
    //搜索框账号
    private String search_account;
    //创建构造器
    AddFriendAdapter adapter;
    User userback=new User();
    ArrayList<User> list=new ArrayList<>();
    //创建spf获取当前用户
    SharedPreferences spf;
    private String account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        spf= getSharedPreferences("user",Context.MODE_PRIVATE);//打开本地存储的spf数据
        account=spf.getString("account","");
        im_return=findViewById(R.id.im_return);
        im_search=findViewById(R.id.im_search);
        et_search=findViewById(R.id.et_search);
        im_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        im_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_account=et_search.getText().toString();
                if(!search_account.isEmpty()){
                    hideKeyboard(v.getWindowToken());
                    loadData();
                    adapter=new AddFriendAdapter(AddFriendActivity.this,R.layout.addfriend, list);
                    lv_addfriend=findViewById(R.id.lv_addfriends);
                    lv_addfriend.setAdapter(adapter);
                    list.clear();
                }else{
                    Toast.makeText(AddFriendActivity.this, "请输入用户蹦蹦号", Toast.LENGTH_SHORT).show();
                }
                
            }
        });
    }

    //隐藏软键盘并让editText失去焦点
    private void hideKeyboard(IBinder token) {
        et_search.clearFocus();
        if (token != null) {
            //这里先获取InputMethodManager再调用他的方法来关闭软键盘
            //InputMethodManager就是一个管理窗口输入的manager
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (im != null) {
                im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private void loadData() {//执行异步任务，返回搜索id成功后的user数据，
        if(isLoading){
            isLoading=false;
            //执行异步任务，加载数据
            new AddFriendTask(new AddFriendTask.CallBack(){
                @Override
                public void getResult(User result) {
                    if(result.getResult()!=0){
                        list.add(result);
                        adapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(AddFriendActivity.this, "没有这样子的人喔", Toast.LENGTH_SHORT).show();
                    }
                }
            }).execute(url+"account="+account+"&fri_account="+search_account);
            isLoading=true;
        }else{

        }
    }
}