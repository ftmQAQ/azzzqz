package com.example.azzzqz.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.azzzqz.R;
import com.example.azzzqz.Javabean.Msg;
import com.example.azzzqz.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> { //定义两个类别标志
    private static final int TYPE_LEFT = 0;
    private static final int TYPE_RIGHT = 1;
    private int type=-1;
    private Context context;
    private String leftimg,rightimg;
    private ArrayList<Msg> data;
    public ChatAdapter(Context context,List data,String left_img,String right_img) {
        this.context = context;
        this.data = (ArrayList<Msg>) data;
        leftimg=left_img;
        rightimg=right_img;
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getType();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==TYPE_LEFT){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_left, parent, false);
            return new ViewHolder(view);
        }else if(viewType==TYPE_RIGHT){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_right, parent, false);
            return new ViewHolder(view);
        }
        return null;
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Msg msg = data.get(position);
        if(data.get(position).getType()==TYPE_LEFT){
            holder.leftMsg.setText(msg.getMsg());
            holder.left_img.setImageResource(Utils.portraitselect(leftimg));
        }else if(data.get(position).getType()==TYPE_RIGHT){
            holder.rightMsg.setText(msg.getMsg());
            holder.right_img.setImageResource(Utils.portraitselect(rightimg));
        }

    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    public void clear() {
        if (data != null) {
            data.clear();
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView leftMsg;
        TextView rightMsg;
        CircleImageView left_img,right_img;
        public ViewHolder(View view){
            super(view);
            leftMsg=view.findViewById(R.id.leftMsg);
            rightMsg=view.findViewById(R.id.rightMsg);
            left_img=view.findViewById(R.id.left_img);
            right_img=view.findViewById(R.id.right_img);
        }
    }
}