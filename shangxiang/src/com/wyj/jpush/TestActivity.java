package com.wyj.jpush;

import cn.jpush.android.api.JPushInterface;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        TextView tv = new TextView(this);
//        tv.setText("用户自定义打开的Activity");
//        Intent intent = getIntent();
//        if (null != intent) {
//	        Bundle bundle = getIntent().getExtras();
//	        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
//	        String content = bundle.getString(JPushInterface.EXTRA_ALERT);
//	        tv.setText("Title : " + title + "  " + "Content : " + content);
//        }
        
        RelativeLayout rl = new RelativeLayout(this); 
        Button btn1 = new Button(this); 
        btn1.setText("按钮1-----------"); 
        btn1.setId(1); 

        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams
        (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); 
        lp1.addRule(RelativeLayout.ALIGN_PARENT_TOP); 
        lp1.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE); 
        // btn1 位于父 View 的顶部，在父 View 中水平居中 
        rl.addView(btn1, lp1 ); 

        Button btn2 = new Button(this); 
        btn2.setText("按钮2"); 
        btn2.setId(2); 

        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams
        (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); 
        lp2.addRule(RelativeLayout.BELOW, 1); 
        lp2.addRule(RelativeLayout.ALIGN_LEFT, 1); 
        // btn2 位于 btn1 的下方、其左边和 btn1 的左边对齐 
        rl.addView(btn2, lp2); 

        Button btn3 = new Button(this); 
        btn3.setText("按钮3"); 
        btn3.setId(3); 

        RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams
        (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); 
        lp3.addRule(RelativeLayout.BELOW, 1); 
        lp3.addRule(RelativeLayout.RIGHT_OF, 2); 
        lp3.addRule(RelativeLayout.ALIGN_RIGHT, 1); 
        // btn3 位于 btn1 的下方、btn2 的右方且其右边和 btn1 的右边对齐（要扩充） 
        rl.addView(btn3,lp3); 

        Button btn4 = new Button(this); 
        btn4.setText("按钮4"); 
        btn4.setId(4); 

        RelativeLayout.LayoutParams lp4 = new RelativeLayout.LayoutParams
        (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); 
        lp4.addRule(RelativeLayout.BELOW, 2); 
        lp4.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE); 
        // btn4 位于 btn2 的下方，在父 Veiw 中水平居中 
        rl.addView(btn4,lp4); 
        setContentView(rl);
       // addContentView(tv, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    }

}
