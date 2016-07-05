package com.example.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.*;
import android.widget.*;
import com.example.adapter.DiscoverListAdapter;
import com.example.adapter.Discover_head_select_adapter;
import com.example.utils.*;
import com.example.xlistview.XListView;

import com.example.pipe.SinhaPipeClient;
import com.example.pipe.SinhaPipeMethod;
import com.example.cms.BaseFragment;
import com.example.cms.Consts;
import com.example.cms.R;
import com.example.cms.Cms;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import org.json.JSONObject;

public class Discover extends BaseFragment implements XListView.IXListViewListener {

	private TextView daochang_select_showname;
	private LinearLayout daochang_select;
	private List<Map<String, Object>> daochang_data; // 加载到适配器中的数据源
	private int tid = 0; // 道场id的标识
	private int mid=0;

	private XListView mListView;
	private List<Map<String, Object>> Listdata = new ArrayList<Map<String, Object>>(); // 加载到适配器中的数据源
	private DiscoverListAdapter mAdapter;
	//private BaseListAdapter mAdapter;
	private int page = 1;
	private int pagesize = 10;
	private boolean isBottom = false;// 判断是否滚动到数据最后一条
	public int lastItemId;

	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private ProgressDialog pDialog = null;
	private  Toast  nomore=null;
	private Handler mHandler;

	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		View view = inflater.inflate(R.layout.discover, null);

		daochang_select =(LinearLayout) view.findViewById(R.id.daochang_select);
		daochang_select_showname= (TextView) view.findViewById(R.id.daochang_select_showname);
		mListView = (XListView) view.findViewById(R.id.find_list);


		Cms.discoverHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case 1:
						goActivity(Login.class);
						break;
					case 2:
							int position = (Integer) ((Button) msg.obj).getTag();
							Bundle bundle = new Bundle();
							bundle.putString("title", mAdapter.mData.get(position).toString());
							bundle.putInt("id", Integer.parseInt((String) mAdapter.mData.get(position).get("orderid")));
							goActivity(ShowDiscover.class, bundle);

						break;
					default:
						break;
				}
			};
		};

		this.httpClient = new SinhaPipeClient();
		return view;
	}

	public void onActivityCreated(Bundle sinha) {
		super.onActivityCreated(sinha);
		ActivityAction();
	}



	private void ActivityAction() {
		// TODO Auto-generated method stub


		mHandler = new Handler();

		mAdapter = new DiscoverListAdapter(getActivity(), Listdata,tid);
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
					new MyPopupWindows(getActivity(),v,getActivity(),daochang_data);
				}else{
					get_daochang_list(null, "", getActivity(),v);
				}


			}
		});

		default_order_list();	//默认加载列表数据
	}


	private void get_daochang_list(Map<String, Object> map, String url,
								   final Context context, final View v) {

		List<Map<String, Object>> items;
		items = JsonToListDemo.gettemplelist_json(JsonString.get_discover_select());
		daochang_data=new ArrayList<Map<String,Object>>();
		@SuppressWarnings("serial")
		Map<String, Object> mapdata = new HashMap<String, Object>(){{put("templeid", 0);put("templename", "全部");}};
		daochang_data.add(mapdata);
		daochang_data.addAll(items);
		new MyPopupWindows(getActivity(),v,getActivity(),daochang_data);

		//一下是异步加载的数据实例=================================================================================
//		AsynTaskHelper asyntask = new AsynTaskHelper();
//		asyntask.dataDownload(url, map, new AsynTaskHelper.OnDataDownloadListener() {
//			@Override
//			public void onDataDownload(String result) {
//				if (result != null) {
//					// Listdata.clear();
//					List<Map<String, Object>> items;
//
//					items = JsonToListDemo.gettemplelist_json(result);
//					daochang_data=new ArrayList<Map<String,Object>>();
//					@SuppressWarnings("serial")
//					Map<String, Object> map = new HashMap<String, Object>(){{put("templeid", 0);put("templename", "全部");}};
//					daochang_data.add(map);
//					daochang_data.addAll(items);
//					// 初始化------------------------------------
//
//					// 初始化下拉选项------------------------------------
//
//					new MyPopupWindows(getActivity(),v,getActivity(),daochang_data);
//
//				} else {
//					Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT).show();
//				}
//
//			}
//		}, context, "GET");

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
		listAdapter(Consts.WEIBO_REDIRECT_URL + "?p=" + page + "&&pz="
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
				listAdapter( Consts.WEIBO_REDIRECT_URL + "?p=" + 1 + "&&pz="
						+ pagesize + "&&tid=" + tid+ "&&mid=" + mid,0,true); // 加载
				onLoad();
			}
		}, 1000);

	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {

				if (isBottom) {

					shownomoreLoading();

				}else{
					page++;
					listAdapter( Consts.WEIBO_REDIRECT_URL + "?p=" + page + "&&pz="
							+ pagesize + "&&tid=" + tid+ "&&mid=" + mid,0,false); // 加载
					onLoad();
				}


			}
		}, 500);
	}

	private void listAdapter( String url ,final int first,final boolean is_refersh_or_load) {

		if(first>0){
			showLoading();
		}
		String result=JsonString.get_discover_list_data();

		if(page==3){	//模拟一共就就三个页面
			result = "{\"code\":\"succeed\",\"msg\":\"\\u8bfb\\u53d6\\u6210\\u529f\\uff01\",\"orderlist\":[]}" ;
		}
		List<Map<String, Object>> items;
		items = JsonHelper.jsonTolistmap(result, "orderlist");

		if (items.toString().equals("[]")) {
			isBottom = true;
			mListView.setPullLoadEnable(false);
			//mListView.setPullLoadEnable_no_view(false);
		//	Utils.ShowToast(getActivity(), "没有更多了");
			shownomoreLoading();
		}else{
			if(first>0){
				showLoading();
			}
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

//		this.httpClient = new SinhaPipeClient();
//		if (Utils.CheckNetwork()) {
//			if(first>0){
//				showLoading();
//			}
//
//			this.httpClient.Config("get", url, null, true);
//			this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
//				public void CallFinished(String error, Object result) {
//					if(first>0){
//						showLoading();
//					}
//					if (null == error) {
//
//						List<Map<String, Object>> items;
//						items = JsonHelper.jsonTolistmap(
//								(String) result, "orderlist");
//						Log.i("bbbb", "-----请求回来-23344---"+items.toString() );
//
//						if (items.toString().equals("[]")) {
//							isBottom = true;
//							mListView.setPullLoadEnable(false);
//							//mListView.setPullLoadEnable_no_view(false);
//							Utils.ShowToast(getActivity(), "没有更多了");
//						}else{
//
//							if(is_refersh_or_load){
//
//								//	Listdata.addAll(0, items);	//更新的
//								Listdata.clear();
//								Listdata.addAll(items);
//								mListView.setAdapter(mAdapter);
//							}else{
//								Listdata.addAll(items);
//							}
//
//							mAdapter.notifyDataSetChanged();
//							lastItemId =Integer.valueOf((String)items.get(0).get("orderid")).intValue();
//						}
//
//					} else {
//						int err = R.string.dialog_system_error_content;
//						if (error == httpClient.ERR_TIME_OUT) {
//							err = R.string.dialog_network_error_timeout;
//						}
//						if (error == httpClient.ERR_GET_ERR) {
//							err = R.string.dialog_network_error_getdata;
//						}
//						Utils.ShowToast(getActivity(), err);
//					}
//				}
//			});
//			this.httpMethod.start();
//
//		} else {
//			Utils.ShowToast(getActivity(), R.string.dialog_network_check_content);
//		}

	}

	private void showLoading() {

		if(pDialog!=null){
			pDialog.dismiss();
		}else{

			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("数据加载中。。。");
			pDialog.show();
		}

	}


	private void shownomoreLoading() {

		if(nomore!=null){
			nomore.cancel();
			nomore=null;
		}else{
			nomore =Toast.makeText(getActivity(), "没有更多了！", Toast.LENGTH_LONG);
			nomore.show();
		}

	}
	@SuppressWarnings("deprecation")
	public class MyPopupWindows extends PopupWindow {

		private PopupWindow popupwindow;
		private Discover_head_select_adapter adapterListTemple;


		public MyPopupWindows(Context mContext, View parent, final Activity activity, final List<Map<String, Object>> items) {

			final Activity pactivity=activity;

			View customView = View.inflate(mContext, R.layout.discover_head_select_popwindow_view,
					null);
			ListView List = (ListView) customView
					.findViewById(R.id.popview_discover_container);

			adapterListTemple = new Discover_head_select_adapter(
					getActivity(), items);
			List.setAdapter(adapterListTemple);


			popupwindow = new PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
			//以下为弹窗后面的背景色设置
			ColorDrawable cd = new ColorDrawable(0x000000);
			popupwindow.setBackgroundDrawable(cd);
			//产生背景变暗效果
			WindowManager.LayoutParams lp=activity.getWindow().getAttributes();
			lp.alpha = 0.7f;
			activity.getWindow().setAttributes(lp);

			popupwindow.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
			popupwindow.setHeight(ViewGroup.LayoutParams.FILL_PARENT);
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

			List.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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

			customView.setOnTouchListener(new View.OnTouchListener() {

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







}