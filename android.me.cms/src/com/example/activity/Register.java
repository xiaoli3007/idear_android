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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class Register extends Activity implements OnClickListener, OnTouchListener {
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private RelativeLayout layoutLoading;
	private Button buttonBack;
	private Button buttonUserAgreement;
	private ScrollView viewMain;
	private EditText inputMobile;
	private EditText inputPassword;
	private Button buttonRegister;
	private boolean showLoading = false;
	private boolean isSubmiting = false;

	@Override
	public void onCreate(Bundle sinha) {
		super.onCreate(sinha);
		setContentView(R.layout.register);

		this.httpClient = new SinhaPipeClient();

		this.layoutLoading = (RelativeLayout) findViewById(R.id.loading);
		this.buttonBack = (Button) findViewById(R.id.register_title_back_button);
		this.buttonBack.setOnClickListener(this);
		this.buttonUserAgreement = (Button) findViewById(R.id.register_title_user_agreement_button);
		this.buttonUserAgreement.setOnClickListener(this);
		this.viewMain = (ScrollView) findViewById(R.id.register_main_layout);
		this.viewMain.setOnTouchListener(this);
		this.inputMobile = (EditText) findViewById(R.id.register_mobile_input);
		this.inputPassword = (EditText) findViewById(R.id.register_password_input);
		this.buttonRegister = (Button) findViewById(R.id.register_submit_button);
		this.buttonRegister.setOnClickListener(this);
	}

	private void showLoading() {
		Utils.animView(this.layoutLoading, !this.showLoading);
		this.showLoading = !this.showLoading;
	}

	private void submitRegister() {
//		if (Utils.CheckNetwork()) {
//			if (checkForm()) {
//				this.isSubmiting = true;
//				showLoading();
//
//				List<NameValuePair> params = new ArrayList<NameValuePair>();
//				params.add(new BasicNameValuePair("mobile", this.inputMobile.getText().toString()));
//				params.add(new BasicNameValuePair("password", this.inputPassword.getText().toString()));
//
//				this.httpClient.Config("post", Consts.URI_REGISTER, params, true);
//				this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
//					public void CallFinished(String error, Object result) {
//						showLoading();
//						isSubmiting = false;
//						if (null == error) {
//							submitRegister((String) result);
//						} else {
//							int err = R.string.dialog_system_error_content;
//							if (error == httpClient.ERR_TIME_OUT) {
//								err = R.string.dialog_network_error_timeout;
//							}
//							if (error == httpClient.ERR_GET_ERR) {
//								err = R.string.dialog_network_error_getdata;
//							}
//							Utils.ShowToast(Register.this, err);
//						}
//					}
//				});
//				this.httpMethod.start();
//			}
//		} else {
//			Utils.ShowToast(this, R.string.dialog_network_check_content);
//		}
	}

	private void submitRegister(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					Cms.memberInfo = result.optJSONObject("memberinfo");
					Cms.APP.setLogin(true, Cms.memberInfo.optString("memberid", ""), this.inputMobile.getText().toString(), this.inputPassword.getText().toString());
					Cms.APP.setConfig(Cms.memberInfo.toString());
					setResult(RESULT_OK);
					finish();
				} else {
					Utils.Dialog(this, getString(R.string.dialog_register_result_err), result.optString("msg", ""));
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
	public void onClick(View v) {
		Utils.hideKeyboard(this);
		if (v == this.buttonBack) {
			finish();
		}
		if (v == this.buttonUserAgreement) {
//			Intent intent = new Intent(this, UserAgreement.class);
//			this.startActivity(intent);
		}
		if (v == this.buttonRegister) {
			if (this.isSubmiting) {
				Utils.Dialog(this, getString(R.string.dialog_tip), getString(R.string.dialog_submiting_content));
			} else {
				submitRegister();
			}
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		onClick(v);
		return false;
	}
}