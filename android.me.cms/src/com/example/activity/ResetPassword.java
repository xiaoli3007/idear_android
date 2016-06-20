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
import com.example.cms.BaseFragment;
import com.example.cms.Consts;
import com.example.cms.R;
import com.example.cms.Cms;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class ResetPassword extends BaseFragment implements OnTouchListener {
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private RelativeLayout layoutLoading;
	private Button buttonBack;
	private ScrollView viewMain;
	private EditText inputNewPassword;
	private EditText inputRenewPassword;
	private Button buttonSubmit;
	private boolean showLoading = false;
	private boolean isSubmiting = false;

	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		View view = inflater.inflate(R.layout.reset_password, null);

		this.httpClient = new SinhaPipeClient();

		this.layoutLoading = (RelativeLayout) view.findViewById(R.id.loading);
		this.buttonBack = (Button) view.findViewById(R.id.reset_password_title_back_button);
		this.buttonBack.setOnClickListener(this);
		this.viewMain = (ScrollView) view.findViewById(R.id.reset_password_main_layout);
		this.viewMain.setOnTouchListener(this);
		this.inputNewPassword = (EditText) view.findViewById(R.id.reset_password_new_password_input);
		this.inputRenewPassword = (EditText) view.findViewById(R.id.reset_password_renew_password_input);
		this.buttonSubmit = (Button) view.findViewById(R.id.reset_password_submit_button);
		this.buttonSubmit.setOnClickListener(this);
		
		return view;
	}

	private void showLoading() {
		Utils.animView(this.layoutLoading, !this.showLoading);
		this.showLoading = !this.showLoading;
	}

	private void submitResetPassword() {
		if (Utils.CheckNetwork()) {
			if (checkResetPassword()) {
				this.isSubmiting = true;
				showLoading();

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("mobile", Cms.APP.getMobile()));
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
							Utils.ShowToast(getActivity(), err);
						}
					}
				});
				this.httpMethod.start();
			}
		} else {
			Utils.ShowToast(getActivity(), R.string.dialog_network_check_content);
		}
	}

	private void submitResetPassword(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					Utils.Dialog(getActivity(), R.string.dialog_reset_password_tip, R.string.dialog_reset_password_success, new Utils.Callback() {
						@Override
						public void callFinished() {
							getActivity().onBackPressed();
						}
					});
				} else {
					Utils.Dialog(getActivity(), getString(R.string.dialog_reset_password_tip), result.optString("msg", ""));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean checkResetPassword() {
		boolean bolAlertPop = false;
		boolean bolCheckResult = true;
		Editable editableNewPassword = this.inputNewPassword.getText();
		if (!editableNewPassword.toString().matches(".{6,50}")) {
			bolAlertPop = true;
			bolCheckResult = false;
			this.inputNewPassword.requestFocus();
			Utils.Dialog(getActivity(), getString(R.string.dialog_form_check_title), getString(R.string.dialog_form_check_err_new_password));
		}
		Editable editableRenewPassword = this.inputRenewPassword.getText();
		if (!bolAlertPop && !editableRenewPassword.toString().equals(editableNewPassword.toString())) {
			bolAlertPop = true;
			bolCheckResult = false;
			this.inputRenewPassword.requestFocus();
			Utils.Dialog(getActivity(), getString(R.string.dialog_form_check_title), getString(R.string.dialog_form_check_err_renew_password));
		}
		return bolCheckResult;
	}

	@Override
	public void onClick(View v) {
		Utils.hideKeyboard(getActivity());
		if (v == this.buttonBack) {
			getActivity().onBackPressed();
		}
		if (v == this.buttonSubmit) {
			if (this.isSubmiting) {
				Utils.Dialog(getActivity(), getString(R.string.dialog_tip), getString(R.string.dialog_submiting_content));
			} else {
				submitResetPassword();
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