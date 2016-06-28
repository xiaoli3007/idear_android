package com.shangxiang.android.services;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.shangxiang.android.ShangXiang;
import com.shangxiang.android.activity.Navigation;
import com.shangxiang.android.utils.Utils;

import cn.jpush.android.api.JPushInterface;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

public class JPushReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		String action = intent.getAction();
		Utils.Log("----" + action);
		if (action.equals(Intent.ACTION_BOOT_COMPLETED) || action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
			Intent newIntent = new Intent(context, AutoLoginService.class);
			newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(newIntent);
		}
		if (action.equals(JPushInterface.ACTION_NOTIFICATION_OPENED)) {
			showNavigation(context);
			String showWhat = bundle.getString(JPushInterface.EXTRA_EXTRA);
			try {
				JSONObject result = new JSONObject(showWhat);
				if (result.optString("msgtype", "1").equals("1") || result.optString("msgtype", "1").equals("2")) {
					Message msg = ShangXiang.tabHandler.obtainMessage(2);
					ShangXiang.tabHandler.sendMessage(msg);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressLint({ "InlinedApi", "NewApi" })
	private void showNavigation(Context context) {
		boolean runNew = true;
		ActivityManager am = (ActivityManager) ShangXiang.APP.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		if (ShangXiang.PACKAGE_NAME.equals(cn.getPackageName())) {
		} else {
			List<RunningTaskInfo> list = am.getRunningTasks(5);
			for (RunningTaskInfo task : list) {
				ComponentName name = task.baseActivity;
				if (ShangXiang.PACKAGE_NAME.equals(name.getPackageName())) {
					am.moveTaskToFront(task.id, ActivityManager.MOVE_TASK_WITH_HOME);
					runNew = false;
					break;
				}
			}
			if (runNew) {
				Intent intent = new Intent(context, Navigation.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
				ShangXiang.APP.startActivity(intent);
			}
		}
	}
}
