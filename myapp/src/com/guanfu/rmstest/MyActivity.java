package com.guanfu.rmstest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MyActivity extends Activity {
    private Button btnOpen;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        btnOpen = (Button) findViewById(R.id.btnOpen);
        btnOpen.setOnClickListener(new Button.OnClickListener(){//创建监听
            public void onClick(View v) {
                Intent mIntent = new Intent();
                mIntent.setClass(MyActivity.this, UrlWebActivity.class);
                mIntent.putExtra("url", "file:///android_asset/www/index.html");
                mIntent.putExtra("title", "测试");
                startActivity(mIntent);
            }

        });
    }
}
