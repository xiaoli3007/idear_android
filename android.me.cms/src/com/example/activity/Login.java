package com.example.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.pipe.SinhaPipeClient;
import com.example.pipe.SinhaPipeMethod;
import com.example.utils.Utils;
import com.example.cms.Consts;
import com.example.cms.R;
import com.example.cms.Cms;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class Login extends Activity implements OnClickListener, OnTouchListener {
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private RelativeLayout layoutLoading;
	private ScrollView viewMain;
	private ImageView viewLogo;
	private Button buttonClose;
	private EditText inputMobile;
	private EditText inputPassword;
	private Button buttonForgetPassword;
	private Button buttonRegister;
	private Button buttonLogin;
	private LinearLayout layoutOtherLogin;
	private Button buttonWechatLogin;
	private Button buttonWeiboLogin;
	private boolean showLoading = false;
	private boolean isSubmiting = false;
	private int keyboardHeight = 0;


	@Override
	public void onCreate(Bundle sinha) {
		super.onCreate(sinha);
		setContentView(R.layout.login);

		this.httpClient = new SinhaPipeClient();
	

		this.layoutLoading = (RelativeLayout) findViewById(R.id.loading);
		this.viewMain = (ScrollView) findViewById(R.id.login_main_layout);
		this.viewMain.setOnTouchListener(this);
		this.viewLogo = (ImageView) findViewById(R.id.logo);
		this.buttonClose = (Button) findViewById(R.id.login_close_button);
		this.buttonClose.setOnClickListener(this);
		this.inputMobile = (EditText) findViewById(R.id.login_mobile_input);
		this.inputPassword = (EditText) findViewById(R.id.login_password_input);
		this.buttonForgetPassword = (Button) findViewById(R.id.login_forget_password_button);
		this.buttonForgetPassword.setOnClickListener(this);
		this.buttonRegister = (Button) findViewById(R.id.login_register_button);
		this.buttonRegister.setOnClickListener(this);
		this.buttonLogin = (Button) findViewById(R.id.login_submit_button);
		this.buttonLogin.setOnClickListener(this);
		this.layoutOtherLogin = (LinearLayout) findViewById(R.id.login_other_login_layout);
		this.buttonWechatLogin = (Button) findViewById(R.id.login_wechat_button);
		this.buttonWechatLogin.setOnClickListener(this);
		this.buttonWeiboLogin = (Button) findViewById(R.id.login_weibo_button);
		this.buttonWeiboLogin.setOnClickListener(this);

		final View viewRoot = findViewById(R.id.login_layout);
		viewRoot.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				int intDiffHeight = viewRoot.getRootView().getHeight() - viewRoot.getHeight();
				if (keyboardHeight == intDiffHeight) {
					return;
				}
				keyboardHeight = intDiffHeight;
				if (intDiffHeight > 100) {
					buttonClose.setVisibility(View.GONE);
					viewLogo.setVisibility(View.GONE);
					layoutOtherLogin.setVisibility(View.GONE);
				} else {
					buttonClose.setVisibility(View.VISIBLE);
					viewLogo.setVisibility(View.VISIBLE);
					layoutOtherLogin.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	private void showLoading() {
		Utils.animView(this.layoutLoading, !this.showLoading);
		this.showLoading = !this.showLoading;
	}

	private void submitLogin() {
		if (Utils.CheckNetwork()) {
			if (checkForm()) {
				this.isSubmiting = true;
				showLoading();

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("mobile", this.inputMobile.getText().toString()));
				params.add(new BasicNameValuePair("password", this.inputPassword.getText().toString()));

				this.httpClient.Config("post", Consts.URI_LOGIN, params, true);
				this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
					public void CallFinished(String error, Object result) {
						showLoading();
						isSubmiting = false;
						if (null == error) {
							submitLogin((String) result);
						} else {
							int err = R.string.dialog_system_error_content;
							if (error == httpClient.ERR_TIME_OUT) {
								err = R.string.dialog_network_error_timeout;
							}
							if (error == httpClient.ERR_GET_ERR) {
								err = R.string.dialog_network_error_getdata;
							}
							Utils.ShowToast(Login.this, err);
						}
					}
				});
				this.httpMethod.start();
			}
		} else {
			Utils.ShowToast(this, R.string.dialog_network_check_content);
		}
	}

	private void submitLogin(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					Cms.memberInfo = result.optJSONObject("memberinfo");
					Cms.APP.setLogin(true, Cms.memberInfo.optString("memberid", ""), this.inputMobile.getText().toString(), this.inputPassword.getText().toString());
					Cms.APP.setConfig(Cms.memberInfo.toString());
					finish();
				} else {
					Utils.Dialog(this, getString(R.string.dialog_login_result_err), result.optString("msg", ""));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean checkForm() {
		boolean bolAlertPop = false;
		boolean bolCheckResult = true;
		Editable editableMobile = this.inputMobile.getText();
		if (!editableMobile.toString().matches(".{11,11}")) {
			bolAlertPop = true;
			bolCheckResult = false;
			this.inputMobile.requestFocus();
			Utils.Dialog(this, getString(R.string.dialog_form_check_title), getString(R.string.dialog_form_check_err_mobile));
		}
		Editable editablePassword = this.inputPassword.getText();
		if (!bolAlertPop && !editablePassword.toString().matches(".{6,50}")) {
			bolAlertPop = true;
			bolCheckResult = false;
			this.inputPassword.requestFocus();
			Utils.Dialog(this, getString(R.string.dialog_form_check_title), getString(R.string.dialog_form_check_err_password));
		}
		return bolCheckResult;
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULT_OK:
			switch (requestCode) {
			case 99:
				finish();
				break;
			default:
				
				break;
			}
			break;
		default:
			break;
		}
	}

	
	
	@Override
	public void onClick(View v) {
		Utils.hideKeyboard(this);
		if (v == this.buttonClose) {
			finish();
		}
		if (v == this.buttonLogin) {
			if (this.isSubmiting) {
				Utils.Dialog(this, getString(R.string.dialog_tip), getString(R.string.dialog_submiting_content));
			} else {
				submitLogin();
			}
		}
		if (v == this.buttonRegister) {
			Intent intent = new Intent(this, Register.class);
			this.startActivityForResult(intent, 99);
		}
		if (v == this.buttonForgetPassword) {
			Intent intent = new Intent(this, ForgetPassword.class);
			this.startActivity(intent);
		}
		
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		onClick(v);
		return false;
	}
}