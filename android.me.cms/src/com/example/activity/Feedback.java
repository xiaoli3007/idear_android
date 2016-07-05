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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class Feedback extends BaseFragment {
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private RelativeLayout layoutLoading;
	private Button buttonBack;
	private Button buttonSubmit;
	private EditText textFeedback;
	private boolean showLoading = false;
	private boolean isSubmiting = false;

	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		View view = inflater.inflate(R.layout.feedback, null);

		this.httpClient = new SinhaPipeClient();

		this.layoutLoading = (RelativeLayout) view.findViewById(R.id.loading);
		this.buttonBack = (Button) view.findViewById(R.id.feedback_title_back_button);
		this.buttonBack.setOnClickListener(this);
		this.buttonSubmit = (Button) view.findViewById(R.id.feedback_title_submit_button);
		this.buttonSubmit.setOnClickListener(this);
		this.textFeedback = (EditText) view.findViewById(R.id.feedback_content_text);

		return view;
	}

	private void showLoading() {
		Utils.animView(this.layoutLoading, !this.showLoading);
		this.showLoading = !this.showLoading;
	}

	private void submitFeedback() {
//		if (Utils.CheckNetwork()) {
//			if (checkFeedback()) {
//				this.isSubmiting = true;
//				showLoading();
//
//				List<NameValuePair> params = new ArrayList<NameValuePair>();
//				params.add(new BasicNameValuePair("mid", Cms.memberInfo.optString("memberid", "")));
//				params.add(new BasicNameValuePair("content", this.textFeedback.getText().toString()));
//
//				this.httpClient.Config("post", Consts.URI_FEEDBACK, params, true);
//				this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
//					public void CallFinished(String error, Object result) {
//						showLoading();
//						isSubmiting = false;
//						if (null == error) {
//							submitFeedback((String) result);
//						} else {
//							int err = R.string.dialog_system_error_content;
//							if (error == httpClient.ERR_TIME_OUT) {
//								err = R.string.dialog_network_error_timeout;
//							}
//							if (error == httpClient.ERR_GET_ERR) {
//								err = R.string.dialog_network_error_getdata;
//							}
//							Utils.ShowToast(getActivity(), err);
//						}
//					}
//				});
//				this.httpMethod.start();
//			}
//		} else {
//			Utils.ShowToast(getActivity(), R.string.dialog_network_check_content);
//		}
	}

	private void submitFeedback(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					Utils.Dialog(getActivity(), R.string.dialog_feedback_tip, R.string.dialog_feedback_success, new Utils.Callback() {
						@Override
						public void callFinished() {
							getActivity().onBackPressed();
						}
					});
				} else {
					Utils.Dialog(getActivity(), getString(R.string.dialog_feedback_err), result.optString("msg", ""));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean checkFeedback() {
		boolean bolCheckResult = true;
		Editable editableNewPassword = this.textFeedback.getText();
		if (!editableNewPassword.toString().matches(".{20,500}")) {
			bolCheckResult = false;
			this.textFeedback.requestFocus();
			Utils.Dialog(getActivity(), getString(R.string.dialog_normal_title), getString(R.string.dialog_feedback_toshort));
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
				submitFeedback();
			}
		}
	}
}