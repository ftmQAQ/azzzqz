package com.example.azzzqz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ContentActivity extends AppCompatActivity {
    WebView wv_content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        wv_content=(WebView)findViewById(R.id.wv_content);
        wv_content.getSettings().setJavaScriptEnabled(true);//设置启用javascript
        wv_content.setWebViewClient(new WebViewClient());//对于新的超链接，在原窗口显示
        Intent intent=getIntent();
        String url=intent.getStringExtra("content_url");
        wv_content.loadUrl(url);
    }
}