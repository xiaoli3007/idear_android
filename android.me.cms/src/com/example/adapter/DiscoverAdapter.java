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

public class DiscoverAdapter extends BaseAdapter implements OnClickListener {
	private Context context;
	public JSONArray data = new JSONArray();

	public static class ListItem {
		public ImageButton avatar;
		public TextView title;
		public ProgressBar avatarLoading;
		public TextView desc;
	}

	public DiscoverAdapter(Context context) {
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
			convertView = LayoutInflater.from(this.context).inflate(R.layout.discover_listview_item, null);
			listItem = new ListItem();
			listItem.avatarLoading = (ProgressBar) convertView.findViewById(R.id.discover_listitem_avatar_loading);
			listItem.avatar = (ImageButton) convertView.findViewById(R.id.discover_listitem_avatar_button);
			listItem.title = (TextView) convertView.findViewById(R.id.discover_listitem_name_text);
			listItem.desc = (TextView) convertView.findViewById(R.id.discover_listitem_content_text);
			convertView.setOnClickListener(this);
			convertView.setTag(listItem);
		} else {
			listItem = (ListItem) convertView.getTag();
		}
		JSONObject item = this.data.optJSONObject(position);
		Cms.imageLoader.displayImage(item.optString("thumb", ""), listItem.avatar, Cms.avatarLoaderOptions, new ImageLoadingListener() {
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
		listItem.title.setText(item.optString("title", "").trim());
		listItem.desc.setText(item.optString("description", "").trim());
		return convertView;
	}

	@Override
	public void onClick(View v) {
		int Id = v.getId();
		int action = 0;

//		if (R.id.discover_listitem_show_detail_button == Id) {
//			action = 2;
//		}
		Message msg = Cms.discoverHandler.obtainMessage(action,v);
		Cms.discoverHandler.sendMessage(msg);
	}
}