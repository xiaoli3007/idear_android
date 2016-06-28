package com.wyj.Activity;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;



import com.wyj.adapter.FindListAdapter;
import com.wyj.adapter.ListTempleNameAdapter;
import com.wyj.dataprocessing.AsynTaskHelper;

import com.wyj.dataprocessing.JsonHelper;
import com.wyj.dataprocessing.JsonToListHelper;
import com.wyj.dataprocessing.AsynTaskHelper.OnDataDownloadListener;

import com.wyj.http.WebApiUrl;

import com.wyj.pipe.Cms;
import com.wyj.pipe.SinhaPipeClient;
import com.wyj.pipe.SinhaPipeMethod;
import com.wyj.pipe.Utils;
import com.wyj.xlistview.XListView;
import com.wyj.xlistview.XListView.IXListViewListener;

import android.app.Activity;
import android.app.ProgressDialog;

import android.content.Context;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;


import android.os.Bundle;
import android.os.Handler;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import android.widget.LinearLayout;

import android.widget.ListView;
import android.widget.PopupWindow;


import android.widget.TextView;

import android.widget.Toast;

public class Find extends Activity implements IXListViewListener{
	


	private TextView daochang_select_showname;
	private LinearLayout daochang_select;
	private List<Map<String, Object>> daochang_data; // 加载到适配器中的数据源
	private int tid = 0; // 道场id的标识
	private int mid=0;

	private XListView mListView;
	private List<Map<String, Object>> Listdata = new ArrayList<Map<String, Object>>(); // 加载到适配器中的数据源
	private FindListAdapter mAdapter;
	//private BaseListAdapter mAdapter;
	private int page = 1;
	private int pagesize = 10;
	private boolean isBottom = false;// 判断是否滚动到数据最后一条
	public int lastItemId;

	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private ProgressDialog pDialog = null;
	private Handler mHandler;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Listdata = new ArrayList<Map<String, Object>>();
		View contenView = LayoutInflater.from(this.getParent()).inflate(
				R.layout.tab_find, null);

		setContentView(contenView);
		if(!TextUtils.isEmpty(Cms.APP.getMemberId())){
			mid=  Integer.valueOf(Cms.APP.getMemberId()).intValue();
		}
		int detail_tid = getIntent().getIntExtra("tid", 0);

		if (detail_tid != 0) {
			tid = detail_tid; // 详情页面返回寺庙的传值
		}
		
		
		ActivityfindViewById();
		ActivityAction();


	}
	
	private void ActivityfindViewById() {
		// TODO Auto-generated method stub
		
		daochang_select =(LinearLayout) findViewById(R.id.daochang_select);
		daochang_select_showname= (TextView) findViewById(R.id.daochang_select_showname);
		
	}
	
	private void ActivityAction() {
		// TODO Auto-generated method stub
		
		
		mHandler = new Handler();
		mListView = (XListView) findViewById(R.id.find_list);
		mAdapter = new FindListAdapter(getBaseContext(), Listdata,tid);
		mListView.setAdapter(mAdapter);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(true);
		mListView.setPullLoadEnable_no_view(true);
		mListView.setXListViewListener(this);
	
		daochang_select.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(daochang_data!=null){
							new MyPopupWindows(Find.this,v,getParent().getParent(),daochang_data);	
						}else{
						    get_daochang_list(null, WebApiUrl.GET_TEMPLELIST, getParent(),v);
						}
						
						
					}
				});
		
		default_order_list();
	}


	private void get_daochang_list(Map<String, Object> map, String url,
			final Context context,final View v) {

		
		AsynTaskHelper asyntask = new AsynTaskHelper();
		asyntask.dataDownload(url, map, new OnDataDownloadListener() {
			@Override
			public void onDataDownload(String result) {
				if (result != null) {
					// Listdata.clear();
					List<Map<String, Object>> items;

					items = JsonToListHelper.gettemplelist_json(result);
					daochang_data=new ArrayList<Map<String,Object>>(); 
					@SuppressWarnings("serial")
					Map<String, Object> map = new HashMap<String, Object>(){{put("templeid", 0);put("templename", "全部道场");}};
					daochang_data.add(map);
					daochang_data.addAll(items);
					//daochang_data =items;
					// 初始化------------------------------------

					// 初始化下拉选项------------------------------------
					
			new MyPopupWindows(Find.this,v,getParent().getParent(),daochang_data);

				} else {
					Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT).show();
				}

			}
		}, context, "GET");

	}



	private void default_order_list() { // 默认加载 和更换道场加载

		if (isBottom) {
			isBottom = false;
		}
		
		page = 1;
		Listdata.clear();
		mListView.setPullLoadEnable(true);
		mListView.setAdapter(mAdapter);
		//mListView.setPullLoadEnable_no_view(true);
		listAdapter(WebApiUrl.GET_ORDERLIST + "?p=" + page + "&&pz="
				+ pagesize + "&&tid=" + tid+ "&&mid=" + mid, 1,false); // 默认加载第一页
	}
	
	
	
	private void onLoad() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
		mListView.setRefreshTime("刚刚");
		
	}
	
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub

		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				listAdapter( WebApiUrl.GET_ORDERLIST + "?p=" + 1 + "&&pz="
						+ pagesize + "&&tid=" + tid+ "&&mid=" + mid,0,true); // 加载
				onLoad();
			}
		}, 2000);
	
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
						
			if (isBottom) {
				
				Utils.ShowToast(Find.this, "没有更多了");
				
			}else{
				page++;
				listAdapter( WebApiUrl.GET_ORDERLIST + "?p=" + page + "&&pz="
						+ pagesize + "&&tid=" + tid+ "&&mid=" + mid,0,false); // 加载
				onLoad();
			}
				
				
			}
		}, 2000);	
	}
	
	private void listAdapter( String url ,final int first,final boolean is_refersh_or_load) {
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
							items = JsonHelper.jsonTolistmap(
									(String) result, "orderlist");
							Log.i("bbbb", "-----请求回来-23344---"+items.toString() );
							
							if (items.toString().equals("[]")) {
								isBottom = true;
								mListView.setPullLoadEnable(false);	
								//mListView.setPullLoadEnable_no_view(false);
								Utils.ShowToast(Find.this, "没有更多了");
							}else{
								
								if(is_refersh_or_load){
									
								//	Listdata.addAll(0, items);	//更新的
									Listdata.clear();
									Listdata.addAll(items);	
									mListView.setAdapter(mAdapter);
								}else{
									Listdata.addAll(items);	
								}
								
								mAdapter.notifyDataSetChanged();
								lastItemId =Integer.valueOf((String)items.get(0).get("orderid")).intValue(); 
							}
							
						} else {
							int err = R.string.dialog_system_error_content;
							if (error == httpClient.ERR_TIME_OUT) {
								err = R.string.dialog_network_error_timeout;
							}
							if (error == httpClient.ERR_GET_ERR) {
								err = R.string.dialog_network_error_getdata;
							}
							Utils.ShowToast(Find.this, err);
						}
					}
				});
				this.httpMethod.start();
		
		} else {
			Utils.ShowToast(Find.this, R.string.dialog_network_check_content);
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
	@SuppressWarnings("deprecation")
	public class MyPopupWindows extends PopupWindow {
		
		private PopupWindow popupwindow;
		private ListTempleNameAdapter adapterListTemple;
		
		
		public MyPopupWindows(Context mContext,  View parent, final Activity activity, final List<Map<String, Object>> items) {
			
			final Activity pactivity=activity;
			
			View customView = View.inflate(mContext, R.layout.popview_item,
					null);
			ListView List = (ListView) customView
					.findViewById(R.id.popview_discover_container);
			
			adapterListTemple = new ListTempleNameAdapter(
					Find.this, items);
			List.setAdapter(adapterListTemple);
			
			
			popupwindow = new PopupWindow(customView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);			
			 //以下为弹窗后面的背景色设置
		 	ColorDrawable cd = new ColorDrawable(0x000000);
		 	popupwindow.setBackgroundDrawable(cd); 
		   	//产生背景变暗效果
		    WindowManager.LayoutParams lp=activity.getWindow().getAttributes(); 
			lp.alpha = 0.7f;
			activity.getWindow().setAttributes(lp);
			
			popupwindow.setWidth(LayoutParams.FILL_PARENT);
			popupwindow.setHeight(LayoutParams.FILL_PARENT);
			popupwindow.setBackgroundDrawable(new BitmapDrawable());
			popupwindow.setFocusable(true);
			popupwindow.setOutsideTouchable(true);
			popupwindow.setContentView(customView);

			int[] location = new int[2];
			parent.getLocationOnScreen(location);

			popupwindow.showAsDropDown(parent); //显示在button的下面

			// 自定义view添加触摸事件
			popupwindow.update();
			popupwindow.setOnDismissListener(new OnDismissListener() {		//恢复背景色
				
				public void onDismiss() {
					// TODO Auto-generated method stub
					WindowManager.LayoutParams lp=pactivity.getWindow().getAttributes();
	    			lp.alpha = 1f;
	    			pactivity.getWindow().setAttributes(lp);
				}
			});
			
			List.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					
					
					 tid = (Integer) items.get(arg2).get("templeid");
					 default_order_list();
					 daochang_select_showname.setText((String) items.get(arg2).get("templename"));
					 popupwindow.dismiss();
				}
			});
			adapterListTemple.notifyDataSetChanged();
			
			customView.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (popupwindow != null && popupwindow.isShowing()) {
						popupwindow.dismiss();
						popupwindow = null;
					}

					return false;
				}
			});		
		}
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
