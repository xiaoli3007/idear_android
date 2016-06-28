package com.wyj.utils;

public class CtLog {
	private static final boolean NEED_DEBUG = true;

	public static void d(String tag, String msg) {
		if (NEED_DEBUG) {
			android.util.Log.d(tag, msg);
		}
	}

	public static void d(String msg) {
		if (NEED_DEBUG) {
			android.util.Log.d("shangxiang", msg);
		}
	}

	public static void e(String tag, String msg) {
		if (NEED_DEBUG) {
			android.util.Log.e(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (NEED_DEBUG) {
			android.util.Log.i(tag, msg);
		}
	}

	public static void v(String tag, String msg) {
		if (NEED_DEBUG) {
			android.util.Log.v(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (NEED_DEBUG) {
			android.util.Log.w(tag, msg);
		}
	}
}
