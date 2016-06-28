package com.wyj.Activity;

import java.util.HashMap;
import java.util.Map;

import com.wyj.dataprocessing.AccessNetwork;
import com.wyj.dataprocessing.JsonToListHelper;
import com.wyj.dataprocessing.RegularUtil;
import com.wyj.http.WebApiUrl;
import com.wyj.pipe.Cms;
import com.wyj.Activity.R;

import android.app.Activity;
import android.app.ProgressDialog;

import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Reg extends Activity implements OnClickListener {

	ImageView login_bak;
	Button reg_submit;
	TextView username;
	TextView passwd;
	private Boolean regflag = false;
	private ProgressDialog pDialog = null;
	private String notice_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.reg);

		findViewById();
		setListener();
	}

	private void findViewById() {
		login_bak = (ImageView) findViewById(R.id.back_login);
		reg_submit = (Button) findViewById(R.id.submit_button);
		username = (TextView) findViewById(R.id.regusername);
		passwd = (TextView) findViewById(R.id.regpasswd);
		
		
		
		if(!TextUtils.isEmpty(Cms.APP.get_notice_id())){
			notice_id= Cms.APP.get_notice_id();	
		}
		
		Log.i("aaaa",
 				"--通知ID2222-------" + 
 						notice_id); 
	}

	private void setListener() {
		login_bak.setOnClickListener(this);
		reg_submit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_login:
			// finish();
			Intent bak_login = new Intent();
			bak_login.setClass(Reg.this, Login.class);
			startActivity(bak_login);
			break;
		case R.id.submit_button:
			submitData();
			break;
		}

	}

	private void submitData() {

		String reg_username = username.getText().toString();
		String reg_passwd = passwd.getText().toString();

		if (!RegularUtil.checkPhone(this, reg_username)) {

			// 设置焦点信息;
			username.setFocusable(true);
			username.setFocusableInTouchMode(true);
			username.requestFocus();
			username.requestFocusFromTouch();
		} else if (!RegularUtil.checkPassword(this, reg_passwd)) {

			// 设置焦点信息;
			passwd.setFocusable(true);
			passwd.setFocusableInTouchMode(true);
			passwd.requestFocus();
			passwd.requestFocusFromTouch();

		} else {
			// String time = MyApplication.getInstances().getSystemDataATime();
			// Member_model Member_models= new Member_model(this);
			// Member_models.Member_insert(reg_username, reg_passwd, time);
			checkapi(reg_username, reg_passwd); // 接口请求
		}

	}

	private void checkapi(String reg_username, String reg_passwd) {
		// TODO Auto-generated method stub
		// 接口验证注册
		// Map<String,Object> params = new HashMap<String, Object>();
		// params.put("mobile", reg_username);
		// params.put("password", reg_passwd);
		// String check_reg =
		// HttpClientHelper.doPostSubmitMe(WebApiUrl.REGSITER,params);

		// 利用Handler更新UI
		final Handler h = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 0x123) {

					pDialog.dismiss();
					String backmsg = msg.obj.toString();
					Map<String, Object> resmsg = new HashMap<String, Object>();
					resmsg = JsonToListHelper.jsontoCode(backmsg);
					if (resmsg.get("code").equals("succeed")) {
						RegularUtil.alert_msg(Reg.this, "注册成功");
						finish();
					} else {

						RegularUtil.alert_msg(Reg.this,"注册失败，" + resmsg.get("msg"));
					}

					Log.i("aaaa", "------线程返回注册验证信息" + backmsg);
				}
			}
		};
		String params = "mobile=" + reg_username + "&password=" + reg_passwd+"&jregid="+notice_id;
		pDialog = new ProgressDialog(Reg.this);
		pDialog.setMessage("请求中。。。");
		pDialog.show();
		new Thread(new AccessNetwork("POST", WebApiUrl.REGSITER, params, h))
				.start();

	}

}
