package com.wyj.Activity;

import com.wyj.framework.BaseGroup;
import com.wyj.Activity.R;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ViewFlipper;

public class WishGroupTab extends BaseGroup {

	// 用于管理本Group中的所有Activity
	public static ActivityGroup group;
	public static WishGroupTab group_manage;

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
		Log.i("bbbb", "containerFlipper----------222" + containerFlipper);
		Intent intent = new Intent(this, Wish.class);
		switchActivity("Wish", intent, -1, -1);

		// Log.i("bbbb","中间页面----------222");

	}

	public static WishGroupTab getInstance() {
		return group_manage;
	}

}
