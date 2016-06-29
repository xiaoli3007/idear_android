package com.example.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.activity.ShowDiscover;
import com.example.cms.Cms;
import com.example.cms.Consts;
import com.example.cms.R;
import com.example.pipe.SinhaPipeClient;
import com.example.pipe.SinhaPipeMethod;
import com.example.utils.BitmapManager;
import com.example.utils.Tools;
import com.example.utils.Utils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DiscoverListAdapter extends BaseAdapter implements OnClickListener{
	private Context context;
	public JSONArray data = new JSONArray();
	public List<Map<String, Object>> mData;
	private int tid;
	private ArrayList<Object> btnMap=new ArrayList<Object>();
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;

	public static class ListItem {
		ImageView list_find_headface;
		TextView list_find_content;
		TextView list_find_username;
		TextView list_find_zan;
		Button discover_show_detail_button;
	}

	public DiscoverListAdapter(Context context, List<Map<String, Object>> list, int tid) {
		this.context = context;
		this.mData = list;
		this.tid=tid;	// 主页面如果有条件传过来值
	}

	@Override
	public int getCount() {
		return this.mData.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ListItem listItem;

		if (convertView == null) {
			listItem = new ListItem();
			convertView = LayoutInflater.from(this.context).inflate(
					R.layout.discover_list_items, null);
			listItem.list_find_headface = (ImageView) convertView
					.findViewById(R.id.list_find_headface);
			listItem.list_find_content = (TextView) convertView
					.findViewById(R.id.list_find_content);
			listItem.list_find_username = (TextView) convertView
					.findViewById(R.id.list_find_username);

			listItem.list_find_zan = (TextView) convertView
					.findViewById(R.id.list_find_zan);

			listItem.discover_show_detail_button = (Button) convertView
					.findViewById(R.id.discover_show_detail_button);

			convertView.setTag(listItem);

		} else {
			listItem = (ListItem) convertView.getTag();

		}

		// viewHolder.img.setBackgroundResource(R.drawable.foot_07);
		BitmapManager.getInstance().loadBitmap(
				(String) this.mData.get(position).get("headface"),
				listItem.list_find_headface,
				Tools.readBitmap(this.context, R.drawable.foot_07));

		listItem.list_find_content.setText((CharSequence) this.mData.get(
				position).get("wishtext"));
		listItem.list_find_username.setText((CharSequence) this.mData.get(
				position).get("wishname"));


		int co_blessings = Integer.valueOf(
				(String) this.mData.get(position).get("co_blessings"))
				.intValue();
		String jiachipeople = "";
		if (co_blessings > 0) {
			jiachipeople = (String) this.mData.get(position).get(
					"name_blessings")
					+ "等" + co_blessings + "人赞";
		}

		listItem.list_find_content.setTag(this.mData.get(position).get("orderid"));


		if(!TextUtils.isEmpty(Cms.APP.getMemberId())){
			String bleuser=(String)this.mData.get(position).get("bleuser");
			if(!bleuser.equals("0")){
				 setcolorstatus(listItem.list_find_zan);
			}else{
				setcolor(listItem.list_find_zan);
			}
		}else{
			setcolor(listItem.list_find_zan);
		}
		listItem.list_find_zan.setTag(position);
		listItem.list_find_zan.setOnClickListener(this);
		listItem.discover_show_detail_button.setTag(position);
		listItem.discover_show_detail_button.setOnClickListener(this);
		return convertView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int Id = v.getId();
		int action = 0;
		if (R.id.discover_show_detail_button == Id) {

			action =2;
//			Integer orderid = Integer.valueOf((String) v.getTag())
//					.intValue();
//			// 要跳转的Activity
//			Intent intent = new Intent(this.context, ShowDiscover.class);
//			Bundle bu = new Bundle(); // 这个组件 存值
//			bu.putInt("orderid", orderid);
//			bu.putInt("tid", tid);
//			intent.putExtras(bu); // 放到 intent 里面 然后 传出去

		}
		//Log.i("bbbb", "-----位置1111----" + Id);
		if (R.id.list_find_zan == Id) {
				action =1;
//			if(!TextUtils.isEmpty(Cms.APP.getMemberId())){
//				int position = (Integer) v.getTag();
//				Map<String, Object> Object =(Map<String, Object>)  this.mData.get(position);
//				String bleuser= (String) Object.get("bleuser");
//				String orderid= (String) Object.get("orderid");
//				if(bleuser.equals("0")){
//
//					 addblessingsdo(orderid,v,position);//赞操作
//				}else{
//					Utils.ShowToast(context,"不能重复赞！");
//				}
//			}else{
//				Utils.ShowToast(context,"请先登录！");
//			}

		}

		Message msg = Cms.discoverHandler.obtainMessage(action, v);
		Cms.discoverHandler.sendMessage(msg);

	}


	private void setcolorstatus(TextView list_find_zany) {
		// TODO Auto-generated method stub


		list_find_zany.setText("已赞");
		 Resources resource = (Resources) this.context.getResources();
         ColorStateList csl = (ColorStateList) resource
                 .getColorStateList(R.color.color_text_selected);
         list_find_zany.setTextColor(csl);
         Drawable drawable= this.context.getResources().getDrawable(R.drawable.load_hover);
         /// 这一步必须要做,否则不会显示.
         drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
         list_find_zany.setCompoundDrawables(drawable, null, null, null);
	}

	private void setcolor(TextView list_find_zany) {
		// TODO Auto-generated method stub


		list_find_zany.setText("赞");
		 Resources resource = (Resources) this.context.getResources();
         ColorStateList csl = (ColorStateList) resource
                 .getColorStateList(R.color.text_normal);
         list_find_zany.setTextColor(csl);
         Drawable drawable= this.context.getResources().getDrawable(R.drawable.load);
         /// 这一步必须要做,否则不会显示.
         drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
         list_find_zany.setCompoundDrawables(drawable, null, null, null);
	}

	private void addblessingsdo(String oid, final View vv, final int positions) {

		this.httpClient = new SinhaPipeClient();
		if (Utils.CheckNetwork()) {

			if(!TextUtils.isEmpty(Cms.APP.getMemberId())){
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("mid", Cms.APP.getMemberId()));
				params.add(new BasicNameValuePair("oid", oid));
				Log.i("bbbb", "-----请求----"+params.toString() );
				this.httpClient.Config("get", Consts.GET_addblessingsdo, params, true);
				this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
					public void CallFinished(String error, Object result) {
						Log.i("bbbb", "-----请求回来----"+result );
						if (null == error) {
							try {
								JSONObject jsonobject=new JSONObject((String) result);
								if(jsonobject.optString("code", "").equals("succeed")){

									TextView list_find_zany=(TextView) vv;
									setcolorstatus(list_find_zany);
									mData.get(positions).put("bleuser", Cms.APP.getMemberId());//更新数据
									Utils.ShowToast(context, jsonobject.optString("msg", ""));
								}else{
									Utils.ShowToast(context, jsonobject.optString("msg", ""));
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
							Utils.ShowToast(context, err);
						}
					}
				});
				this.httpMethod.start();

			}else{

			//	Utils.Dialog(context, "提示","请先登录账户！");
				Utils.ShowToast(context, "请先登录账户！");
			}

		} else {
			Utils.ShowToast(context, R.string.dialog_network_check_content);
		}
	}






}