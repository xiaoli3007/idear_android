package com.wyj.Activity;

import com.wyj.framework.BaseGroup;
import com.wyj.Activity.R;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ViewFlipper;

public class UserGroupTab extends BaseGroup {

	// 用于管理本Group中的所有Activity
	public static ActivityGroup group;
	public static UserGroupTab group_manage;

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

		Intent intent = new Intent(this, My.class);
		switchActivity("My", intent, -1, -1);

		// Log.i("bbbb","中间页面----------222");

	}

	public static UserGroupTab getInstance() {
		return group_manage;
	}

	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Log.i("aaaa", "后退总部usergroup----------" );
			//group_manage.getLocalActivityManager().getCurrentActivity().onKeyDown(keyCode, event);
			((TabMenu) getParent()).exitApp();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode > 0) {
			Log.i("aaaa", "------父级-回来了-MY----------" + resultCode);
			switch (requestCode) {
			case 1: // global variable to indicate camera result
				Log.i("aaaa", "------父级-回来了-activity----------"
						+ getLocalActivityManager().getCurrentActivity()
								.toString());
				My activity = (My) getLocalActivityManager()
						.getCurrentActivity();
				activity.onActivityResult(resultCode, resultCode, data);

				break;

			}
		} else { // 头像返回
			// Log.i("aaaa", "---------------" + requestCode);
			switch (requestCode) {

			case 10: // global variable to indicate camera result

				User activity2 = (User) getLocalActivityManager()
						.getCurrentActivity();

				activity2.onActivityResult(requestCode, resultCode, data);

				break;
			case 20: // global variable to indicate camera result

				User activity3 = (User) getLocalActivityManager()
						.getCurrentActivity();

				activity3.onActivityResult(requestCode, resultCode, data);

				break;
			case 30: // global variable to indicate camera result

				User activity4 = (User) getLocalActivityManager()
						.getCurrentActivity();

				activity4.onActivityResult(requestCode, resultCode, data);

				break;
			}

		}
	}

}
