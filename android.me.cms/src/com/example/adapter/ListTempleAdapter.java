package com.example.adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.cms.R;
import com.example.cms.Cms;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ListTempleAdapter extends BaseAdapter implements OnClickListener {
	private Context context;
	public JSONArray data = new JSONArray();

	public static class ListItem {
		public ImageButton thumbHall;
		public ProgressBar thumbHallLoading;
		public ImageButton thumbBuddhist;
		public ProgressBar thumbBuddhistLoading;
		public TextView nameHall;
		public TextView nameBuddhist;
		public Button createOrder;
	}

	public ListTempleAdapter(Context context) {
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
		final ListItem listItem;
		if (null == convertView) {
			convertView = LayoutInflater.from(this.context).inflate(R.layout.list_temple_listview_item, null);
			listItem = new ListItem();
			listItem.thumbHall = (ImageButton) convertView.findViewById(R.id.list_temple_listitem_hall_thumb_button);
			listItem.thumbHallLoading = (ProgressBar) convertView.findViewById(R.id.list_temple_listitem_hall_thumb_loading);
			listItem.thumbBuddhist = (ImageButton) convertView.findViewById(R.id.list_temple_listitem_buddhist_thumb_button);
			listItem.thumbBuddhistLoading = (ProgressBar) convertView.findViewById(R.id.list_temple_listitem_buddhist_thumb_loading);
			listItem.nameHall = (TextView) convertView.findViewById(R.id.list_temple_listitem_hall_name_text);
			listItem.nameBuddhist = (TextView) convertView.findViewById(R.id.list_temple_listitem_buddhist_name_text);
			listItem.createOrder = (Button) convertView.findViewById(R.id.list_temple_listitem_create_order_button);
			convertView.setTag(listItem);
		} else {
			listItem = (ListItem) convertView.getTag();
		}
		JSONObject item = this.data.optJSONObject(position);
		Cms.imageLoader.displayImage(item.optString("pic_tmb_path", ""), listItem.thumbHall, Cms.imageLoaderOptions, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				listItem.thumbHallLoading.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				listItem.thumbHallLoading.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				listItem.thumbHallLoading.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				listItem.thumbHallLoading.setVisibility(View.GONE);
			}
		});
		Cms.imageLoader.displayImage(item.optString("tmb_headface", ""), listItem.thumbBuddhist, Cms.imageLoaderOptions, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				listItem.thumbBuddhistLoading.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				listItem.thumbBuddhistLoading.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				listItem.thumbBuddhistLoading.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				listItem.thumbBuddhistLoading.setVisibility(View.GONE);
			}
		});
		listItem.nameHall.setText(item.optString("templename", ""));
		listItem.nameBuddhist.setText(item.optString("buddhistname", ""));
		listItem.createOrder.setTag(item.toString());
		listItem.createOrder.setOnClickListener(this);
		return convertView;
	}

	@Override
	public void onClick(View v) {
		Message msg = Cms.listTempleHandler.obtainMessage(0, v.getTag());
		Cms.listTempleHandler.sendMessage(msg);
	}
}