package com.shangxiang.android.adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import com.shangxiang.android.R;
import com.shangxiang.android.ShangXiang;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class NoticeAdapter extends BaseAdapter implements OnClickListener {
	private Context context;
	public int orderType = 0;
	public JSONArray data = new JSONArray();

	public static class ListItem {
		public TextView title;
		public TextView date;
		public Button showDetail;
	}

	public NoticeAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return data.length();
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
			convertView = LayoutInflater.from(this.context).inflate(R.layout.notice_listview_item, null);
			listItem = new ListItem();
			listItem.title = (TextView) convertView.findViewById(R.id.notice_listitem_title_text);
			listItem.date = (TextView) convertView.findViewById(R.id.notice_listitem_date_text);
			listItem.showDetail = (Button) convertView.findViewById(R.id.notice_listitem_show_detail_button);
			convertView.setTag(listItem);
		} else {
			listItem = (ListItem) convertView.getTag();
		}
		JSONObject item = data.optJSONObject(position);
		listItem.title.setText(item.optString("title", ""));
		listItem.date.setText(item.optString("cn_retime", ""));
		Bundle bundle = new Bundle();
		bundle.putString("id", item.optString("id", ""));
		bundle.putString("title", item.optString("title", ""));
		bundle.putString("date", item.optString("cn_retime", ""));
		listItem.showDetail.setTag(bundle);
		listItem.showDetail.setOnClickListener(this);
		return convertView;
	}

	@Override
	public void onClick(View v) {
		Message msg = ShangXiang.noticeHandler.obtainMessage(0, v.getTag());
		ShangXiang.noticeHandler.sendMessage(msg);
	}
}