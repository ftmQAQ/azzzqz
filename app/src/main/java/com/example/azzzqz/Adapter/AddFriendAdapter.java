package com.example.azzzqz.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.azzzqz.R;
import com.example.azzzqz.javabean.User;
import com.example.azzzqz.task.AddFriendRequestTask;
import com.example.azzzqz.task.AddFriendTask;
import com.example.azzzqz.task.ImageTask;

import java.util.ArrayList;
import java.util.List;

public class AddFriendAdapter extends ArrayAdapter {
    private int item_layout_id;
    private String url="http://friends.ftmqaq.cn/addfriend.php?"; //添加好友的url
    private Boolean isLoading=true;
    private String account,fri_account;
    private int isrequest=0;
    //利用spf获取当前登录用户值
    SharedPreferences spf;
    public AddFriendAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        item_layout_id=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        ViewHolder holder;
        if(convertView==null){
            //getContext()获取当前上下文
            //LayoutInflater.from(getContext())从当前上下文中获取布局填充器
            //把列表项布局填充到要显示的列表项中成为一个视图
            view= LayoutInflater.from(getContext())
                    .inflate(item_layout_id,parent,false);
            holder=new ViewHolder(view);
            view.setTag(holder);
        }else {
            view=convertView;
            holder=(ViewHolder) view.getTag();
        }
        spf= PreferenceManager.getDefaultSharedPreferences(getContext());//打开本地存储的spf数据
        account=spf.getString("account","");
        User user=(User)getItem(position);
        if(user.getResult()==2){//判断返回值，如果是2就是已经添加了好友
            holder.bt_addfriend.setEnabled(false);
            holder.bt_addfriend.setText("已添加");
        }
        if(user.getResult()==3){//判断返回值，如果是3就是已经申请了好友
            holder.bt_addfriend.setEnabled(false);
            holder.bt_addfriend.setText("申请中");
        }
        holder.friend_name.setText(user.getUsername());
        fri_account=String.valueOf(user.getAccount());
        holder.friend_account.setText("蹦蹦号："+String.valueOf(user.getAccount()));
        holder.bt_addfriend.setOnClickListener(new View.OnClickListener() {//给按钮添加添加好友事件
            @Override
            public void onClick(View v) {
                holder.bt_addfriend.setEnabled(false);
                holder.bt_addfriend.setText("申请中");
                loadData();
            }
        });
        //给图片控件设置要显示的图片
        String image_url=user.getPortrait_img();
        if(TextUtils.isEmpty(image_url)){
        }else{
            new ImageTask(new ImageTask.CallBack(){
                @Override
                public void getResult(Bitmap result){
                    holder.iv_image.setImageBitmap(result);
                }
            }).execute(image_url);
        }
        return view;
    }
    public class ViewHolder{
        TextView friend_name;
        TextView friend_account;
        ImageView iv_image;
        Button bt_addfriend;
        public ViewHolder(View view){
            friend_name=view.findViewById(R.id.friend_name);
            iv_image=view.findViewById(R.id.friend_img);
            bt_addfriend=view.findViewById(R.id.bt_addfriend);
            friend_account=view.findViewById(R.id.addfriend_account);
        }
    }

    private void loadData() {//执行异步任务，返回注册成功后的user数据，
        if(isLoading){
            isLoading=false;
            //执行异步任务，加载数据
            new AddFriendTask(new AddFriendTask.CallBack(){
                @Override
                public void getResult(User result) {
                    isrequest=result.getResult();
                }
            }).execute(url+"account="+account+"&fri_account="+fri_account);
            isLoading=true;
        }else{

        }
    }
}
