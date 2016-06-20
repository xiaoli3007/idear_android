package com.example.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.adapter.DiscoverAdapter;
import com.example.pipe.SinhaPipeClient;
import com.example.pipe.SinhaPipeMethod;
import com.example.utils.Utils;
import com.example.cms.BaseFragment;
import com.example.cms.Consts;
import com.example.cms.R;
import com.example.cms.Cms;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AbsListView.OnScrollListener;

public class Discover extends BaseFragment  {
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private RelativeLayout layoutLoading;
	private Button buttonShowTempleList;
	private LinearLayout layoutTempleList;
	private ListView viewList;
	private DiscoverAdapter adapterDiscoverList;
	private boolean showLoading = false;
	private boolean showTempleList = false;
	private int page = 1;
	private int pageSize = 10;
	private int lastItem = 0;
	private boolean pageEnd = false;

	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		View view = inflater.inflate(R.layout.discover, null);

		this.layoutLoading = (RelativeLayout) view.findViewById(R.id.loading);
		this.buttonShowTempleList = (Button) view.findViewById(R.id.discover_title_button);
		this.buttonShowTempleList.setOnClickListener(this);
		this.layoutTempleList = (LinearLayout) view.findViewById(R.id.discover_temple_list_layout);
		this.viewList = (ListView) view.findViewById(R.id.discover_container);

		Cms.discoverHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Bundle bundle = new Bundle();
				bundle.putString("title", "");
				goActivity(ShowDiscover.class, bundle);
			};
		};

		this.httpClient = new SinhaPipeClient();
		return view;
	}

	public void onActivityCreated(Bundle sinha) {
		super.onActivityCreated(sinha);
		this.adapterDiscoverList = new DiscoverAdapter(getActivity());
		this.viewList.setAdapter(this.adapterDiscoverList);
		this.viewList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				Log.i("aaaa", "--------scrollState----------11---"+scrollState );
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() > view.getCount() - 2 && !pageEnd) {
						page += 1;
						loadDiscover();
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				lastItem=firstVisibleItem;

			}
		});
		loadDiscover();
	}


	private void loadDiscover() {
		if (Utils.CheckNetwork()) {
			showLoading();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("p", String.valueOf(this.page)));
			params.add(new BasicNameValuePair("pz", String.valueOf(this.pageSize)));
			//params.add(new BasicNameValuePair("tid", templeId > 0 ? String.valueOf(templeId) : ""));


			this.httpClient.Config("get", Consts.URI_LIST_INFO, params, true);
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
					if (null != result.optJSONArray("data")) {
						if (result.optJSONArray("data").length() < this.pageSize) {
							this.pageEnd = true;
						}
						for (int i = 0; i < result.optJSONArray("data").length(); i++) {
							JSONObject discover = result.optJSONArray("data").optJSONObject(i);
							this.adapterDiscoverList.data.put(discover);
						}
						this.adapterDiscoverList.notifyDataSetChanged();
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




	private void showLoading() {
		Log.i("aaaa", "--------showLoading----------11---" );
		Utils.animView(this.layoutLoading, !this.showLoading);
		this.showLoading = !this.showLoading;
	}

	@Override
	public void onClick(View v) {
		if (v == this.buttonShowTempleList) {
			this.layoutTempleList.setVisibility(!this.showTempleList ? View.VISIBLE : View.GONE);
			this.showTempleList = !this.showTempleList;
		}
	}


}