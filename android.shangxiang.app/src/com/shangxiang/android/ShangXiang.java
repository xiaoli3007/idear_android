package com.shangxiang.android;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.shangxiang.android.R;
import com.shangxiang.android.utils.DeviceUuidUtil;
import com.shangxiang.android.utils.SinhaCrashUtil;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.app.Fragment;

public class ShangXiang extends Application {
	public static ShangXiang APP;
	public static String VERSION = "";
	public static String PACKAGE_NAME = "";
	public static int AR_ORIGINAL = 0;
	public static int AR_CUR = 0;

	public static String UUID;
	public static String allowPartnerLogin = "1";
	public static IWXAPI wechatAPI;
	public static ImageLoader imageLoader;
	public static DisplayImageOptions imageLoaderOptions;
	public static DisplayImageOptions avatarLoaderOptions;
	public static DisplayImageOptions calendarLoaderOptions;
	public static int tabContent = R.id.tab_content;
	public static boolean dialogUpdate = false;
	public static Class<?> classCurr = null;
	public static Fragment fragmentCurr = null;
	public static String cookies = null;
	public static JSONObject memberInfo = new JSONObject();
	public static JSONArray templeList = new JSONArray();
	public static HashMap<String, Integer> desireTypeId = new HashMap<String, Integer>();
	@SuppressLint("UseSparseArrays")
	public static HashMap<Integer, String> desireTypeName = new HashMap<Integer, String>();
	public static JSONArray jsonHolidays = new JSONArray();
	public static JSONArray jsonEvents = new JSONArray();
	public static JSONArray jsonAction = new JSONArray();
	public static int firstDayOfWeek = Calendar.SUNDAY;
	public static long lastClickTime;

	public static Handler tabHandler = new Handler();
	public static Handler listTempleHandler = new Handler();
	public static Handler discoverHandler = new Handler();
	public static Handler orderRecordHandler = new Handler();
	public static Handler wxHandler = new Handler();
	public static Handler wxPayHandler = new Handler();
	public static Handler aliPayHandler = new Handler();
	public static Handler noticeHandler = new Handler();
	public static Handler updateHandler = new Handler();
	public static Handler updateServiceHandler = new Handler();

	public boolean logined = false;
	private String cfgName = "shangxiang_android";
	private String cfgSaveMemberId = "save_member_id";
	private String cfgSaveMobile = "save_mobile";
	private String cfgSavePassword = "save_password";
	private String cfgConfig = "save_config";
	private String cfgFirstRun = "save_first_run";
	private String cfgShowPush = "save_show_push";
	private String cfgObtainLocation = "save_obtain_location";

	@SuppressLint("SdCardPath")
	@Override
	public void onCreate() {
		super.onCreate();
		APP = this;
		try {
			VERSION = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			PACKAGE_NAME = getPackageManager().getPackageInfo(getPackageName(), 0).packageName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		SinhaCrashUtil sc = SinhaCrashUtil.getInstance();
		sc.init(APP);

		File file = new File(Consts.FOLDER_LOCAL);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(Consts.UPDATE_LOCAL);
		if (!file.exists()) {
			file.mkdirs();
		}
		UUID = new DeviceUuidUtil(APP).getDeviceUuid().toString();

		File cache = StorageUtils.getOwnCacheDirectory(APP, "/mnt/sdcard/ShangXiang/Cache");
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(APP).threadPriority(Thread.NORM_PRIORITY - 2).threadPoolSize(5).denyCacheImageMultipleSizesInMemory().diskCache(new UnlimitedDiscCache(cache)).diskCacheFileNameGenerator(new Md5FileNameGenerator()).diskCacheSize(50 * 1024 * 1024).tasksProcessingOrder(QueueProcessingType.LIFO).imageDownloader(new BaseImageDownloader(APP, 5 * 1000, 30 * 1000)).build();
		imageLoaderOptions = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.showImageForEmptyUri(R.drawable.img_not_available)
		.showImageOnFail(R.drawable.img_not_available)
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.displayer(new RoundedBitmapDisplayer(20))
		.displayer(new FadeInBitmapDisplayer(100))
		.build();
		avatarLoaderOptions = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.showImageForEmptyUri(R.drawable.avatar_null)
		.showImageOnFail(R.drawable.avatar_null)
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.displayer(new RoundedBitmapDisplayer(20))
		.displayer(new FadeInBitmapDisplayer(100))
		.build();
		calendarLoaderOptions = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.showImageForEmptyUri(R.drawable.background_calendar)
		.showImageOnFail(R.drawable.background_calendar)
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.displayer(new RoundedBitmapDisplayer(20))
		.displayer(new FadeInBitmapDisplayer(100))
		.build();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);

		wechatAPI = WXAPIFactory.createWXAPI(this, Consts.WECHAT_APPID, false);
		if (wechatAPI.isWXAppInstalled() && wechatAPI.isWXAppSupportAPI()) {
			wechatAPI.registerApp(Consts.WECHAT_APPID);
		}
		
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
		UUID = JPushInterface.getRegistrationID(APP);

		desireTypeId.put("财富", 1);
		desireTypeId.put("健康", 2);
		desireTypeId.put("求子", 3);
		desireTypeId.put("平安", 4);
		desireTypeId.put("学业", 5);
		desireTypeId.put("姻缘", 6);
		desireTypeId.put("事业", 7);

		desireTypeName.put(1, "财富");
		desireTypeName.put(2, "健康");
		desireTypeName.put(3, "求子");
		desireTypeName.put(4, "平安");
		desireTypeName.put(5, "学业");
		desireTypeName.put(6, "姻缘");
		desireTypeName.put(7, "事业");
	}

	public boolean getLogin() {
		return this.logined;
	}

	public void setLogin(boolean isLogin, String memberId, String mobile, String password) {
		this.logined = isLogin;
		Editor editor = getSharedPreferences(cfgName, Context.MODE_PRIVATE).edit();
		editor.putString(cfgSaveMemberId, memberId);
		editor.putString(cfgSaveMobile, mobile);
		editor.putString(cfgSavePassword, password);
		editor.commit();
		JPushInterface.setAlias(APP, memberId, new TagAliasCallback() {
			@Override
			public void gotResult(int arg0, String arg1, Set<String> arg2) {
			}
		});
	}

	public String getMemberId() {
		SharedPreferences sp = getSharedPreferences(cfgName, Context.MODE_PRIVATE);
		return sp.getString(cfgSaveMemberId, null);
	}

	public String getMobile() {
		SharedPreferences sp = getSharedPreferences(cfgName, Context.MODE_PRIVATE);
		return sp.getString(cfgSaveMobile, null);
	}

	public String getPassword() {
		SharedPreferences sp = getSharedPreferences(cfgName, Context.MODE_PRIVATE);
		return sp.getString(cfgSavePassword, null);
	}

	public String getConfig() {
		SharedPreferences sp = getSharedPreferences(cfgName, Context.MODE_PRIVATE);
		return sp.getString(cfgConfig, null);
	}

	public void setConfig(String config) {
		Editor editor = getSharedPreferences(cfgName, Context.MODE_PRIVATE).edit();
		editor.putString(cfgConfig, config);
		editor.commit();
	}

	public boolean getFirstRun() {
		SharedPreferences sp = getSharedPreferences(cfgName, Context.MODE_PRIVATE);
		return sp.getBoolean(cfgFirstRun, true);
	}

	public void setFirstRun(boolean firstRun) {
		Editor editor = getSharedPreferences(cfgName, Context.MODE_PRIVATE).edit();
		editor.putBoolean(cfgFirstRun, firstRun);
		editor.commit();
	}

	public boolean getShowPush() {
		SharedPreferences sp = getSharedPreferences(cfgName, Context.MODE_PRIVATE);
		return sp.getBoolean(cfgShowPush, false);
	}

	public void setShowPush(boolean showPush) {
		Editor editor = getSharedPreferences(cfgName, Context.MODE_PRIVATE).edit();
		editor.putBoolean(cfgShowPush, showPush);
		editor.commit();
	}

	public boolean getObtainLocation() {
		SharedPreferences sp = getSharedPreferences(cfgName, Context.MODE_PRIVATE);
		return sp.getBoolean(cfgObtainLocation, false);
	}

	public void setObtainLocation(boolean obtainLocation) {
		Editor editor = getSharedPreferences(cfgName, Context.MODE_PRIVATE).edit();
		editor.putBoolean(cfgObtainLocation, obtainLocation);
		editor.commit();
	}

	public void Logout() {
		setLogin(false, null, null, null);
		setConfig(null);
		memberInfo = new JSONObject();
		cookies = null;
	}
}