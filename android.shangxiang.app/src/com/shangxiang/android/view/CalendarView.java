package com.shangxiang.android.view;

import com.shangxiang.android.R;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.LinearLayout;

public class CalendarView extends GridView {
	private Context context;

	public CalendarView(Context context) {
		super(context);
		this.context = context;
		setGirdView();
	}

	private void setGirdView() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		setLayoutParams(params);
		setNumColumns(7);
		setGravity(Gravity.CENTER_VERTICAL);
		setVerticalSpacing(1);
		setHorizontalSpacing(1);
		setBackgroundColor(getResources().getColor(R.color.background_normal));

		WindowManager windowManager = ((Activity) this.context).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int i = display.getWidth() / 7;
		int j = display.getWidth() - (i * 7);
		int x = j / 2;
		setPadding(x, 0, 0, 0);
	}
}
