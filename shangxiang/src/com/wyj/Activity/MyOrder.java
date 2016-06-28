package com.wyj.Activity;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import com.wyj.Activity.R;

import com.wyj.adapter.MyOrderAdapter;

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

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;



public class MyOrder extends Activity implements OnClickListener , IXListViewListener{

	private ImageView back_my;
	private LinearLayout my_order_wish_list, my_order_alsowish_list;
	private TextView my_order_wish_list_text,my_order_alsowish_list_text;
	private XListView wishorder;
	private XListView alsowishorder;
	private List<Map<String, Object>> order_data = new ArrayList<Map<String, Object>>(); // 加载到适配器中的数据源
	private List<Map<String, Object>> alsowish_order_data = new ArrayList<Map<String, Object>>(); // 还愿数据
	
	
	private  MyOrderAdapter OrderlistAdapter,alsowishlistAdapter;
	private String mid;
	private int page = 1;
	private int alsopage = 1;
	private int pagesize = 10;
	private boolean isBottom = false;// 判断是否滚动到数据最后一条
	private boolean is_alsowish_Bottom = false;// 判断是否滚动到数据最后一条
	private int also=0;
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private ProgressDialog pDialog = null;
	private Handler mHandler;
	private boolean left =true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_order);
		findViewById();
		setListener();

	}

	private void findViewById() {

		back_my = (ImageView) findViewById(R.id.my_order_back);
		my_order_wish_list = (LinearLayout) findViewById(R.id.my_order_wish_list);
		my_order_alsowish_list = (LinearLayout) findViewById(R.id.my_order_alsowish_list);
		
		
		my_order_wish_list_text= (TextView) findViewById(R.id.my_order_wish_list_text);
		my_order_alsowish_list_text= (TextView) findViewById(R.id.my_order_alsowish_list_text);
		
		wishorder=(XListView) findViewById(R.id.my_find_order_list);
		alsowishorder=(XListView) findViewById(R.id.my_find_alsowishorder_list);
		
		if(!TextUtils.isEmpty(Cms.APP.getMemberId())){
			
			mid=Cms.APP.getMemberId();
		}
		mHandler = new Handler();
		
	}

	private void setListener() {
		back_my.setOnClickListener(this);
		my_order_wish_list.setOnClickListener(this);
		my_order_alsowish_list.setOnClickListener(this);
		
		
		OrderlistAdapter = new MyOrderAdapter(MyOrder.this, order_data);
		wishorder.setAdapter(OrderlistAdapter);
		wishorder.setPullLoadEnable(true);
		wishorder.setPullRefreshEnable(false);
		wishorder.setXListViewListener(this);
		
		wishorder.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
				TabMenu mainactivity = (TabMenu) getParent().getParent(); // 查找父级的父级
				mainactivity.setCurrentActivity(1);
				Intent intent1 = new Intent(
						MyOrder.this,
						OrderFormDetail.class);
				 Bundle bu=new Bundle();
				 bu.putString("orderid",(String) order_data.get(arg2-1)
							.get("orderid"));
				 
				 Log.i("bbbb", "------订单号传 位置"+arg2+ order_data.get(arg2-1)
							.get("orderid").toString());
				 intent1.putExtras(bu);
				// context.startActivity(intent1);
				 WishGroupTab.getInstance()
						.switchActivity("OrderFormDetail",
								intent1, -1, -1);// 接口请求
			}
		});
		
		alsowishlistAdapter = new MyOrderAdapter(MyOrder.this, alsowish_order_data);
		alsowishorder.setAdapter(alsowishlistAdapter);
		alsowishorder.setPullLoadEnable(true);
		alsowishorder.setPullRefreshEnable(false);
		alsowishorder.setXListViewListener(this);
		
		
		alsowishorder.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
				TabMenu mainactivity = (TabMenu) getParent().getParent(); // 查找父级的父级
				mainactivity.setCurrentActivity(1);
				Intent intent1 = new Intent(
						MyOrder.this,
						OrderFormDetail.class);
				 Bundle bu=new Bundle();
				 bu.putString("orderid",(String) alsowish_order_data.get(arg2-1)
							.get("orderid"));
				
				 intent1.putExtras(bu);
				// context.startActivity(intent1);
				 WishGroupTab.getInstance()
						.switchActivity("OrderFormDetail",
								intent1, -1, -1);// 接口请求
			}
		});
		
		default_order_list() ;
	}
	
	private void onLoad() {
		wishorder.stopRefresh();
		wishorder.stopLoadMore();
		wishorder.setRefreshTime("刚刚");
		
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
				
				if(also>0){
				
					if (is_alsowish_Bottom) {
						
						Utils.ShowToast(MyOrder.this, "没有更多了");
						
					}else{
						alsopage++;
						listAdapter( WebApiUrl.Getgetmemberorderlist + "?p=" + alsopage + "&&pz="
								+ pagesize + "&&mid=" + mid+ "&&also=" + also,0); // 加载
						onLoad();
					}
				}else{
					
					if (isBottom) {
						
						Utils.ShowToast(MyOrder.this, "没有更多了");
						
					}else{
						page++;
						listAdapter( WebApiUrl.Getgetmemberorderlist + "?p=" + page + "&&pz="
								+ pagesize + "&&mid=" + mid+ "&&also=" + also,0); // 加载
						onLoad();
					}
				}
				
			}
		}, 2000);		
	}
	
	private void default_order_list() { // 默认加载 和更换求愿 还愿

		int pages =0;
		if(also>0){
			 pages = alsopage;
		}else{
			 pages = page;
		}
		
		//order_data.clear();
		//alsowish_order_data.clear();
		listAdapter( WebApiUrl.Getgetmemberorderlist + "?p=" + pages + "&&pz="
				+ pagesize + "&&mid=" + mid+ "&&also=" + also,1); // 默认加载第一页
	}
	
	private void listAdapter( String url ,final int first) {
		this.httpClient = new SinhaPipeClient();
		
		if (Utils.CheckNetwork()) {
			if(first>0){
				showLoading();
			}
			
			this.httpClient.Config("get", url, null, true);
				this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
					public void CallFinished(String error, Object result) {
						if(first>0){
							showLoading();
						}
						if (null == error) {
							
							List<Map<String, Object>> items;
							items = JsonHelper.jsonTolistmap((String)result,"myorderlist");
							Log.i("bbbb", "-----请求回来-23344---"+items.toString() );
							if (items.toString().equals("[]")) {
								
								if(also>0){
									is_alsowish_Bottom=true;
									alsowishorder.setPullLoadEnable(false);	
								}else{
									isBottom = true;
									wishorder.setPullLoadEnable(false);	
								}
							}else{
								if(also>0){
									alsowish_order_data.addAll(items);
									alsowishlistAdapter.notifyDataSetChanged();
								}else{
									order_data.addAll(items);
									OrderlistAdapter.notifyDataSetChanged();
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
							Utils.ShowToast(MyOrder.this, err);
						}
					}
				});
				this.httpMethod.start();
		
		} else {
			Utils.ShowToast(MyOrder.this, R.string.dialog_network_check_content);
		}
		
	
	}
	
private void showLoading() {
		
		if(pDialog!=null){
			pDialog.dismiss();
		}else{
			
			pDialog = new ProgressDialog(getParent().getParent());
			pDialog.setMessage("数据加载中。。。");
			pDialog.show();
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.my_order_back:

			Intent intent = new Intent(MyOrder.this, My.class);
			UserGroupTab.getInstance().switchActivity("My", intent, -1, -1);

			break;

		case R.id.my_order_wish_list:  
			
			if(!left){
				
				Resources resource = (Resources) getBaseContext().getResources();
                ColorStateList csl = (ColorStateList) resource
                        .getColorStateList(R.color.color_text_normal);
                ColorStateList cs2 = (ColorStateList) resource
                        .getColorStateList(R.color.text_normal);
                
                my_order_wish_list.setBackgroundColor(getResources().getColor(R.color.button_greg_hover));
                my_order_wish_list_text.setTextColor(csl);
                
                my_order_alsowish_list.setBackgroundColor(getResources().getColor(R.color.button_greg));
                my_order_alsowish_list_text.setTextColor(cs2);
                
                wishorder.setVisibility(View.VISIBLE);
                alsowishorder.setVisibility(View.GONE);
                left=true;
                also=0;
			}
			//Log.i("bbbb", "-----请求回来----"+my_order_wish_list.getBackground().toString() );
			break;

		case R.id.my_order_alsowish_list:
			
			if(left){
				
				Resources resource = (Resources) getBaseContext().getResources();
                ColorStateList csl = (ColorStateList) resource
                        .getColorStateList(R.color.color_text_normal);
                ColorStateList cs2 = (ColorStateList) resource
                        .getColorStateList(R.color.text_normal);
                
                my_order_alsowish_list.setBackgroundColor(getResources().getColor(R.color.button_greg_hover));
                my_order_alsowish_list_text.setTextColor(csl);
                
                my_order_wish_list.setBackgroundColor(getResources().getColor(R.color.button_greg));
                my_order_wish_list_text.setTextColor(cs2);   
                
                alsowishorder.setVisibility(View.VISIBLE);
                wishorder.setVisibility(View.GONE);
                left=false;
                
                also=1;
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
