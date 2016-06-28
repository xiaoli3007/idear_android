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
import com.shangxiang.android.pipe.SinhaPipeClient;
import com.shangxiang.android.pipe.SinhaPipeMethod;
import com.shangxiang.android.utils.Utils;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShowNotice extends BaseFragment {
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private RelativeLayout layoutLoading;
	private Button buttonBack;
	private TextView viewNewsTitle;
	private TextView viewNewsDate;
	private TextView viewNewsContent;
	private boolean showLoading = false;
	private String newsId = "";

	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		View view = inflater.inflate(R.layout.show_notice, null);

		this.httpClient = new SinhaPipeClient();

		this.layoutLoading = (RelativeLayout) view.findViewById(R.id.loading);
		this.buttonBack = (Button) view.findViewById(R.id.show_notice_title_back_button);
		this.buttonBack.setOnClickListener(this);
		this.viewNewsTitle = (TextView) view.findViewById(R.id.show_notice_news_title_text);
		this.viewNewsDate = (TextView) view.findViewById(R.id.show_notice_news_date_text);
		this.viewNewsContent = (TextView) view.findViewById(R.id.show_notice_news_content_text);

		return view;
	}

	public void onActivityCreated(Bundle sinha) {
		super.onActivityCreated(sinha);
		Bundle bundle = getArguments();
		if (null != bundle && !TextUtils.isEmpty(bundle.getString("id"))) {
			this.newsId = bundle.getString("id");
			loadNews();
			this.viewNewsTitle.setText(bundle.getString("title"));
			this.viewNewsDate.setText(bundle.getString("date"));
		}
	}

	private void showLoading() {
		Utils.animLoading(this.layoutLoading, !this.showLoading);
		this.showLoading = !this.showLoading;
	}

	private void loadNews() {
		if (Utils.CheckNetwork()) {
			showLoading();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("smid", this.newsId));

			this.httpClient.Config("get", Consts.URI_NOTICE_DETAIL, params, true);
			this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
				public void CallFinished(String error, Object result) {
					showLoading();
					if (null == error) {
						loadOrderRecord((String) result);
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

	private void loadOrderRecord(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					if (null != result.optJSONObject("sysmsginfo")) {
						JSONObject newsInfo = result.optJSONObject("sysmsginfo");
						this.viewNewsContent.setText(newsInfo.optString("content", ""));
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
	}
}