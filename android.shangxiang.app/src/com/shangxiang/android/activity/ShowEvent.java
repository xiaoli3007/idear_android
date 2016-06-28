package com.shangxiang.android.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShowEvent extends BaseFragment {
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private RelativeLayout layoutLoading;
	private Button buttonBack;
	private Button buttonDelete;
	private Button buttonEdit;
	private Button buttonDesire;
	private TextView viewTitle;
	private TextView viewDate;
	private Bundle bundle;
	private JSONObject jsonEvent = new JSONObject();
	private boolean showLoading = false;

	@SuppressLint({ "InflateParams", "SimpleDateFormat" })
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		View view = inflater.inflate(R.layout.show_event, null);
		
		this.httpClient = new SinhaPipeClient();

		this.layoutLoading = (RelativeLayout) view.findViewById(R.id.loading);
		this.buttonBack = (Button) view.findViewById(R.id.show_event_title_back_button);
		this.buttonBack.setOnClickListener(this);
		this.buttonDelete = (Button) view.findViewById(R.id.show_event_delete_button);
		this.buttonDelete.setOnClickListener(this);
		this.buttonEdit = (Button) view.findViewById(R.id.show_event_edit_button);
		this.buttonEdit.setOnClickListener(this);
		this.buttonDesire = (Button) view.findViewById(R.id.show_event_desire_button);
		this.buttonDesire.setOnClickListener(this);
		this.viewTitle = (TextView) view.findViewById(R.id.show_event_content_title_text);
		this.viewDate = (TextView) view.findViewById(R.id.show_event_content_date_text);
		
		this.bundle = getArguments();
		if (null != this.bundle && !TextUtils.isEmpty(this.bundle.getString("event"))) {
			try {
				this.jsonEvent = new JSONObject(this.bundle.getString("event"));
				this.viewTitle.setText(this.jsonEvent.optString("name", ""));
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
				long time = Long.valueOf(this.jsonEvent.optString("date", ""));
				this.viewDate.setText(formatter.format(new Date(time * 1000L)));
			} catch (JSONException e) {
			}
		}

		return view;
	}

	public void onActivityCreated(Bundle sinha) {
		super.onActivityCreated(sinha);
	}

	private void showLoading() {
		Utils.animLoading(this.layoutLoading, !this.showLoading);
		this.showLoading = !this.showLoading;
	}

	private void DeleteEvent() {
		if (Utils.CheckNetwork()) {
			showLoading();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("crid", this.jsonEvent.optString("id")));

			this.httpClient.Config("get", Consts.URI_CALENDAR_DELETE_EVENT, params, true);
			this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
				public void CallFinished(String error, Object result) {
					showLoading();
					if (null == error) {
						DeleteEvent((String) result);
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

	private void DeleteEvent(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					getActivity().onBackPressed();
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
		if (v == this.buttonDelete) {
			Utils.Dialog(getActivity(), R.string.dialog_order_delete_tip, R.string.dialog_order_delete_success, new Utils.Callback() {
				@Override
				public void callFinished() {
					DeleteEvent();
				}
			}, new Utils.Callback() {
				@Override
				public void callFinished() {
				}
			}, new Utils.Callback() {
				@Override
				public void callFinished() {
				}
			});
		}
		if (v == this.buttonEdit) {
			goFragment(new ModifyEvent(), this.bundle);
		}
		if (v == this.buttonDesire) {
			Message msg = ShangXiang.tabHandler.obtainMessage(0);
			ShangXiang.tabHandler.sendMessage(msg);
		}
	}
}