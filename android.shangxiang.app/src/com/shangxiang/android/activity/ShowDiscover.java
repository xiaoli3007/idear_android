package com.shangxiang.android.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.shangxiang.android.Consts;
import com.shangxiang.android.R;
import com.shangxiang.android.BaseFragment;
import com.shangxiang.android.ShangXiang;
import com.shangxiang.android.adapter.ShowDiscoverAdapter;
import com.shangxiang.android.pipe.SinhaPipeClient;
import com.shangxiang.android.pipe.SinhaPipeMethod;
import com.shangxiang.android.utils.Utils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShowDiscover extends BaseFragment {
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private RelativeLayout layoutLoading;
	private Button buttonBack;
	private Button buttonCreateOrder;
	private ProgressBar viewAvatarLoading;
	private ImageButton viewAvatar;
	private TextView viewName;
	private TextView viewDesc;
	private TextView viewContent;
	private TextView viewBlessed;
	private RelativeLayout layoutBlessit;
	private Button buttonBlessit;
	private ListView viewList;
	private ShowDiscoverAdapter adapterShowDiscoverList;
	private Bundle bundle;
	private JSONObject discover = new JSONObject();
	private JSONObject orderInfo = new JSONObject();
	private boolean showLoading = false;
	private boolean isSubmiting = false;
	private boolean showBlessitLoading = false;

	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		View view = inflater.inflate(R.layout.show_discover, null);

		this.bundle = getArguments();
		this.httpClient = new SinhaPipeClient();

		this.layoutLoading = (RelativeLayout) view.findViewById(R.id.loading);
		this.buttonBack = (Button) view.findViewById(R.id.show_discover_title_back_button);
		this.buttonBack.setOnClickListener(this);
		this.buttonCreateOrder = (Button) view.findViewById(R.id.show_discover_create_order_button);
		this.buttonCreateOrder.setOnClickListener(this);
		this.viewAvatar = (ImageButton) view.findViewById(R.id.show_discover_avatar_button);
		this.viewAvatarLoading = (ProgressBar) view.findViewById(R.id.show_discover_avatar_loading);
		this.viewName = (TextView) view.findViewById(R.id.show_discover_name_text);
		this.viewDesc = (TextView) view.findViewById(R.id.show_discover_desc_text);
		this.viewContent = (TextView) view.findViewById(R.id.show_discover_content_text);
		this.viewBlessed = (TextView) view.findViewById(R.id.show_discover_blessed_text);
		this.layoutBlessit = (RelativeLayout) view.findViewById(R.id.show_discover_blessit_layout);
		this.buttonBlessit = (Button) view.findViewById(R.id.show_discover_blessit_button);
		this.viewList = (ListView) view.findViewById(R.id.show_discover_container);

		if (null != this.bundle && !TextUtils.isEmpty(this.bundle.getString("discover"))) {
			try {
				this.discover = new JSONObject(bundle.getString("discover"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			ShangXiang.imageLoader.displayImage(this.discover.optString("headface", ""), this.viewAvatar, ShangXiang.avatarLoaderOptions, new ImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					viewAvatarLoading.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					viewAvatarLoading.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					viewAvatarLoading.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					viewAvatarLoading.setVisibility(View.GONE);
				}
			});
			this.viewName.setText(this.discover.optString("wishname", ""));
			this.viewDesc.setText(this.discover.optString("wishdate", "") + "在" + this.discover.optString("templename", "") + this.discover.optString("alsowish", "") + this.discover.optString("wishtype", ""));
			this.viewContent.setText(this.discover.optString("wishtext", ""));
			this.viewBlessed.setText(Utils.formatBlessed(this.discover.optString("name_blessings", ""), this.discover.optString("co_blessings", "")));
			if (ShangXiang.APP.getLogin() && ("," + this.discover.optString("bleuser", "") + ",").indexOf("," + ShangXiang.APP.getMemberId() + ",") != -1) {
				setBlessitON();
				this.layoutBlessit.setOnClickListener(null);
			} else {
				this.layoutBlessit.setOnClickListener(this);
			}
		}

		return view;
	}

	public void onActivityCreated(Bundle sinha) {
		super.onActivityCreated(sinha);
		this.adapterShowDiscoverList = new ShowDiscoverAdapter(getActivity());
		this.viewList.setAdapter(this.adapterShowDiscoverList);
		if (!"".equals(this.discover.optString("orderid", ""))) {
			loadDiscoverDetail();
		}
	}

	private void showLoading() {
		Utils.animLoading(this.layoutLoading, !this.showLoading);
		this.showLoading = !this.showLoading;
	}

	private void showBlessitLoading() {
		Button buttonBlessitStatus = (Button) this.layoutBlessit.findViewById(R.id.show_discover_blessit_button);
		buttonBlessitStatus.setVisibility(!this.showBlessitLoading ? View.GONE : View.VISIBLE);
		ProgressBar progressBar = (ProgressBar) this.layoutBlessit.findViewById(R.id.progress_bar);
		progressBar.setVisibility(!this.showBlessitLoading ? View.VISIBLE : View.GONE);
		this.showBlessitLoading = !this.showBlessitLoading;
	}

	private void submitBlessit() {
		if (Utils.CheckNetwork()) {
			if (this.isSubmiting) {
				Utils.Dialog(getActivity(), getString(R.string.dialog_tip), getString(R.string.dialog_submiting_content));
			} else {
				this.isSubmiting = true;
				showBlessitLoading();

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("mid", ShangXiang.APP.getMemberId()));
				params.add(new BasicNameValuePair("oid", this.discover.optString("orderid", "")));

				this.httpClient.Config("get", Consts.URI_DISCOVER_BLESSIT, params, true);
				this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
					public void CallFinished(String error, Object result) {
						showBlessitLoading();
						isSubmiting = false;
						if (null == error) {
							submitBlessit((String) result);
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

	private void submitBlessit(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					int doBlessit = Integer.valueOf(ShangXiang.memberInfo.optString("do_blessings", "0"));
					doBlessit += 1;
					ShangXiang.memberInfo.put("do_blessings", String.valueOf(doBlessit));
					setBlessitON();
				} else {
					Utils.Dialog(getActivity(), getString(R.string.dialog_normal_title), result.optString("msg", ""));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void setBlessitON() {
		this.buttonBlessit.setTextColor(getActivity().getResources().getColor(R.color.text_title));
		Drawable imageBlessitPress = getActivity().getResources().getDrawable(R.drawable.button_blessit_pressed);
		imageBlessitPress.setBounds(0, 0, imageBlessitPress.getMinimumWidth(), imageBlessitPress.getMinimumHeight());
		this.buttonBlessit.setCompoundDrawables(imageBlessitPress, null, null, null);
	}

	private void loadDiscoverDetail() {
		if (Utils.CheckNetwork()) {
			showLoading();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("oid", this.discover.optString("orderid", "")));

			this.httpClient.Config("get", Consts.URI_DISCOVER_DETAIL, params, true);
			this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
				public void CallFinished(String error, Object result) {
					showLoading();
					if (null == error) {
						loadDiscoverDetail((String) result);
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

	private void loadDiscoverDetail(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					if (null != result.optJSONObject("orderinfo")) {
						this.orderInfo = result.optJSONObject("orderinfo");
					}
					if ("1".equals(result.optString("isblessings", "0"))) {
						setBlessitON();
					}
					if (null != result.optJSONArray("blessings_members")) {
						this.adapterShowDiscoverList.data = result.optJSONArray("blessings_members");
						this.adapterShowDiscoverList.notifyDataSetChanged();
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
		if (v == this.buttonCreateOrder) {
			if (null != this.orderInfo) {
				Bundle bundle = new Bundle();
				bundle.putInt("order_type", 0);
				bundle.putInt("desire_type", ShangXiang.desireTypeId.get(this.orderInfo.optString("wishtype", ShangXiang.desireTypeName.get(1))));
				bundle.putString("desire_content", this.orderInfo.optString("wishtext", ""));
				bundle.putString("temple", "{\"pic_tmb_path\":\"" + this.orderInfo.optString("templepic_path", "") +
						"\",\"tmb_headface\":\"" + this.orderInfo.optString("attache_headface", "") +
						"\",\"templename\":\"" + this.orderInfo.optString("templename", "") +
						"\",\"buddhistname\":\"" + this.orderInfo.optString("buddhistname", "") +
						"\",\"templeid\":\"" + this.orderInfo.optString("tid", "") +
						"\",\"attacheid\":\"" + this.orderInfo.optString("aid", "") + "\"}");
				Intent intent = new Intent(ShangXiang.APP, CreateOrder.class);
				intent.putExtras(bundle);
				this.startActivity(intent);
			}
		}
		if (v == this.layoutBlessit) {
			if (ShangXiang.APP.getLogin()) {
				submitBlessit();
			} else {
				goActivity(Login.class);
			}
		}
	}
}