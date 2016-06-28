package com.wyj.Activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.wyj.http.WebApiUrl;
import com.wyj.pipe.SinhaPipeClient;
import com.wyj.pipe.SinhaPipeMethod;
import com.wyj.pipe.Utils;

import com.wyj.Activity.R;

import android.app.Activity;
import android.app.ProgressDialog;

import android.content.Intent;

import android.os.Bundle;
import android.os.CountDownTimer;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class Forget_Passwd_two extends Activity implements OnClickListener {

	ImageView forget_passwd_two_back;
	EditText forget_passwd_yzm;
	Button forget_submit_button, forget_passwd_resetsend,
			forget_submit_button_two;

	private String mobile;
	private String yzm;
	private ProgressDialog pDialog = null;

	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private TextView forget_passwd_phones;
	private TimeCount time= new TimeCount(60000, 1000);//构造CountDownTimer对象;
	
	private boolean isSending = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.forget_passwd_two);

		findViewById();
		setListener();
		Intent inten = this.getIntent();
		mobile = inten.getStringExtra("mobile");
		yzm = inten.getStringExtra("yzm");
		forget_passwd_phones.setText(mobile);
	}

	private void findViewById() {
		
		
		
		forget_passwd_two_back = (ImageView) findViewById(R.id.forget_passwd_two_back);
		
		forget_passwd_phones =(TextView) findViewById(R.id.forget_passwd_phones);
		forget_passwd_yzm = (EditText) findViewById(R.id.forget_passwd_yzm);

		forget_passwd_resetsend = (Button) findViewById(R.id.forget_passwd_resetsend);
		forget_submit_button_two = (Button) findViewById(R.id.forget_submit_button_two);
		this.httpClient = new SinhaPipeClient();
	}

	private void setListener() {
		forget_passwd_two_back.setOnClickListener(this);
		forget_passwd_resetsend.setOnClickListener(this);
		forget_submit_button_two.setOnClickListener(this);
		this.time.start();
	}
	
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
		}
		@Override
		public void onFinish() {//计时完毕时触发
			forget_passwd_resetsend.setEnabled(true);
			forget_passwd_resetsend.setText("重新验证");
			forget_passwd_resetsend.setClickable(true);
		}
		@Override
		public void onTick(long millisUntilFinished){//计时过程显示
			forget_passwd_resetsend.setEnabled(false);
			forget_passwd_resetsend.setClickable(false);
			forget_passwd_resetsend.setText("重新发送"+"("+millisUntilFinished /1000+")");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.forget_passwd_two_back:
			// finish();
			Intent bak_login = new Intent();
			bak_login.setClass(Forget_Passwd_two.this, Login.class);
			startActivity(bak_login);
			break;
		case R.id.forget_passwd_resetsend:
			
			if(isSending){
				Utils.Dialog(this, "提示", "稍等再发送！");
			}else{
				send_msg(WebApiUrl.Sendsmsdo) ;
			}
			
			break;
		case R.id.forget_submit_button_two:
			submitData();
			break;
		}

	}

	private void send_msg(String url) {
		this.httpClient = new SinhaPipeClient();

		if (Utils.CheckNetwork()) {
			isSending=true;
			showLoading();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("mobile",mobile));
			this.httpClient.Config("post", url, params, true);
			this.httpMethod = new SinhaPipeMethod(this.httpClient,
					new SinhaPipeMethod.MethodCallback() {
						public void CallFinished(String error, Object result) {

							showLoading();
							isSending=false;
							if (null == error) {

								Log.i("bbbb",
										"-----请求回来- 验证码---" + result.toString());
								try {
									JSONObject object = new JSONObject(
											(String) result);

									if (object.optString("code", "").equals(
											"succeed")) {
										
										yzm=object.optString("msg", "");
										time.start();
										Utils.ShowToast(Forget_Passwd_two.this,
												"发送成功");
									} else {

										Utils.ShowToast(Forget_Passwd_two.this,
												"发送失败");
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
								Utils.ShowToast(Forget_Passwd_two.this, err);
							}
						}
					});
			this.httpMethod.start();

		} else {
			Utils.ShowToast(Forget_Passwd_two.this,
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

	private void submitData() {

		String input_yzm = forget_passwd_yzm.getText().toString();

		if (!input_yzm.equals(yzm)) {
			Utils.Dialog(Forget_Passwd_two.this, "提示", "验证码输入有误！");
			// 设置焦点信息;
			forget_passwd_yzm.setFocusable(true);
			forget_passwd_yzm.setFocusableInTouchMode(true);
			forget_passwd_yzm.requestFocus();
			forget_passwd_yzm.requestFocusFromTouch();
		} else {
				
			Intent inten=new Intent(Forget_Passwd_two.this, Forget_Passwd_three.class);
			
			inten.putExtra("mobile", mobile);
			startActivity(inten);
			finish();
		}

	}

}
