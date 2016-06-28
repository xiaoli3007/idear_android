package com.wyj.alipay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.content.Context;

import android.os.Handler;
import android.os.Message;

import android.text.TextUtils;
import android.util.Log;

import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

public class Alipay  {

	//商户PID
	public static final String PARTNER = "2088811642353234";
	//商户收款账号
	public static final String SELLER = "nanjingshangxiang@qq.com";
	//商户私钥，pkcs8格式
	//public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMt8tZKMTk9zpxox6/SYVPWExWSdOZIhj2B5MEqkSM5WcoqQvgJkp9X3acNYD7W/65o4iS1P5AcFyzb/7dV9AQCUxs2xZNVUVoenleessDNTtUO6Ngah4xit/zRaslHRa0KMFLxDBerJeVNDR05YSXMYSRhBH3CZh6ZDYwdmUOwjAgMBAAECgYEAhaEqJEkbjDWpARzTlVcMRAejEYXmrr53R6WOPxZP8aD3q2OOREgRqAPIYs5L0tFsSWTjQOx8FNiLMV00tMRYiPnxTlb4xKqDr+NJPxW58kNOrA361VXN5ctKCmUxaeHik3FdS6aRIuOeWL2HBH8EdOeywqEc16q2sjl/JcdxcjECQQD72M/23jaPZE90Bmi65f0SJv7mxKgV1sbwBV7GEs8Ynjyge+I/vEU4qFbjCqKn9HmI6CypSfXcopEfrIOrCWgPAkEAzte+OUCWfl/Or5YYoQ3qLsD2poEvDXZg8E+S8O3LfB1Bwoat/yq1D/JOQpGTWTJSJvEIN3zShkap51SKI+bGrQJBANNf1KQRO1+8REdQPcRn1bDPk+9hrOWvfwbiqvm5vaGe5amYcHsn7D0yZMJoIb6vnPXAUAPB9J26v+0CoPwDiBcCQAYt/+rp+RURe7VXQKkfJ036SeQzm9pFHSRQ3E0Cbb/ph9tt9qjW983gKWJnwwU5MackMGCoBAoq9kyJBA+Kh4kCQAyT8A0icweqKwKCgd8K0xQjr1BLb1Vkah06qMkhW7GFkk9FmHdusu5KKgjpxF+GSH6fCa2NXPoTS6QpMmmcc28=";

	
	//商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALiv1aT/Ufu23AikcNYPySLNBguuJ/FI6OCKhfBz7Wvfewo7nuS2LF5QCegCVoyw1KYTkP0Zb5B7NZUQ4gILIFSiqoVOezAeQHTSM/1ghdwdAZzUMkh2HM6Jjs+a+FgUXfXNSbSHh4VK6b8zJHCDrgXK9Na8T1Qv1R3mOMJd9wL7AgMBAAECgYEAlcHPBcobGnc+mKtu68VFHbkOS+5eaSLr4xewYDhArxY6WSPbRi4KcDeKsN0kfVTuOfTnvrQfaRLfcg6MlYecIGPfo+5UjMZ89B2AY+PtMQQVaNvLhbHk2UG0LgGAfWOucQMrbIe2K1OlCzkB8BXSJpyRzNE4jRHr017fwexyuNECQQDlKZ54zaQuTZqwsKUPUr1b49MbLj19BEwhonFlAJMtCoMzluJHtx104NGo7sGk/+fu+ABOmgczN51UQ39B3sElAkEAzlDRCCQ10Y2Krp5TH2S23kwipNlL+Hn8gwI/34O3bwUdhPjz5bfIx6r/EJ2sXbN7I0CmDGlq4WwUkDVxmuvJnwJASTh/FgI+zzykjIgkdTzunAmzTh/8LZHN8YFB0g/Y9q9BNJ6lNlzf4JRk6SFAZkQOC2DaWEMGweqnLmFSq+1MsQJAGXiaxfGKf2uFEpfTVU3e0cT+hfGZ0nxk81ukvRiK3fb4tQDzQ4oUDKqMwOVmcU8GRczmcyPUoS3xv/gJJYI0qwJAZH3IAZzgvLshvqjP0kRU1F9ToTrkYprKfc3QaGwUxLCHhhkduYJgAL4im7a5Wd+BILp0N8V3bNADZXuKFkC8kA==";
		//支付宝公钥
	//public static final String RSA_PUBLIC  = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

	//支付宝公钥
	public static final String RSA_PUBLIC  = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

	private static final int SDK_PAY_FLAG = 1;

	private static final int SDK_CHECK_FLAG = 2;
	
	Context context;
	
	Activity activity;
	private String title ;
	private String description ;
	private String price ;
	
	private String ordernumber;
	Handler mHandler;
	
	public Alipay(Context context,String title ,String description,String price,String ordernumber,Handler mHandler) {
		
		this.activity=(Activity) context;
		this.title=title;
		this.description=description;
		this.price=price;
		this.ordernumber=ordernumber;
		this.mHandler=mHandler;
		pay();
	}
	
	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void pay() {
		// 订单
		
		String orderInfo = getOrderInfo(title, description, price,ordernumber);
		Log.i("aaaa", "支付按钮111------------------------------"+title+description+price);
		// 对订单做RSA 签名
		String sign = sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
			
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		}

		// 完整的符合支付宝参数规范的订单信息
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(activity);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};
	
		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * check whether the device has authentication alipay account.
	 * 查询终端设备是否存在支付宝认证账户
	 * 
	 */
	public void check() {
		Runnable checkRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask payTask = new PayTask(activity);
				// 调用查询接口，获取查询结果
				boolean isExist = payTask.checkAccountIfExist();

				Message msg = new Message();
				msg.what = SDK_CHECK_FLAG;
				msg.obj = isExist;
				mHandler.sendMessage(msg);
			}
		};

		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();

	}

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public void getSDKVersion() {
//		PayTask payTask = new PayTask(this);
//		String version = payTask.getVersion();
//		Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
	}

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	public String getOrderInfo(String subject, String body, String price, String ordernumber) {
		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + ordernumber + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径                                        
		orderInfo += "&notify_url=" + "\"" + "http://demo123.shangxiang.com/api/app_alipay/notify_url.php"
				+ "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	public String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}

}
