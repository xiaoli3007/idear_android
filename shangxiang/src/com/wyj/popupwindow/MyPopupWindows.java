package com.wyj.popupwindow;


import java.util.List;
import java.util.Map;

import com.wyj.Activity.FindGroupTab;
import com.wyj.Activity.R;

import com.wyj.Activity.WishGroupTab;
import com.wyj.select.AbstractWheel;
import com.wyj.select.AbstractWheelTextAdapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.PopupWindow.OnDismissListener;


public class MyPopupWindows extends PopupWindow {
	
	private PopupWindow popupwindow;
	private Button order_hide_select_content_button;
	public MyPopupWindows(Context mContext, final View parent, final Activity activity, final String[] data) {
		
		final Activity pactivity=activity;
		
		View customView = View.inflate(mContext, R.layout.order_form_popwindow_wishcontent,
				null);
		order_hide_select_content_button =(Button) customView.findViewById(R.id.order_hide_select_content_button);
		
		final AbstractWheel contentSelect = (AbstractWheel) customView
		.findViewById(R.id.create_order_select_content_view);
		ContentAdapter contentAdapter = new ContentAdapter(mContext);
		contentAdapter.contents = data;
		contentSelect.setViewAdapter(contentAdapter);
		contentSelect.setCurrentItem(2);
		contentSelect.setCyclic(true);
		
		// 创建PopupWindow实例,200,150分别是宽度和高度-- popupwindow = new
		// PopupWindow(customView,200,150);
		popupwindow = new PopupWindow(customView);
		// 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
	//	 popupwindow.setAnimationStyle(R.style.AnimationFade);	 //从上往下弹出效果
//		customView.startAnimation(AnimationUtils.loadAnimation(
//				MainActivity.this, R.anim.fade_in));
		
		 //以下为弹窗后面的背景色设置
	 	ColorDrawable cd = new ColorDrawable(0x000000);
	 	popupwindow.setBackgroundDrawable(cd); 
	   	//产生背景变暗效果
	    WindowManager.LayoutParams lp=activity.getWindow().getAttributes(); 
		lp.alpha = 0.7f;
		activity.getWindow().setAttributes(lp);
		
		popupwindow.setWidth(LayoutParams.FILL_PARENT);
		popupwindow.setHeight(LayoutParams.FILL_PARENT);
		popupwindow.setBackgroundDrawable(new BitmapDrawable());
		popupwindow.setFocusable(true);
		popupwindow.setOutsideTouchable(true);
		popupwindow.setContentView(customView);

		int[] location = new int[2];
		parent.getLocationOnScreen(location);
		// popupwindow.showAtLocation(button, Gravity.NO_GRAVITY, location[0],
		// location[1]-popupwindow.getHeight());//显示在button的上面
		 popupwindow.showAsDropDown(parent); //显示在button的下面
//		popupwindow.showAtLocation(button, Gravity.NO_GRAVITY, location[0]
//				- popupwindow.getWidth(), location[1]); // 左边
//		popupwindow.showAtLocation(button, Gravity.NO_GRAVITY, location[0]
//				+ button.getWidth(), location[1]); // 右边
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
		
		order_hide_select_content_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				;
				
				String aaa=data[contentSelect.getCurrentItem()];
				Log.i("aaaa", "-----轮转值"+contentSelect.getCurrentItem());
				EditText list_find_zany=(EditText) parent;
				list_find_zany.setText(aaa);
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
	
	private class ContentAdapter extends AbstractWheelTextAdapter {
		String[] contents = new String[] {};

		protected ContentAdapter(Context context) {
			super(context, R.layout.select_custom_text, NO_RESOURCE);
		}

		@Override
		public int getItemsCount() {
			return this.contents.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return "";
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			TextView JSCView = (TextView) view
					.findViewById(R.id.select_custom_text);
			JSCView.setText(this.contents[index]);
			return view;
		}
	}
}


