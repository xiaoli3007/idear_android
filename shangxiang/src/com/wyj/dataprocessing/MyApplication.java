package com.wyj.dataprocessing;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wyj.weixin.WeiXinConstants;

import android.app.Activity;
import android.app.Application;

public class MyApplication  {
	private static MyApplication instance;
	private List<Activity> activityList = new LinkedList<Activity>();

	private static String username = "";
	private static int memberid;
	private static String headface = "";
	private static String tmb_headface = "";
	private static String truename = "";
	private static int sex;
	private static String area = "";
	public static IWXAPI WXapi;

	public static MyApplication getInstances() {
		if (null == instance) {
			instance = new MyApplication();
		}
		return instance;

	}

	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	public void exit() {
		for (Activity activity : activityList) {
			activity.finish();
		}
		System.exit(0);
	}

	public static MyApplication getApp() {
		return instance;
	}

	

	public String getUserName() {
		return username;
	}

	public void setName(String username) {
		MyApplication.username = username;
	}

	public int getMemberid() {
		return memberid;
	}

	public void setMemberid(int memberid) {
		MyApplication.memberid = memberid;
	}

	public String getHeadface() {
		return headface;
	}

	public void setHeadface(String headface) {
		MyApplication.headface = headface;
	}

	public String getTmb_headface() {
		return tmb_headface;
	}

	public void setTmb_headface(String tmb_headface) {
		MyApplication.tmb_headface = tmb_headface;
	}

	public String getTruename() {
		return truename;
	}

	public void setTruename(String truename) {
		MyApplication.truename = truename;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		MyApplication.sex = sex;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		MyApplication.area = area;
	}

	public String getSystemDataATime() {
		// 24小时制
		SimpleDateFormat dateFormat24 = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return dateFormat24.format(Calendar.getInstance().getTime());
	}
}
