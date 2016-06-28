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
import com.shangxiang.android.ShangXiang;
import com.shangxiang.android.alipay.AliEntryActivity;
import com.shangxiang.android.alipay.PayResult;
import com.shangxiang.android.imageviewer.ImagePageViewer;
import com.shangxiang.android.pipe.SinhaPipeClient;
import com.shangxiang.android.pipe.SinhaPipeMethod;
import com.shangxiang.android.utils.Utils;
import com.shangxiang.android.wxapi.WXPayEntryActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ShowOrderRecord extends Activity implements OnClickListener {
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private RelativeLayout layoutLoading;
	private Button buttonBack;
	private Button buttonDelete;
	private TextView viewOrderNo;
	private TextView viewOrderTitle;
	private TextView viewOrderHall;
	private TextView viewOrderBuddhist;
	private TextView viewOrderJSC;
	private TextView viewOrderDate;
	private TextView viewDesireContent;
	private LinearLayout layoutStatusPay;
	private LinearLayout layoutStatusProcessing;
	private LinearLayout layoutStatusProcessed;
	private Button buttonAlipay;
	private Button buttonWechat;
	private Button buttonUnionpay;
	private Button buttonMobilepay;
	private TextView viewProcessingDate;
	private LinearLayout layoutReceiptThumbs;
	private Button buttonCreateRedeemOrder;
	private boolean showLoading = false;
	private Bundle bundle;
	private String orderId = "";
	private int orderType = 0;
	private int desireType = 0;
	private JSONObject orderInfo = new JSONObject();
	private JSONObject temple = new JSONObject();
	private String[] thumbs = new String[] {};

	@SuppressLint("InflateParams")
	public void onCreate(Bundle sinha) {
		super.onCreate(sinha);
		setContentView(R.layout.show_order_record);

		this.bundle = getIntent().getExtras();
		this.httpClient = new SinhaPipeClient();

		this.layoutLoading = (RelativeLayout) findViewById(R.id.loading);
		this.buttonBack = (Button) findViewById(R.id.show_order_record_title_back_button);
		this.buttonBack.setOnClickListener(this);
		this.buttonDelete = (Button) findViewById(R.id.show_order_record_title_delete_button);
		this.buttonDelete.setOnClickListener(this);
		this.viewOrderNo = (TextView) findViewById(R.id.show_order_record_order_id_text);
		this.viewOrderTitle = (TextView) findViewById(R.id.show_order_record_order_title_text);
		this.viewOrderHall = (TextView) findViewById(R.id.show_order_record_order_hall_text);
		this.viewOrderBuddhist = (TextView) findViewById(R.id.show_order_record_order_buddhist_text);
		this.viewOrderJSC = (TextView) findViewById(R.id.show_order_record_order_JSC_text);
		this.viewOrderDate = (TextView) findViewById(R.id.show_order_record_order_date_text);
		this.viewDesireContent = (TextView) findViewById(R.id.show_order_record_order_content_text);

		this.layoutStatusPay = (LinearLayout) findViewById(R.id.show_order_record_status_pay_layout);
		this.layoutStatusProcessing = (LinearLayout) findViewById(R.id.show_order_record_status_processing_layout);
		this.layoutStatusProcessed = (LinearLayout) findViewById(R.id.show_order_record_status_processed_layout);

		this.buttonAlipay = (Button) findViewById(R.id.show_order_record_pay_alipay_button);
		this.buttonAlipay.setOnClickListener(this);
		this.buttonWechat = (Button) findViewById(R.id.show_order_record_pay_wechat_button);
		this.buttonWechat.setOnClickListener(this);
		this.buttonUnionpay = (Button) findViewById(R.id.show_order_record_pay_unionpay_button);
		this.buttonUnionpay.setOnClickListener(this);
		this.buttonMobilepay = (Button) findViewById(R.id.show_order_record_pay_mobilepay_button);
		this.buttonMobilepay.setOnClickListener(this);
		this.viewProcessingDate = (TextView) findViewById(R.id.show_order_record_processing_date_text);
		this.layoutReceiptThumbs = (LinearLayout) findViewById(R.id.show_order_record_processed_receipt_thumbs_layout);
		this.buttonCreateRedeemOrder = (Button) findViewById(R.id.show_order_record_processed_create_redeem_order_button);
		this.buttonCreateRedeemOrder.setOnClickListener(this);

		if (null != this.bundle) {
			this.orderType = this.bundle.getInt("order_type");
			switch (this.orderType) {
			case 0:
				this.buttonCreateRedeemOrder.setVisibility(View.VISIBLE);
				break;
			case 1:
				this.buttonCreateRedeemOrder.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		}
		if (null != this.bundle && !TextUtils.isEmpty(this.bundle.getString("order_id"))) {
			this.orderId = this.bundle.getString("order_id");
			this.viewOrderNo.setText("订单编号：" + this.bundle.getString("order_id"));
		}
		if (null != this.bundle && !TextUtils.isEmpty(this.bundle.getString("desirer")) && !TextUtils.isEmpty(this.bundle.getString("desire_type"))) {
			this.desireType = this.bundle.getInt("desire_type");
			String orderTitle = this.bundle.getString("desirer");
			switch (this.bundle.getInt("order_type")) {
			case 0:
				orderTitle += " 求愿";
				break;
			case 1:
				orderTitle += " 还愿";
				break;
			default:
				break;
			}
			orderTitle += ShangXiang.desireTypeName.get(this.bundle.getInt("desire_type"));
			this.viewOrderTitle.setText(orderTitle);
		}
		if (null != this.bundle && !TextUtils.isEmpty(this.bundle.getString("temple"))) {
			try {
				this.temple = new JSONObject(bundle.getString("temple"));
				this.viewOrderHall.setText(this.temple.optString("templename", ""));
				this.viewOrderBuddhist.setText(this.temple.optString("buddhistname", ""));
			} catch (JSONException e) {
			}
		}
		if (null != this.bundle && !TextUtils.isEmpty(this.bundle.getString("JSC"))) {
			this.viewOrderJSC.setText(this.bundle.getString("JSC"));
		}
		if (null != this.bundle && !TextUtils.isEmpty(this.bundle.getString("date"))) {
			this.viewOrderDate.setText(this.bundle.getString("date"));
		}
		if (null != this.bundle && !TextUtils.isEmpty(this.bundle.getString("desire_content"))) {
			this.viewDesireContent.setText(this.bundle.getString("desire_content"));
		}

		ShangXiang.aliPayHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Consts.ALIPAY_PAY_FLAG: {
					PayResult payResult = new PayResult((String) msg.obj);
					String resultStatus = payResult.getResultStatus();
					if (TextUtils.equals(resultStatus, "9000")) {
						Message newMsg = ShangXiang.orderRecordHandler.obtainMessage(2);
						ShangXiang.orderRecordHandler.sendMessage(newMsg);
						loadOrderRecord();
						PaidOrder();
					} else {
						if (TextUtils.equals(resultStatus, "8000")) {
							Toast.makeText(ShowOrderRecord.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(ShowOrderRecord.this, "支付失败", Toast.LENGTH_SHORT).show();
						}
					}
					break;
				}
				case Consts.ALIPAY_CHECK_FLAG: {
					Toast.makeText(ShowOrderRecord.this, "检查结果为：" + msg.obj, Toast.LENGTH_SHORT).show();
					break;
				}
				default:
					break;
				}
			};
		};

		ShangXiang.wxPayHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (3 == msg.what) {
					Message newMsg = ShangXiang.orderRecordHandler.obtainMessage(2);
					ShangXiang.orderRecordHandler.sendMessage(newMsg);
					loadOrderRecord();
					PaidOrder();
				}
			};
		};

		loadOrderRecord();
	}

	private void showLoading() {
		Utils.animLoading(this.layoutLoading, !this.showLoading);
		this.showLoading = !this.showLoading;
	}

	private void loadOrderRecord() {
		if (!"".equals(this.orderId)) {

		}
		if (Utils.CheckNetwork()) {
			showLoading();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("oid", this.orderId));

			this.httpClient.Config("get", Consts.URI_ORDER_DETAIL, params, true);
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
						Utils.ShowToast(ShowOrderRecord.this, err);
					}
				}
			});
			this.httpMethod.start();
		} else {
			Utils.ShowToast(this, R.string.dialog_network_check_content);
		}
	}

	@SuppressLint("SimpleDateFormat")
	private void loadOrderRecord(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					if (null != result.optJSONObject("orderinfo")) {
						this.orderInfo = result.optJSONObject("orderinfo");
						if (null != this.orderInfo) {
							this.desireType = ShangXiang.desireTypeId.get(this.orderInfo.optString("wishtype", ""));
							try {
								this.temple = new JSONObject("{\"templeid\":\"" + this.orderInfo.optString("tid", "") + "\",\"templename\":\"" + this.orderInfo.optString("templename", "") + "\",\"province\":\"" + this.orderInfo.optString("province", "") + "\",\"pic_path\":\"" + this.orderInfo.optString("templepic_path", "") + "\",\"pic_tmb_path\":\"" + this.orderInfo.optString("templepic_path", "") + "\",\"attacheid\":\"" + this.orderInfo.optString("aid", "") + "\",\"buddhistname\":\"" + this.orderInfo.optString("buddhistname", "") + "\",\"headface\":\"" + this.orderInfo.optString("attache_headface", "") + "\",\"tmb_headface\":\"" + this.orderInfo.optString("attache_headface", "") + "\"}");
							} catch (JSONException e) {
								e.printStackTrace();
							}
							this.viewOrderHall.setText(this.orderInfo.optString("templename", ""));
							this.viewOrderBuddhist.setText(this.orderInfo.optString("buddhistname", ""));
							this.viewOrderNo.setText("订单编号：" + this.orderInfo.optString("ordernumber", ""));
							this.viewOrderTitle.setText(this.orderInfo.optString("wishname", "") + " " + this.orderInfo.optString("alsowish", "") + this.orderInfo.optString("wishtype", ""));
							this.viewOrderJSC.setText(Html.fromHtml(this.orderInfo.optString("wishgrade", "") + "  <font color=\"#dca358\">￥" + this.orderInfo.optString("price", "") + "</font>"));
							this.viewDesireContent.setText(this.orderInfo.optString("wishtext", ""));
							switch (this.orderInfo.optInt("en_status", -1)) {
							case 0:
								this.buttonDelete.setVisibility(View.VISIBLE);
								this.layoutStatusPay.setVisibility(View.VISIBLE);
								this.layoutStatusProcessing.setVisibility(View.GONE);
								this.layoutStatusProcessed.setVisibility(View.GONE);
								break;
							case 1:
								this.buttonDelete.setVisibility(View.GONE);
								this.layoutStatusPay.setVisibility(View.GONE);
								this.layoutStatusProcessing.setVisibility(View.VISIBLE);
								this.layoutStatusProcessed.setVisibility(View.GONE);
								this.viewProcessingDate.setText("预计" + this.orderInfo.optString("expect_buddhadate", "") + "回执照片");
								break;
							case 3:
								this.buttonDelete.setVisibility(View.GONE);
								this.layoutStatusPay.setVisibility(View.GONE);
								this.layoutStatusProcessing.setVisibility(View.GONE);
								this.layoutStatusProcessed.setVisibility(View.VISIBLE);
								JSONArray jsonThumbs = result.optJSONArray("receipt_pic");
								if (null != jsonThumbs) {
									this.thumbs = new String[jsonThumbs.length()];
									for (int i = 0; i < jsonThumbs.length(); i++) {
										JSONObject jsonThumb = jsonThumbs.optJSONObject(i);
										if (null != jsonThumb) {
											this.thumbs[i] = jsonThumb.optString("pic_path", "");
											this.layoutReceiptThumbs.addView(createThumb(jsonThumb.optString("pic_tmb_path", "")));
										}
									}
								}
								if (0 == this.orderInfo.optInt("hy_orderid", 0) && 0 == this.orderType) {
									this.buttonCreateRedeemOrder.setVisibility(View.VISIBLE);
								} else {
									this.buttonCreateRedeemOrder.setVisibility(View.GONE);
								}
								break;
							default:
								this.buttonDelete.setVisibility(View.VISIBLE);
								this.layoutStatusPay.setVisibility(View.GONE);
								this.layoutStatusProcessing.setVisibility(View.GONE);
								this.layoutStatusProcessed.setVisibility(View.GONE);
								break;
							}
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

	@SuppressLint("InflateParams")
	private View createThumb(String thumb) {
		LayoutInflater inflater = LayoutInflater.from(this);
		View imageLayout = inflater.inflate(R.layout.thumb_item_layout, null);
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
				imageView.setOnClickListener(ShowOrderRecord.this);
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				progressBar.setVisibility(View.GONE);
			}
		});
		return imageLayout;
	}

	private void DeleteOrderRecord() {
		if (Utils.CheckNetwork()) {
			showLoading();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("mid", ShangXiang.APP.getMemberId()));
			params.add(new BasicNameValuePair("oid", this.orderId));

			this.httpClient.Config("get", Consts.URI_ORDER_DELETE, params, true);
			this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
				public void CallFinished(String error, Object result) {
					showLoading();
					if (null == error) {
						DeleteOrderRecord((String) result);
					} else {
						int err = R.string.dialog_system_error_content;
						if (error == httpClient.ERR_TIME_OUT) {
							err = R.string.dialog_network_error_timeout;
						}
						if (error == httpClient.ERR_GET_ERR) {
							err = R.string.dialog_network_error_getdata;
						}
						Utils.ShowToast(ShowOrderRecord.this, err);
					}
				}
			});
			this.httpMethod.start();
		} else {
			Utils.ShowToast(this, R.string.dialog_network_check_content);
		}
	}

	private void DeleteOrderRecord(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					Message newMsg = ShangXiang.orderRecordHandler.obtainMessage(2);
					ShangXiang.orderRecordHandler.sendMessage(newMsg);
					finish();
				} else {
					Utils.Dialog(this, getString(R.string.dialog_normal_title), result.optString("msg", ""));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};

	private String getOrderTitle() {
		return this.orderInfo.optString("city", "") + "" + this.orderInfo.optString("wishname", "") + "请" + this.orderInfo.optString("buddhistname", "") + "于" + this.orderInfo.optString("templename", "") + "祈求" + this.orderInfo.optString("wishtype", "") + "订单";
	}

	private void AlipayOrder() {
		Bundle bundle = new Bundle();
		bundle.putString("action", "pay");
		bundle.putString("order_no", this.orderInfo.optString("ordernumber", ""));
		bundle.putString("title", getOrderTitle());
		bundle.putString("body", getOrderTitle());
		bundle.putString("price", this.orderInfo.optString("price", ""));
		Intent intent = new Intent(this, AliEntryActivity.class);
		intent.putExtras(bundle);
		this.startActivity(intent);
	};

	private void WechatPayOrder() {
		if (Utils.CheckNetwork()) {
			showLoading();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("product_name", getOrderTitle()));
			params.add(new BasicNameValuePair("order_no", this.orderInfo.optString("ordernumber", "")));
			params.add(new BasicNameValuePair("order_price", this.orderInfo.optString("price", "")));

			this.httpClient.Config("get", Consts.WECHAT_PAY_TOKEN_URI, params, true);
			this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
				public void CallFinished(String error, Object result) {
					showLoading();
					if (null == error) {
						WechatPayOrder((String) result);
					} else {
						int err = R.string.dialog_system_error_content;
						if (error == httpClient.ERR_TIME_OUT) {
							err = R.string.dialog_network_error_timeout;
						}
						if (error == httpClient.ERR_GET_ERR) {
							err = R.string.dialog_network_error_getdata;
						}
						Utils.ShowToast(ShowOrderRecord.this, err);
					}
				}
			});
			this.httpMethod.start();
		} else {
			Utils.ShowToast(this, R.string.dialog_network_check_content);
		}
	}

	private void WechatPayOrder(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);
				if (result.optString("retmsg", "").equals("ok")) {
					Bundle bundle = new Bundle();
					bundle.putString("action", "pay");
					bundle.putString("prepayid", result.optString("prepayid", ""));
					bundle.putString("noncestr", result.optString("noncestr", ""));
					bundle.putString("timestamp", result.optString("timestamp", ""));
					bundle.putString("package", result.optString("package", ""));
					bundle.putString("sign", result.optString("sign", ""));
					Intent intent = new Intent(this, WXPayEntryActivity.class);
					intent.putExtras(bundle);
					this.startActivity(intent);
				} else {
					Utils.Dialog(this, getString(R.string.dialog_normal_title), result.optString("retmsg", ""));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void PaidOrder() {
		Bundle bundle = new Bundle();
		bundle.putInt("order_type", this.orderType);
		bundle.putString("orderinfo", this.orderInfo.toString());
		Intent intent = new Intent(ShangXiang.APP, PaidOrder.class);
		intent.putExtras(bundle);
		this.startActivity(intent);
		finish();
	};

	@Override
	public void onClick(View v) {
		if (v == this.buttonBack) {
			finish();
		}
		if (v == this.buttonDelete) {
			Utils.Dialog(this, R.string.dialog_order_delete_tip, R.string.dialog_order_delete_success, new Utils.Callback() {
				@Override
				public void callFinished() {
					DeleteOrderRecord();
				}
			}, new Utils.Callback() {
				@Override
				public void callFinished() {
				}
			}, new Utils.Callback() {
				@Override
				public void callFinished() {
				}
			});
		}
		if (v == this.buttonAlipay) {
			AlipayOrder();
		}
		if (v == this.buttonWechat) {
			WechatPayOrder();
		}
		if (v == this.buttonUnionpay) {
		}
		if (v == this.buttonMobilepay) {
		}
		if (v.getId() == R.id.thumb_item_view) {
			String[] thumbs = (String[]) v.getTag();
			Bundle bundle = new Bundle();
			bundle.putStringArray("thumbs", thumbs);
			Intent intent = new Intent(ShangXiang.APP, ImagePageViewer.class);
			intent.putExtras(bundle);
			this.startActivity(intent);
		}
		if (v == this.buttonCreateRedeemOrder) {
			Bundle bundle = new Bundle();
			bundle.putInt("order_type", 1);
			bundle.putInt("desire_type", this.desireType);
			bundle.putString("vorderid", this.orderId);
			bundle.putString("temple", this.temple.toString());
			Intent intent = new Intent(ShangXiang.APP, CreateOrder.class);
			intent.putExtras(bundle);
			this.startActivity(intent);
		}
	}
}