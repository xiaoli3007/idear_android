package com.example.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.example.cms.Cms;
import com.example.cms.Consts;
import com.example.cms.R;

import java.io.*;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

@SuppressLint("DefaultLocale")
public class JsonString {





	public static String get_home_single_select() {
		return "{\"code\":\"succeed\",\"msg\":\"\\u8bfb\\u53d6\\u6210\\u529f\\uff01\",\"wishgradeinfo\":[{\"val\":1,\"name\":\"张三\",\"price\":19\"},{\"val\":2,\"name\":\"李四\",\"price\":99\"},{\"val\":3,\"name\":\"王五\",\"price\":99\"},{\"val\":4,\"name\":\"赵六\",\"price\":99\"}]}";
	}



	public static String get_discover_select() {
		return "{\"code\":\"succeed\",\"msg\":\"\\u8bfb\\u53d6\\u6210\\u529f\\uff01\",\"templelist\":[{\"templeid\":\"4\",\"templename\":\"选择一\",\"province\":\"选择二\"},{\"templeid\":\"9\",\"templename\":\"选择三\",\"province\":\"\\u6d59\\u6c5f\\u7701\"}]}";
	}

	public static  String get_discover_list_data(){

		return "{\"code\":\"succeed\",\"msg\":\"\\u8bfb\\u53d6\\u6210\\u529f\\uff01\",\"orderlist\":[{\"orderid\":\"45681\",\"ordernumber\":\"2015111610551845681\",\"wishtype\":\"\\u8d22\\u5bcc\",\"mid\":\"14681\",\"tid\":\"5\",\"alsowish\":\"\\u6c42\\u613f\",\"votive_orderid\":\"0\",\"retime\":\"1447642518\",\"wishname\":\"\\u548c\\u4ed5\\u9e3f\",\"wishtext\":\"\\u4e07\\u4e8b\\u5982\\u610f\\uff01\",\"status\":\"\\u5df2\\u5b8c\\u6210\",\"time_diff\":\"226\",\"nickname\":\"\",\"truename\":\"\\u548c\\u4ed5\\u9e3f\",\"headface\":\"\",\"tmb_headface\":\"\",\"co_blessings\":\"4\",\"name_blessings\":\"\\u51af\\u540c\\u52c7\",\"templename\":\"\\u4e5d\\u534e\\u5c71\",\"bleuser\":\"15631,15631,19251,19251\",\"wishdate\":\"7\\u4e2a\\u6708\\u524d\"},{\"orderid\":\"45681\",\"ordernumber\":\"2015111610551845681\",\"wishtype\":\"\\u8d22\\u5bcc\",\"mid\":\"14681\",\"tid\":\"5\",\"alsowish\":\"\\u6c42\\u613f\",\"votive_orderid\":\"0\",\"retime\":\"1447642518\",\"wishname\":\"\\u548c\\u4ed5\\u9e3f\",\"wishtext\":\"\\u4e07\\u4e8b\\u5982\\u610f\\uff01\",\"status\":\"\\u5df2\\u5b8c\\u6210\",\"time_diff\":\"226\",\"nickname\":\"\",\"truename\":\"\\u548c\\u4ed5\\u9e3f\",\"headface\":\"\",\"tmb_headface\":\"\",\"co_blessings\":\"4\",\"name_blessings\":\"\\u51af\\u540c\\u52c7\",\"templename\":\"\\u4e5d\\u534e\\u5c71\",\"bleuser\":\"15631,15631,19251,19251\",\"wishdate\":\"7\\u4e2a\\u6708\\u524d\"},{\"orderid\":\"45681\",\"ordernumber\":\"2015111610551845681\",\"wishtype\":\"\\u8d22\\u5bcc\",\"mid\":\"14681\",\"tid\":\"5\",\"alsowish\":\"\\u6c42\\u613f\",\"votive_orderid\":\"0\",\"retime\":\"1447642518\",\"wishname\":\"\\u548c\\u4ed5\\u9e3f\",\"wishtext\":\"\\u4e07\\u4e8b\\u5982\\u610f\\uff01\",\"status\":\"\\u5df2\\u5b8c\\u6210\",\"time_diff\":\"226\",\"nickname\":\"\",\"truename\":\"\\u548c\\u4ed5\\u9e3f\",\"headface\":\"\",\"tmb_headface\":\"\",\"co_blessings\":\"4\",\"name_blessings\":\"\\u51af\\u540c\\u52c7\",\"templename\":\"\\u4e5d\\u534e\\u5c71\",\"bleuser\":\"15631,15631,19251,19251\",\"wishdate\":\"7\\u4e2a\\u6708\\u524d\"},{\"orderid\":\"45681\",\"ordernumber\":\"2015111610551845681\",\"wishtype\":\"\\u8d22\\u5bcc\",\"mid\":\"14681\",\"tid\":\"5\",\"alsowish\":\"\\u6c42\\u613f\",\"votive_orderid\":\"0\",\"retime\":\"1447642518\",\"wishname\":\"\\u548c\\u4ed5\\u9e3f\",\"wishtext\":\"\\u4e07\\u4e8b\\u5982\\u610f\\uff01\",\"status\":\"\\u5df2\\u5b8c\\u6210\",\"time_diff\":\"226\",\"nickname\":\"\",\"truename\":\"\\u548c\\u4ed5\\u9e3f\",\"headface\":\"\",\"tmb_headface\":\"\",\"co_blessings\":\"4\",\"name_blessings\":\"\\u51af\\u540c\\u52c7\",\"templename\":\"\\u4e5d\\u534e\\u5c71\",\"bleuser\":\"15631,15631,19251,19251\",\"wishdate\":\"7\\u4e2a\\u6708\\u524d\"},{\"orderid\":\"45681\",\"ordernumber\":\"2015111610551845681\",\"wishtype\":\"\\u8d22\\u5bcc\",\"mid\":\"14681\",\"tid\":\"5\",\"alsowish\":\"\\u6c42\\u613f\",\"votive_orderid\":\"0\",\"retime\":\"1447642518\",\"wishname\":\"\\u548c\\u4ed5\\u9e3f\",\"wishtext\":\"\\u4e07\\u4e8b\\u5982\\u610f\\uff01\",\"status\":\"\\u5df2\\u5b8c\\u6210\",\"time_diff\":\"226\",\"nickname\":\"\",\"truename\":\"\\u548c\\u4ed5\\u9e3f\",\"headface\":\"\",\"tmb_headface\":\"\",\"co_blessings\":\"4\",\"name_blessings\":\"\\u51af\\u540c\\u52c7\",\"templename\":\"\\u4e5d\\u534e\\u5c71\",\"bleuser\":\"15631,15631,19251,19251\",\"wishdate\":\"7\\u4e2a\\u6708\\u524d\"},{\"orderid\":\"45681\",\"ordernumber\":\"2015111610551845681\",\"wishtype\":\"\\u8d22\\u5bcc\",\"mid\":\"14681\",\"tid\":\"5\",\"alsowish\":\"\\u6c42\\u613f\",\"votive_orderid\":\"0\",\"retime\":\"1447642518\",\"wishname\":\"\\u548c\\u4ed5\\u9e3f\",\"wishtext\":\"\\u4e07\\u4e8b\\u5982\\u610f\\uff01\",\"status\":\"\\u5df2\\u5b8c\\u6210\",\"time_diff\":\"226\",\"nickname\":\"\",\"truename\":\"\\u548c\\u4ed5\\u9e3f\",\"headface\":\"\",\"tmb_headface\":\"\",\"co_blessings\":\"4\",\"name_blessings\":\"\\u51af\\u540c\\u52c7\",\"templename\":\"\\u4e5d\\u534e\\u5c71\",\"bleuser\":\"15631,15631,19251,19251\",\"wishdate\":\"7\\u4e2a\\u6708\\u524d\"},{\"orderid\":\"45681\",\"ordernumber\":\"2015111610551845681\",\"wishtype\":\"\\u8d22\\u5bcc\",\"mid\":\"14681\",\"tid\":\"5\",\"alsowish\":\"\\u6c42\\u613f\",\"votive_orderid\":\"0\",\"retime\":\"1447642518\",\"wishname\":\"\\u548c\\u4ed5\\u9e3f\",\"wishtext\":\"\\u4e07\\u4e8b\\u5982\\u610f\\uff01\",\"status\":\"\\u5df2\\u5b8c\\u6210\",\"time_diff\":\"226\",\"nickname\":\"\",\"truename\":\"\\u548c\\u4ed5\\u9e3f\",\"headface\":\"\",\"tmb_headface\":\"\",\"co_blessings\":\"4\",\"name_blessings\":\"\\u51af\\u540c\\u52c7\",\"templename\":\"\\u4e5d\\u534e\\u5c71\",\"bleuser\":\"15631,15631,19251,19251\",\"wishdate\":\"7\\u4e2a\\u6708\\u524d\"}]}";
	}
}
