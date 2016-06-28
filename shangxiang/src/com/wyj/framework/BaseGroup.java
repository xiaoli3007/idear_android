package com.wyj.framework;

import com.wyj.Activity.TabMenu;
import com.wyj.Activity.R;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;
import android.widget.LinearLayout.LayoutParams;

public class BaseGroup extends ActivityGroup {

	private static final String TAG = "BaseGroup";
	protected TabStack stack = new TabStack();
	protected ViewFlipper containerFlipper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	public void switchActivity(String id, Intent intent, int inAnimation,
			int outAnimation) {
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Window window = getLocalActivityManager().startActivity(id, intent);
		View v = window.getDecorView();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		LayoutParams param = new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT);
		v.setLayoutParams(param);

		// Log.i("aaaa","-cccc2---"+containerFlipper);
		if (inAnimation != -1) {
			try {

				containerFlipper.setInAnimation(AnimationUtils.loadAnimation(
						this, inAnimation));
				containerFlipper.setOutAnimation(AnimationUtils.loadAnimation(
						this, outAnimation));
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
		} else {

			containerFlipper.setInAnimation(null);
			containerFlipper.setOutAnimation(null);

		}
		// printViewFlipper();
		Log.i(TAG, "当前页面-添加前 size---------" + stack.size());
		Log.i(TAG, "当前页面-添加---------" + id);
		containerFlipper.addView(v);
		containerFlipper.showNext();
		
		
		if (inAnimation == R.anim.in_left_right) {
			containerFlipper.removeViewAt(stack.size());
		}
		stack.push(id);
		Log.i(TAG, "当前页面-添加后 size---------" + stack.size());
	}

	public void back() {
		Log.i(TAG, "当前页面-view--------" + stack.top());
		if (stack.size() > 1) {
			// stack.
			
			if (stack.top().equals("Find") || stack.top().equals("Wish")
					|| stack.top().equals("My") || stack.top().equals("Foli")) {
				
				((TabMenu) getParent()).exitApp();
			} else {
				
				containerFlipper.showPrevious();
				containerFlipper.removeViewAt(stack.size() - 1);
				// View view_old=containerFlipper.getChildAt(stack.size() - 1);
				// containerFlipper.removeView(view_old);
				stack.pop();
				Log.i(TAG, "删除页面后剩余size----------" + stack.size());
			}
		} else {

			((TabMenu) getParent()).exitApp();
		}
	}

	public void noAnimationback() {
		if (stack.size() > 1) {
			containerFlipper.showPrevious();
			containerFlipper.removeViewAt(stack.size() - 1);
			stack.pop();
		} else {
			((TabMenu) getParent()).exitApp();
		}
	}

	public Activity getActivityByTag(String tag) {
		return getLocalActivityManager().getActivity(tag);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i("aaaa", "后退总部----------" );
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!stack.isEmpty()) {
				back();
			} else {
				((TabMenu) getParent()).onKeyDown(keyCode, event);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void popSome(String id) {
		int sum = stack.getTheSumToPop(id);
		containerFlipper.removeViews(stack.size() - sum, sum - 1);
		stack.popSome(id);
		containerFlipper
				.setDisplayedChild(containerFlipper.getChildCount() - 1);
	}

}
