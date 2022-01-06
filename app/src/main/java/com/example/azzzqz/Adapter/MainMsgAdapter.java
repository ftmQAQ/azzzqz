package com.example.azzzqz.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.azzzqz.ChatActivity;
import com.example.azzzqz.R;
import com.example.azzzqz.Javabean.Msg;
import com.example.azzzqz.Javabean.User;
import com.example.azzzqz.Task.GetNameTask;

import java.util.ArrayList;
import java.util.List;

public class MainMsgAdapter extends ArrayAdapter {
    private int item_layout_id;
    private String url="http://msg.ftmqaq.cn/getname.php?";
    private String account;
    private Boolean isLoading=true;
    private String fri_name;
    private ArrayList<Msg> data;
    public MainMsgAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        data= (ArrayList<Msg>) objects;
        item_layout_id=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        MainMsgAdapter.ViewHolder holder;
        if(convertView==null){
            //getContext()获取当前上下文
            //LayoutInflater.from(getContext())从当前上下文中获取布局填充器
            //把列表项布局填充到要显示的列表项中成为一个视图
            view= LayoutInflater.from(getContext())
                    .inflate(item_layout_id,parent,false);
            holder=new MainMsgAdapter.ViewHolder(view);
            view.setTag(holder);
        }else {
            view=convertView;
            holder=(MainMsgAdapter.ViewHolder) view.getTag();
        }
        account=String.valueOf(data.get(position).getProposer());
        loadname(holder.friend_name);
        holder.fri_other_msg.setText(data.get(position).getMsg());
        holder.fri_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), ChatActivity.class);
                intent.putExtra("account",String.valueOf(data.get(position).getProposer()));
                getContext().startActivity(intent);
                //发送广播消息给消息接收页
                Intent intent2=new Intent("com.example.azzzqz.wlx123");
                intent2.putExtra("account",data.get(position).getProposer());
                getContext().sendBroadcast(intent2);
            }
        });
        return view;
    }
    public class ViewHolder{
        TextView friend_name,fri_other_msg;
        ImageView iv_image;
        LinearLayout fri_item;
        public ViewHolder(View view){
            fri_other_msg=view.findViewById(R.id.fri_other_msg);
            friend_name=view.findViewById(R.id.friend_name);
            iv_image=view.findViewById(R.id.friend_img);
            fri_item=view.findViewById(R.id.fri_item);
        }
    }

    private void loadname(TextView fri) {
        if(isLoading){
            isLoading=false;
            //执行异步任务，加载数据
            new GetNameTask(new GetNameTask.CallBack(){
                @Override
                public void getResult(User result) {
                    fri_name= result.getUsername();
                    fri.setText(fri_name);
                }
            }).execute(url+"account="+account);
            isLoading=true;
        }else{

        }
    }
}
