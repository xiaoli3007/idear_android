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
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ForgetPassword extends Activity implements OnClickListener, OnTouchListener {
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private RelativeLayout layoutLoading;
	private Button buttonBack;
	private ScrollView viewMain;
	private LinearLayout layoutForm;
	private EditText inputMobile;
	private Button buttonCheckMobile;
	private LinearLayout layoutVerfycode;
	private TextView textVerfyCodeSendTo;
	private EditText inputVerfycode;
	private Button buttonSendVerfycode;
	private Button buttonCheckVerfycode;
	private LinearLayout layoutNewPassword;
	private EditText inputNewPassword;
	private Button buttonResetPassword;
	private boolean showLoading = false;
	private boolean isSubmiting = false;

	private String verfyCode = null;

	private SendVerfycodeTimer countTimer = new SendVerfycodeTimer(60000, 1000);

	@Override
	public void onCreate(Bundle sinha) {
		super.onCreate(sinha);
		setContentView(R.layout.forget_password);

		this.httpClient = new SinhaPipeClient();

		this.layoutLoading = (RelativeLayout) findViewById(R.id.loading);
		this.buttonBack = (Button) findViewById(R.id.forget_password_title_back_button);
		this.buttonBack.setOnClickListener(this);
		this.viewMain = (ScrollView) findViewById(R.id.forget_password_main_layout);
		this.viewMain.setOnTouchListener(this);
		this.layoutForm = (LinearLayout) findViewById(R.id.forget_password_form_layout);
		this.inputMobile = (EditText) findViewById(R.id.forget_password_mobile_input);
		this.buttonCheckMobile = (Button) findViewById(R.id.forget_password_check_mobile_button);
		this.buttonCheckMobile.setOnClickListener(this);
		this.layoutVerfycode = (LinearLayout) findViewById(R.id.forget_password_verfycode_layout);
		this.textVerfyCodeSendTo = (TextView) findViewById(R.id.forget_password_verfycode_sendto_text);
		this.inputVerfycode = (EditText) findViewById(R.id.forget_password_verfycode_input);
		this.buttonSendVerfycode = (Button) findViewById(R.id.forget_password_send_verfycode_button);
		this.buttonSendVerfycode.setOnClickListener(this);
		this.buttonCheckVerfycode = (Button) findViewById(R.id.forget_password_check_verfycode_button);
		this.buttonCheckVerfycode.setOnClickListener(this);
		this.layoutNewPassword = (LinearLayout) findViewById(R.id.forget_password_new_password_layout);
		this.inputNewPassword = (EditText) findViewById(R.id.forget_password_new_password_input);
		this.buttonResetPassword = (Button) findViewById(R.id.forget_password_submit_button);
		this.buttonResetPassword.setOnClickListener(this);
	}

	private void showLoading() {
		Utils.animView(this.layoutLoading, !this.showLoading);
		this.showLoading = !this.showLoading;
	}

	class SendVerfycodeTimer extends CountDownTimer {
		public SendVerfycodeTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			buttonSendVerfycode.setEnabled(true);
			buttonSendVerfycode.setClickable(true);
			buttonSendVerfycode.setText(R.string.send_verfycode);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			buttonSendVerfycode.setEnabled(false);
			buttonSendVerfycode.setClickable(false);
			buttonSendVerfycode.setText(millisUntilFinished / 1000 + getString(R.string.resend_verfycode));
		}
	}

	private void sendVerfyCode() {
		if (Utils.CheckNetwork()) {
			if (checkMobile()) {
				this.isSubmiting = true;
				showLoading();

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("mobile", this.inputMobile.getText().toString()));

				this.httpClient.Config("post", Consts.URI_SEND_VERFY_CODE, params, true);
				this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
					public void CallFinished(String error, Object result) {
						showLoading();
						isSubmiting = false;
						if (null == error) {
							sendVerfyCode((String) result);
						} else {
							int err = R.string.dialog_system_error_content;
							if (error == httpClient.ERR_TIME_OUT) {
								err = R.string.dialog_network_error_timeout;
							}
							if (error == httpClient.ERR_GET_ERR) {
								err = R.string.dialog_network_error_getdata;
							}
							Utils.ShowToast(ForgetPassword.this, err);
						}
					}
				});
				this.httpMethod.start();
			}
		} else {
			Utils.ShowToast(this, R.string.dialog_network_check_content);
		}
	}

	private void sendVerfyCode(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					this.verfyCode = result.optString("msg", null);
					this.countTimer.start();
					this.textVerfyCodeSendTo.setText(this.inputMobile.getText().toString());
					this.layoutForm.setVisibility(View.GONE);
					this.layoutVerfycode.setVisibility(View.VISIBLE);
					this.layoutNewPassword.setVisibility(View.GONE);
				} else {
					Utils.Dialog(this, getString(R.string.dialog_login_result_err), result.optString("msg", ""));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void submitResetPassword() {
		if (Utils.CheckNetwork()) {
			if (checkResetPassword()) {
				this.isSubmiting = true;
				showLoading();

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("mobile", this.inputMobile.getText().toString()));
				params.add(new BasicNameValuePair("password", this.inputNewPassword.getText().toString()));

				this.httpClient.Config("post", Consts.URI_RESET_PASSWORD, params, true);
				this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
					public void CallFinished(String error, Object result) {
						showLoading();
						isSubmiting = false;
						if (null == error) {
							submitResetPassword((String) result);
						} else {
							int err = R.string.dialog_system_error_content;
							if (error == httpClient.ERR_TIME_OUT) {
								err = R.string.dialog_network_error_timeout;
							}
							if (error == httpClient.ERR_GET_ERR) {
								err = R.string.dialog_network_error_getdata;
							}
							Utils.ShowToast(ForgetPassword.this, err);
						}
					}
				});
				this.httpMethod.start();
			}
		} else {
			Utils.ShowToast(this, R.string.dialog_network_check_content);
		}
	}

	private void submitResetPassword(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					Utils.Dialog(this, R.string.dialog_reset_password_tip, R.string.dialog_reset_password_success, new Utils.Callback() {
						@Override
						public void callFinished() {
							ForgetPassword.this.finish();
						}
					});
				} else {
					Utils.Dialog(this, getString(R.string.dialog_reset_password_tip), result.optString("msg", ""));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean checkMobile() {
		boolean bolCheckResult = true;
		Editable editableMobile = this.inputMobile.getText();
		if (!editableMobile.toString().matches(".{11,11}")) {
			bolCheckResult = false;
			this.inputMobile.requestFocus();
			Utils.Dialog(this, getString(R.string.dialog_form_check_title), getString(R.string.dialog_form_check_err_mobile));
		}
		return bolCheckResult;
	}

	private boolean checkVerfycode() {
		boolean bolCheckResult = true;
		Editable editableVerfycode = this.inputVerfycode.getText();
		if (!editableVerfycode.toString().matches(".{6,6}") || !editableVerfycode.toString().equals(this.verfyCode)) {
			bolCheckResult = false;
			this.inputVerfycode.requestFocus();
			Utils.Dialog(this, getString(R.string.dialog_form_check_title), getString(R.string.dialog_form_check_err_verfycode));
		}
		return bolCheckResult;
	}

	private boolean checkResetPassword() {
		boolean bolCheckResult = true;
		Editable editableNewPassword = this.inputNewPassword.getText();
		if (!editableNewPassword.toString().matches(".{6,50}")) {
			bolCheckResult = false;
			this.inputNewPassword.requestFocus();
			Utils.Dialog(this, getString(R.string.dialog_form_check_title), getString(R.string.dialog_form_check_err_new_password));
		}
		return bolCheckResult;
	}

	@Override
	public void onClick(View v) {
		Utils.hideKeyboard(this);
		if (v == this.buttonBack) {
			finish();
		}
		if (v == this.buttonCheckMobile || v == this.buttonSendVerfycode) {
			if (this.isSubmiting) {
				Utils.Dialog(this, getString(R.string.dialog_tip), getString(R.string.dialog_submiting_content));
			} else {
				sendVerfyCode();
			}
		}
		if (v == this.buttonCheckVerfycode) {
			if (checkVerfycode()) {
				this.countTimer.cancel();
				this.layoutForm.setVisibility(View.GONE);
				this.layoutVerfycode.setVisibility(View.GONE);
				this.layoutNewPassword.setVisibility(View.VISIBLE);
			}
		}
		if (v == this.buttonResetPassword) {
			if (this.isSubmiting) {
				Utils.Dialog(this, getString(R.string.dialog_tip), getString(R.string.dialog_submiting_content));
			} else {
				submitResetPassword();
			}
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		this.countTimer.cancel();
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		onClick(v);
		return false;
	}
}