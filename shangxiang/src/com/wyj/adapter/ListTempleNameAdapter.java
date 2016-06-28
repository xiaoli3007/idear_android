package com.wyj.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wyj.Activity.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;


public class ListTempleNameAdapter extends BaseAdapter {
	private Context context;
	//public List<Map<String, Object>> Listdatas=new ArrayList<Map<String,Object>>(){{add(new HashMap<String, Object>(){{put("templeid", 0);put("templename", "全部道场");}});}};
	
	public List<Map<String, Object>> Listdatas;
	public class ListItem {

		public TextView nameHall;

	}

	public ListTempleNameAdapter(Context context, List<Map<String, Object>> data) {
		this.context = context;
		
//		Map<String, Object> map = new HashMap<String, Object>(){{put("templeid", 0);put("templename", "全部道场");}};
//		map.put("templeid", 0);
//		map.put("templename","全部道场");
		
		this.Listdatas = data;
		//this.Listdatas.addAll(data);
		//this.Listdatas.add(0,map); 添加到第一个
		
	}

	@Override
	public int getCount() {
		return this.Listdatas.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListItem listItem;

		
		if (null == convertView) {
			convertView = LayoutInflater.from(this.context).inflate(
					R.layout.temlpate_item, null);
			listItem = new ListItem();
			listItem.nameHall = (TextView) convertView
					.findViewById(R.id.select_template_button);
			convertView.setTag(listItem);
		} else {
			listItem = (ListItem) convertView.getTag();
		}

		listItem.nameHall.setText((String) this.Listdatas.get(position).get("templename"));
		
//		listItem.nameHall.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Log.i("aaaa", "-----弹窗点击222222222-");
//			}
//		});
		return convertView;
	}

}

