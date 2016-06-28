package com.guanfu.rmstest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

public class MyActivity extends Activity {


    private WebView webview;
    private String url = "http://www.baidu.com";
    private String title = "网址导航";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);


        title ="测试";
        url="file:///android_asset/www/index.html";
//        btnOpen = (Button) findViewById(R.id.btnOpen);
//        btnOpen.setOnClickListener(new Button.OnClickListener(){//创建监听
//            public void onClick(View v) {
//                Intent mIntent = new Intent();
//                mIntent.setClass(MyActivity.this, UrlWebActivity.class);
//                mIntent.putExtra("url", "file:///android_asset/www/index.html");
//                mIntent.putExtra("title", "测试");
//                startActivity(mIntent);
//            }
//
//        });

        initView();
    }


    protected void initView() {
        //mAbout = (TextView) findViewById(R.id.tv_top_title);
        //mAbout.setText(title);

        webview = (WebView) findViewById(R.id.webview);

        webview.loadUrl(Utf8URLencode(url));
        // 设置Web视图
        webview.setWebViewClient(new HelloWebViewClient());
        //支持javascript
        webview.getSettings().setJavaScriptEnabled(true);
       /*
        // 设置可以支持缩放
        webview.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        webview.getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
        webview.getSettings().setUseWideViewPort(true);
        */
        //自适应屏幕
//		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN)
        webview.getSettings().setUseWideViewPort(true);//關鍵點
        webview.getSettings().setLoadWithOverviewMode(true);
    }

    // Web视图
    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }



    /**
     * Utf8URL编码
     *
     * @param
     * @return
     */
    public String Utf8URLencode(String text) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= 0 && c <= 255) {
                result.append(c);
            } else {
                byte[] b = new byte[0];
                try {
                    b = Character.toString(c).getBytes("UTF-8");
                } catch (Exception ex) {
                }
                for (int j = 0; j < b.length; j++) {
                    int k = b[j];
                    if (k < 0)
                        k += 256;
                    result.append("%" + Integer.toHexString(k).toUpperCase());
                }
            }
        }
        return result.toString();
    }


}
