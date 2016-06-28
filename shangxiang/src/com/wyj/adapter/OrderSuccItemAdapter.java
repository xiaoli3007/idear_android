package com.wyj.adapter;

import org.json.JSONArray;
import org.json.JSONObject;

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
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class OrderSuccItemAdapter extends BaseAdapter implements OnClickListener {
	private Context context;
	public JSONArray data = new JSONArray();

	public static class ListItem {
		
		public TextView ordersucc_item_info;

	}

	public OrderSuccItemAdapter(Context context, JSONArray data) {
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
		if (null == convertView) {
			convertView = LayoutInflater.from(this.context).inflate(
					R.layout.ordersucc_list_item, null);
			listItem = new ListItem();
			
			listItem.ordersucc_item_info = (TextView) convertView
					.findViewById(R.id.ordersucc_item_info);
			
			convertView.setOnClickListener(this);
			convertView.setTag(listItem);
		} else {
			listItem = (ListItem) convertView.getTag();
		}
		
		JSONObject item = this.data.optJSONObject(position);
	
		listItem.ordersucc_item_info.setText(item.optString("info", ""));


		return convertView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ordersucc_item_info:
			
			break;
		}
	}
}