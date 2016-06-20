package com.example.adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.cms.R;
import com.example.cms.Cms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class ShowDiscoverAdapter extends BaseAdapter {
	private Context context;
	public JSONArray data = new JSONArray();

	public static class ListItem {
		public ImageButton avatar;
		public TextView name;
		public TextView date;
	}

	public ShowDiscoverAdapter(Context context) {
		this.context = context;
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
			convertView = LayoutInflater.from(this.context).inflate(R.layout.show_discover_listview_item, null);
			listItem = new ListItem();
			listItem.avatar = (ImageButton) convertView.findViewById(R.id.show_discover_listitem_avatar_button);
			listItem.name = (TextView) convertView.findViewById(R.id.show_discover_listitem_name_text);
			listItem.date = (TextView) convertView.findViewById(R.id.show_discover_listitem_date_text);
			convertView.setTag(listItem);
		} else {
			listItem = (ListItem) convertView.getTag();
		}
		JSONObject item = this.data.optJSONObject(position);
		listItem.name.setText(item.optString("name", "").trim());
		listItem.date.setText(item.optString("date", "").trim());
		return convertView;
	}
}