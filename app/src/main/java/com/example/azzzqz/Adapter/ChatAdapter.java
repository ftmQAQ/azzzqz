package com.example.azzzqz.Adapter;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter; import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.azzzqz.ChatActivity;
import com.example.azzzqz.R;
import com.example.azzzqz.javabean.Msg;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> { //定义两个类别标志
    private static final int TYPE_LEFT = 0;
    private static final int TYPE_RIGHT = 1;
    private int type=-1;
    private Context context;
    private ArrayList<Msg> data;
    public ChatAdapter(Context context,List data) {
        this.context = context;
        this.data = (ArrayList<Msg>) data;
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
        }else if(data.get(position).getType()==TYPE_RIGHT){
            holder.rightMsg.setText(msg.getMsg());
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
        public ViewHolder(View view){
            super(view);
            leftMsg=view.findViewById(R.id.leftMsg);
            rightMsg=view.findViewById(R.id.rightMsg);
        }
    }
}