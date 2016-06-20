package com.example.activity;


import com.example.cms.BaseFragment;
import com.example.cms.R;


import android.annotation.SuppressLint;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;

public class LunarCalendar extends BaseFragment implements OnTouchListener {
	
	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		View view = inflater.inflate(R.layout.calendar, null);


		return view;
	}

	public void onActivityCreated(Bundle sinha) {
		super.onActivityCreated(sinha);
		initCalendarView();
	}



	private void initCalendarView() {
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	

	

}