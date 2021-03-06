package com.example.viewpagerdemo;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public class MainActivity extends Activity implements OnPageChangeListener {
	private ViewPager viewPager;
	private ArrayList<View> list;
	private ImageView imageView;
	private ImageView[] imageViews;
	private ViewGroup  group;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		LayoutInflater inflater = getLayoutInflater();
		list = new ArrayList<View>();
//		list.add(inflater.inflate(R.layout.item01, null));
//		list.add(inflater.inflate(R.layout.item02, null));
//		list.add(inflater.inflate(R.layout.item03, null));
//		list.add(inflater.inflate(R.layout.item04, null));
//		list.add(inflater.inflate(R.layout.item05, null));


		imageViews = new ImageView[list.size()];

		ViewGroup group = (ViewGroup)findViewById(R.id.viewGroup);
		viewPager = (ViewPager)findViewById(R.id.viewPager);

		for(int i=0; i<list.size(); i++){
			imageView = new ImageView(this);
			imageView.setLayoutParams(new LayoutParams(12,12));
			imageViews[i] = imageView;
			if(i == 0){
				imageView.setBackgroundResource(R.drawable.page_indicator_focused);
			}else{
				imageView.setBackgroundResource(R.drawable.page_indicator_unfocused);
			}

			group.addView(imageView);
		}


		viewPager.setAdapter(new MyAdapter());
		viewPager.setOnPageChangeListener(this);
		viewPager.setCurrentItem(0);

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		Log.e("log", "你当前选择的是  " + arg0);
		setImageBackground(arg0%list.size());
	}


	private void setImageBackground( int selectItems){
		for(int i=0; i<imageViews.length; i++){
			if(i == selectItems){
				imageViews[i].setBackgroundResource(R.drawable.page_indicator_focused);
			}else{
				imageViews[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
			}
		}
	}


	public class MyAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			((ViewPager) container).addView(list.get(position%list.size()),0);
			return list.get(position%list.size());
		}


		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}


		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager)container).removeView(list.get(position%list.size()));
		}


	}


}
