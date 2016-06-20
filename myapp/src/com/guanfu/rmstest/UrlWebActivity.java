/**
 * 打开外部链接
 */
package com.guanfu.rmstest;

import android.os.Bundle;
import android.util.StringBuilderPrinter;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.guanfu.rmstest.BaseActivity;

public class UrlWebActivity extends BaseActivity {
    /**
     * R文件
     */
    private TextView mAbout;
    private WebView webview;
    private String url = "http://www.baidu.com";
    private String title = "网址导航";
    private String mDATA = "";

    @Override
    protected void setContentView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // TODO Auto-generated method stub
        setContentView(R.layout.activity_aboutus);
        Bundle bun = getIntent().getExtras();
        if (bun != null) {
            url = bun.getString("url");
            if (!url.equals("")) {
                if (!url.contains("http")) { // 判断地址不是完整的url
                    // 就加上http://
                    //url = "http://" + url;
                }
            }
            title = bun.getString("title");
        }
    }

    @Override
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

    @Override
    protected void setOnClickListener() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void processLogic() {
        // TODO Auto-generated method stub

    }

    /*
     * 返回键事件
     */
    public void goBack(View v) {
        this.finish();
    }

    /**
     * 拦截物理返回键
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
