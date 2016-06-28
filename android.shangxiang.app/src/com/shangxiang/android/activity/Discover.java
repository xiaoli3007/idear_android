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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.AbsListView.OnScrollListener;
import android.widget.TextView;

public class Discover extends BaseFragment implements OnScrollListener {
	private LayoutInflater inflater;
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private RelativeLayout layoutLoading;
	private Button buttonShowTempleList;
	private TextView viewShowTempleList;
	private LinearLayout layoutTempleList;
	private ListView viewList;
	private DiscoverAdapter adapterDiscoverList;
	private boolean showLoading = false;
	private boolean isSubmiting = false;
	private boolean showBlessitLoading = false;
	private boolean showTempleList = false;
	private int currType = 0;
	private int page = 1;
	private int pageSize = 20;
	private int lastItem = 0;
	private boolean pageEnd = false;

	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		this.inflater = inflater;
		View view = this.inflater.inflate(R.layout.discover, null);

		ShangXiang.discoverHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					if (ShangXiang.APP.getLogin()) {
						submitBlessit((RelativeLayout) msg.obj);
					} else {
						goActivity(Login.class);
					}
					break;
				case 2:
					if (Utils.allowClicked()) {
						int position = (Integer) ((Button) msg.obj).getTag();
						JSONObject discover = adapterDiscoverList.data.optJSONObject(position);
						Bundle bundle = new Bundle();
						bundle.putString("discover", discover.toString());
						goFragment(new ShowDiscover(), bundle);
					}
					break;
				default:
					break;
				}
			};
		};

		this.httpClient = new SinhaPipeClient();

		this.layoutLoading = (RelativeLayout) view.findViewById(R.id.loading);
		this.buttonShowTempleList = (Button) view.findViewById(R.id.discover_title_button);
		this.buttonShowTempleList.setOnClickListener(this);
		this.viewShowTempleList = (TextView) view.findViewById(R.id.discover_title_text);
		this.layoutTempleList = (LinearLayout) view.findViewById(R.id.discover_temple_list_layout);
		this.layoutTempleList.setOnClickListener(this);
		this.viewList = (ListView) view.findViewById(R.id.discover_container);
		this.viewList.setOnScrollListener(this);

		return view;
	}

	public void onActivityCreated(Bundle sinha) {
		super.onActivityCreated(sinha);
		this.adapterDiscoverList = new DiscoverAdapter(getActivity());
		this.viewList.setAdapter(this.adapterDiscoverList);
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
			params.add(new BasicNameValuePair("pz", "20"));
			params.add(new BasicNameValuePair("wishtype", ""));

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
					if (null != result.optJSONArray("wishtemplelist")) {
						loadedTempleList(result.optJSONArray("wishtemplelist"));
					}
				} else {
					Utils.Dialog(getActivity(), getString(R.string.dialog_normal_title), result.optString("msg", ""));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressLint("InflateParams")
	private void loadedTempleList(JSONArray temples) {
		this.layoutTempleList.removeAllViews();
		this.page = 1;
		this.pageEnd = false;
		this.currType = 0;
		ShangXiang.templeList = temples;
		for (int i = -1; i < temples.length(); i++) {
			JSONObject temple = temples.optJSONObject(i);
			LinearLayout viewListTemple = (LinearLayout) this.inflater.inflate(R.layout.discover_list_temple_item, null);
			Button buttonShowTemple = (Button) viewListTemple.findViewById(R.id.discover_temple_list_button);
			if (i == -1) {
				buttonShowTemple.setTag(this.currType);
				buttonShowTemple.setText("全部道场");
			} else {
				buttonShowTemple.setTag(temple.optInt("templeid", 0));
				buttonShowTemple.setText(temple.optString("templename", ""));
			}
			buttonShowTemple.setOnClickListener(this);
			this.layoutTempleList.addView(viewListTemple);
		}
		loadDiscover(this.currType);
	}

	private void loadDiscover(int templeId) {
		if (Utils.CheckNetwork()) {
			showLoading();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("p", String.valueOf(this.page)));
			params.add(new BasicNameValuePair("pz", String.valueOf(this.pageSize)));
			params.add(new BasicNameValuePair("tid", templeId > 0 ? String.valueOf(templeId) : ""));
			params.add(new BasicNameValuePair("mid", ShangXiang.APP.getMemberId()));
			params.add(new BasicNameValuePair("bless", "0"));

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
					Utils.Dialog(getActivity(), getString(R.string.dialog_normal_title), result.optString("msg", ""));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void showBlessitLoading(RelativeLayout layoutBlessit) {
		Button buttonBlessitStatus = (Button) layoutBlessit.findViewById(R.id.discover_listitem_blessit_button);
		buttonBlessitStatus.setVisibility(!this.showBlessitLoading ? View.GONE : View.VISIBLE);
		ProgressBar progressBar = (ProgressBar) layoutBlessit.findViewById(R.id.progress_bar);
		progressBar.setVisibility(!this.showBlessitLoading ? View.VISIBLE : View.GONE);
		this.showBlessitLoading = !this.showBlessitLoading;
	}

	private void submitBlessit(final RelativeLayout layoutBlessit) {
		if (Utils.CheckNetwork()) {
			if (this.isSubmiting) {
				Utils.Dialog(getActivity(), getString(R.string.dialog_tip), getString(R.string.dialog_submiting_content));
			} else {
				this.isSubmiting = true;
				showBlessitLoading(layoutBlessit);

				int position = (Integer) layoutBlessit.getTag();
				JSONObject discover = this.adapterDiscoverList.data.optJSONObject(position);

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("mid", ShangXiang.APP.getMemberId()));
				params.add(new BasicNameValuePair("oid", discover.optString("orderid", "")));

				this.httpClient.Config("get", Consts.URI_DISCOVER_BLESSIT, params, true);
				this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
					public void CallFinished(String error, Object result) {
						showBlessitLoading(layoutBlessit);
						isSubmiting = false;
						if (null == error) {
							submitBlessit(layoutBlessit, (String) result);
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

	private void submitBlessit(RelativeLayout layoutBlessit, String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					int doBlessit = Integer.valueOf(ShangXiang.memberInfo.optString("do_blessings", "0"));
					doBlessit += 1;
					ShangXiang.memberInfo.put("do_blessings", String.valueOf(doBlessit));
					
					int position = (Integer) layoutBlessit.getTag();
					JSONObject discover = this.adapterDiscoverList.data.optJSONObject(position);
					String blessUser = discover.optString("bleuser", "") + "," + ShangXiang.APP.getMemberId();
					discover.put("bleuser", blessUser);
					this.adapterDiscoverList.data.put(position, discover);

					Button buttonBlessitStatus = (Button) layoutBlessit.findViewById(R.id.discover_listitem_blessit_button);
					buttonBlessitStatus.setTextColor(getActivity().getResources().getColor(R.color.text_title));
					Drawable imageBlessitPress = getActivity().getResources().getDrawable(R.drawable.button_blessit_pressed);
					imageBlessitPress.setBounds(0, 0, imageBlessitPress.getMinimumWidth(), imageBlessitPress.getMinimumHeight());
					buttonBlessitStatus.setCompoundDrawables(imageBlessitPress, null, null, null);
				} else {
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
				loadDiscover(this.currType);
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v == this.buttonShowTempleList) {
			this.layoutTempleList.setVisibility(!this.showTempleList ? View.VISIBLE : View.GONE);
			this.showTempleList = !this.showTempleList;
		}
		if (v.getId() == R.id.discover_temple_list_button) {
			this.viewShowTempleList.setText(((Button) v).getText());
			this.adapterDiscoverList.data = new JSONArray();
			this.adapterDiscoverList.notifyDataSetChanged();
			this.page = 1;
			this.pageEnd = false;
			this.currType = (Integer) v.getTag();
			loadDiscover(this.currType);
		}
		if (v == this.layoutTempleList || v.getId() == R.id.discover_temple_list_button) {
			this.layoutTempleList.setVisibility(View.GONE);
			this.showTempleList = false;
		}
	}
}