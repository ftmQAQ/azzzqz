package com.example.azzzqz.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.azzzqz.R;
import com.example.azzzqz.Javabean.User;
import com.example.azzzqz.Task.AllowTask;
import com.example.azzzqz.Task.ImageTask;

import java.util.ArrayList;
import java.util.List;

public class AddFriendRequestAdapter extends ArrayAdapter {
    private int item_layout_id;
    private String url="http://friends.ftmqaq.cn/allowfri.php/?";
    private Boolean isLoading=true;
    private int code=0;
    private User userback;
    private ArrayList<User> data;
    //利用spf获取当前登录用户值
    SharedPreferences spf;
    //用户信息
    private String account;
    public AddFriendRequestAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        item_layout_id=resource;
        data= (ArrayList<User>) objects;
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
        spf= getContext().getSharedPreferences("user", Context.MODE_PRIVATE);;//打开本地存储的spf数据
        account=spf.getString("account","");
        User user=(User)getItem(position);
        holder.friend_request_name.setText(user.getUsername());
        holder.addre_allow.setOnClickListener(new View.OnClickListener() {//同意按钮事件，弹出小窗口确认是否同意
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setTitle("添加好友")
                        .setMessage("是否确认添加"+user.getUsername()+"为好友")
                        .setNegativeButton("取消",null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                code=1;
                                loadData(String.valueOf(user.getAccount()));
                                data.remove(position);
                                notifyDataSetChanged();
                            }
                        });
                builder.create();
                builder.show();
            }
        });
        holder.addre_refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setTitle("添加好友")
                        .setMessage("是否确认拒绝添加"+user.getUsername()+"为好友")
                        .setNegativeButton("取消",null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                code=0;
                                loadData(String.valueOf(user.getAccount()));
                                data.remove(position);
                                notifyDataSetChanged();
                            }
                        });
                builder.create();
                builder.show();
            }
        });
        //给图片控件设置要显示的图片
        String image_url=user.getPortrait_img();
        Log.i("TAG", "getView: "+image_url);
        if(TextUtils.isEmpty(image_url)){
            System.out.println("null");
        }else{
            new ImageTask(new ImageTask.CallBack(){
                @Override
                public void getResult(Bitmap result){
                    holder.friend_request_img.setImageBitmap(result);
                }
            }).execute(image_url);
        }
        return view;
    }

    public class ViewHolder{
        TextView friend_request_name;
        ImageView friend_request_img;
        Button addre_allow,addre_refuse;
        public ViewHolder(View view){
            addre_allow=view.findViewById(R.id.addre_allow);
            addre_refuse=view.findViewById(R.id.addre_refuse);
            friend_request_img=view.findViewById(R.id.friend_request_img);
            friend_request_name=view.findViewById(R.id.friend_request_name);
        }
    }

    private void loadData(String proposer) {
        if(isLoading){
            Log.i("好友：","xxxxx");
            isLoading=false;
            //执行异步任务，加载数据
            new AllowTask(new AllowTask.CallBack(){
                @Override
                public void getResult(User result) {
                    userback=result;
                }
            }).execute(url+"code="+code+"&proposer="+proposer+"&recipient="+account);
            isLoading=true;
        }else{

        }
    }
}
