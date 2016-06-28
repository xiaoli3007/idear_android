package com.wyj.Activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import org.json.JSONException;
import org.json.JSONObject;

import com.wyj.Activity.R;
import com.wyj.alipay.Alipay;
import com.wyj.alipay.PayResult;
import com.wyj.http.WebApiUrl;
import com.wyj.pipe.Cms;
import com.wyj.pipe.SinhaPipeClient;
import com.wyj.pipe.SinhaPipeMethod;
import com.wyj.pipe.Utils;

import com.wyj.utils.StingUtil;

import android.app.Activity;

import android.app.ProgressDialog;

import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class OrderFormPay extends Activity implements OnClickListener {

	// private static String TAG = "OrderFormPay";

	private ImageView order_form_pay_back;
	private ProgressDialog pDialog = null;
	private Button pay_alipay_button;
	private Button pay_weixin_button;
	private Button pay_yinlian_button;

	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private TextView orderinfo_ordernumber, orderinfo_orderpeople,
			order_baseinfo_simiao_input, order_baseinfo_xiangtype_input,
			order_baseinfo_fashi_input, order_baseinfo_date_input,
			order_wishcontent_input;

	private String orderid;
	private String ordernumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_form_pay);

		findViewById();
		setListener();
		this.httpClient = new SinhaPipeClient();
		Intent intens = this.getIntent();
		Bundle bu = intens.getExtras();
		orderid = bu.getString("orderid");
		loadOrderInfo(orderid);

	}

	private void findViewById() {

		order_form_pay_back = (ImageView) findViewById(R.id.order_form_pay_back);
		pay_alipay_button = (Button) findViewById(R.id.order_form_pay_alipay_submit);
		pay_weixin_button = (Button) findViewById(R.id.order_form_pay_weixin_submit);
		pay_yinlian_button = (Button) findViewById(R.id.order_form_pay_yinlian_submit);

		orderinfo_ordernumber = (TextView) findViewById(R.id.orderinfo_ordernumber);
		orderinfo_orderpeople = (TextView) findViewById(R.id.orderinfo_orderpeople);
		order_baseinfo_simiao_input = (TextView) findViewById(R.id.order_baseinfo_simiao_input);
		order_baseinfo_xiangtype_input = (TextView) findViewById(R.id.order_baseinfo_xiangtype_input);
		order_baseinfo_fashi_input = (TextView) findViewById(R.id.order_baseinfo_fashi_input);
		order_baseinfo_date_input = (TextView) findViewById(R.id.order_baseinfo_date_input);
		order_wishcontent_input = (TextView) findViewById(R.id.order_wishcontent_input);

	}

	private void setListener() {

		order_form_pay_back.setOnClickListener(this);
		pay_alipay_button.setOnClickListener(this);
		pay_weixin_button.setOnClickListener(this);
		pay_yinlian_button.setOnClickListener(this);

	}

	private void loadOrderInfo(String orderid) {
		if (Utils.CheckNetwork()) {
			showLoading();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("oid", orderid));
			this.httpClient.Config("get", WebApiUrl.Getgetorderinfo, params,
					true);
			this.httpMethod = new SinhaPipeMethod(this.httpClient,
					new SinhaPipeMethod.MethodCallback() {
						public void CallFinished(String error, Object result) {
							showLoading();
							if (null == error) {
								loadOrderInfo_ui((String) result);
							} else {
								int err = R.string.dialog_system_error_content;
								if (error == httpClient.ERR_TIME_OUT) {
									err = R.string.dialog_network_error_timeout;
								}
								if (error == httpClient.ERR_GET_ERR) {
									err = R.string.dialog_network_error_getdata;
								}
								Utils.ShowToast(OrderFormPay.this, err);
							}
						}
					});
			this.httpMethod.start();
		} else {
			Utils.ShowToast(OrderFormPay.this,
					R.string.dialog_network_check_content);
		}
	}

	private void loadOrderInfo_ui(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);

				if (result.optString("code", "").equals("succeed")) {

					JSONObject Object = result.getJSONObject("orderinfo");

					String retime = StingUtil.get_date(Object.optString(
							"retime", ""));

					// System.out.println(date);

					ordernumber = Object.optString("ordernumber", "");
					orderinfo_ordernumber.setText("订单号："
							+ Object.optString("orderid", ""));
					orderinfo_orderpeople.setText(Object.optString("wishname",
							"") + "祈求" + Object.optString("wishtype", ""));
					order_baseinfo_simiao_input.setText(Object.optString(
							"templename", ""));
					order_baseinfo_xiangtype_input.setText(Object.optString(
							"wishgrade", ""));
					order_baseinfo_fashi_input.setText(Object.optString(
							"buddhistname", ""));
					order_baseinfo_date_input.setText(retime);
					order_wishcontent_input.setText(Object.optString(
							"wishtext", ""));

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
			pDialog = null;
		} else {

			pDialog = new ProgressDialog(getParent().getParent());
			pDialog.setMessage("数据加载中。。。");
			pDialog.show();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.order_form_pay_back:
			Intent bak_My_intent = new Intent(OrderFormPay.this, Wish.class);
			WishGroupTab.getInstance().switchActivity("Wish", bak_My_intent,
					-1, -1);
			break;
		case R.id.order_form_pay_alipay_submit:

			pay_alipay_submit();
			break;
		case R.id.order_form_pay_weixin_submit:
			Intent intent3 = new Intent(OrderFormPay.this, OrderPaySucc.class);
			WishGroupTab.getInstance().switchActivity("OrderPaySucc", intent3,
					-1, -1);
			break;
		case R.id.order_form_pay_yinlian_submit:
			Intent intent4 = new Intent(OrderFormPay.this, OrderPaySucc.class);
			WishGroupTab.getInstance().switchActivity("OrderPaySucc", intent4,
					-1, -1);
			break;

		}
	}

	private void pay_alipay_submit() {
		// TODO Auto-generated method stub
		
		final Handler mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1: {
					PayResult payResult = new PayResult((String) msg.obj);
					
					// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
					String resultInfo = payResult.getResult();
					Log.i("aaaa", "支付状态------------------------------"+ msg.obj.toString());
					Log.i("aaaa", "支付结果------------------------------"+resultInfo);
					String resultStatus = payResult.getResultStatus();

					// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
					if (TextUtils.equals(resultStatus, "9000")) {
						
						Log.i("aaaa", "支付成功------------------------------");
				
						Intent intent2 = new Intent(
								OrderFormPay.this,
								OrderPaySucc.class);
						WishGroupTab.getInstance()
								.switchActivity(
										"OrderPaySucc",
										intent2, -1, -1);
						Toast.makeText(OrderFormPay.this, "支付成功",
								Toast.LENGTH_SHORT).show(); 
						
					} else {
						// 判断resultStatus 为非“9000”则代表可能支付失败
						// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
						if (TextUtils.equals(resultStatus, "8000")) {
							
							Toast.makeText(OrderFormPay.this, "支付结果确认中",
									Toast.LENGTH_SHORT).show();

						} else {
							// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						
							Toast.makeText(OrderFormPay.this, "支付失败",
									Toast.LENGTH_SHORT).show();

						}
					}
					break;
				}
				case 2: {
//					Toast.makeText(PayDemoActivity.this, "检查结果为：" + msg.obj,
//							Toast.LENGTH_SHORT).show();
					break;
				}
				default:
					break;
				}
			};
		};
		
		new Alipay(OrderFormPay.this, orderinfo_orderpeople.getText()
				.toString(), orderinfo_orderpeople.getText().toString(),
				"0.01", ordernumber,mHandler); 

	

	}

	private void pay_submit_server(String url, String type, String result) {
		this.httpClient = new SinhaPipeClient();

		if (Utils.CheckNetwork()) {
			if (!TextUtils.isEmpty(Cms.APP.getMemberId())) {
				showLoading();
				List<NameValuePair> parm = new ArrayList<NameValuePair>();
				parm.add(new BasicNameValuePair("oid", orderid));
				parm.add(new BasicNameValuePair("type", type));
				parm.add(new BasicNameValuePair("result", result));
				String nowTime = Long.toString(System.currentTimeMillis()); // 当前时间戳
				parm.add(new BasicNameValuePair("paytime", nowTime));
				this.httpClient.Config("post", url, parm, true);
				this.httpMethod = new SinhaPipeMethod(this.httpClient,
						new SinhaPipeMethod.MethodCallback() {
							public void CallFinished(String error, Object result) {

								showLoading();

								if (null == error) {

									try {
										JSONObject object = new JSONObject(
												(String) result);
										if (object.optString("code").equals(
												"succeed")) {
											
											Log.i("aaaa", "支付成功返回服务器更改状态------------------------------");
											Utils.ShowToast(OrderFormPay.this,
													object.optString("msg"));
											Intent intent2 = new Intent(
													OrderFormPay.this,
													OrderPaySucc.class);
											WishGroupTab.getInstance()
													.switchActivity(
															"OrderPaySucc",
															intent2, -1, -1);

										} else {
											Utils.ShowToast(OrderFormPay.this,
													object.optString("msg"));
										}
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									Log.i("bbbb", "-----请求回来-23344---");

								} else {
									int err = R.string.dialog_system_error_content;
									if (error == httpClient.ERR_TIME_OUT) {
										err = R.string.dialog_network_error_timeout;
									}
									if (error == httpClient.ERR_GET_ERR) {
										err = R.string.dialog_network_error_getdata;
									}
									Utils.ShowToast(OrderFormPay.this, err);
								}
							}
						});
				this.httpMethod.start();
			} else {

				Utils.ShowToast(OrderFormPay.this, "请登录后操作！");
			}

		} else {
			Utils.ShowToast(OrderFormPay.this,
					R.string.dialog_network_check_content);
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
