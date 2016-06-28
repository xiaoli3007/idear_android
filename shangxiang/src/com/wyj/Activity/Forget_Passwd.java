package com.wyj.Activity;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.wyj.dataprocessing.RegularUtil;
import com.wyj.http.WebApiUrl;
import com.wyj.pipe.SinhaPipeClient;
import com.wyj.pipe.SinhaPipeMethod;
import com.wyj.pipe.Utils;

import com.wyj.Activity.R;

import android.app.Activity;
import android.app.ProgressDialog;

import android.content.Intent;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class Forget_Passwd extends Activity implements OnClickListener {

	ImageView Forget_Passwd_bak;
	EditText forget_passwd_mobile;
	Button forget_submit_button;
	private ProgressDialog pDialog = null;
	
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private String mobile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.forget_passwd);

		findViewById();
		setListener();
	}

	private void findViewById() {
		Forget_Passwd_bak = (ImageView) findViewById(R.id.forget_passwd_back);

		forget_passwd_mobile = (EditText) findViewById(R.id.forget_passwd_mobile);
		forget_submit_button = (Button) findViewById(R.id.forget_submit_button);
		this.httpClient = new SinhaPipeClient();
	}

	private void setListener() {
		Forget_Passwd_bak.setOnClickListener(this);
		forget_submit_button.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_login:
			// finish();
			Intent bak_login = new Intent();
			bak_login.setClass(Forget_Passwd.this, Login.class);
			startActivity(bak_login);
			break;
		case R.id.forget_submit_button:
			
			submitData();
			break;
		}

	}

	private void submitData() {

		 mobile = forget_passwd_mobile.getText().toString();
		
		if (!RegularUtil.checkPhone(this, mobile)) {

			// 设置焦点信息;
			forget_passwd_mobile.setFocusable(true);
			forget_passwd_mobile.setFocusableInTouchMode(true);
			forget_passwd_mobile.requestFocus();
			forget_passwd_mobile.requestFocusFromTouch();
		} else {
			
			send_msg(WebApiUrl.Sendsmsdo) ;
		}

	}
	
	private void send_msg( String url ) {
		this.httpClient = new SinhaPipeClient();
		
		if (Utils.CheckNetwork()) {
			
			showLoading();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("mobile",mobile));
			
			this.httpClient.Config("post", url, params, true);
				this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
					public void CallFinished(String error, Object result) {
						
							showLoading();
						
						if (null == error) {
							
							Log.i("bbbb", "-----请求回来- 验证码---"+result.toString() );
							try {
								JSONObject object=new JSONObject((String)result);
								
								if(object.optString("code", "").equals("succeed")){
									
									Intent inten=new Intent(Forget_Passwd.this, Forget_Passwd_two.class);
									
									inten.putExtra("yzm", object.optString("msg", ""));
									inten.putExtra("mobile", mobile);
									startActivity(inten);
									finish();
								}else{
									
									Utils.ShowToast(Forget_Passwd.this, "发送失败,请检查手机号是否正确");
								}
								
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						} else {
							int err = R.string.dialog_system_error_content;
							if (error == httpClient.ERR_TIME_OUT) {
								err = R.string.dialog_network_error_timeout;
							}
							if (error == httpClient.ERR_GET_ERR) {
								err = R.string.dialog_network_error_getdata;
							}
							Utils.ShowToast(Forget_Passwd.this, err);
						}
					}
				});
				this.httpMethod.start();
		
		} else {
			Utils.ShowToast(Forget_Passwd.this, R.string.dialog_network_check_content);
		}
		
	
	}
	
private void showLoading() {
		
		if(pDialog!=null){
			pDialog.dismiss();
		}else{
			
			pDialog = new ProgressDialog(this);
			pDialog.setMessage("数据加载中。。。");
			pDialog.show();
		}
		
	}



	

}
