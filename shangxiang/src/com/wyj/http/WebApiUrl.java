package com.wyj.http;

public class WebApiUrl {

	public static final String IP = "http://192.168.1.102/shangxiang_app";
	public static final String PORT = "";

	public static final String GET_TEMPLELIST = IP + PORT
			+ "/api/app/gettemplelist.php"; // 寺庙列表接口
	public static final String GET_ORDERLIST = IP + PORT
			+ "/api/app/getorderlist.php"; // 发现列表接口
	public static final String GET_ORDER_DETAIL = IP + PORT
			+ "/api/app/getorderinfo.php"; // 发现详情接口

	public static final String GET_addblessingsdo = IP + PORT
			+ "/api/app/addblessingsdo.php"; // 发现加持接口


	public static final String Getwishtemplelist = IP + PORT
			+ "/api/app/getwishtemplelist.php"; // 委托求愿寺庙接口

	public static final String Gettemplelistinfo = IP + PORT
			+ "/api/app/gettempleinfo.php"; //  寺庙介绍接口
	public static final String Getattacheinfo = IP + PORT
			+ "/api/app/getattacheinfo.php"; //  法师介绍接口


	public static final String GetIncense = IP + PORT
			+ "/api/app/getwishgradeinfo.php"; //  订单的香的种类和价格
	public static final String GetWishcontent = IP + PORT
			+ "/api/app/getwishtextchoice.php"; //  订单祈福内容精选
	public static final String Getprovincecitylist = IP + PORT
			+ "/api/app/getprovincecitylist.php"; //  订单祈福地区

	public static final String Getaddorderdo = IP + PORT
			+ "/api/app/addorderdo.php"; //  订单提交

	public static final String GetOrderPay = IP + PORT
			+ "/api/app/confirmationpaysuccessdo.php"; //  订单支付接口

	public static final String Getgetorderinfo = IP + PORT
			+ "/api/app/getorderinfo.php"; //  订单详情


	public static final String Getgetmemberorderlist = IP + PORT
			+ "/api/app/getmemberorderlist.php"; //  用户订单列表

	public static final String Addcalendarreminddo = IP + PORT
			+ "/api/app/addcalendarreminddo.php"; // 添加生日
	public static final String Modifycalendarreminddo = IP + PORT
			+ "/api/app/modifycalendarreminddo.php"; // 修改生日
	public static final String Getcalendarremindinfo = IP + PORT
			+ "/api/app/getcalendarremindinfo.php"; // 读取生日

	public static final String Getcalendarremindlist = IP + PORT
			+ "/api/app/getcalendarremindlist.php"; // 生日列表

	public static final String Deletecalendarreminddo = IP + PORT
			+ "/api/app/deletecalendarreminddo.php"; // 删除生日

	public static final String REGSITER = IP + PORT + "/api/app/regdo.php"; // 注册接口
	public static final String LOGIN = IP + PORT + "/api/app/logindo.php"; // 登录接口
	public static final String THREE_LOGIN = IP + PORT
			+ "/api/app/ologindo.php"; // 第三方登录接口

	public static final String Sendsmsdo = IP + PORT
			+ "/api/app/sendsmsdo.php"; // 密码重置发送验证吗

	public static final String Modifypassdo = IP + PORT
			+ "/api/app/modifypassdo.php"; // 密码重置修改密码


	public static final String GET_USERINFO = IP + PORT
			+ "/api/app/getmemberinfo.php"; // 读取用户信息接口
	public static final String MEMBERINFO = IP + PORT
			+ "/api/app/modifymemberinfodo.php"; // 用户资料修改接口
	public static final String CESHIURL = IP + PORT + "/api/app/hfupload.php"; // 用户头像上传
	// public static final String CESHIURL =
	// "http://192.168.1.30/sms/module.php?m=sms&c=index&a=ceshi";
	public static final String GETaddfeedbackdo= IP + PORT
			+ "/api/app/addfeedbackdo.php"; // 意见反馈

	public static String[] wishtypes = {  "财富", "健康",
		"求子", "平安", "学业", "姻缘", "事业" };//

	public static String[] keyNames = { "memberid", "membername", "headface",
			"nickname", "truename", "sex", "area", "tmb_headface" };// 读取用户信息接口
	public static String[] simiaoimages = { "pic_path", "pic_tmb_path"};// 读取寺庙介绍图片数组接口

	public static String[] fashiinfo = { "buddhistname", "conversion", "headface", "templename", "description"};// 读取法师数组

}
