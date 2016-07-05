package com.mycalendar;


import java.io.IOException;
import java.io.InputStream;

import data.DateInfo;
import adapter.CalendarAdapter;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

public class OnItemClickListenerImpl implements OnItemClickListener {

	private CalendarAdapter adapter = null;
	private MainActivity activity = null;

	public OnItemClickListenerImpl(CalendarAdapter adapter,
			MainActivity activity) {
		this.adapter = adapter;
		this.activity = activity;
	}

	public void onItemClick(AdapterView<?> gridView, View view, int position,
			long id) {
		if (activity.currList.get(position).isThisMonth() == false) {
			return;
		}
//		for(int i=0 ;i<activity.currList.size();i++){
//			
//			Log.i("eeee", "----点击的信息--"+activity.currList.get(i).getMonth());	
//			
//		}

		DateInfo selectdata = activity.currList.get(position);
		//DateInfo selectdata = (DateInfo) adapter.getList();
		Log.i("eeee",
				"-点击的日期--" + selectdata.isHoliday() + "-日-"
						+ selectdata.getDate() + "---农历---"
						+ selectdata.getNongliDate() + "----农历info--"
						+ selectdata.getNongliInfo()+ "----农历info--"+selectdata.getNonglinumber());

		activity.lastSelected = selectdata.getDate(); // 手指选的 灰色的
														// 那天---为了上下的日期------------

		activity.lastSelectedGray = selectdata.getDate();// 手指选的 灰色的
															// 那天---为了当前的背景------------

		activity.date_infos_yangli.setText(String.format("%04d年%02d月%02d日",
				selectdata.getYear(), selectdata.getMonth(),
				selectdata.getDate()));

		activity.date_infos_yinli.setText("农历" + selectdata.getNongliInfo());

		if (selectdata.isHoliday()) {
			activity.date_add_layout.setVisibility(View.VISIBLE);
			activity.date_infos_foli.setText(selectdata.getHoliday());
		} else {
			activity.date_add_layout.setVisibility(View.GONE);
		}
		activity.date_infos_left.setText(selectdata.getDate() + "");
		
		displayBackground(selectdata.getNonglinumber() + ".jpg",activity.foli_bottom_images,activity);
		
		
		adapter.setSelectedPosition(position);
		adapter.notifyDataSetInvalidated();
		
	}
	
	private void displayBackground(String path,ImageView foli_bottom_images,MainActivity activity) {
		//foli_bottom_images.setBackgroundResource(R.drawable.background_calendar);
		//这里才是重点  
		AssetManager  assetManager=activity.getAssets();  
        try {  
            InputStream in=assetManager.open(path);  
            Bitmap bmp=BitmapFactory.decodeStream(in);  
            foli_bottom_images.setImageBitmap(bmp);  
        } catch (Exception e) {  
            // TODO: handle exception  
        	
        	Log.i("eeee", "-----找不到图了");
        	try {
				InputStream in = assetManager.open("0000.jpg");
				Bitmap bmp=BitmapFactory.decodeStream(in);  
	             foli_bottom_images.setImageBitmap(bmp); 
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
	}

}
