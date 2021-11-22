package com.example.azzzqz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.azzzqz.Adapter.NewsAdapter;
import com.example.azzzqz.javabean.News;
import com.example.azzzqz.task.NewsTask;

import java.util.ArrayList;

public class BiliActivity extends AppCompatActivity {
    ListView lv_news;
    ImageView bili_back;
    ArrayList<News> list=new ArrayList<>();
    NewsAdapter adapter;
    boolean isLoading=true,isDown=false;
    String url="https://www.bilibili.com/index/ding.json";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bili);
        lv_news=findViewById(R.id.lv_news);
        bili_back=findViewById(R.id.bili_back);
        loadData();
        adapter=new NewsAdapter(BiliActivity.this,R.layout.news_item,list);
        lv_news.setAdapter(adapter);
//        lv_news.setOnClickListener();//整个列表的点击事件
//        lv_news.setOnItemClickListener();//列表项的点击事件
        lv_news.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(isDown&&scrollState==SCROLL_STATE_IDLE){
                    loadData();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem+visibleItemCount==totalItemCount){
                    isDown=true;
                }else {
                    isDown=false;
                }
            }
        });//列表的滑动事件
        bili_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadData() {
        if(isLoading){
            isLoading=false;
            //执行异步任务，加载数据
            new NewsTask(new NewsTask.CallBack() {
                @Override
                public void getResult(ArrayList<News> result) {
                    list.addAll(result);
                    adapter.notifyDataSetChanged();
                }
            }).execute(url);
            isLoading=true;
        }else{

        }
    }
}