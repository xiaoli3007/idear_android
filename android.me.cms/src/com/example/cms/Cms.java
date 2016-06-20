package com.example.cms;

import java.io.File;

import org.json.JSONObject;


import com.example.utils.SinhaCrashUtil;
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

public class Cms extends Application {
	public static Cms APP;
	public static String VERSION = "";
	public static int AR_ORIGINAL = 0;
	public static int AR_CUR = 0;

	public static int tabContent = R.id.tab_content;
	public static boolean dialogUpdate = false;
	public static Class<?> classCurr = null;
	public static String cookies = null;
	public static JSONObject memberInfo = new JSONObject();
	public static ImageLoader imageLoader;
	public static DisplayImageOptions imageLoaderOptions;
	
	public static DisplayImageOptions avatarLoaderOptions;

	public static Handler tabHandler = new Handler();
	public static Handler listTempleHandler = new Handler();
	public static Handler discoverHandler = new Handler();
	public static Handler orderRecordHandler = new Handler();

	public boolean logined = false;
	private String cfgName = "shangxiang_android";
	private String cfgSaveMemberId = "save_member_id";
	private String cfgSaveMobile = "save_mobile";
	private String cfgSavePassword = "save_password";
	private String cfgConfig = "save_config";

	@SuppressLint("SdCardPath")
	@Override
	public void onCreate() {
		super.onCreate();
		APP = this;
		try {
			VERSION = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
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

		File cache = StorageUtils.getOwnCacheDirectory(APP, "/mnt/sdcard/ShangXiang/Cache");
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(APP)
		.threadPriority(Thread.NORM_PRIORITY - 2)
		.threadPoolSize(5)
		.denyCacheImageMultipleSizesInMemory()
		.diskCache(new UnlimitedDiscCache(cache))
		.diskCacheFileNameGenerator(new Md5FileNameGenerator())
		.diskCacheSize(50 * 1024 * 1024)
		.tasksProcessingOrder(QueueProcessingType.LIFO)
		.imageDownloader(new BaseImageDownloader(APP, 5 * 1000, 30 * 1000))
		.build();
		imageLoaderOptions = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.img_not_available)
		.showImageOnFail(R.drawable.img_not_available)
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.displayer(new RoundedBitmapDisplayer(20))
		.displayer(new FadeInBitmapDisplayer(100))
		.build();
		
		avatarLoaderOptions = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.showImageForEmptyUri(R.drawable.avatar_null)
		.showImageOnFail(R.drawable.avatar_null)
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.displayer(new RoundedBitmapDisplayer(20))
		.displayer(new FadeInBitmapDisplayer(100))
		.build();
		
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);

	
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

	public void Logout() {
		setLogin(false, null, null, null);
		setConfig(null);
		memberInfo = new JSONObject();
		cookies = null;
	}
}