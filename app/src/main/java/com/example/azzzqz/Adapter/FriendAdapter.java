package com.example.azzzqz.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.example.azzzqz.Javabean.User;
import com.example.azzzqz.UserinfoActivity;
import com.example.azzzqz.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendAdapter extends ArrayAdapter {
    private int item_layout_id;
    private ArrayList<User> data;
    public FriendAdapter(@NonNull Context context, int resource, @NonNull List objects) {
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
        User user=(User)getItem(position);
        holder.friend_name.setText(user.getUsername());
        holder.fri_other_msg.setText("蹦蹦号： "+String.valueOf(user.getAccount()));
        holder.friend_img.setImageResource(Utils.portraitselect(user.getPortrait_img()));
        holder.friend_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), UserinfoActivity.class);
                intent.putExtra("friaccount",user.getAccount());
                intent.putExtra("username",user.getUsername());
                intent.putExtra("userinfo_img",user.getPortrait_img());
                intent.putExtra("usertype",2);
                getContext().startActivity(intent);
            }
        });
        holder.fri_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), ChatActivity.class);
                intent.putExtra("account",String.valueOf(user.getAccount()));
                intent.putExtra("portrait",user.getPortrait_img());
                getContext().startActivity(intent);
                //发送广播消息给消息接收页
                Intent intent2=new Intent("com.example.azzzqz.wlx123");
                intent2.putExtra("account",data.get(position).getAccount());
                getContext().sendBroadcast(intent2);
            }
        });
        //给图片控件设置要显示的图片
//        String image_url=user.getPortrait_img();
//        Log.i("TAG", "getView: "+image_url);
//        if(TextUtils.isEmpty(image_url)){
//            System.out.println("null");
//        }else{
//            new ImageTask(new ImageTask.CallBack(){
//                @Override
//                public void getResult(Bitmap result){
//                    holder.iv_image.setImageBitmap(result);
//                }
//            }).execute(image_url);
//        }
        return view;
    }
    public class ViewHolder{
        TextView friend_name;
        TextView fri_other_msg;
        ImageView iv_image;
        LinearLayout fri_item;
        CircleImageView friend_img;
        public ViewHolder(View view){
            friend_name=view.findViewById(R.id.friend_name);
            iv_image=view.findViewById(R.id.friend_img);
            fri_item=view.findViewById(R.id.fri_item);
            fri_other_msg=view.findViewById(R.id.fri_other_msg);
            friend_img=view.findViewById(R.id.friend_img);
        }
    }
}
