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
import com.shangxiang.android.adapter.DiscoverAdapter;
import com.shangxiang.android.pipe.SinhaPipeClient;
import com.shangxiang.android.pipe.SinhaPipeMethod;
import com.shangxiang.android.utils.Utils;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
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

public class MyDiscover extends BaseFragment implements OnCheckedChangeListener, OnScrollListener {
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private RelativeLayout layoutLoading;
	private Button buttonBack;
	private RadioButton buttonSwitchToother;
	private RadioButton buttonSwitchTome;
	private ListView viewList;
	private DiscoverAdapter adapterDiscoverList;
	private boolean showLoading = false;
	private int discoverType = 1;
	private int page = 1;
	private int pageSize = 20;
	private int lastItem = 0;
	private boolean pageEnd = false;

	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		View view = inflater.inflate(R.layout.my_discover, null);

		ShangXiang.discoverHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					break;
				case 2:
					int position = (Integer) ((Button) msg.obj).getTag();
					JSONObject discover = adapterDiscoverList.data.optJSONObject(position);
					Bundle bundle = new Bundle();
					bundle.putString("discover", discover.toString());
					goFragment(new ShowDiscover(), bundle);
					break;
				default:
					break;
				}
			};
		};

		this.httpClient = new SinhaPipeClient();

		this.layoutLoading = (RelativeLayout) view.findViewById(R.id.loading);
		this.buttonBack = (Button) view.findViewById(R.id.my_discover_title_back_button);
		this.buttonBack.setOnClickListener(this);
		this.buttonSwitchToother = (RadioButton) view.findViewById(R.id.my_discover_switch_toother_text);
		this.buttonSwitchToother.setOnCheckedChangeListener(this);
		this.buttonSwitchTome = (RadioButton) view.findViewById(R.id.my_discover_switch_tome_text);
		this.buttonSwitchTome.setOnCheckedChangeListener(this);
		this.viewList = (ListView) view.findViewById(R.id.my_discover_container);
		this.viewList.setOnScrollListener(this);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		this.buttonSwitchToother.setText(Html.fromHtml("我的加持 <font color=\"#dca358\">" + ShangXiang.memberInfo.optString("do_blessings", "") + "</font>"));
		this.buttonSwitchTome.setText(Html.fromHtml("为我加持 <font color=\"#dca358\">" + ShangXiang.memberInfo.optString("received_blessings", "") + "</font>"));
	}

	public void onActivityCreated(Bundle sinha) {
		super.onActivityCreated(sinha);
		this.adapterDiscoverList = new DiscoverAdapter(getActivity());
		this.viewList.setAdapter(this.adapterDiscoverList);

		Bundle bundle = getArguments();
		if (null != bundle && !TextUtils.isEmpty(bundle.getString("discover_type"))) {
			this.page = 1;
			if ("2".equals(bundle.getString("discover_type"))) {
				this.discoverType = 2;
				this.buttonSwitchTome.performClick();
			} else {
				this.discoverType = 1;
				this.buttonSwitchToother.performClick();
			}
		}
		loadDiscover();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
		} else {
		}
	}

	private void showLoading() {
		Utils.animLoading(this.layoutLoading, !this.showLoading);
		this.showLoading = !this.showLoading;
	}

	private void loadDiscover() {
		if (Utils.CheckNetwork()) {
			showLoading();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("p", String.valueOf(this.page)));
			params.add(new BasicNameValuePair("pz", String.valueOf(this.pageSize)));
			params.add(new BasicNameValuePair("tid", ""));
			params.add(new BasicNameValuePair("mid", ShangXiang.APP.getMemberId()));
			params.add(new BasicNameValuePair("bless", String.valueOf(this.discoverType)));

			this.httpClient.Config("get", Consts.URI_DISCOVER_LIST, params, true);
			this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
				public void CallFinished(String error, Object result) {
					showLoading();
					if (null == error) {
						loadDiscover((String) result);
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

	private void loadDiscover(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					if (null != result.optJSONArray("orderlist")) {
						if (result.optJSONArray("orderlist").length() < this.pageSize) {
							this.pageEnd = true;
						}
						for (int i = 0; i < result.optJSONArray("orderlist").length(); i++) {
							JSONObject discover = result.optJSONArray("orderlist").optJSONObject(i);
							this.adapterDiscoverList.data.put(discover);
						}
						this.adapterDiscoverList.notifyDataSetChanged();
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
				loadDiscover();
			}
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.isPressed() && isChecked) {
			this.adapterDiscoverList.data = new JSONArray();
			this.adapterDiscoverList.notifyDataSetChanged();
			this.page = 1;
			this.pageEnd = false;
			if (buttonView == this.buttonSwitchToother) {
				this.discoverType = 1;
			}
			if (buttonView == this.buttonSwitchTome) {
				this.discoverType = 2;
			}
			loadDiscover();
		}
	}

	@Override
	public void onClick(View v) {
		if (v == this.buttonBack) {
			getActivity().onBackPressed();
		}
	}
}