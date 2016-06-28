package com.shangxiang.android.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.shangxiang.android.Consts;
import com.shangxiang.android.R;
import com.shangxiang.android.ShangXiang;
import com.shangxiang.android.pipe.SinhaPipeClient;
import com.shangxiang.android.pipe.SinhaPipeMethod;
import com.shangxiang.android.utils.AccessTokenKeeper;
import com.shangxiang.android.utils.Utils;
import com.shangxiang.android.wxapi.WXEntryActivity;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

	private SsoHandler wechatSsoHandler;
	private String wechatCode;
	private JSONObject wechatAccessToken;
	private JSONObject wechatUserInfo;

	private AuthInfo weiboAuthInfo;
	private Oauth2AccessToken weiboAccessToken;
	private JSONObject weiboUserInfo;

	@Override
	public void onCreate(Bundle sinha) {
		super.onCreate(sinha);
		setContentView(R.layout.login);

		this.httpClient = new SinhaPipeClient();
		this.weiboAuthInfo = new AuthInfo(this, Consts.WEIBO_APP_KEY, Consts.WEIBO_REDIRECT_URL, Consts.WEIBO_SCOPE);
		this.wechatSsoHandler = new SsoHandler(this, this.weiboAuthInfo);

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

		if ("1".equals(ShangXiang.allowPartnerLogin)) {
			this.layoutOtherLogin.setVisibility(View.VISIBLE);
		} else {
			this.layoutOtherLogin.setVisibility(View.GONE);
		}

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
					if ("1".equals(ShangXiang.allowPartnerLogin)) {
						layoutOtherLogin.setVisibility(View.VISIBLE);
					}
				}
			}
		});

		ShangXiang.wxHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (1 == msg.what && null != msg.obj) {
					wechatCode = (String) msg.obj;
					getWechatLogin();
				}
			};
		};
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private void showLoading() {
		Utils.animLoading(this.layoutLoading, !this.showLoading);
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
				params.add(new BasicNameValuePair("jregid", ShangXiang.UUID));

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
					ShangXiang.memberInfo = result.optJSONObject("memberinfo");
					ShangXiang.APP.setLogin(true, ShangXiang.memberInfo.optString("memberid", ""), this.inputMobile.getText().toString(), this.inputPassword.getText().toString());
					ShangXiang.APP.setConfig(ShangXiang.memberInfo.toString());
					finish();
				} else {
					Utils.Dialog(this, getString(R.string.dialog_login_result_err), result.optString("msg", ""));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void wechatLogin() {
		ShangXiang.wechatAPI = WXAPIFactory.createWXAPI(this, Consts.WECHAT_APPID, false);
		if (ShangXiang.wechatAPI.isWXAppInstalled() && ShangXiang.wechatAPI.isWXAppSupportAPI()) {
			Bundle bundle = new Bundle();
			bundle.putString("action", "login");
			Intent intent = new Intent(Login.this, WXEntryActivity.class);
			intent.putExtras(bundle);
			this.startActivity(intent);
		} else {
			Utils.Dialog(this, getString(R.string.dialog_normal_title), getString(R.string.dialog_login_need_wechat));
		}
	}

	private void getWechatLogin() {
		if (Utils.CheckNetwork()) {
			this.isSubmiting = true;
			showLoading();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("appid", Consts.WECHAT_APPID));
			params.add(new BasicNameValuePair("secret", Consts.WECHAT_APP_SECRET));
			params.add(new BasicNameValuePair("code", this.wechatCode));
			params.add(new BasicNameValuePair("grant_type", "authorization_code"));

			this.httpClient.Config("get", Consts.WECHAT_ACCESSTOKEN_URI, params, true);
			this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
				public void CallFinished(String error, Object result) {
					showLoading();
					isSubmiting = false;
					if (null == error) {
						getWechatLogin((String) result);
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
		} else {
			Utils.ShowToast(this, R.string.dialog_network_check_content);
		}
	}

	private void getWechatLogin(String s) {
		if (null != s) {
			try {
				this.wechatAccessToken = new JSONObject(s);
				getWechatUserInfo();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void getWechatUserInfo() {
		if (Utils.CheckNetwork()) {
			this.isSubmiting = true;
			showLoading();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("access_token", this.wechatAccessToken.optString("access_token", "")));
			params.add(new BasicNameValuePair("openid", this.wechatAccessToken.optString("openid", "")));

			this.httpClient.Config("get", Consts.WECHAT_USER_URI, params, true);
			this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
				public void CallFinished(String error, Object result) {
					showLoading();
					isSubmiting = false;
					if (null == error) {
						getWechatUserInfo((String) result);
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
		} else {
			Utils.ShowToast(this, R.string.dialog_network_check_content);
		}
	}

	private void getWechatUserInfo(String s) {
		if (null != s) {
			try {
				this.wechatUserInfo = new JSONObject(s);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("name", "" + this.wechatAccessToken.optString("openid", "")));
				params.add(new BasicNameValuePair("token", this.wechatAccessToken.optString("openid", "")));
				params.add(new BasicNameValuePair("regtype", "wx"));
				params.add(new BasicNameValuePair("hf", this.wechatUserInfo.optString("headimgurl", "")));
				params.add(new BasicNameValuePair("nname", this.wechatUserInfo.optString("nickname", "")));
				params.add(new BasicNameValuePair("jregid", ShangXiang.UUID));
				sendPartnerLogin(params);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void weiboLogin() {
		this.wechatSsoHandler.authorize(new WBAuthListener());
	}

	class WBAuthListener implements WeiboAuthListener {
		@Override
		public void onComplete(Bundle values) {
			weiboAccessToken = Oauth2AccessToken.parseAccessToken(values);
			if (weiboAccessToken.isSessionValid()) {
				AccessTokenKeeper.writeAccessToken(Login.this, weiboAccessToken);
				getWeiboUserInfo();
			} else {
				String code = values.getString("code");
				String message = getString(R.string.dialog_login_result_err_weibo);
				if (!TextUtils.isEmpty(code)) {
					message = message + "\nCode: " + code;
				}
				Utils.Dialog(Login.this, getString(R.string.dialog_normal_title), message);
			}
		}

		@Override
		public void onCancel() {
		}

		@Override
		public void onWeiboException(WeiboException e) {
		}
	}



	private void getWeiboUserInfo() {
		if (Utils.CheckNetwork()) {
			this.isSubmiting = true;
			showLoading();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("source", Consts.WEIBO_APP_KEY));
			params.add(new BasicNameValuePair("access_token", this.weiboAccessToken.getToken()));
			params.add(new BasicNameValuePair("uid", String.valueOf(Long.parseLong(this.weiboAccessToken.getUid()))));

			this.httpClient.Config("get", Consts.WEIBO_USER_URL, params, true);
			this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
				public void CallFinished(String error, Object result) {
					showLoading();
					isSubmiting = false;
					if (null == error) {
						getWeiboUserInfo((String) result);
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
		} else {
			Utils.ShowToast(this, R.string.dialog_network_check_content);
		}
	}

	private void getWeiboUserInfo(String s) {
		if (null != s) {
			try {
				this.weiboUserInfo = new JSONObject(s);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("name", "" + this.weiboAccessToken.getUid()));
				params.add(new BasicNameValuePair("token", this.weiboAccessToken.getToken()));
				params.add(new BasicNameValuePair("regtype", "sina"));
				params.add(new BasicNameValuePair("hf", this.weiboUserInfo.optString("avatar_large", "")));
				params.add(new BasicNameValuePair("nname", this.weiboUserInfo.optString("screen_name", "")));
				params.add(new BasicNameValuePair("jregid", ShangXiang.UUID));
				sendPartnerLogin(params);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void sendPartnerLogin(List<NameValuePair> params) {
		if (Utils.CheckNetwork()) {
			this.isSubmiting = true;
			showLoading();

			this.httpClient.Config("post", Consts.URI_OTHER_LOGIN, params, true);
			this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
				public void CallFinished(String error, Object result) {
					showLoading();
					isSubmiting = false;
					if (null == error) {
						sendPartnerLogin((String) result);
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
		} else {
			Utils.ShowToast(this, R.string.dialog_network_check_content);
		}
	}

	private void sendPartnerLogin(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					ShangXiang.memberInfo = result.optJSONObject("memberinfo");
					ShangXiang.APP.setLogin(true, ShangXiang.memberInfo.optString("memberid", ""), "", "");
					ShangXiang.APP.setConfig(ShangXiang.memberInfo.toString());
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
		switch (requestCode) {
		case 99:
			finish();
			break;
		case 98:
			break;
		default:
			if (this.wechatSsoHandler != null) {
				this.wechatSsoHandler.authorizeCallBack(requestCode, resultCode, data);
			}
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
		if (v == this.buttonWechatLogin) {
			wechatLogin();
		}
		if (v == this.buttonWeiboLogin) {
			weiboLogin();
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		onClick(v);
		return false;
	}
}