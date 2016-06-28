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
import com.shangxiang.android.ShangXiang;
import com.shangxiang.android.pipe.SinhaPipeClient;
import com.shangxiang.android.pipe.SinhaPipeMethod;
import com.shangxiang.android.utils.Utils;
import com.shangxiang.android.wxapi.WXEntryActivity;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PaidOrder extends Activity implements OnClickListener, IWeiboHandler.Response {
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private RelativeLayout layoutLoading;
	private Button buttonBack;
	private Button buttonShowOrder;
	private Button buttonShareto;
	private LinearLayout layoutShareto;
	private TextView buttonSharetoCircle;
	private TextView buttonSharetoFriend;
	private TextView buttonSharetoWeibo;
	private TextView viewContent;
	private LinearLayout layoutSome;
	private Bundle bundle;
	private int orderType = 0;
	private JSONObject orderInfo = new JSONObject();
	private boolean showLoading = false;
	private IWeiboShareAPI weiboShareAPI = null;

	@SuppressLint("InflateParams")
	public void onCreate(Bundle sinha) {
		super.onCreate(sinha);
		setContentView(R.layout.paid_order);

		this.bundle = getIntent().getExtras();
		this.httpClient = new SinhaPipeClient();

		this.weiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Consts.WEIBO_APP_KEY);
		this.weiboShareAPI.registerApp();
		if (sinha != null) {
			this.weiboShareAPI.handleWeiboResponse(getIntent(), this);
		}

		this.layoutLoading = (RelativeLayout) findViewById(R.id.loading);
		this.buttonBack = (Button) findViewById(R.id.paid_order_title_back_button);
		this.buttonBack.setOnClickListener(this);
		this.buttonShowOrder = (Button) findViewById(R.id.paid_order_show_order_button);
		this.buttonShowOrder.setOnClickListener(this);
		this.buttonShareto = (Button) findViewById(R.id.paid_order_shareto_button);
		this.buttonShareto.setOnClickListener(this);
		this.layoutShareto = (LinearLayout) findViewById(R.id.paid_order_shareto_layout);
		this.layoutShareto.setOnClickListener(this);
		this.buttonSharetoCircle = (TextView) findViewById(R.id.paid_order_shareto_circle_button);
		this.buttonSharetoCircle.setOnClickListener(this);
		this.buttonSharetoFriend = (TextView) findViewById(R.id.paid_order_shareto_friend_button);
		this.buttonSharetoFriend.setOnClickListener(this);
		this.buttonSharetoWeibo = (TextView) findViewById(R.id.paid_order_shareto_weibo_button);
		this.buttonSharetoWeibo.setOnClickListener(this);
		this.viewContent = (TextView) findViewById(R.id.paid_order_shareto_content_text);
		this.layoutSome = (LinearLayout) findViewById(R.id.paid_order_some_layout);

		if (null != this.bundle) {
			this.orderType = this.bundle.getInt("order_type");
		}
		if (null != this.bundle && null != this.bundle.getString("orderinfo")) {
			try {
				this.orderInfo = new JSONObject(this.bundle.getString("orderinfo"));
				this.buttonShareto.setTag(this.orderInfo.optString("wishtext", ""));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		loadSomeOrder();

		ShangXiang.wxHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (2 == msg.what) {
					Toast.makeText(PaidOrder.this, getString(R.string.toast_shareto_success), Toast.LENGTH_SHORT).show();
				}
			};
		};
	}

	private void showLoading() {
		Utils.animLoading(this.layoutLoading, !this.showLoading);
		this.showLoading = !this.showLoading;
	}

	private void loadSomeOrder() {
		if (Utils.CheckNetwork()) {
			showLoading();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("num", "3"));

			this.httpClient.Config("get", Consts.URI_SOME_ORDER_LIST, params, true);
			this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
				public void CallFinished(String error, Object result) {
					showLoading();
					if (null == error) {
						loadSomeOrder((String) result);
					} else {
						int err = R.string.dialog_system_error_content;
						if (error == httpClient.ERR_TIME_OUT) {
							err = R.string.dialog_network_error_timeout;
						}
						if (error == httpClient.ERR_GET_ERR) {
							err = R.string.dialog_network_error_getdata;
						}
						Utils.ShowToast(PaidOrder.this, err);
					}
				}
			});
			this.httpMethod.start();
		} else {
			Utils.ShowToast(this, R.string.dialog_network_check_content);
		}
	}

	@SuppressLint("InflateParams")
	private void loadSomeOrder(String s) {
		if (null != s) {
			try {
				LayoutInflater inflater = LayoutInflater.from(this);
				JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					if (null != result.optJSONArray("orderlist")) {
						JSONArray jsonOrders = result.optJSONArray("orderlist");
						for (int i = 0; i < jsonOrders.length(); i++) {
							JSONObject jsonOrder = jsonOrders.optJSONObject(i);
							LinearLayout layoutSomeItem = (LinearLayout) inflater.inflate(R.layout.paid_order_some_item, null);
							String desire = jsonOrder.optString("city", "") + " " + jsonOrder.optString("wishname", "") + " 请" + jsonOrder.optString("buddhistname", "") + "于" + jsonOrder.optString("templename", "") + "祈求" + jsonOrder.optString("wishtype", "") + "，已达成心愿！";
							TextView viewSomeTitle = (TextView) layoutSomeItem.findViewById(R.id.paid_order_some_title_text);
							viewSomeTitle.setText(desire);
//							layoutSomeItem.setTag(desire);
							layoutSomeItem.setTag(this.orderInfo.optString("wishtext", ""));
							layoutSomeItem.setOnClickListener(this);
							this.layoutSome.addView(layoutSomeItem);
						}
					}
				} else {
					Utils.Dialog(this, getString(R.string.dialog_normal_title), result.optString("msg", ""));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onResponse(BaseResponse resp) {
		Toast.makeText(this, getString(R.string.toast_shareto_success), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(View v) {
		if (v == this.buttonBack) {
			finish();
		}
		if (v == this.buttonShowOrder) {
			Bundle bundle = new Bundle();
			bundle.putString("order_id", this.orderInfo.optString("orderid", ""));
			bundle.putInt("order_type", this.orderType);
			bundle.putString("hall_name", this.orderInfo.optString("templename", ""));
			bundle.putString("buddhist_name", this.orderInfo.optString("buddhistname", ""));
			bundle.putString("date", this.orderInfo.optString("buddhadate", ""));
			Intent intent = new Intent(PaidOrder.this, ShowOrderRecord.class);
			intent.putExtras(bundle);
			this.startActivity(intent);
			finish();
		}
		if (v == this.buttonShareto || v.getId() == R.id.paid_order_some_shareto_layout) {
			this.viewContent.setText((String) v.getTag());
			this.layoutShareto.setVisibility(View.VISIBLE);
		}
		if (v == this.buttonSharetoCircle || v == this.buttonSharetoFriend) {
			Bundle bundle = new Bundle();
			bundle.putString("action", "shareto");
			bundle.putString("shareto_content", this.viewContent.getText().toString());
			bundle.putString("shareto_where", v == this.buttonSharetoCircle ? "1" : "0");
			Intent intent = new Intent(PaidOrder.this, WXEntryActivity.class);
			intent.putExtras(bundle);
			this.startActivity(intent);
		}
		if (v == this.buttonSharetoWeibo) {
			TextObject textObject = new TextObject();
			textObject.text = this.viewContent.getText().toString();
			WeiboMessage weiboMessage = new WeiboMessage();
			weiboMessage.mediaObject = textObject;
			SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
			request.transaction = "text" + System.currentTimeMillis();
			request.message = weiboMessage;
			this.weiboShareAPI.sendRequest(this, request);
		}
		if (v == this.layoutShareto || v == this.buttonSharetoCircle || v == this.buttonSharetoFriend || v == this.buttonSharetoWeibo) {
			this.layoutShareto.setVisibility(View.GONE);
		}
	}
}