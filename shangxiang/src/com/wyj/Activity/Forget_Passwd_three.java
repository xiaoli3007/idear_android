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

public class Forget_Passwd_three extends Activity implements OnClickListener {

	ImageView Forget_Passwd_bak;
	EditText forget_passwd_passwd;
	Button forget_submit_button_three;
	private ProgressDialog pDialog = null;
	private String mobile;

	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private String password;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.forget_passwd_three);
		
		Intent inten=this.getIntent();
		mobile=inten.getStringExtra("mobile");
		findViewById();
		setListener();
	}

	private void findViewById() {
		Forget_Passwd_bak = (ImageView) findViewById(R.id.forget_passwd_back);

		forget_passwd_passwd = (EditText) findViewById(R.id.forget_passwd_passwd);
		forget_submit_button_three = (Button) findViewById(R.id.forget_submit_button_three);
	}

	private void setListener() {
		Forget_Passwd_bak.setOnClickListener(this);
		forget_submit_button_three.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.forget_passwd_back:
			// finish();
			Intent bak_login = new Intent();
			bak_login.setClass(Forget_Passwd_three.this, Login.class);
			startActivity(bak_login);
			break;
		case R.id.forget_submit_button_three:
			submitData();
			break;
		}

	}

	private void submitData() {

		 password = forget_passwd_passwd.getText().toString();

		if (!RegularUtil.checkPassword(this, password)) {

			// 设置焦点信息;
			forget_passwd_passwd.setFocusable(true);
			forget_passwd_passwd.setFocusableInTouchMode(true);
			forget_passwd_passwd.requestFocus();
			forget_passwd_passwd.requestFocusFromTouch();
		} else {
			
			Modifypasswd(WebApiUrl.Modifypassdo);
		
			
		}

	}
	

	private void Modifypasswd(String url) {
		this.httpClient = new SinhaPipeClient();

		if (Utils.CheckNetwork()) {

			showLoading();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("mobile",mobile));
			params.add(new BasicNameValuePair("password",password));
			
			this.httpClient.Config("post", url, params, true);
			this.httpMethod = new SinhaPipeMethod(this.httpClient,
					new SinhaPipeMethod.MethodCallback() {
						public void CallFinished(String error, Object result) {

							showLoading();

							if (null == error) {

								Log.i("bbbb",
										"-----请求回来- 验证码---" + result.toString());
								try {
									JSONObject object = new JSONObject(
											(String) result);

									if (object.optString("code", "").equals(
											"succeed")) {
											
										Utils.ShowToast(Forget_Passwd_three.this,  "密码重置成功，请重新登录");
//										Intent bak_login = new Intent();
//										bak_login.setClass(Forget_Passwd_three.this, Login.class);
										UserGroupTab.getInstance().startActivityForResult(
												new Intent(Forget_Passwd_three.this, Login.class), 1);
										finish();
									} else {

										Utils.ShowToast(Forget_Passwd_three.this,
												"修改失败"+object.optString("msg", ""));
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
								Utils.ShowToast(Forget_Passwd_three.this, err);
							}
						}
					});
			this.httpMethod.start();

		} else {
			Utils.ShowToast(Forget_Passwd_three.this,
					R.string.dialog_network_check_content);
		}

	}

	private void showLoading() {

		if (pDialog != null) {
			pDialog.dismiss();
		} else {

			pDialog = new ProgressDialog(this);
			pDialog.setMessage("数据加载中。。。");
			pDialog.show();
		}

	}

	

}
