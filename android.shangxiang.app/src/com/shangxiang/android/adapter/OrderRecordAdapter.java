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

public class OrderRecordAdapter extends BaseAdapter implements OnClickListener {
	private Context context;
	public int orderType = 0;
	public JSONArray data = new JSONArray();

	public static class ListItem {
		public TextView title;
		public TextView desc;
		public TextView status;
		public Button showDetail;
	}

	public OrderRecordAdapter(Context context) {
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
			convertView = LayoutInflater.from(this.context).inflate(R.layout.order_record_listview_item, null);
			listItem = new ListItem();
			listItem.title = (TextView) convertView.findViewById(R.id.order_record_listitem_title_text);
			listItem.desc = (TextView) convertView.findViewById(R.id.order_record_listitem_desc_text);
			listItem.status = (TextView) convertView.findViewById(R.id.order_record_listitem_status_text);
			listItem.showDetail = (Button) convertView.findViewById(R.id.order_record_listitem_show_detail_button);
			convertView.setTag(listItem);
		} else {
			listItem = (ListItem) convertView.getTag();
		}
		JSONObject item = data.optJSONObject(position);
		listItem.title.setText(item.optString("templename", "") + "（" + item.optString("buddhistname", "") + "）代祈福" + (this.orderType == 0 ? "求愿" : "还愿"));
		listItem.desc.setText("订单号：" + item.optString("orderid", "") + "   " + item.optString("buddhadate", ""));
		if ("已完成".equals(item.optString("status", ""))) {
			listItem.status.setTextColor(this.context.getResources().getColor(R.color.text_title));
		} else {
			listItem.status.setTextColor(this.context.getResources().getColor(R.color.text_normal));
		}
		listItem.status.setText(item.optString("status", ""));
		Bundle bundle = new Bundle();
		bundle.putString("order_id", item.optString("orderid", ""));
		bundle.putInt("order_type", this.orderType);
		bundle.putString("hall_name", item.optString("templename", ""));
		bundle.putString("buddhist_name", item.optString("buddhistname", ""));
		bundle.putString("date", item.optString("buddhadate", ""));
		listItem.showDetail.setTag(bundle);
		listItem.showDetail.setOnClickListener(this);
		return convertView;
	}

	@Override
	public void onClick(View v) {
		Message msg = ShangXiang.orderRecordHandler.obtainMessage(1, v.getTag());
		ShangXiang.orderRecordHandler.sendMessage(msg);
	}
}