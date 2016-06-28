package com.shangxiang.android.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.shangxiang.android.Consts;
import com.shangxiang.android.R;
import com.shangxiang.android.BaseFragment;
import com.shangxiang.android.ShangXiang;
import com.shangxiang.android.pipe.SinhaPipeClient;
import com.shangxiang.android.pipe.SinhaPipeMethod;
import com.shangxiang.android.utils.Utils;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

public class Settings extends BaseFragment {
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private RelativeLayout layoutLoading;
	private Button buttonBack;
	private CheckBox buttonMenuShowPush;
	private CheckBox buttonMenuObtainLocation;
	private boolean showLoading = false;
	private boolean isSubmiting = false;

	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		View view = inflater.inflate(R.layout.settings, null);

		this.httpClient = new SinhaPipeClient();

		this.layoutLoading = (RelativeLayout) view.findViewById(R.id.loading);
		this.buttonBack = (Button) view.findViewById(R.id.settings_title_back_button);
		this.buttonBack.setOnClickListener(this);
		this.buttonMenuShowPush = (CheckBox) view.findViewById(R.id.settings_menu_show_push_checkbox);
		this.buttonMenuShowPush.setOnClickListener(this);
		this.buttonMenuObtainLocation = (CheckBox) view.findViewById(R.id.settings_menu_obtain_region_checkbox);
		this.buttonMenuObtainLocation.setOnClickListener(this);

		return view;
	}

	public void onActivityCreated(Bundle sinha) {
		super.onActivityCreated(sinha);
		if (ShangXiang.APP.getLogin()) {
			this.buttonMenuShowPush.setChecked("1".equals(ShangXiang.memberInfo.optString("isflremind", ShangXiang.APP.getShowPush() ? "1" : "0")) ? true : false);
		} else {
			this.buttonMenuShowPush.setChecked(ShangXiang.APP.getShowPush());
		}
		this.buttonMenuObtainLocation.setChecked(ShangXiang.APP.getObtainLocation());
	}

	private void showLoading() {
		Utils.animLoading(this.layoutLoading, !this.showLoading);
		this.showLoading = !this.showLoading;
	}

	private void saveSettings(final int action, final boolean setting) {
		if (Utils.CheckNetwork()) {
			this.isSubmiting = true;
			showLoading();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("mid", ShangXiang.APP.getMemberId()));
			params.add(new BasicNameValuePair("remind", setting ? "1" : "0"));

			String url = action == 1 ? Consts.URI_SETTINGS_SHOW_PUSH : Consts.URI_SETTINGS_SHOW_PUSH;

			this.httpClient.Config("get", url, params, true);
			this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
				public void CallFinished(String error, Object result) {
					showLoading();
					isSubmiting = false;
					if (null == error) {
						saveSettings(action, setting, (String) result);
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
		} else {
			Utils.ShowToast(getActivity(), R.string.dialog_network_check_content);
		}
	}

	private void saveSettings(int action, boolean setting, String s) {
		if (null != s) {
			try {
				final JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					if (action == 1) {
						ShangXiang.APP.setObtainLocation(setting);
					} else {
						ShangXiang.memberInfo.put("isflremind", setting ? "1" : "0");
						ShangXiang.APP.setConfig(ShangXiang.memberInfo.toString());
						ShangXiang.APP.setShowPush(setting);
					}
				} else {
					Utils.Dialog(getActivity(), getString(R.string.dialog_normal_title), result.optString("msg", ""));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v == this.buttonBack) {
			getActivity().onBackPressed();
		}
		if (v == this.buttonMenuShowPush) {
			if (ShangXiang.APP.getLogin()) {
				if (this.isSubmiting) {
					Utils.Dialog(getActivity(), getString(R.string.dialog_tip), getString(R.string.dialog_submiting_content));
				} else {
					saveSettings(0, this.buttonMenuShowPush.isChecked());
				}
			} else {
				ShangXiang.APP.setShowPush(this.buttonMenuShowPush.isChecked());
			}
		}
		if (v == this.buttonMenuObtainLocation) {
			boolean obtainLocation = ShangXiang.APP.getObtainLocation();
			this.buttonMenuObtainLocation.setChecked(!obtainLocation);
			ShangXiang.APP.setObtainLocation(!obtainLocation);
		}
	}
}