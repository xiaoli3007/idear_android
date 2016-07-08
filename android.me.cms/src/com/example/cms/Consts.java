package com.example.cms;

import android.os.Environment;

public class Consts {
	public static final String TAG = "ShangXiang";
	public static final int APP_ID = 22200304;
	public static final boolean DEBUG_NET = true;
	public static final String FOLDER_LOCAL = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ShangXiang";
	public static final String UPDATE_LOCAL = FOLDER_LOCAL + "/update/";
	public static final String DATABASE_NAME = "shangxiang";
	public static final int DATABASE_VERSION = 5;
	public static final long WEBVIEW_TIMEOUT = 30000;

	public static final String WECHAT_APP_KEY = "wx3f87a4ecbf40adcd";
	public static final String WECHAT_APP_SECRET = "840d18550a41a9dfdd038b62cd8c689b";

	public static final String WEIBO_APP_KEY = "2392815987";
	public static final String WEIBO_APP_SECRET = "e4b380a0d0ecb48750c20d13af09d101";
	public static final String WEIBO_SCOPE = "";
	public static final String WEIBO_REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

	public static final String URI_DOMAIN = "http://192.168.1.102/cms/api/app.php/";

	public static final String URI_LIST_INFO = URI_DOMAIN + "?act=news_list";



	public static final String URI_DOMAIN2 = "http://192.168.1.102/shangxiang_app";



}