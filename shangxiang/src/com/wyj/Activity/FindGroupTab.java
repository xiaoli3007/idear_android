package com.wyj.Activity;

import com.wyj.framework.BaseGroup;
import com.wyj.Activity.R;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ViewFlipper;

public class FindGroupTab extends BaseGroup {

	// 用于管理本Group中的所有Activity
	public static ActivityGroup group;
	public static FindGroupTab group_manage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.group_main);
		group = this;
		group_manage = this;
		initViews();
	}

	private void initViews() {
		containerFlipper = (ViewFlipper) findViewById(R.id.group_content);

		Intent intent = new Intent(this, Find.class);
		switchActivity("Find", intent, -1, -1);

		// Log.i("bbbb","中间页面----------222");

	}

	// @Override
	// public void onBackPressed() {
	// //把后退事件交给子Activity处理
	// group.getLocalActivityManager().getCurrentActivity().onBackPressed();
	// }

	// @Override
	// protected void onStart() {
	// super.onStart();
	// //要跳转的Activity
	// Intent intent = new Intent(this, Find.class);
	// // 把Activity转换成一个Window，然后转换成View
	// Window w = group.getLocalActivityManager().startActivity(
	// "Find", intent);
	// View view = w.getDecorView();
	// //设置要跳转的Activity显示为本ActivityGroup的内容
	// group.setContentView(view);
	// }

	public static FindGroupTab getInstance() {
		return group_manage;
	}

}
