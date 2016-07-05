package com.example.popupwindow;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.cms.R;
import com.example.spinnerwheel.AbstractWheel;

import com.example.spinnerwheel.AbstractWheelTextAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MyPopupWindowsHomeSelectSingle extends PopupWindow {

	private PopupWindow popupwindow;
	private Button orderform_incense_confirm;
	public MyPopupWindowsHomeSelectSingle(Context mContext, final View parent,  final Activity activity, final JSONArray jsonArray) {

		final Activity pactivity=activity;

		final String[] data = new String[jsonArray.length()];

		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				JSONObject	jsonboject2 = jsonArray.getJSONObject(i);
				data[i] = jsonboject2.optString("name");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		View customView = View.inflate(mContext, R.layout.home_select_single,
				null);
		orderform_incense_confirm =(Button) customView.findViewById(R.id.orderform_incense_confirm);

		final AbstractWheel JSCSelect = (AbstractWheel) customView.findViewById(R.id.orderform_incense_view);
		RemindAdapter JSCAdapter = new RemindAdapter(mContext);
		JSCAdapter.JSCs = data;
		JSCSelect.setViewAdapter(JSCAdapter);
		JSCSelect.setCurrentItem(0);
		JSCSelect.setCyclic(false);
	//	JSCAdapter.getItem(JSCSelect.getCurrentItem(),customView,null);

		popupwindow = new PopupWindow(customView);
		 //以下为弹窗后面的背景色设置
	 	ColorDrawable cd = new ColorDrawable(0x000000);
	 	popupwindow.setBackgroundDrawable(cd);
	   	//产生背景变暗效果
	    WindowManager.LayoutParams lp=activity.getWindow().getAttributes();
		lp.alpha = 0.7f;
		activity.getWindow().setAttributes(lp);

		popupwindow.setWidth(LayoutParams.MATCH_PARENT);
		popupwindow.setHeight(LayoutParams.MATCH_PARENT);
		popupwindow.setBackgroundDrawable(new BitmapDrawable());
		popupwindow.setFocusable(true);
		popupwindow.setOutsideTouchable(true);
		popupwindow.setContentView(customView);

		int[] location = new int[2];
		parent.getLocationOnScreen(location);

		 popupwindow.showAsDropDown(parent); //显示在button的下面



		// 自定义view添加触摸事件
		popupwindow.update();
		popupwindow.setOnDismissListener(new OnDismissListener() {		//恢复背景色

			public void onDismiss() {
				// TODO Auto-generated method stub
				WindowManager.LayoutParams lp=pactivity.getWindow().getAttributes();
    			lp.alpha = 1f;
    			pactivity.getWindow().setAttributes(lp);
			}
		});

		orderform_incense_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				;

				//String aaa=data[JSCSelect.getCurrentItem()];
				int select=JSCSelect.getCurrentItem();
				Log.i("aaaa", "-----轮转值"+select);
				try {
					JSONObject	jsonboject2 = jsonArray.getJSONObject(select);

					TextView list_find_zany=(TextView) parent;
					//TextView list_find_price=(TextView) price;
					list_find_zany.setText(jsonboject2.optString("name"));
				//	list_find_price.setText("￥"+jsonboject2.optString("price"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


				popupwindow.dismiss();
			}
		});

		customView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (popupwindow != null && popupwindow.isShowing()) {
					popupwindow.dismiss();
					popupwindow = null;
				}

				return false;
			}
		});
	}

	private class RemindAdapter extends AbstractWheelTextAdapter {
		String[] JSCs = new String[] {};

		protected RemindAdapter(Context context) {
			super(context, R.layout.select_custom_text, NO_RESOURCE);
		}

		@Override
		public int getItemsCount() {
			return this.JSCs.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return "";
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			//view.setBackgroundColor(context.getResources().getColor(R.color.button_gray ));
			TextView JSCView = (TextView) view.findViewById(R.id.select_custom_text);
			JSCView.setText(this.JSCs[index]);
			return view;
		}
	}
}


