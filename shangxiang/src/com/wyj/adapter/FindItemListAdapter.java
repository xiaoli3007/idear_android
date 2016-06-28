package com.wyj.adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import com.wyj.Activity.OrderForm;
import com.wyj.Activity.R;
import com.wyj.Activity.ShowTemple;
import com.wyj.Activity.Wish;
import com.wyj.Activity.WishGroupTab;
import com.wyj.dataprocessing.BitmapManager;
import com.wyj.http.WebApiUrl;
import com.wyj.utils.Tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class FindItemListAdapter extends BaseAdapter  {
	private Context context;
	public JSONArray data = new JSONArray();

	public static class ListItem {
		ImageView finditem_member_list_head;
		TextView finditem_member_list_username;
		TextView finditem_member_list_cn_retime;
	}

	public FindItemListAdapter(Context context, JSONArray data) {
		this.context = context;
		this.data = data;
	}

	@Override
	public int getCount() {
		return this.data.length();
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
		ListItem listItem;
		
		final JSONObject item = this.data.optJSONObject(position);
		
		if (null == convertView) {
			convertView = LayoutInflater.from(this.context).inflate(
					R.layout.find_list_items, null);
			
			listItem = new ListItem();
			listItem.finditem_member_list_head = (ImageView) convertView
					.findViewById(R.id.finditem_member_list_head);
			listItem.finditem_member_list_username = (TextView) convertView
					.findViewById(R.id.finditem_member_list_username);
			listItem.finditem_member_list_cn_retime = (TextView) convertView
					.findViewById(R.id.finditem_member_list_cn_retime);
			
			convertView.setTag(listItem);
		} else {
			listItem = (ListItem) convertView.getTag();
		}
		
		String truename=(!item.optString("truename", "").equals(""))?item.optString("truename", ""):"匿名";
		
		String name=(!item.optString("nickname", "").equals(""))?item.optString("nickname", ""):truename;
		listItem.finditem_member_list_username.setText(name);
		listItem.finditem_member_list_cn_retime.setText(item.optString("cn_retime", "")+"加持过");

		BitmapManager.getInstance().loadBitmap(
				item.optString("headface", ""), listItem.finditem_member_list_head,
				Tools.readBitmap(this.context, R.drawable.me));


		return convertView; 
	}


}