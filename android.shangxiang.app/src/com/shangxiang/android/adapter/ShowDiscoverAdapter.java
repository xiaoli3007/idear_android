package com.shangxiang.android.adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.shangxiang.android.R;
import com.shangxiang.android.ShangXiang;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ShowDiscoverAdapter extends BaseAdapter {
	private Context context;
	public JSONArray data = new JSONArray();

	public static class ListItem {
		public ImageButton avatar;
		public ProgressBar avatarLoading;
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
		final ListItem listItem;
		if (null == convertView) {
			convertView = LayoutInflater.from(this.context).inflate(R.layout.show_discover_listview_item, null);
			listItem = new ListItem();
			listItem.avatar = (ImageButton) convertView.findViewById(R.id.show_discover_listitem_avatar_button);
			listItem.avatarLoading = (ProgressBar) convertView.findViewById(R.id.show_discover_listitem_avatar_loading);
			listItem.name = (TextView) convertView.findViewById(R.id.show_discover_listitem_name_text);
			listItem.date = (TextView) convertView.findViewById(R.id.show_discover_listitem_date_text);
			convertView.setTag(listItem);
		} else {
			listItem = (ListItem) convertView.getTag();
		}
		JSONObject item = this.data.optJSONObject(position);
		ShangXiang.imageLoader.displayImage(item.optString("headface", ""), listItem.avatar, ShangXiang.avatarLoaderOptions, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				listItem.avatarLoading.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				listItem.avatarLoading.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				listItem.avatarLoading.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				listItem.avatarLoading.setVisibility(View.GONE);
			}
		});
		String name = "未知用户";
		if (!TextUtils.isEmpty(item.optString("nickname", ""))) {
			name = item.optString("nickname", "");
		} else if (!TextUtils.isEmpty(item.optString("truename", ""))) {
			name = item.optString("truename", "");
		}
		listItem.name.setText(name);
		listItem.date.setText(item.optString("cn_retime", "") + "加持祈福");
		return convertView;
	}
}