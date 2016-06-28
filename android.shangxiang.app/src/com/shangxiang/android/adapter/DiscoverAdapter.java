package com.shangxiang.android.adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.shangxiang.android.R;
import com.shangxiang.android.ShangXiang;
import com.shangxiang.android.utils.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DiscoverAdapter extends BaseAdapter implements OnClickListener {
	private Context context;
	public JSONArray data = new JSONArray();

	public static class ListItem {
		public ImageButton avatar;
		public ProgressBar avatarLoading;
		public TextView name;
		public TextView desc;
		public TextView content;
		public TextView blessed;
		public RelativeLayout layoutBlessit;
		public Button blessit;
		public Button showDetail;
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

	@SuppressLint({ "InflateParams", "ResourceAsColor" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ListItem listItem;
		if (null == convertView) {
			convertView = LayoutInflater.from(this.context).inflate(R.layout.discover_listview_item, null);
			listItem = new ListItem();
			listItem.avatar = (ImageButton) convertView.findViewById(R.id.discover_listitem_avatar_button);
			listItem.avatarLoading = (ProgressBar) convertView.findViewById(R.id.discover_listitem_avatar_loading);
			listItem.name = (TextView) convertView.findViewById(R.id.discover_listitem_name_text);
			listItem.desc = (TextView) convertView.findViewById(R.id.discover_listitem_desc_text);
			listItem.content = (TextView) convertView.findViewById(R.id.discover_listitem_content_text);
			listItem.blessed = (TextView) convertView.findViewById(R.id.discover_listitem_blessed_text);
			listItem.layoutBlessit = (RelativeLayout) convertView.findViewById(R.id.discover_listitem_blessit_layout);
			listItem.blessit = (Button) convertView.findViewById(R.id.discover_listitem_blessit_button);
			listItem.showDetail = (Button) convertView.findViewById(R.id.discover_listitem_show_detail_button);
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
		listItem.name.setText(item.optString("wishname", ""));
		listItem.desc.setText(item.optString("wishdate", "") + "在" + item.optString("templename", "") + item.optString("alsowish", "") + item.optString("wishtype", ""));
		listItem.content.setText(item.optString("wishtext", ""));
		listItem.blessed.setText(Utils.formatBlessed(item.optString("name_blessings", ""), item.optString("co_blessings", "")));
		listItem.layoutBlessit.setTag(position);
		if (ShangXiang.APP.getLogin() && ("," + item.optString("bleuser", "") + ",").indexOf("," + ShangXiang.APP.getMemberId() + ",") != -1) {
			listItem.blessit.setTextColor(this.context.getResources().getColor(R.color.text_title));
			Drawable imageBlessitPress = this.context.getResources().getDrawable(R.drawable.button_blessit_pressed);
			imageBlessitPress.setBounds(0, 0, imageBlessitPress.getMinimumWidth(), imageBlessitPress.getMinimumHeight());
			listItem.blessit.setCompoundDrawables(imageBlessitPress, null, null, null);
			listItem.layoutBlessit.setOnClickListener(null);
		} else {
			listItem.blessit.setTextColor(this.context.getResources().getColor(R.color.text_gray));
			Drawable imageBlessitPress = this.context.getResources().getDrawable(R.drawable.button_blessit_normal);
			imageBlessitPress.setBounds(0, 0, imageBlessitPress.getMinimumWidth(), imageBlessitPress.getMinimumHeight());
			listItem.blessit.setCompoundDrawables(imageBlessitPress, null, null, null);
			listItem.layoutBlessit.setOnClickListener(this);
		}
		listItem.showDetail.setTag(position);
		listItem.showDetail.setOnClickListener(this);
		return convertView;
	}

	@Override
	public void onClick(View v) {
		int Id = v.getId();
		int action = 0;
		if (R.id.discover_listitem_blessit_layout == Id) {
			action = 1;
		}
		if (R.id.discover_listitem_show_detail_button == Id) {
			action = 2;
		}
		Message msg = ShangXiang.discoverHandler.obtainMessage(action, v);
		ShangXiang.discoverHandler.sendMessage(msg);
	}
}