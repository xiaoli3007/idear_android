package com.shangxiang.android.services;

import org.json.JSONException;
import org.json.JSONObject;

import com.shangxiang.android.ShangXiang;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AutoLoginService extends Service {
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			if (null != ShangXiang.APP.getConfig()) {
				ShangXiang.memberInfo = new JSONObject(ShangXiang.APP.getConfig());
				ShangXiang.APP.setLogin(true, ShangXiang.memberInfo.optString("memberid", ""), ShangXiang.APP.getMobile(), ShangXiang.APP.getPassword());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		stopService(intent);
		return super.onStartCommand(intent, flags, startId);
	}
}