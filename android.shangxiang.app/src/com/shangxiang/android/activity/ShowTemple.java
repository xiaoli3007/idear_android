package com.shangxiang.android.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.shangxiang.android.Consts;
import com.shangxiang.android.R;
import com.shangxiang.android.BaseFragment;
import com.shangxiang.android.ShangXiang;
import com.shangxiang.android.imageviewer.ImagePageViewer;
import com.shangxiang.android.pipe.SinhaPipeClient;
import com.shangxiang.android.pipe.SinhaPipeMethod;
import com.shangxiang.android.utils.Utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ShowTemple extends BaseFragment implements OnCheckedChangeListener {
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private RelativeLayout layoutLoading;
	private TextView viewTitle;
	private Button buttonBack;
	private RadioButton buttonSwitchHall;
	private RadioButton buttonSwitchBuddhist;
	private LinearLayout layoutHall;
	private LinearLayout layoutBuddhist;
	private LinearLayout layoutHallThumbs;
	private TextView viewHallName;
	private TextView viewHallDesc;
	private TextView viewHallDesireCount;
	private TextView viewHallContent;
	private ImageButton viewBuddhistThumb;
	private ProgressBar viewBuddhistThumbLoading;
	private TextView viewBuddhistName;
	private TextView viewBuddhistExperience;
	private TextView viewBuddhistContent;
	private Button buttonCreateOrder;
	private boolean showLoading = false;
	private JSONObject temple = new JSONObject();
	private Bundle bundle;
	private String[] thumbs = new String[] {};
	private LayoutInflater inflater;

	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		View view = inflater.inflate(R.layout.show_temple, null);

		this.bundle = getArguments();
		this.httpClient = new SinhaPipeClient();
		this.inflater = LayoutInflater.from(getActivity());

		this.layoutLoading = (RelativeLayout) view.findViewById(R.id.loading);
		this.viewTitle = (TextView) view.findViewById(R.id.show_temple_title_text);
		this.buttonBack = (Button) view.findViewById(R.id.show_temple_title_back_button);
		this.buttonBack.setOnClickListener(this);
		this.buttonSwitchHall = (RadioButton) view.findViewById(R.id.show_temple_switch_hall_text);
		this.buttonSwitchHall.setOnCheckedChangeListener(this);
		this.buttonSwitchBuddhist = (RadioButton) view.findViewById(R.id.show_temple_switch_buddhist_text);
		this.buttonSwitchBuddhist.setOnCheckedChangeListener(this);
		this.layoutHall = (LinearLayout) view.findViewById(R.id.show_temple_hall_layout);
		this.layoutBuddhist = (LinearLayout) view.findViewById(R.id.show_temple_buddhist_layout);
		this.layoutHallThumbs = (LinearLayout) view.findViewById(R.id.show_temple_hall_thumbs_layout);
		this.viewHallName = (TextView) view.findViewById(R.id.show_temple_hall_name_text);
		this.viewHallDesc = (TextView) view.findViewById(R.id.show_temple_hall_desc_text);
		this.viewHallDesireCount = (TextView) view.findViewById(R.id.show_temple_hall_desire_count_text);
		this.viewHallContent = (TextView) view.findViewById(R.id.show_temple_hall_content_text);
		this.viewBuddhistThumb = (ImageButton) view.findViewById(R.id.show_temple_buddhist_thumb_button);
		this.viewBuddhistThumbLoading = (ProgressBar) view.findViewById(R.id.show_temple_buddhist_thumb_loading);
		this.viewBuddhistName = (TextView) view.findViewById(R.id.show_temple_buddhist_name_text);
		this.viewBuddhistExperience = (TextView) view.findViewById(R.id.show_temple_buddhist_experience_text);
		this.viewBuddhistContent = (TextView) view.findViewById(R.id.show_temple_buddhist_content_text);
		this.buttonCreateOrder = (Button) view.findViewById(R.id.show_temple_create_order_button);
		this.buttonCreateOrder.setOnClickListener(this);

		if (null != this.bundle && !TextUtils.isEmpty(this.bundle.getString("temple"))) {
			try {
				this.temple = new JSONObject(bundle.getString("temple"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			this.viewTitle.setText(this.temple.optString("templename", ""));
			this.viewHallName.setText(this.temple.optString("templename", ""));
			this.viewBuddhistName.setText(this.temple.optString("buddhistname", ""));
		}

		return view;
	}

	public void onActivityCreated(Bundle sinha) {
		super.onActivityCreated(sinha);
		if (null != this.bundle && !TextUtils.isEmpty(this.bundle.getString("temple"))) {
			loadTempleInfo();
		}
	}

	private void showLoading() {
		Utils.animLoading(this.layoutLoading, !this.showLoading);
		this.showLoading = !this.showLoading;
	}

	private void loadTempleInfo() {
		if (Utils.CheckNetwork()) {
			showLoading();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("tid", this.temple.optString("templeid", "")));

			this.httpClient.Config("get", Consts.URI_TEMPLE_INFO, params, true);
			this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
				public void CallFinished(String error, Object result) {
					showLoading();
					if (null == error) {
						loadTempleInfo((String) result);
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

	private void loadTempleInfo(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					JSONObject jsonHall = result.optJSONObject("templeinfo");
					JSONArray jsonThumbs = jsonHall.optJSONArray("templepic");
					if (null != jsonThumbs) {
						this.thumbs = new String[jsonThumbs.length()];
						for (int i = 0; i < jsonThumbs.length(); i++) {
							JSONObject jsonThumb = jsonThumbs.optJSONObject(i);
							if (null != jsonThumb) {
								this.thumbs[i] = jsonThumb.optString("pic_path", "");
								this.layoutHallThumbs.addView(createThumb(jsonThumb.optString("pic_tmb_path", "")));
							}
						}
					}
					this.viewHallDesc.setText("（" + jsonHall.optString("province", "") + "，建于公元" + jsonHall.optString("buildtime", "") + "年）");
					this.viewHallDesireCount.setText("（已有" + jsonHall.optString("co_order", "") + "人在此求愿）");
					this.viewHallContent.setText(jsonHall.optString("description", ""));
				} else {
					Utils.Dialog(getActivity(), getString(R.string.dialog_normal_title), result.optString("msg", ""));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		loadBuddhistInfo();
	}

	private void loadBuddhistInfo() {
		if (Utils.CheckNetwork()) {
			showLoading();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("aid", this.temple.optString("attacheid", "")));

			this.httpClient.Config("get", Consts.URI_BUDDHIST_INFO, params, true);
			this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
				public void CallFinished(String error, Object result) {
					showLoading();
					if (null == error) {
						loadBuddhistInfo((String) result);
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

	private void loadBuddhistInfo(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					JSONObject jsonBuddhist = result.optJSONObject("attacheinfo");
					ShangXiang.imageLoader.displayImage(jsonBuddhist.optString("headface", ""), this.viewBuddhistThumb, ShangXiang.imageLoaderOptions, new ImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							viewBuddhistThumbLoading.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
							viewBuddhistThumbLoading.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
							viewBuddhistThumbLoading.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingCancelled(String imageUri, View view) {
							viewBuddhistThumbLoading.setVisibility(View.GONE);
						}
					});
					this.viewBuddhistExperience.setText("[皈依：" + jsonBuddhist.optString("conversion", "") + "年]");
					this.viewBuddhistContent.setText(jsonBuddhist.optString("description", ""));
				} else {
					Utils.Dialog(getActivity(), getString(R.string.dialog_normal_title), result.optString("msg", ""));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressLint("InflateParams")
	private View createThumb(String thumb) {
		View imageLayout = this.inflater.inflate(R.layout.thumb_item_layout, null);
		imageLayout.setPadding(0, 0, 30, 0);
		final ProgressBar progressBar = (ProgressBar) imageLayout.findViewById(R.id.thumb_item_loading);
		final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.thumb_item_view);
		ShangXiang.imageLoader.displayImage(thumb, imageView, ShangXiang.imageLoaderOptions, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				progressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				progressBar.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				progressBar.setVisibility(View.GONE);
				imageView.setTag(thumbs);
				imageView.setOnClickListener(ShowTemple.this);
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				progressBar.setVisibility(View.GONE);
			}
		});
		return imageLayout;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.isPressed() && isChecked) {
			if (buttonView == this.buttonSwitchHall) {
				this.layoutHall.setVisibility(View.VISIBLE);
				this.layoutBuddhist.setVisibility(View.GONE);
			}
			if (buttonView == this.buttonSwitchBuddhist) {
				this.layoutHall.setVisibility(View.GONE);
				this.layoutBuddhist.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v == this.buttonBack) {
			getActivity().onBackPressed();
		}
		if (v == this.buttonCreateOrder) {
			goActivity(CreateOrder.class, this.bundle);
		}
		if (v.getId() == R.id.thumb_item_view) {
			String[] thumbs = (String[]) v.getTag();
			Bundle bundle = new Bundle();
			bundle.putStringArray("thumbs", thumbs);
			goActivity(ImagePageViewer.class, bundle);
		}
	}
}