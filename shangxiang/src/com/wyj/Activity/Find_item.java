package com.wyj.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wyj.Activity.TabMenu;
import com.wyj.adapter.FindItemListAdapter;
import com.wyj.dataprocessing.AsynTaskHelper;
import com.wyj.dataprocessing.BitmapManager;

import com.wyj.dataprocessing.AsynTaskHelper.OnDataDownloadListener;

import com.wyj.http.WebApiUrl;
import com.wyj.Activity.R;
import com.wyj.pipe.Cms;
import com.wyj.pipe.SinhaPipeClient;
import com.wyj.pipe.SinhaPipeMethod;
import com.wyj.pipe.Utils;
import com.wyj.utils.Tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import android.view.View;

import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Find_item extends Activity implements OnClickListener {

	private ListView mListView;
	private int tid;
	private TextView finditem_jiachi;
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private int orderid;
	private String isbless;
	private String mid;
	private ProgressBar finditem_jiachi_loading;
	private ImageView back;
	private Button tongyuanqifu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_item);

		findViewById();
		setListener();

	}

	private void findViewById() {
		// TODO Auto-generated method stub
		finditem_jiachi = (TextView) findViewById(R.id.finditem_jiachi);

		finditem_jiachi_loading = (ProgressBar) findViewById(R.id.finditem_jiachi_loading);

		back = (ImageView) findViewById(R.id.order_back);

		tongyuanqifu = (Button) findViewById(R.id.tongyuan);

		mListView = (ListView) findViewById(R.id.find_list_item);

		Intent intent = this.getIntent(); // 接受的数据
		Bundle budle = intent.getExtras();
		orderid = budle.getInt("orderid");
		tid = budle.getInt("tid"); // 寺庙的传值

		mid = "0";
		if (!TextUtils.isEmpty(Cms.APP.getMemberId())) {
			mid = Cms.APP.getMemberId();
		}

	}

	private void setListener() {
		// TODO Auto-generated method stub
		finditem_jiachi.setOnClickListener(this);
		back.setOnClickListener(this);
		tongyuanqifu.setOnClickListener(this);
		api_show_detail(null, WebApiUrl.GET_ORDER_DETAIL + "?oid=" + orderid
				+ "&&mid=" + mid, getParent());
	}

	private void api_show_detail(Map<String, Object> map,
			String order_detail_api_url, final Context context) {
		// TODO Auto-generated method stub
		Log.i("aaaa", "------orderid-----" + order_detail_api_url);
		AsynTaskHelper asyntask = new AsynTaskHelper();
		asyntask.dataDownload(order_detail_api_url, map,
				new OnDataDownloadListener() {
					@Override
					public void onDataDownload(String result) {
						if (result != null) {

							try {
								JSONObject jsonobject = new JSONObject(result);
								JSONObject orderinfoobject = jsonobject
										.getJSONObject("orderinfo");
								Ui_orderinfo(orderinfoobject);

								isbless = jsonobject.optString("isblessings",
										"");
								Log.i("aaaa", "------orderidsss-----" + isbless);
								if (!isbless.equals("0")) {
									setcolorstatus(finditem_jiachi);
								}

								JSONArray member_jiachilist = jsonobject
										.getJSONArray("blessings_members");
								Ui_orderinfo_memberlist(member_jiachilist);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						} else {
							Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT)
									.show();
						}

					}
				}, context, "GET");
	}

	protected void Ui_orderinfo(JSONObject orderdetail_orderinfo) {

		// TODO Auto-generated method stub
		ImageView order_people_head = (ImageView) findViewById(R.id.finditem_people_head);
		TextView finditem_username = (TextView) findViewById(R.id.finditem_username);
		TextView finditem_templename = (TextView) findViewById(R.id.finditem_templename);
		TextView finditem_wishtext = (TextView) findViewById(R.id.finditem_wishtext);
		TextView finditem_blessingser = (TextView) findViewById(R.id.finditem_blessingser);

		BitmapManager.getInstance().loadBitmap(
				orderdetail_orderinfo.optString("headface", ""),
				order_people_head,
				Tools.readBitmap(Find_item.this, R.drawable.foot_07));
		finditem_username.setText(orderdetail_orderinfo.optString("wishname",
				""));
		finditem_templename.setText("刚刚在"
				+ orderdetail_orderinfo.optString("templename", "")
				+ orderdetail_orderinfo.optString("alsowish", "")
				+ orderdetail_orderinfo.optString("wishtype", "") + "");
		finditem_wishtext.setText(orderdetail_orderinfo.optString("wishtext",
				""));

		String bless = "";
		if (!orderdetail_orderinfo.optString("co_blessings").equals("0")) {
			bless = orderdetail_orderinfo.optString("blessingser", "") + "等"
					+ orderdetail_orderinfo.optString("co_blessings")
					+ "人刚刚加持过";
		}
		finditem_blessingser.setText(bless);
	}

	protected void Ui_orderinfo_memberlist(JSONArray member_listitems) {
		// TODO Auto-generated method stub

		mListView.setAdapter(new FindItemListAdapter(this, member_listitems));
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.order_back:
			// 要跳转的Activity
			Intent intent = new Intent(Find_item.this, Find.class);
			intent.putExtra("tid", tid);

			FindGroupTab.getInstance().switchActivity("Find_item", intent, -1,
					-1);
			break;
		case R.id.tongyuan:
			// 要跳转的Activity
			TabMenu mainactivity = (TabMenu) getParent().getParent(); // 查找父级的父级

			if (WishGroupTab.getInstance() == null) {

				mainactivity.setCurrentActivity(1);
			} else {
				Log.i("aaaa", "------view不为null-----");
				mainactivity.setCurrentActivity(1);
				Intent intent2 = new Intent(Find_item.this, ListTemple.class);
				intent2.putExtra("tid", tid);
				WishGroupTab.getInstance().switchActivity("ListTemple",
						intent2, -1, -1);
			}

			break;
		case R.id.finditem_jiachi: 
			// 要跳转的Activity
			if (!isbless.equals("0")) {
				Utils.ShowToast(Find_item.this,"您已经加持过了");
			}else{
				addblessingsdo(String.valueOf(orderid), finditem_jiachi);
			}
			
			break;
		}

	}

	private void addblessingsdo(String oid, final View vv) {

		this.httpClient = new SinhaPipeClient();
		if (Utils.CheckNetwork()) {

			if (!TextUtils.isEmpty(Cms.APP.getMemberId())) {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("mid", Cms.APP.getMemberId()));
				params.add(new BasicNameValuePair("oid", oid));
				
				finditem_jiachi_loading.setVisibility(View.VISIBLE);
				finditem_jiachi.setVisibility(View.GONE);
				
				this.httpClient.Config("get", WebApiUrl.GET_addblessingsdo,
						params, true);
				this.httpMethod = new SinhaPipeMethod(this.httpClient,
						new SinhaPipeMethod.MethodCallback() {
							public void CallFinished(String error, Object result) {
								Log.i("bbbb", "-----请求回来----" + result);
								
								finditem_jiachi_loading.setVisibility(View.GONE);
								finditem_jiachi.setVisibility(View.VISIBLE);
								
								if (null == error) {
									try {
										JSONObject jsonobject = new JSONObject(
												(String) result);
										if (jsonobject.optString("code", "")
												.equals("succeed")) {

											TextView list_find_zany = (TextView) vv;
											setcolorstatus(list_find_zany);

											Utils.ShowToast(Find_item.this,
													jsonobject.optString("msg",
															""));
											isbless="1";
										} else {
											Utils.ShowToast(Find_item.this,
													jsonobject.optString("msg",
															""));
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
									Utils.ShowToast(Find_item.this, err);
								}
							}
						});
				this.httpMethod.start();

			} else {

				// Utils.Dialog(context, "提示","请先登录账户！");
				Utils.ShowToast(Find_item.this, "请先登录账户！");
			}

		} else {
			Utils.ShowToast(Find_item.this,
					R.string.dialog_network_check_content);
		}
	}

	private void setcolorstatus(TextView list_find_zany) {
		// TODO Auto-generated method stub

		list_find_zany.setText("已加持");
		Resources resource = (Resources) this.getResources();
		ColorStateList csl = (ColorStateList) resource
				.getColorStateList(R.color.color_text_selected);
		list_find_zany.setTextColor(csl);
		Drawable drawable = this.getResources().getDrawable(
				R.drawable.load_hover);
		// / 这一步必须要做,否则不会显示.
		drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				drawable.getMinimumHeight());
		list_find_zany.setCompoundDrawables(drawable, null, null, null);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			FindGroupTab.getInstance().onKeyDown(keyCode, event);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
