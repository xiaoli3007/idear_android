package com.wyj.pipe;

import java.io.File;
import java.util.Set;

import org.json.JSONObject;



import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

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
import com.wyj.Activity.R;

import com.wyj.utils.FilePath;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;



public class Cms extends Application {
	public static Cms APP;
	private static Cms instance;
	public static String VERSION = "";
	public static int AR_ORIGINAL = 0;
	public static int AR_CUR = 0;


	public static boolean dialogUpdate = false;
	public static Class<?> classCurr = null;
	public static String cookies = null;
	public static JSONObject memberInfo = new JSONObject();
	public static ImageLoader imageLoader;
	public static DisplayImageOptions imageLoaderOptions;

	public static Handler tabHandler = new Handler();
	public static Handler listTempleHandler = new Handler();
	public static Handler discoverHandler = new Handler();
	public static Handler orderRecordHandler = new Handler();

	public boolean logined = false;
	private String cfgName = "cms_android";
	private String cfgSaveMemberId = "save_member_id";
	private String cfgSaveMobile = "save_mobile";
	private String cfgSavePassword = "save_password";
	
	private String cfgSavememberinfos = "save_memberinfos";
	private String cfgSaveheadface = "save_headface";
	private String cfgSavetruename = "save_truename";
	private String cfgSavesex = "save_sex";
	private String cfgSavearea = "save_area";
	
	private String cfgConfig = "cms_config";
	
	private String reg_notice_id="save_reg_notice_id";

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
		Log.i("aaaa",
 				"--image路劲-------" + 
 						FilePath.Cmsimage);
		File cache = StorageUtils.getOwnCacheDirectory(APP, "/mnt/sdcard/Cms/Cache");
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
		 .cacheInMemory(true)//设置下载的图片是否缓存在内存中  
		 .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中  
		.build();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);
		
		//推送
		 JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
         JPushInterface.init(this);     		// 初始化 JPush
         
         reg_notice_id= JPushInterface.getRegistrationID(getApplicationContext());
 		
 		Log.i("aaaa",
 				"--通知ID-------" + 
 						reg_notice_id);
 		
 		
 		set_notice_id(reg_notice_id);
 		
 		//SQLiteDataBaseHelper db=new SQLiteDataBaseHelper(this);
	}
	
	public static Cms getInstances() {
		if (null == instance) {
			instance = new Cms();
		}
		return instance;

	}
	
	public boolean getLogin() {
		return this.logined;
	}
	public void set_notice_id( String noticeId) {

		Editor editor = getSharedPreferences(cfgName, Context.MODE_PRIVATE).edit();
		editor.putString(reg_notice_id, noticeId);
		editor.commit();
	}
	
	public String get_notice_id() {
		SharedPreferences sp = getSharedPreferences(cfgName, Context.MODE_PRIVATE);
		return sp.getString(reg_notice_id, null);
	}

	public void setLogin(boolean isLogin, String memberId, String mobile, String password) {
		this.logined = isLogin;
		Editor editor = getSharedPreferences(cfgName, Context.MODE_PRIVATE).edit();
		editor.putString(cfgSaveMemberId, memberId);
		editor.putString(cfgSaveMobile, mobile);
		editor.putString(cfgSavePassword, password);
		editor.commit();
		if(memberId!=null){
		JPushInterface.setAlias(APP, memberId, new TagAliasCallback() {
			
			@Override
			public void gotResult(int arg0, String arg1, Set<String> arg2) {
				// TODO Auto-generated method stub
				//Log.i("aaaa", "------别名"+arg0+"-----"+arg1+"-------"+arg2.toString());
			}
		});
		}
	}

	
	public String getMemberId() {
		SharedPreferences sp = getSharedPreferences(cfgName, Context.MODE_PRIVATE);
		return sp.getString(cfgSaveMemberId, null);
	}

	public String getMobile() {
		SharedPreferences sp = getSharedPreferences(cfgName, Context.MODE_PRIVATE);
		return sp.getString(cfgSaveMobile,null);
	}

	public String getPassword() {
		SharedPreferences sp = getSharedPreferences(cfgName, Context.MODE_PRIVATE);
		return sp.getString(cfgSavePassword, null);
	}
	
	
//	public void setMember(memberinfo memberinfos) {
//		
//		Editor editor = getSharedPreferences(cfgName, Context.MODE_PRIVATE).edit();
//		editor.putString(cfgSaveheadface, memberinfos.getHeadface());
//		editor.putString(cfgSavetruename, memberinfos.getTruename());
//		editor.putInt(cfgSavesex,  memberinfos.getSex());
//		editor.putString(cfgSavearea,  memberinfos.getArea());
//		Log.i("aaaa", "------用户姓名保存"+memberinfos.getTruename());
//		
//		editor.commit();
//	}
	public String getHeadface() {
		SharedPreferences sp = getSharedPreferences(cfgName, Context.MODE_PRIVATE);
		return sp.getString(cfgSaveheadface, null);
	}
	public String getTruename() {
		SharedPreferences sp = getSharedPreferences(cfgName, Context.MODE_PRIVATE);
		return sp.getString(cfgSavetruename, null);
	}
	public int getSex() {
		SharedPreferences sp = getSharedPreferences(cfgName, Context.MODE_PRIVATE);
		return sp.getInt(cfgSavesex, 0);
	}
	public String getArea() {
		SharedPreferences sp = getSharedPreferences(cfgName, Context.MODE_PRIVATE);
		return sp.getString(cfgSavearea, null);
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