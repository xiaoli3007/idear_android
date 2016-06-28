package com.wyj.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.wyj.Activity.R;
import com.wyj.adapter.MyFindListAdapter;
import com.wyj.dataprocessing.JsonHelper;
import com.wyj.http.WebApiUrl;
import com.wyj.pipe.Cms;
import com.wyj.pipe.SinhaPipeClient;
import com.wyj.pipe.SinhaPipeMethod;
import com.wyj.pipe.Utils;
import com.wyj.xlistview.XListView;
import com.wyj.xlistview.XListView.IXListViewListener;

import android.app.Activity;
import android.app.ProgressDialog;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;

import android.os.Bundle;
import android.os.Handler;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyFind extends Activity implements OnClickListener,
		IXListViewListener {

	private ImageView back_my;
	private LinearLayout my_bless_order_list, formy_bless_order_list;
	private TextView my_bless_order_list_text, formy_bless_order_list_text,my_bless_nums,formy_bless_nums;
	private Boolean left = true;
	private XListView my_find_bless_list;
	private List<Map<String, Object>> my_list_data = new ArrayList<Map<String, Object>>(); // 加载到适配器中的数据源
	private List<Map<String, Object>> formy_list_data = new ArrayList<Map<String, Object>>(); // 加载到适配器中的数据源
	private MyFindListAdapter myFindListAdapter,formyFindListAdapter;

	private String mid;
	private int page = 1;
	private int forpage = 1;
	private int pagesize = 10;
	private int bless = 1;
	private boolean isBottom = false;// 判断是否滚动到数据最后一条
	private boolean isforBottom = false;// 判断是否滚动到数据最后一条
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private ProgressDialog pDialog = null;
	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_find);
		findViewById();
		setListener();

	}

	private void findViewById() {

		back_my = (ImageView) findViewById(R.id.my_find_bless_back);
		my_bless_order_list = (LinearLayout) findViewById(R.id.my_bless_order_list);
		formy_bless_order_list = (LinearLayout) findViewById(R.id.formy_bless_order_list);

		my_bless_order_list_text = (TextView) findViewById(R.id.my_bless_order_list_text);
		formy_bless_order_list_text = (TextView) findViewById(R.id.formy_bless_order_list_text);

		my_find_bless_list = (XListView) findViewById(R.id.my_find_bless_list);
		
		my_bless_nums = (TextView) findViewById(R.id.my_bless_nums);
		formy_bless_nums = (TextView) findViewById(R.id.formy_bless_nums);
		if (!TextUtils.isEmpty(Cms.APP.getMemberId())) {

			mid = Cms.APP.getMemberId();
		}
		
		mHandler = new Handler();
	}

	private void setListener() {
		
		my_bless_nums.setText(Cms.memberInfo.optString("do_blessings", ""));
		formy_bless_nums.setText(Cms.memberInfo.optString("received_blessings", ""));
		back_my.setOnClickListener(this);
		my_bless_order_list.setOnClickListener(this);
		formy_bless_order_list.setOnClickListener(this);

		myFindListAdapter = new MyFindListAdapter(MyFind.this, my_list_data, 0,getParent().getParent());	
		formyFindListAdapter  = new MyFindListAdapter(MyFind.this, formy_list_data, 0,getParent().getParent());
		
		my_find_bless_list.setAdapter(myFindListAdapter);
		my_find_bless_list.setPullLoadEnable(true);
		my_find_bless_list.setPullRefreshEnable(false);
		my_find_bless_list.setXListViewListener(this);
		default_order_list();
	}

	private void default_order_list() { // 默认加载 和更换求愿 还愿
		
		Log.i("bbbb",
				"-----请求回来数目--" + formy_list_data.size());
		
		my_find_bless_list.setPullLoadEnable(true);
		int pages = 1;
		switch (bless) {
		case 1:
			Log.i("bbbb",
					"----执行111-" );
			listAdapter(WebApiUrl.GET_ORDERLIST + "?p=" + pages + "&&pz=" + pagesize
					+ "&&mid=" + mid + "&&bless=" + bless, 1); // 默认加载第一页
			break;
		case 2:
			
			if (formy_list_data.size()==0) {
				Log.i("bbbb",
						"----执行2222-" );
				listAdapter(WebApiUrl.GET_ORDERLIST + "?p=" + pages + "&&pz=" + pagesize
						+ "&&mid=" + mid + "&&bless=" + bless, 1); // 默认加载第一页
			}
			break;	

		default:
			break;
		}
		
		//my_list_data.clear();
		// my_list_data = new ArrayList<Map<String, Object>>();
		
	}

	private void onLoad() {
		my_find_bless_list.stopRefresh();
		my_find_bless_list.stopLoadMore();
		my_find_bless_list.setRefreshTime("刚刚");

	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		onLoad();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				
				if(bless==1){
					
					if (isBottom) {

						Utils.ShowToast(MyFind.this, "没有更多了");

					} else {
						page++;
						listAdapter(WebApiUrl.GET_ORDERLIST + "?p=" + page
								+ "&&pz=" + pagesize + "&&mid=" + mid + "&&bless="
								+ bless, 0); // 加载
						onLoad();
					}
				}else{
					if (isforBottom) {

						Utils.ShowToast(MyFind.this, "没有更多了");

					} else {
						forpage++;
						listAdapter(WebApiUrl.GET_ORDERLIST + "?p=" + forpage
								+ "&&pz=" + pagesize + "&&mid=" + mid + "&&bless="
								+ bless, 0); // 加载
						onLoad();
					}
				}
				
				
				
				
			}
		}, 2000);
	}

	private void listAdapter(String url, final int first) {
		
	
		this.httpClient = new SinhaPipeClient();

		if (Utils.CheckNetwork()) {
			if (first > 0) {
				showLoading();
			}

			this.httpClient.Config("get", url, null, true);
			this.httpMethod = new SinhaPipeMethod(this.httpClient,
					new SinhaPipeMethod.MethodCallback() {
						public void CallFinished(String error, Object result) {
							if (first > 0) {
								showLoading();
							}
							if (null == error) {

								List<Map<String, Object>> items;
								items = JsonHelper.jsonTolistmap(
										(String) result, "orderlist");
								
								if (items.toString().equals("[]")) {

									
									if(bless==1){
										isBottom = true;
										
									}else{
										isforBottom= true;
										my_find_bless_list.setAdapter(formyFindListAdapter);
										formyFindListAdapter.notifyDataSetChanged();
									}
									
									my_find_bless_list.setPullLoadEnable(false);
									Utils.ShowToast(MyFind.this, "没有更多了");

								} else {
									if(bless==1){
										my_list_data.addAll(items);
										myFindListAdapter.notifyDataSetChanged();
									}else{
										formy_list_data.addAll(items);
										my_find_bless_list.setAdapter(formyFindListAdapter);
										formyFindListAdapter.notifyDataSetChanged();
									}
								}

							} else {
								int err = R.string.dialog_system_error_content;
								if (error == httpClient.ERR_TIME_OUT) {
									err = R.string.dialog_network_error_timeout;
								}
								if (error == httpClient.ERR_GET_ERR) {
									err = R.string.dialog_network_error_getdata;
								}
								Utils.ShowToast(MyFind.this, err);
							}
						}
					});
			this.httpMethod.start();

		} else {
			Utils.ShowToast(MyFind.this, R.string.dialog_network_check_content);
		}

	}

	private void showLoading() {

		if (pDialog != null) {
			pDialog.dismiss();
		} else {

			pDialog = new ProgressDialog(getParent().getParent());
			pDialog.setMessage("数据加载中。。。");
			pDialog.show();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.my_find_bless_back:

			Intent intent = new Intent(MyFind.this, My.class);
			UserGroupTab.getInstance().switchActivity("My", intent, -1, -1);

			break;

		case R.id.my_bless_order_list:
			if (!left) {
				
				my_find_bless_list.setAdapter(myFindListAdapter);
				Resources resource = (Resources) getBaseContext()
						.getResources();
				ColorStateList csl = (ColorStateList) resource
						.getColorStateList(R.color.color_text_normal);
				ColorStateList cs2 = (ColorStateList) resource
						.getColorStateList(R.color.text_normal);

				my_bless_order_list.setBackgroundColor(getResources().getColor(
						R.color.button_greg_hover));
				my_bless_order_list_text.setTextColor(csl);

				formy_bless_order_list.setBackgroundColor(getResources()
						.getColor(R.color.button_greg));
				formy_bless_order_list_text.setTextColor(cs2);

				left = true;
				bless=1;
				my_find_bless_list.setAdapter(myFindListAdapter);
				myFindListAdapter.notifyDataSetChanged();
				if(!isBottom){
					my_find_bless_list.setPullLoadEnable(true);
				}

			}
			break;

		case R.id.formy_bless_order_list:
			if (left) {
				my_find_bless_list.setAdapter(formyFindListAdapter);
				Resources resource = (Resources) getBaseContext()
						.getResources();
				ColorStateList csl = (ColorStateList) resource
						.getColorStateList(R.color.color_text_normal);
				ColorStateList cs2 = (ColorStateList) resource
						.getColorStateList(R.color.text_normal);

				formy_bless_order_list.setBackgroundColor(getResources()
						.getColor(R.color.button_greg_hover));
				formy_bless_order_list_text.setTextColor(csl);

				my_bless_order_list.setBackgroundColor(getResources().getColor(
						R.color.button_greg));
				my_bless_order_list_text.setTextColor(cs2);

				left = false;
				bless=2;
	            default_order_list();

			}
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			UserGroupTab.getInstance().onKeyDown(keyCode, event);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
