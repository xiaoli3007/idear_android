package com.wyj.Activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wyj.Activity.R;

import com.wyj.dataprocessing.BitmapManager;
import com.wyj.dataprocessing.RegularUtil;
import com.wyj.http.WebApiUrl;
import com.wyj.pipe.Cms;
import com.wyj.pipe.SinhaPipeClient;
import com.wyj.pipe.SinhaPipeMethod;
import com.wyj.pipe.Utils;
import com.wyj.popupwindow.MyPopupWindows;
import com.wyj.popupwindow.MyPopupWindowsCity;
import com.wyj.popupwindow.MyPopupWindowsDate;
import com.wyj.popupwindow.MyPopupWindowsIncense;
import com.wyj.utils.StingUtil;
import com.wyj.utils.Tools;

import android.app.Activity;

import android.app.ProgressDialog;

import android.content.Intent;

import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.ScrollView;
import android.widget.TextView;

public class OrderForm extends Activity implements OnClickListener {

	private static String TAG = "OrderForm";

	private ImageView order_form_back, order_form_image_1, order_form_image_2;
	private LinearLayout order_form_layout_wish_xiangtypes;
	private ProgressDialog pDialog = null;
	private Button order_form_wish_button, order_form_submit;
	private TextView buttonShowContentSelect;
	private LinearLayout create_order_select_location_layout;
	private ScrollView ScrollView_form;
	private EditText order_form_layout_wish_content_input;

	private TextView order_form_hall_name_text, order_form_buddhist_name_text;
	private TextView order_form_layout_wish_address_input;
	private TextView order_form_layout_wish_date_input;
	private TextView order_form_layout_wish_xiangtype_input,
			order_form_layout_wish_xiangtype_input_price;
	private EditText order_form_layout_wish_people_input,
			order_form_layout_wish_phone_input;

	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;

	private String[] wishcontent_data;
	private JSONArray citylist_data;
	private JSONObject intent_info;
	private JSONArray incense_data;

	private int wishtype, aid, tid, mid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_form);

		Intent intent = this.getIntent();
		Bundle bu = intent.getExtras();
		wishtype = bu.getInt("wishtype");
		aid = bu.getInt("aid");
		tid = bu.getInt("tid");
		mid = Integer.valueOf(Cms.APP.getMemberId()).intValue();

		findViewById();
		setListener();
		try {
			intent_info = new JSONObject(bu.getString("info"));
			UI_update(intent_info);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void UI_update(JSONObject intent_info2) {
		// TODO Auto-generated method stub

		order_form_hall_name_text.setText(intent_info2.optString("templename",
				"") + "(" + intent_info2.optString("province", "") + ")");

		String buddhistname = (!intent_info2.optString("buddhistname", "")
				.equals("")) ? intent_info2.optString("buddhistname", "")
				: intent_info2.optString("attachename", "");
		order_form_buddhist_name_text.setText(buddhistname);

		BitmapManager.getInstance().loadBitmap(
				intent_info2.optString("tmb_headface", ""), order_form_image_2,
				Tools.readBitmap(OrderForm.this, R.drawable.temp2));
		BitmapManager.getInstance().loadBitmap(
				intent_info2.optString("pic_tmb_path", ""), order_form_image_1,
				Tools.readBitmap(OrderForm.this, R.drawable.temp1));

	}

	private void findViewById() {

		order_form_back = (ImageView) findViewById(R.id.order_form_back);
		order_form_image_1 = (ImageView) findViewById(R.id.order_form_image_1);
		order_form_image_2 = (ImageView) findViewById(R.id.order_form_image_2);
		order_form_hall_name_text = (TextView) findViewById(R.id.order_form_hall_name_text);
		order_form_buddhist_name_text = (TextView) findViewById(R.id.order_form_buddhist_name_text);

		order_form_submit = (Button) findViewById(R.id.order_form_submit);
		buttonShowContentSelect = (TextView) findViewById(R.id.order_form_layout_wish_content_head_right); // 显示祝福语的点击按钮
		ScrollView_form = (ScrollView) findViewById(R.id.order_form_ScrollView);

		order_form_wish_button = (Button) findViewById(R.id.order_form_wish_button);

		order_form_wish_button.setText(WebApiUrl.wishtypes[wishtype - 1]);

		order_form_layout_wish_xiangtypes = (LinearLayout) findViewById(R.id.order_form_layout_wish_xiangtypes);
		// 表单所填字段
		order_form_layout_wish_people_input = (EditText) findViewById(R.id.order_form_layout_wish_people_input);
		if (!TextUtils.isEmpty(Cms.memberInfo.optString("truename", ""))) {
			order_form_layout_wish_people_input.setText(Cms.memberInfo
					.optString("truename", ""));
		}
		order_form_layout_wish_phone_input = (EditText) findViewById(R.id.order_form_layout_wish_phone_input);
		if (!TextUtils.isEmpty(Cms.memberInfo.optString("mobile", ""))) {
			order_form_layout_wish_phone_input.setText(Cms.memberInfo
					.optString("mobile", ""));
		}
		order_form_layout_wish_content_input = (EditText) findViewById(R.id.order_form_layout_wish_content_input); // 内容
		order_form_layout_wish_address_input = (TextView) findViewById(R.id.order_form_layout_wish_address_input);// 地址
		order_form_layout_wish_date_input = (TextView) findViewById(R.id.order_form_layout_wish_date_input);// 日期
		order_form_layout_wish_xiangtype_input = (TextView) findViewById(R.id.order_form_layout_wish_xiangtype_input);// 商品
		order_form_layout_wish_xiangtype_input_price = (TextView) findViewById(R.id.order_form_layout_wish_xiangtype_input_price);// 价格
	}

	private void setListener() {
		buttonShowContentSelect.setOnClickListener(this);
		order_form_back.setOnClickListener(this);
		order_form_submit.setOnClickListener(this);
		order_form_layout_wish_address_input.setOnClickListener(this);
		order_form_layout_wish_date_input.setOnClickListener(this);
		order_form_layout_wish_xiangtypes.setOnClickListener(this);

		this.httpClient = new SinhaPipeClient();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.order_form_back:
			Intent bak_My_intent = new Intent(OrderForm.this, Wish.class);
			WishGroupTab.getInstance().switchActivity("Wish",
					bak_My_intent, -1, -1);
			break;
		case R.id.order_form_submit:
			// 提交订单
			submit_form_order();

			break;
		case R.id.order_form_layout_wish_content_head_right:

			if (wishcontent_data != null) {
				new MyPopupWindows(OrderForm.this,
						order_form_layout_wish_content_input, getParent()
								.getParent(), wishcontent_data);
			} else {
				loadWishcontent();
			}

			break;
		case R.id.order_form_layout_wish_address_input:

			if (citylist_data != null) {
				new MyPopupWindowsCity(OrderForm.this,
						order_form_layout_wish_address_input, getParent()
								.getParent(), citylist_data);
			} else {
				loadCity();
			}

			break;
		case R.id.order_form_layout_wish_date_input:

			new MyPopupWindowsDate(OrderForm.this,
					order_form_layout_wish_date_input, getParent().getParent());

			break;
		case R.id.order_form_layout_wish_xiangtypes:

			if (incense_data != null) {
				new MyPopupWindowsIncense(OrderForm.this,
						order_form_layout_wish_xiangtype_input,
						order_form_layout_wish_xiangtype_input_price,
						getParent().getParent(), incense_data);
			} else {
				loadIncense();
			}

			break;

		}
	}

	private void submit_form_order() {
		// TODO Auto-generated method stub

		if (TextUtils.isEmpty(order_form_layout_wish_people_input.getText()
				.toString())) {

			// RegularUtil.alert_msg(OrderForm.this, "求愿人不能为空");
			Utils.Dialog(getParent().getParent(), "提示", "求愿人不能为空");
			// 设置焦点信息;
			order_form_layout_wish_people_input.setFocusable(true);
			order_form_layout_wish_people_input.setFocusableInTouchMode(true);
			order_form_layout_wish_people_input.requestFocus();
			order_form_layout_wish_people_input.requestFocusFromTouch();
		} else if (!RegularUtil.phoneFormat(order_form_layout_wish_phone_input
				.getText().toString())) {

			Utils.Dialog(getParent().getParent(), "提示", "手机号码错误");
			// 设置焦点信息;
			order_form_layout_wish_phone_input.setFocusable(true);
			order_form_layout_wish_phone_input.setFocusableInTouchMode(true);
			order_form_layout_wish_phone_input.requestFocus();
			order_form_layout_wish_phone_input.requestFocusFromTouch();

		} else if (TextUtils.isEmpty(order_form_layout_wish_content_input
				.getText().toString())) {

			Utils.Dialog(getParent().getParent(), "提示", "求愿内容不能为空");

		} else if (TextUtils.isEmpty(order_form_layout_wish_xiangtype_input
				.getText().toString())) {

			Utils.Dialog(getParent().getParent(), "提示", "香烛类型不能为空");

		} else if (TextUtils.isEmpty(order_form_layout_wish_date_input
				.getText().toString())) {

			Utils.Dialog(getParent().getParent(), "提示", "时间不能为空");

		} else if (TextUtils.isEmpty(order_form_layout_wish_address_input
				.getText().toString())) {

			Utils.Dialog(getParent().getParent(), "提示", "地址不能为空");

		} else {

			submit_order_to_server();
		}

	}

	private void submit_order_to_server() {
		if (Utils.CheckNetwork()) {
			showLoading();
			// String wishgrade =
			// order_form_layout_wish_xiangtype_input.getText()
			// .toString();
			String wishgrade = "1";
			String buddhadate = StingUtil.replace("/", "-",
					order_form_layout_wish_date_input.getText().toString());
			int alsowish = 0;
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("wishname",
					order_form_layout_wish_people_input.getText().toString()));
			params.add(new BasicNameValuePair("wishplace",
					order_form_layout_wish_address_input.getText().toString()));
			params.add(new BasicNameValuePair("wishtext",
					order_form_layout_wish_content_input.getText().toString()));
			params.add(new BasicNameValuePair("mobile",
					order_form_layout_wish_phone_input.getText().toString()));
			params.add(new BasicNameValuePair("buddhadate", buddhadate));
			params.add(new BasicNameValuePair("wishgrade", wishgrade));
			params.add(new BasicNameValuePair("wishtype", String
					.valueOf(wishtype)));
			params.add(new BasicNameValuePair("tid", String.valueOf(tid)));
			params.add(new BasicNameValuePair("aid", String.valueOf(aid)));
			params.add(new BasicNameValuePair("mid", String.valueOf(mid)));
			params.add(new BasicNameValuePair("alsowish", String
					.valueOf(alsowish)));
			Log.i("aaaa", "-----订单请求参数 " + params.toString());
			this.httpClient.Config("post", WebApiUrl.Getaddorderdo, params,
					true);
			this.httpMethod = new SinhaPipeMethod(this.httpClient,
					new SinhaPipeMethod.MethodCallback() {
						public void CallFinished(String error, Object result) {
							showLoading();
							if (null == error) {
								
								try {
									JSONObject objects = new JSONObject(
											(String) result);
									if (objects.optString("code", "").equals(
											"succeed")) {
										Log.i("aaaa", "-----订单请求返回订单号" + objects.optString("orderid", ""));
										Utils.ShowToast(OrderForm.this, "下单成功");
										Intent intent1 = new Intent(
												OrderForm.this,
												OrderFormPay.class);
										 Bundle bu=new Bundle();
										 bu.putString("orderid", objects.optString("orderid", ""));
										 intent1.putExtras(bu);
										 WishGroupTab.getInstance()
												.switchActivity("OrderFormPay",
														intent1, -1, -1);// 接口请求
										// Utils.Dialog(getParent().getParent(),
										// "提示", "下单成功");

									} else {

									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							} else {
								int err = R.string.dialog_system_error_content;
								if (error == httpClient.ERR_TIME_OUT) {
									err = R.string.dialog_network_error_timeout;
								}
								if (error == httpClient.ERR_GET_ERR) {
									err = R.string.dialog_network_error_getdata;
								}

								// Utils.Dialog(getParent().getParent(), "提示",
								// getString(err));
							}
						}
					});
			this.httpMethod.start();
		} else {
			Utils.ShowToast(OrderForm.this,
					R.string.dialog_network_check_content);
		}
	}

	private void loadIncense() {
		if (Utils.CheckNetwork()) {
			showLoading();

			this.httpClient.Config("get", WebApiUrl.GetIncense, null, true);
			this.httpMethod = new SinhaPipeMethod(this.httpClient,
					new SinhaPipeMethod.MethodCallback() {
						public void CallFinished(String error, Object result) {
							showLoading();
							if (null == error) {
								loadIncense((String) result);
							} else {
								int err = R.string.dialog_system_error_content;
								if (error == httpClient.ERR_TIME_OUT) {
									err = R.string.dialog_network_error_timeout;
								}
								if (error == httpClient.ERR_GET_ERR) {
									err = R.string.dialog_network_error_getdata;
								}
								Utils.ShowToast(OrderForm.this, err);
							}
						}
					});
			this.httpMethod.start();
		} else {
			Utils.ShowToast(OrderForm.this,
					R.string.dialog_network_check_content);
		}
	}

	private void loadIncense(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);

				if (result.optString("code", "").equals("succeed")) {

					JSONArray jsonArray = result.getJSONArray("wishgradeinfo");
					incense_data = jsonArray;

					new MyPopupWindowsIncense(OrderForm.this,
							order_form_layout_wish_xiangtype_input,
							order_form_layout_wish_xiangtype_input_price,
							getParent().getParent(), jsonArray);

				} else {

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadWishcontent() {
		if (Utils.CheckNetwork()) {
			showLoading();

			this.httpClient.Config("get", WebApiUrl.GetWishcontent, null, true);
			this.httpMethod = new SinhaPipeMethod(this.httpClient,
					new SinhaPipeMethod.MethodCallback() {
						public void CallFinished(String error, Object result) {
							showLoading();
							if (null == error) {
								loadWishcontent((String) result);
							} else {
								int err = R.string.dialog_system_error_content;
								if (error == httpClient.ERR_TIME_OUT) {
									err = R.string.dialog_network_error_timeout;
								}
								if (error == httpClient.ERR_GET_ERR) {
									err = R.string.dialog_network_error_getdata;
								}
								Utils.ShowToast(OrderForm.this, err);
							}
						}
					});
			this.httpMethod.start();
		} else {
			Utils.ShowToast(OrderForm.this,
					R.string.dialog_network_check_content);
		}
	}

	private void loadWishcontent(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {

					JSONArray jsonArray = result.getJSONArray("wishtextchoice");
					Log.i("aaaa",
							"-----GET请求11111111111111-" + jsonArray.toString());

					String[] arrString = new String[jsonArray.length()];
					for (int i = 0; i < jsonArray.length(); i++) {

						// JSONObject jsonboject2=jsonArray.getString(i);
						// Log.i("aaaa",
						// "-----GET请求22222222222222222222-"+jsonboject2.toString());
						arrString[i] = jsonArray.getString(i);

					}
					wishcontent_data = arrString;
					new MyPopupWindows(OrderForm.this,
							order_form_layout_wish_content_input, getParent()
									.getParent(), arrString);

				} else {

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadCity() {
		if (Utils.CheckNetwork()) {
			showLoading();

			this.httpClient.Config("post", WebApiUrl.Getprovincecitylist, null,
					true);
			this.httpMethod = new SinhaPipeMethod(this.httpClient,
					new SinhaPipeMethod.MethodCallback() {
						public void CallFinished(String error, Object result) {
							showLoading();
							if (null == error) {
								loadCity((String) result);
							} else {
								int err = R.string.dialog_system_error_content;
								if (error == httpClient.ERR_TIME_OUT) {
									err = R.string.dialog_network_error_timeout;
								}
								if (error == httpClient.ERR_GET_ERR) {
									err = R.string.dialog_network_error_getdata;
								}
								Utils.ShowToast(OrderForm.this, err);
							}
						}
					});
			this.httpMethod.start();
		} else {
			Utils.ShowToast(OrderForm.this,
					R.string.dialog_network_check_content);
		}
	}

	private void loadCity(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {

					JSONArray jsonArray = result.getJSONArray("province_city");

					citylist_data = jsonArray;
					new MyPopupWindowsCity(OrderForm.this,
							order_form_layout_wish_address_input, getParent()
									.getParent(), citylist_data);

				} else {

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void showLoading() {

		if (pDialog != null) {
			pDialog.dismiss();
			pDialog=null;
		} else {

			pDialog = new ProgressDialog(getParent().getParent());
			pDialog.setMessage("数据加载中。。。");
			pDialog.show();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			WishGroupTab.getInstance().onKeyDown(keyCode, event);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
