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
import com.wyj.pipe.Cms;
import com.wyj.pipe.Utils;
import com.wyj.utils.Tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
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

public class ListTempleAdapter extends BaseAdapter implements OnClickListener {
	private Context context;
	public JSONArray data = new JSONArray();
	private int wishtype;
	private Activity Parent;

	public static class ListItem {
		public ImageView thumbHall;
		public ImageView thumbMaster;
		public TextView nameHall;
		public TextView nameMaster;
		public Button createOrder;
	}

	public ListTempleAdapter(Context context, JSONArray data,int wishtype, Activity activity) {
		this.context = context;
		this.data = data;
		this.wishtype=wishtype;
		this.Parent=activity;
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
					R.layout.list_temple_listview_item, null);
			listItem = new ListItem();
			listItem.thumbHall = (ImageView) convertView
					.findViewById(R.id.list_temlple_image_1);
			listItem.thumbMaster = (ImageView) convertView
					.findViewById(R.id.list_temlple_image_2);
			listItem.nameHall = (TextView) convertView
					.findViewById(R.id.list_temple_buddhist_name_text);
			listItem.nameMaster = (TextView) convertView
					.findViewById(R.id.list_temple_hall_name_text);
			listItem.createOrder = (Button) convertView
					.findViewById(R.id.list_temple_order_button);
			
			convertView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(context, ShowTemple.class);
					Bundle bu=new Bundle();
					bu.putInt("tid", item.optInt("templeid"));
					bu.putInt("aid", item.optInt("attacheid"));
					bu.putInt("wishtype", wishtype);
					bu.putString("info", item.toString());
					intent.putExtras(bu);
					WishGroupTab.getInstance().switchActivity("ShowTemple", intent, -1,
							-1);
				}
			});
			
			convertView.setTag(listItem);
		} else {
			listItem = (ListItem) convertView.getTag();
		}
		
		listItem.nameMaster.setText(item.optString("templename", "") + "("
				+ item.optString("province", "") + ")");
		listItem.nameHall.setText(item.optString("buddhistname", ""));

		BitmapManager.getInstance().loadBitmap(
				item.optString("tmb_headface", ""), listItem.thumbMaster,
				Tools.readBitmap(this.context, R.drawable.temp2));
		BitmapManager.getInstance().loadBitmap(
				item.optString("pic_tmb_path", ""), listItem.thumbHall,
				Tools.readBitmap(this.context, R.drawable.temp1));
		
		listItem.createOrder.setTag(item);
		listItem.createOrder.setOnClickListener(this);
		//listItem.thumbHall.setOnClickListener(this);
		//listItem.thumbMaster.setOnClickListener(this);
		return convertView; 
	}

	@Override
	public void onClick(View v) {
		
		
		switch (v.getId()) {
		case R.id.list_temple_order_button:
			Log.i("aaaa", "------点击view" +v.getTag() );
			
			if(TextUtils.isEmpty(Cms.APP.getMemberId())){
				
				Utils.Dialog(Parent, "提示", "请登录后操作！");
			
			}else{
				JSONObject item =(JSONObject) v.getTag();
				Intent intent2 = new Intent(context, OrderForm.class);
				
				Bundle bu=new Bundle();
				bu.putInt("tid", item.optInt("templeid"));
				bu.putInt("aid", item.optInt("attacheid"));
				bu.putInt("wishtype", wishtype);
				bu.putString("info", item.toString());
				intent2.putExtras(bu);
				WishGroupTab.getInstance().switchActivity("OrderForm", intent2, -1,
						-1);
			}
			
			break;
			
		}
	}
}