package com.wyj.Activity;

import com.wyj.framework.BaseGroup;
import com.wyj.Activity.R;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ViewFlipper;

public class MemberGroupTab extends BaseGroup {

	// 用于管理本Group中的所有Activity
	public static ActivityGroup group;
	public static MemberGroupTab group_manage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		group = this;
		group_manage = this;
		initViews();
	}

	private void initViews() {
		containerFlipper = (ViewFlipper) findViewById(R.id.group_content);
		Log.i("bbbb", "containerFlipper----------222" + containerFlipper);
		Intent intent = new Intent(this, User.class);
		switchActivity("My", intent, -1, -1);

	}

	public static MemberGroupTab getInstance() {
		return group_manage;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode > 0) {
			switch (requestCode) {
			case 1: // global variable to indicate camera result
				My activity = (My) getLocalActivityManager()
						.getCurrentActivity();
				activity.onActivityResult(resultCode, resultCode, data);
				Log.i("aaaa", "------父级-回来了-----------" + requestCode);
				// activity.onActivityResult(requestCode, resultCode, data);
				break;
			}
		}
	}

	// @Override
	// public void onBackPressed() {
	// //把后退事件交给子Activity处理
	// group.getLocalActivityManager().getCurrentActivity().onBackPressed();
	// }
	//
	// @Override
	// protected void onStart() {
	// super.onStart();
	// //要跳转的Activity
	// Intent intent = new Intent(this, My.class);
	// // 把Activity转换成一个Window，然后转换成View
	// Window w = group.getLocalActivityManager().startActivity(
	// "Find", intent);
	// View view = w.getDecorView();
	// //设置要跳转的Activity显示为本ActivityGroup的内容
	// group.setContentView(view);
	// }

}
