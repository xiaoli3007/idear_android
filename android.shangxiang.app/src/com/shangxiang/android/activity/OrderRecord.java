package com.shangxiang.android.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.shangxiang.android.Consts;
import com.shangxiang.android.R;
import com.shangxiang.android.BaseFragment;
import com.shangxiang.android.ShangXiang;
import com.shangxiang.android.adapter.OrderRecordAdapter;
import com.shangxiang.android.pipe.SinhaPipeClient;
import com.shangxiang.android.pipe.SinhaPipeMethod;
import com.shangxiang.android.utils.Utils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.AbsListView.OnScrollListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class OrderRecord extends BaseFragment implements OnCheckedChangeListener, OnScrollListener {
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private RelativeLayout layoutLoading;
	private Button buttonBack;
	private RadioButton buttonSwitchDesire;
	private RadioButton buttonSwitchRedeem;
	private ListView viewList;
	private OrderRecordAdapter adapterOrderRecordList;
	private boolean showLoading = false;
	private int orderType = 0;
	private int page = 1;
	private int pageSize = 20;
	private int lastItem = 0;
	private boolean pageEnd = false;

	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		View view = inflater.inflate(R.layout.order_record, null);

		ShangXiang.orderRecordHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					if (Utils.allowClicked()) {
						Intent intent = new Intent(ShangXiang.APP, ShowOrderRecord.class);
						intent.putExtras((Bundle) msg.obj);
						startActivity(intent);
					}
					break;
				case 2:
					adapterOrderRecordList.data = new JSONArray();
					adapterOrderRecordList.notifyDataSetChanged();
					page = 1;
					pageEnd = false;
					loadOrderRecord();
					break;
				default:
					break;
				}
			};
		};

		this.httpClient = new SinhaPipeClient();

		this.layoutLoading = (RelativeLayout) view.findViewById(R.id.loading);
		this.buttonBack = (Button) view.findViewById(R.id.order_record_title_back_button);
		this.buttonBack.setOnClickListener(this);
		this.buttonSwitchDesire = (RadioButton) view.findViewById(R.id.order_record_switch_desire_text);
		this.buttonSwitchDesire.setOnCheckedChangeListener(this);
		this.buttonSwitchRedeem = (RadioButton) view.findViewById(R.id.order_record_switch_redeem_text);
		this.buttonSwitchRedeem.setOnCheckedChangeListener(this);
		this.viewList = (ListView) view.findViewById(R.id.order_record_container);
		this.viewList.setOnScrollListener(this);

		return view;
	}

	public void onActivityCreated(Bundle sinha) {
		super.onActivityCreated(sinha);
		this.adapterOrderRecordList = new OrderRecordAdapter(getActivity());
		this.viewList.setAdapter(this.adapterOrderRecordList);
		this.page = 1;
		this.pageEnd = false;
		loadOrderRecord();
	}

	private void showLoading() {
		Utils.animLoading(this.layoutLoading, !this.showLoading);
		this.showLoading = !this.showLoading;
	}

	private void loadOrderRecord() {
		if (Utils.CheckNetwork()) {
			showLoading();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("p", String.valueOf(this.page)));
			params.add(new BasicNameValuePair("pz", String.valueOf(this.pageSize)));
			params.add(new BasicNameValuePair("mid", ShangXiang.APP.getMemberId()));
			params.add(new BasicNameValuePair("also", String.valueOf(this.orderType)));

			this.httpClient.Config("get", Consts.URI_ORDER_LIST, params, true);
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
					if (null != result.optJSONArray("myorderlist")) {
						if (result.optJSONArray("myorderlist").length() < this.pageSize) {
							this.pageEnd = true;
						}
						for (int i = 0; i < result.optJSONArray("myorderlist").length(); i++) {
							JSONObject order = result.optJSONArray("myorderlist").optJSONObject(i);
							this.adapterOrderRecordList.data.put(order);
						}
						this.adapterOrderRecordList.notifyDataSetChanged();
						this.viewList.setSelection(this.lastItem);
					} else {
						this.pageEnd = true;
					}
				} else {
					this.pageEnd = true;
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
				loadOrderRecord();
			}
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.isPressed() && isChecked) {
			this.adapterOrderRecordList.data = new JSONArray();
			this.adapterOrderRecordList.notifyDataSetChanged();
			this.page = 1;
			this.pageEnd = false;
			if (buttonView == this.buttonSwitchDesire) {
				this.orderType = 0;
				this.adapterOrderRecordList.orderType = 0;
			}
			if (buttonView == this.buttonSwitchRedeem) {
				this.orderType = 1;
				this.adapterOrderRecordList.orderType = 1;
			}
			loadOrderRecord();
		}
	}

	@Override
	public void onClick(View v) {
		if (v == this.buttonBack) {
			getActivity().onBackPressed();
		}
	}
}