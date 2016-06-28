package com.wyj.adapter;

import java.util.List;
import java.util.Map;

import com.wyj.Activity.OrderForm;
import com.wyj.Activity.OrderFormDetail;
import com.wyj.Activity.OrderFormPay;
import com.wyj.Activity.R;
import com.wyj.Activity.TabMenu;
import com.wyj.Activity.WishGroupTab;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class OrderImageAdapter extends BaseAdapter implements OnClickListener {
	private Context context;

	int mGralleyItemBackground;// 使用简单的计数器，填充背景图片
	private int[] resIds = { R.drawable.temp1, R.drawable.temp2,
			R.drawable.temp3 };

	public OrderImageAdapter(Context context) {
		this.context = context;


	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Integer.MAX_VALUE;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return resIds[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// 自定义的适配器，需要用自定义的布局来显示，通常android的通用布局是不能满足我们的需求
		// 可以手工创建一个View视图，也可以inflate填充一个XML
		// 从数据源中根据position 获得每一个Item的值，填充到指定的XML布局中
		// View convertView 是一个旧的布局，如果没有新的布局填充的时候，将使用旧的布局
		// 当前的布局，会被追加到父布局中
		ImageView imageView = new ImageView(context);
		imageView.setImageResource(resIds[position % resIds.length]);
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		imageView.setLayoutParams(new Gallery.LayoutParams(136, 88));
		return imageView;
	}

	@Override
	public void onClick(View v) {

		//
		// switch (v.getId()) {
		// case R.id.list_temple_order_button:
		// Intent intent2 = new Intent(context, OrderForm.class);
		// WishGroupTab.getInstance().switchActivity("OrderForm", intent2, -1,
		// -1);
		// break;
		//
		// }
	}
}