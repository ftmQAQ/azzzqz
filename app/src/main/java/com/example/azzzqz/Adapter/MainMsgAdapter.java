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
import com.example.azzzqz.Database.MyDatabaseHelper;
import com.example.azzzqz.R;
import com.example.azzzqz.Javabean.Msg;
import com.example.azzzqz.Javabean.User;
import com.example.azzzqz.Task.GetNameTask;
import com.example.azzzqz.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainMsgAdapter extends ArrayAdapter {
    private int item_layout_id;
    private String url="http://msg.ftmqaq.cn/getname.php?";
    private String account,own_account;
    private Boolean isLoading=true;
    private String fri_name,fri_portrait;
    private ArrayList<Msg> data;
    //本地数据查找对象myDatabaseHelper
    private MyDatabaseHelper myDatabaseHelper;
    public MainMsgAdapter(@NonNull Context context, int resource, @NonNull List objects,String own) {
        super(context, resource, objects);
        data= (ArrayList<Msg>) objects;
        own_account=own;
        item_layout_id=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        MainMsgAdapter.ViewHolder holder;
        myDatabaseHelper=new MyDatabaseHelper(getContext());
        myDatabaseHelper.open(own_account);//打开本地数据库
        User fri=new User();
        fri=myDatabaseHelper.querryfriend(data.get(position).getProposer())[0];//获取好友信息
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
        fri_name=fri.getUsername();
        fri_portrait=fri.getPortrait_img();
        holder.friend_name.setText(fri_name);
        holder.fri_other_msg.setText(data.get(position).getMsg());
        holder.friend_img.setImageResource(Utils.portraitselect(fri_portrait));
        holder.fri_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), ChatActivity.class);
                intent.putExtra("account",String.valueOf(data.get(position).getProposer()));
                intent.putExtra("username",fri_name);
                intent.putExtra("portrait",fri_portrait);
                getContext().startActivity(intent);
            }
        });
        return view;
    }
    public class ViewHolder{
        TextView friend_name,fri_other_msg;
        CircleImageView friend_img;
        LinearLayout fri_item;
        public ViewHolder(View view){
            fri_other_msg=view.findViewById(R.id.fri_other_msg);
            friend_name=view.findViewById(R.id.friend_name);
            friend_img=view.findViewById(R.id.friend_img);
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
