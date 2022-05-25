package com.example.btnaddtab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import java.io.InputStream;

public class ArticleActivity extends AppCompatActivity {

    WebView web_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        web_view = findViewById(R.id.web_view);


        /*    接收資料  */
        Bundle bundle = this.getIntent().getExtras();
        String forumAlias = bundle.getString("forumAlias");
        int id = bundle.getInt("id");


        web_view.getSettings().setJavaScriptEnabled(true);
        web_view.loadUrl("https://www.dcard.tw/f/" + forumAlias + "/p/" + id);
        System.out.println("https://www.dcard.tw/f/" + forumAlias + "/p/" + id);

        //https://www.dcard.tw/f/版名/p/貼文ID

    }
}