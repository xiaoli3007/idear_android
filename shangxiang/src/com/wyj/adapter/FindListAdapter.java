package com.wyj.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.wyj.Activity.FindGroupTab;
import com.wyj.Activity.Find_item;

import com.wyj.Activity.R;

import com.wyj.dataprocessing.BitmapManager;
import com.wyj.http.WebApiUrl;

import com.wyj.pipe.Cms;
import com.wyj.pipe.SinhaPipeClient;
import com.wyj.pipe.SinhaPipeMethod;
import com.wyj.pipe.Utils;
import com.wyj.utils.Tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import android.widget.BaseAdapter;

import android.widget.ImageView;

import android.widget.TextView;

public class FindListAdapter extends BaseAdapter implements OnClickListener{
	private Context context;
	public JSONArray data = new JSONArray();
	private List<Map<String, Object>> mData;
	private int tid;
	private ArrayList<Object> btnMap=new ArrayList<Object>();
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;

	public static class ListItem {
		ImageView list_find_headface;
		TextView list_find_content;
		TextView list_find_username;
		TextView list_find_address;
		TextView list_find_jiachi;
		TextView list_find_zan;
	}

	public FindListAdapter(Context context, List<Map<String, Object>> list,int tid) {
		this.context = context;
		this.mData = list;
		this.tid=tid;
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
					R.layout.list_find_items, null);
			listItem.list_find_headface = (ImageView) convertView
					.findViewById(R.id.list_find_headface);
			listItem.list_find_content = (TextView) convertView
					.findViewById(R.id.list_find_content);
			listItem.list_find_username = (TextView) convertView
					.findViewById(R.id.list_find_username);
			listItem.list_find_address = (TextView) convertView
					.findViewById(R.id.list_find_address);
			listItem.list_find_jiachi = (TextView) convertView
					.findViewById(R.id.list_find_jiachi);

			listItem.list_find_zan = (TextView) convertView
					.findViewById(R.id.list_find_zan);

			
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

		String findaddress = "刚刚在"
				+ (String) this.mData.get(position).get("templename")
				+ (String) this.mData.get(position).get("alsowish")
				+ (String) this.mData.get(position).get("wishtype");

		int co_blessings = Integer.valueOf(
				(String) this.mData.get(position).get("co_blessings"))
				.intValue();
		String jiachipeople = "";
		if (co_blessings > 0) {
			jiachipeople = (String) this.mData.get(position).get(
					"name_blessings")
					+ "等" + co_blessings + "人加持";
		}
		listItem.list_find_address.setText(findaddress);
		listItem.list_find_jiachi.setText(jiachipeople);
		
		listItem.list_find_content.setTag(this.mData.get(position).get("orderid"));
		listItem.list_find_content.setOnClickListener(this);
		
		
		
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
		// listItem.list_find_zan.setText(bb);
		listItem.list_find_zan.setTag(position);
		listItem.list_find_zan.setOnClickListener(this);
		return convertView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int Id = v.getId();
		if (R.id.list_find_content == Id) {
			
			Integer orderid = Integer.valueOf((String) v.getTag())
					.intValue();
			// 要跳转的Activity
			Intent intent = new Intent(this.context, Find_item.class);
		
			Bundle bu = new Bundle(); // 这个组件 存值
			bu.putInt("orderid", orderid);
			bu.putInt("tid", tid);
			intent.putExtras(bu); // 放到 intent 里面 然后 传出去
			// 把Activity转换成一个Window，然后转换成View
			
			FindGroupTab.getInstance().switchActivity("Find_item", intent,
					-1, -1);
		}
		//Log.i("bbbb", "-----位置1111----" + Id);
		if (R.id.list_find_zan == Id) {
			
				if(!TextUtils.isEmpty(Cms.APP.getMemberId())){
				int position = (Integer) v.getTag();
				Map<String, Object> Object =(Map<String, Object>)  this.mData.get(position);
				String bleuser= (String) Object.get("bleuser");
				String orderid= (String) Object.get("orderid");
				if(bleuser.equals("0")){
					
					 addblessingsdo(orderid,v,position);//加持操作
				}else{
					Utils.ShowToast(context,"不能重复加持！");
				}
			}else{
				Utils.ShowToast(context,"请先登录！");
			}
			  
			  
		}
		
	}
	
	
	private void setcolorstatus(TextView list_find_zany) {
		// TODO Auto-generated method stub
		
		
		list_find_zany.setText("已加持");
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
		
		
		list_find_zany.setText("加持");
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
				this.httpClient.Config("get", WebApiUrl.GET_addblessingsdo, params, true);
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