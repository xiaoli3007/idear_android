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
import com.shangxiang.android.adapter.ListTempleAdapter;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class ListTemple extends BaseFragment {
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private RelativeLayout layoutLoading;
	private Button buttonBack;
	private ListView viewList;
	private ListTempleAdapter adapterListTemple;
	private boolean showLoading = false;
	private Bundle bundle;
	private int desireType = 1;

	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		View view = inflater.inflate(R.layout.list_temple, null);

		this.bundle = getArguments();
		if (null != this.bundle) {
			this.desireType = bundle.getInt("desire_type");
		}

		ShangXiang.listTempleHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				bundle.putString("temple", (String) msg.obj);
				goFragment(new ShowTemple(), bundle);
			};
		};

		this.httpClient = new SinhaPipeClient();

		this.layoutLoading = (RelativeLayout) view.findViewById(R.id.loading);
		this.buttonBack = (Button) view.findViewById(R.id.list_temple_title_back_button);
		this.buttonBack.setOnClickListener(this);
		this.viewList = (ListView) view.findViewById(R.id.list_temple_container);

		return view;
	}

	public void onActivityCreated(Bundle sinha) {
		super.onActivityCreated(sinha);
		this.adapterListTemple = new ListTempleAdapter(getActivity());
		this.viewList.setAdapter(this.adapterListTemple);
		if (ShangXiang.templeList.length() > 0) {
			loadedTempleList(ShangXiang.templeList);
		} else {
			loadTempleList();
		}
	}

	private void showLoading() {
		Utils.animLoading(this.layoutLoading, !this.showLoading);
		this.showLoading = !this.showLoading;
	}

	private void loadTempleList() {
		if (Utils.CheckNetwork()) {
			showLoading();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("p", "1"));
			params.add(new BasicNameValuePair("pz", "1000"));
			params.add(new BasicNameValuePair("wishtype", String.valueOf(this.desireType)));

			this.httpClient.Config("get", Consts.URI_TEMPLE_LIST, params, true);
			this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
				public void CallFinished(String error, Object result) {
					showLoading();
					if (null == error) {
						loadTempleList((String) result);
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

	private void loadTempleList(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					loadedTempleList(result.optJSONArray("wishtemplelist"));
				} else {
					Utils.Dialog(getActivity(), getString(R.string.dialog_normal_title), result.optString("msg", ""));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void loadedTempleList(JSONArray temples) {
		ShangXiang.templeList = temples;
		this.adapterListTemple.data = temples;
		this.adapterListTemple.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		if (v == this.buttonBack) {
			getActivity().onBackPressed();
		}
	}
}