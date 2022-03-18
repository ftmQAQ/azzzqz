package com.example.azzzqz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.azzzqz.Javabean.User;
import com.example.azzzqz.Task.RePortraitTask;
import com.example.azzzqz.Task.UserInfoTask;
import com.example.azzzqz.Utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RePortraitActivity extends AppCompatActivity {
    private GridView reportrait_gv;
    private CircleImageView reportrait_img;
    private ImageView reportrait_return;
    private Button btn_portrait_ok;
    private int[] listImg = new int[] { R.drawable.test, R.drawable.test2, R.drawable.test3, R.drawable.test4, R.drawable.test5};
    private String[] listName = new String[] { "丸山彩", "七海灯子", "小糸侑", "宵宫1", "宵宫2"};
    private String imgstr="test";
    private String account;
    private Boolean isLoading=true;
    private String url="http://register.ftmqaq.cn/upportrait.php?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_portrait);
        reportrait_gv=this.findViewById(R.id.reportrait_gv);
        reportrait_img=this.findViewById(R.id.reportrait_img);
        reportrait_return=this.findViewById(R.id.reportrait_return);
        btn_portrait_ok=this.findViewById(R.id.btn_portrait_ok);
        List<Map<String, Object>> item = getData();
        //加载当前用户的头像和账号
        Intent intent=getIntent();
        account=intent.getStringExtra("account");
        imgstr=intent.getStringExtra("portrait");
        reportrait_img.setImageResource(Utils.portraitselect(imgstr));
        // SimpleAdapter对象，匹配ArrayList中的元素
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, item, R.layout.portrait_item, new String[] { "PortraitImage", "PortraitName" }, new int[] { R.id.PortraitImage, R.id.PortraitName });
        reportrait_gv.setAdapter(simpleAdapter);
        reportrait_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    reportrait_img.setImageResource(listImg[position]);
                    imgstr="test";
                }else{
                    reportrait_img.setImageResource(listImg[position]);
                    imgstr="test"+(position+1);
                }
            }
        });
        reportrait_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_portrait_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportrait(account,imgstr);
            }
        });
    }

    /**
     * 将图标图片和图标名称存入ArrayList中
     *
     * @return
     */
    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < listImg.length; i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("PortraitImage", listImg[i]);
            item.put("PortraitName", listName[i]);
            items.add(item);
        }
        return items;

    }

    private void reportrait(String account,String imgstr) {
        if(isLoading){
            isLoading=false;
            //执行异步任务，加载数据
            new RePortraitTask(new RePortraitTask.CallBack(){
                @Override
                public void getResult(Boolean result) {
                    if(result){
                        Toast.makeText(RePortraitActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        send_reportrait();
                        finish();
                    }else{
                        Toast.makeText(RePortraitActivity.this, "修改失败我也不知道为啥", Toast.LENGTH_SHORT).show();
                    }
                }
            }).execute(url+"account="+account+"&"+"portrait=\""+imgstr+"\"");
            isLoading=true;
        }
    }

    public void send_reportrait(){
        Intent intent=new Intent("com.example.azzzqz.reportraittouserinfo");
        sendBroadcast(intent);
    }
}