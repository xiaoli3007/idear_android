package com.shangxiang.android.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import android.os.Process;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

@SuppressLint({ "SimpleDateFormat", "SdCardPath" })
public class SinhaCrashUtil implements UncaughtExceptionHandler {
	public static final String TAG = "SinhaCrashUtil";
	private Thread.UncaughtExceptionHandler handler;
	private static SinhaCrashUtil INSTANCE = new SinhaCrashUtil();
	private Context context;
	private Map<String, String> infos = new HashMap<String, String>();
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	private String crashPath = "/sdcard/Sinha/";

	private SinhaCrashUtil() {
	}

	public static SinhaCrashUtil getInstance() {
		return INSTANCE;
	}

	public void init(Context context) {
		this.context = context;
		handler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && handler != null) {
			handler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error : ", e);
			}
			Process.killProcess(Process.myPid());
			System.exit(1);
		}
	}

	private boolean handleException(Throwable e) {
		if (e == null) {
			return false;
		}
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(context, "很抱歉，程序出现异常，即将退出", Toast.LENGTH_LONG).show();
				Looper.loop();
			}
		}.start();
		Log.d(TAG, "", e);
		CollectDeviceInfo();
		SaveCrashInfo(e);
		return true;
	}

	public void CollectDeviceInfo() {
		try {
			PackageManager pm = this.context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(this.context.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String strVersionName = pi.versionName == null ? "null" : pi.versionName;
				String strVersionCode = pi.versionCode + "";
				infos.put("VersionName", strVersionName);
				infos.put("VersionCode", strVersionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info : ", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
			} catch (Exception e) {
				Log.e(TAG, "an error occured when collect crash info : ", e);
			}
		}
	}

	private String SaveCrashInfo(Throwable ex) {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		try {
			long timestamp = System.currentTimeMillis();
			String time = formatter.format(new Date());
			String filename = "crash-" + time + "-" + timestamp + ".log";
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				File dir = new File(this.crashPath);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(this.crashPath + filename);
				fos.write(sb.toString().getBytes());
				fos.close();
			}
			return filename;
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing file : ", e);
		}
		return null;
	}
}
