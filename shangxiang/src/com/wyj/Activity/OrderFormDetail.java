package com.wyj.Activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;


import com.wyj.Activity.R;

import com.wyj.http.WebApiUrl;
import com.wyj.pipe.Cms;
import com.wyj.pipe.SinhaPipeClient;
import com.wyj.pipe.SinhaPipeMethod;
import com.wyj.pipe.Utils;
import com.wyj.utils.StingUtil;


import android.app.Activity;

import android.app.ProgressDialog;

import android.content.Intent;
import android.graphics.Bitmap;

import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class OrderFormDetail extends Activity implements OnClickListener {

	private static String TAG = "OrderFormDetail";

	private ImageView order_form_detail_back;
	private ProgressDialog pDialog = null;
	private Button detail_button;
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private TextView order_detail_wishcontent, order_detail_date_input,
			order_detail_fashi_input, order_detail_xiangtype_input,
			order_detail_simiao_input, order_detail_people,
			order_detail_number;
	private  LinearLayout order_form_detail_layout_image,order_form_detail_layout_dai;
	private Button order_form_detail_submit;
	private String orderid;
	private LinearLayout show_order_gallery_images;
	private String[] orderthumbs;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_form_detail);

		findViewById();
		setListener();
		this.httpClient = new SinhaPipeClient();
		Intent intens = this.getIntent();
		Bundle bu = intens.getExtras();
		 orderid = bu.getString("orderid");
		loadOrderInfo(orderid);
		Log.i(TAG, "------订单号"+orderid);
	}

	private void findViewById() {

		order_form_detail_back = (ImageView) findViewById(R.id.order_form_detail_back);
		detail_button = (Button) findViewById(R.id.order_form_detail_submit);
		
		order_form_detail_submit = (Button) findViewById(R.id.order_form_detail_submit);
		order_form_detail_layout_image=(LinearLayout) findViewById(R.id.order_form_detail_layout_image);
		order_form_detail_layout_dai=(LinearLayout) findViewById(R.id.order_form_detail_layout_dai);
		
		
		order_detail_number=(TextView) findViewById(R.id.order_detail_number);
		order_detail_people=(TextView) findViewById(R.id.order_detail_people);
		order_detail_simiao_input=(TextView) findViewById(R.id.order_detail_simiao_input);
		order_detail_fashi_input=(TextView) findViewById(R.id.order_detail_fashi_input);
		order_detail_xiangtype_input=(TextView) findViewById(R.id.order_detail_xiangtype_input);
		order_detail_wishcontent=(TextView) findViewById(R.id.order_detail_wishcontent);
		order_detail_date_input=(TextView) findViewById(R.id.order_detail_date_input);
		
		show_order_gallery_images=(LinearLayout) findViewById(R.id.show_order_gallery_images);
	}

	private void setListener() {

		order_form_detail_back.setOnClickListener(this);
		detail_button.setOnClickListener(this);
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
								Utils.ShowToast(OrderFormDetail.this, err);
							}
						}
					});
			this.httpMethod.start();
		} else {
			Utils.ShowToast(OrderFormDetail.this,
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
					
					order_detail_number.setText("订单号："
							+ Object.optString("orderid", ""));
					order_detail_people.setText(Object.optString("wishname",
							"") + "祈求" + Object.optString("wishtype", ""));
					order_detail_simiao_input.setText(Object.optString(
							"templename", ""));
					order_detail_xiangtype_input.setText(Object.optString(
							"wishgrade", ""));
					order_detail_fashi_input.setText(Object.optString(
							"buddhistname", ""));
					order_detail_date_input.setText(retime);
					order_detail_wishcontent.setText(Object.optString(
							"wishtext", ""));
					
					if(Object.optString(
							"en_status", "").equals("0")){	//未支付
						
						order_form_detail_layout_image.setVisibility(View.GONE);
						order_form_detail_layout_dai.setVisibility(View.GONE);
					}else if(Object.optString(
							"en_status", "").equals("1")){	//执行中
						order_form_detail_layout_image.setVisibility(View.GONE);
						detail_button.setVisibility(View.GONE);
					}else if(Object.optString(
							"en_status", "").equals("3")){	//已经完成
						detail_button.setVisibility(View.GONE);
						
						JSONArray jsonThumbs = result.getJSONArray("receipt_pic");
						if (null != jsonThumbs) {
							orderthumbs = new String[jsonThumbs.length()];
							for (int i = 0; i < jsonThumbs.length(); i++) {
								JSONObject jsonThumb = jsonThumbs.optJSONObject(i);
							
								if (null != jsonThumb) {
									orderthumbs[i]=jsonThumb.optString("pic_path", "");
									show_order_gallery_images.addView(createThumb(jsonThumb.optString("pic_tmb_path", ""),i));
								}
							}
						}
						
					}

				} else {

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private View createThumb(String thumb,final int position) {
		View imageLayout = LayoutInflater.from(OrderFormDetail.this).inflate(R.layout.thumb_item_layout, null);
		imageLayout.setPadding(0, 0, 10, 0);
		final ProgressBar progressBar = (ProgressBar) imageLayout.findViewById(R.id.thumb_item_loading);
		final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.thumb_item_view);
		Cms.imageLoader.displayImage(thumb, imageView, Cms.imageLoaderOptions, new ImageLoadingListener() {
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
				imageView.setTag(position);
				imageView.setOnClickListener(OrderFormDetail.this);
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				progressBar.setVisibility(View.GONE);
			}
		});
		return imageLayout;
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

		case R.id.order_form_detail_back:
			Intent bak_My_intent = new Intent(OrderFormDetail.this,
					Wish.class);
			WishGroupTab.getInstance().switchActivity("Wish",
					bak_My_intent, -1, -1);
			break;
		case R.id.thumb_item_view:
			
			int position = (Integer) v.getTag();
			Intent intent2 = new Intent(
					OrderFormDetail.this,
					Imageviewpager.class);
			Bundle bundle = new Bundle();
			bundle.putStringArray("thumbs", orderthumbs);
			bundle.putInt("position", position);
			intent2.putExtras(bundle);
			
			Log.i("aaaa", "-------viewpager传位置"+position);
			WishGroupTab.getInstance().startActivityForResult(intent2, 1);

			break;
			
		case R.id.order_form_detail_submit:
			
			break;

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
