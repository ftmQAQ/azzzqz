package com.example.azzzqz.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.azzzqz.ContentActivity;
import com.example.azzzqz.R;
import com.example.azzzqz.javabean.News;
import com.example.azzzqz.task.ImageTask;

import java.util.List;

public class NewsAdapter extends ArrayAdapter {
    private int item_layout_id;
    public NewsAdapter(@NonNull Context context, int resource, @NonNull List objects) {
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
        News news=(News)getItem(position);
        holder.tv_title.setText(news.getTitle());
        holder.tv_ownername.setText("up主："+news.getOwnername());
        holder.tv_tname.setText("分区："+news.getTname());
        String content_url=news.getContent_url();
        holder.tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(content_url)){
                    Intent intent=new Intent(getContext(), ContentActivity.class);
                    intent.putExtra("content_url","https://www.bilibili.com/av"+content_url);
                    getContext().startActivity(intent);
                }else{
                    Toast.makeText(getContext(), "xxx", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //给图片控件设置要显示的图片
        String image_url=news.getPic();
        Log.i("TAG", "getView: "+image_url);
        if(TextUtils.isEmpty(image_url)){
            holder.iv_image.setVisibility(View.GONE);
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
        TextView tv_title;
        TextView tv_tname;
        TextView tv_ownername;
        ImageView iv_image;
        public ViewHolder(View view){
            tv_title=(TextView) view.findViewById(R.id.tv_title);
            tv_tname=(TextView) view.findViewById(R.id.tv_tname);
            tv_ownername=(TextView)view.findViewById(R.id.tv_ownername);
            iv_image=(ImageView)view.findViewById(R.id.iv_image);

        }
    }
}