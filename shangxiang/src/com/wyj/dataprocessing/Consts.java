package com.wyj.dataprocessing;

import android.os.Environment;

public class Consts {
	public static final String TAG = "ShangXiang";
	public static final int APP_ID = 22200304;
	public static final boolean DEBUG_NET = true;
	public static final String FOLDER_LOCAL = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/ShangXiang";
	public static final String UPDATE_LOCAL = FOLDER_LOCAL + "/update/";
	public static final String DATABASE_NAME = "shangxiang";
	public static final int DATABASE_VERSION = 5;
	public static final long WEBVIEW_TIMEOUT = 30000;

	public static final String URI_UPDATE = "http://www.3ad.cn";
	public static final String URI_TEST = "http://www.3ad.cn";
	public static final String URI_FEEDBACK = "http://www.3ad.cn";
}