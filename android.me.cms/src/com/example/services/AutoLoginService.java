package com.example.services;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.cms.Cms;


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
			Cms.memberInfo = new JSONObject(Cms.APP.getConfig());
			Cms.APP.setLogin(true, Cms.memberInfo.optString("memberid", ""), Cms.APP.getMobile(), Cms.APP.getPassword());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return super.onStartCommand(intent, flags, startId);
	}
}