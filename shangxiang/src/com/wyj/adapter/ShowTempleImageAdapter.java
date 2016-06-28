package com.wyj.adapter;



import com.wyj.Activity.R;
import com.wyj.Activity.ShowPhotoActivity;

import com.wyj.dataprocessing.BitmapManager;
import com.wyj.http.WebApiUrl;
import com.wyj.utils.Tools;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;

import android.widget.ImageView;


public class ShowTempleImageAdapter extends BaseAdapter implements OnClickListener {
	private Context context;
	public String[] data = { };
	public String[] bigdata = { };

	public static class ViewHolder {
		public ImageView imageView;
	}

	public ShowTempleImageAdapter(Context context, String[] data, String[] bigdata) {
		this.context = context;
		this.data = data;
		this.bigdata = bigdata;
	}
	
	@Override
	public int getCount() {
		return this.data.length;
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
		ViewHolder viewHolder;
		final int p=position;
		
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.show_photo_item, null);
			viewHolder = new ViewHolder();
			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.show_simiao_image);
			convertView.setTag(viewHolder);
			
			convertView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(context, ShowPhotoActivity.class);
					Bundle bu=new Bundle();
					bu.putStringArray("bigimages", bigdata);
					bu.putInt("position", p);
					intent.putExtras(bu);
					context.startActivity(intent);
					
				}
			});
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		BitmapManager.getInstance().loadBitmap(
				this.data[position], viewHolder.imageView,
				Tools.readBitmap(this.context, R.drawable.temp1));
		//viewHolder.imageView.setPadding(4, 4, 4, 4);
		//viewHolder.imageView.setImageResource(imgIds[position]);
		
		return convertView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	

}