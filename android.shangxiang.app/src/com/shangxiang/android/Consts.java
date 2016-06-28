package com.shangxiang.android;

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
	public static final String ALL_HOLIDAYS = "[\"1005*达摩祖师圣诞\",\"1117*阿弥陀佛圣诞\",\"1208*释迦如来成道日\",\"1229*华严菩萨圣诞\",\"0101*弥勒菩萨圣诞\",\"0106*定光佛圣诞\",\"0208*释迦牟尼佛出家\",\"0215*释迦牟尼佛涅盘\",\"0219*观世音菩萨圣诞\",\"0221*普贤菩萨圣诞\",\"0316*准提菩萨圣诞\",\"0404*文殊菩萨圣诞\",\"0408*释迦牟尼佛圣诞\",\"0415*佛吉祥日——释迦牟尼佛诞生、成道、涅盘三期同一庆(即南传佛教国家的卫塞节)\",\"0513*伽蓝菩萨圣诞\",\"0603*护法韦驮尊天菩萨圣诞\",\"0619*观世音菩萨成道——此日放生、念佛，功德殊胜\",\"0713*大势至菩萨圣诞\",\"0724*龙树菩萨圣诞\",\"0730*地藏菩萨圣诞\",\"0822*燃灯佛圣诞\",\"0919*观世音菩萨出家纪念日\",\"0930*药师琉璃光如来圣诞\"]";
	public static final String URI_DOMAIN = "http://192.168.1.102/shangxiang_app";

	public static final String ALIPAY_PARTNER = "2088811642353234";
	public static final String ALIPAY_KEY = "9pw8o8vymgl0pqtvesd7yfzsd6uzitg0";
	public static final String ALIPAY_SELLER = "nanjingshangxiang@qq.com";
	public static final String ALIPAY_RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALiv1aT/Ufu23AikcNYPySLNBguuJ/FI6OCKhfBz7Wvfewo7nuS2LF5QCegCVoyw1KYTkP0Zb5B7NZUQ4gILIFSiqoVOezAeQHTSM/1ghdwdAZzUMkh2HM6Jjs+a+FgUXfXNSbSHh4VK6b8zJHCDrgXK9Na8T1Qv1R3mOMJd9wL7AgMBAAECgYEAlcHPBcobGnc+mKtu68VFHbkOS+5eaSLr4xewYDhArxY6WSPbRi4KcDeKsN0kfVTuOfTnvrQfaRLfcg6MlYecIGPfo+5UjMZ89B2AY+PtMQQVaNvLhbHk2UG0LgGAfWOucQMrbIe2K1OlCzkB8BXSJpyRzNE4jRHr017fwexyuNECQQDlKZ54zaQuTZqwsKUPUr1b49MbLj19BEwhonFlAJMtCoMzluJHtx104NGo7sGk/+fu+ABOmgczN51UQ39B3sElAkEAzlDRCCQ10Y2Krp5TH2S23kwipNlL+Hn8gwI/34O3bwUdhPjz5bfIx6r/EJ2sXbN7I0CmDGlq4WwUkDVxmuvJnwJASTh/FgI+zzykjIgkdTzunAmzTh/8LZHN8YFB0g/Y9q9BNJ6lNlzf4JRk6SFAZkQOC2DaWEMGweqnLmFSq+1MsQJAGXiaxfGKf2uFEpfTVU3e0cT+hfGZ0nxk81ukvRiK3fb4tQDzQ4oUDKqMwOVmcU8GRczmcyPUoS3xv/gJJYI0qwJAZH3IAZzgvLshvqjP0kRU1F9ToTrkYprKfc3QaGwUxLCHhhkduYJgAL4im7a5Wd+BILp0N8V3bNADZXuKFkC8kA==";
	public static final String ALIPAY_RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
	public static final int ALIPAY_PAY_FLAG = 1;
	public static final int ALIPAY_CHECK_FLAG = 2;
	public static final String ALIPAY_RETURN = "http://192.168.1.102/api/app_alipay/notify_url.php";

	public static final String WECHAT_APPID = "wx078a2a02ec9b0ac4";
	public static final String WECHAT_APP_SECRET = "edf598917c1f9932de0d152e50a1bdd2";
	public static final String WECHAT_ACCESSTOKEN_URI = "https://api.weixin.qq.com/sns/oauth2/access_token";
	public static final String WECHAT_USER_URI = "https://api.weixin.qq.com/sns/userinfo";
	public static final String WECHAT_PARTNERID = "1233292701";
	public static final String WECHAT_PAY_TOKEN_URI = URI_DOMAIN + "/api/app_weixinpay/index.php";

	public static final String WEIBO_APP_KEY = "2392815987";
	public static final String WEIBO_APP_SECRET = "e4b380a0d0ecb48750c20d13af09d101";
	public static final String WEIBO_SCOPE = "";
	public static final String WEIBO_USER_URL = "https://api.weibo.com/2/users/show.json";
	public static final String WEIBO_REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

	public static final String URI_ABOUT = URI_DOMAIN + "/app_aboutus.html";
	public static final String URI_CHECK_VERSION = URI_DOMAIN + "/api/app/getversioninfo.php";
	public static final String URI_CHECK_PARTNER_LOGIN = URI_DOMAIN + "/api/app/isallowotherlogin.php";
	public static final String URI_SEND_VERFY_CODE = URI_DOMAIN + "/api/app/sendsmsdo.php";
	public static final String URI_LOGIN = URI_DOMAIN + "/api/app/logindo.php";
	public static final String URI_OTHER_LOGIN = URI_DOMAIN + "/api/app/ologindo.php";
	public static final String URI_REGISTER = URI_DOMAIN + "/api/app/regdo.php";
	public static final String URI_RESET_PASSWORD = URI_DOMAIN + "/api/app/modifypassdo.php";
	public static final String URI_MODIFY_AVATAR = URI_DOMAIN + "/api/app/hfupload.php";
	public static final String URI_MEMBERINFO = URI_DOMAIN + "/api/app/getmemberinfo.php";
	public static final String URI_MODIFY_MEMBERINFO = URI_DOMAIN + "/api/app/modifymemberinfodo.php";
	public static final String URI_SETTINGS_SHOW_PUSH = URI_DOMAIN + "/api/app/setupfolireminddo.php";
	public static final String URI_TEMPLE_LIST = URI_DOMAIN + "/api/app/getwishtemplelist.php";
	public static final String URI_TEMPLE_INFO = URI_DOMAIN + "/api/app/gettempleinfo.php";
	public static final String URI_BUDDHIST_INFO = URI_DOMAIN + "/api/app/getattacheinfo.php";
	public static final String URI_SELECT_CONTENT = URI_DOMAIN + "/api/app/getwishtextchoice.php";
	public static final String URI_JSC = URI_DOMAIN + "/api/app/getwishgradeinfo.php";
	public static final String URI_CREATE_ORDER = URI_DOMAIN + "/api/app/addorderdo.php";
	public static final String URI_SOME_ORDER_LIST = URI_DOMAIN + "/api/app/getlastneworderlist.php";
	public static final String URI_ORDER_LIST = URI_DOMAIN + "/api/app/getmemberorderlist.php";
	public static final String URI_ORDER_DETAIL = URI_DOMAIN + "/api/app/getorderinfo.php";
	public static final String URI_ORDER_DELETE = URI_DOMAIN + "/api/app/deleteorderdo.php";
	public static final String URI_DISCOVER_LIST = URI_DOMAIN + "/api/app/getorderlist.php";
	public static final String URI_DISCOVER_BLESSIT = URI_DOMAIN + "/api/app/addblessingsdo.php";
	public static final String URI_DISCOVER_DETAIL = URI_DOMAIN + "/api/app/getorderinfo.php";
	public static final String URI_CALENDAR_ADD_EVENT = URI_DOMAIN + "/api/app/addcalendarreminddo.php";
	public static final String URI_CALENDAR_MODIFY_EVENT = URI_DOMAIN + "/api/app/modifycalendarreminddo.php";
	public static final String URI_CALENDAR_DELETE_EVENT = URI_DOMAIN + "/api/app/deletecalendarreminddo.php";
	public static final String URI_CALENDAR_ALL_HOLIDAY_LIST = URI_DOMAIN + "/api/app/getbuddhismholidaylist.php";
	public static final String URI_CALENDAR_ACTION_LIST = URI_DOMAIN + "/api/app/getorderwishdateremindlist.php";
	public static final String URI_CALENDAR_EVENT_LIST = URI_DOMAIN + "/api/app/getcalendarremindlist.php";
	public static final String URI_NOTICE_LIST = URI_DOMAIN + "/api/app/getsystemmessagelist.php";
	public static final String URI_NOTICE_DETAIL = URI_DOMAIN + "/api/app/getsystemmessageinfo.php";
	public static final String URI_FEEDBACK = URI_DOMAIN + "/api/app/addfeedbackdo.php";
}