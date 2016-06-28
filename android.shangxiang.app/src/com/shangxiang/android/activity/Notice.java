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
import com.shangxiang.android.adapter.NoticeAdapter;
import com.shangxiang.android.pipe.SinhaPipeClient;
import com.shangxiang.android.pipe.SinhaPipeMethod;
import com.shangxiang.android.utils.Utils;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AbsListView.OnScrollListener;

public class Notice extends BaseFragment implements OnScrollListener {
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private RelativeLayout layoutLoading;
	private Button buttonBack;
	private ListView viewList;
	private NoticeAdapter adapterNoticeList;
	private boolean showLoading = false;
	private int page = 1;
	private int pageSize = 20;
	private int lastItem = 0;
	private boolean pageEnd = false;

	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		View view = inflater.inflate(R.layout.notice, null);

		ShangXiang.noticeHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				goFragment(new ShowNotice(), (Bundle) msg.obj);
			};
		};

		this.httpClient = new SinhaPipeClient();

		this.layoutLoading = (RelativeLayout) view.findViewById(R.id.loading);
		this.buttonBack = (Button) view.findViewById(R.id.notice_title_back_button);
		this.buttonBack.setOnClickListener(this);
		this.viewList = (ListView) view.findViewById(R.id.notice_container);
		this.viewList.setOnScrollListener(this);

		return view;
	}

	public void onActivityCreated(Bundle sinha) {
		super.onActivityCreated(sinha);
		this.adapterNoticeList = new NoticeAdapter(getActivity());
		this.viewList.setAdapter(this.adapterNoticeList);
		loadNotice();
	}

	private void showLoading() {
		Utils.animLoading(this.layoutLoading, !this.showLoading);
		this.showLoading = !this.showLoading;
	}

	private void loadNotice() {
		if (Utils.CheckNetwork()) {
			showLoading();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("p", String.valueOf(this.page)));
			params.add(new BasicNameValuePair("pz", String.valueOf(this.pageSize)));

			this.httpClient.Config("get", Consts.URI_NOTICE_LIST, params, true);
			this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
				public void CallFinished(String error, Object result) {
					showLoading();
					if (null == error) {
						loadNotice((String) result);
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

	private void loadNotice(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					if (null != result.optJSONArray("sysmsglist")) {
						if (result.optJSONArray("sysmsglist").length() < this.pageSize) {
							this.pageEnd = true;
						}
						for (int i = 0; i < result.optJSONArray("sysmsglist").length(); i++) {
							JSONObject notice = result.optJSONArray("sysmsglist").optJSONObject(i);
							this.adapterNoticeList.data.put(notice);
						}
						this.adapterNoticeList.notifyDataSetChanged();
						this.viewList.setSelection(this.lastItem);
					} else {
						this.pageEnd = true;
					}
				} else {
					this.pageEnd = true;
					Utils.Dialog(getActivity(), getString(R.string.dialog_normal_title), result.optString("msg", ""));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		this.lastItem = firstVisibleItem;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			if (view.getLastVisiblePosition() > view.getCount() - 2 && !this.pageEnd) {
				this.page += 1;
				loadNotice();
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