package com.wyj.popupwindow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wyj.Activity.R;

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


public class MyPopupWindowsRemind extends PopupWindow {

	private PopupWindow popupwindow;
	private Button addbirthday_remind_confirm;

	public MyPopupWindowsRemind(Context mContext, final View parent,
			final Activity activity, final JSONArray jsonArray,
			final OnSelectRemindListener SelectListeners) {

		final Activity pactivity = activity;

		final String[] data = new String[jsonArray.length()];

		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				JSONObject jsonboject2 = jsonArray.getJSONObject(i);
				data[i] = jsonboject2.optString("name");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		View customView = View.inflate(mContext,
				R.layout.birthday_popwindow_remind, null);
		addbirthday_remind_confirm = (Button) customView
				.findViewById(R.id.addbirthday_remind_confirm);

		final AbstractWheel JSCSelect = (AbstractWheel) customView
				.findViewById(R.id.addbirthday_remind_view);
		RemindAdapter JSCAdapter = new RemindAdapter(mContext);
		JSCAdapter.JSCs = data;
		JSCSelect.setViewAdapter(JSCAdapter);
		JSCSelect.setCurrentItem(1);
		JSCSelect.setCyclic(true);

		popupwindow = new PopupWindow(customView);
		// 以下为弹窗后面的背景色设置
		ColorDrawable cd = new ColorDrawable(0x000000);
		popupwindow.setBackgroundDrawable(cd);
		// 产生背景变暗效果
		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
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

		popupwindow.showAsDropDown(parent, 0, -400); // 显示在button的下面

		// 自定义view添加触摸事件
		popupwindow.update();
		popupwindow.setOnDismissListener(new OnDismissListener() { // 恢复背景色

					public void onDismiss() {
						// TODO Auto-generated method stub
						WindowManager.LayoutParams lp = pactivity.getWindow()
								.getAttributes();
						lp.alpha = 1f;
						pactivity.getWindow().setAttributes(lp);
					}
				});

		addbirthday_remind_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				;

				// String aaa=data[JSCSelect.getCurrentItem()];
				int select = JSCSelect.getCurrentItem();
				Log.i("aaaa", "-----轮转值" + select);
				try {
					JSONObject jsonboject2 = jsonArray.getJSONObject(select);
					TextView list_find_zany = (TextView) parent;
					list_find_zany.setText(jsonboject2.optString("name"));

					SelectListeners.OnSelect(jsonboject2.optString("value"), 1);

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
			TextView JSCView = (TextView) view
					.findViewById(R.id.select_custom_text);
			JSCView.setText(this.JSCs[index]);
			return view;
		}
	}

	public interface OnSelectRemindListener {
		void OnSelect(String result, int type);
	}

}
