package com.shangxiang.android.alipay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.alipay.sdk.app.PayTask;
import com.shangxiang.android.Consts;
import com.shangxiang.android.ShangXiang;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;

public class AliEntryActivity extends Activity {
	@Override
	public void onCreate(Bundle sinha) {
		super.onCreate(sinha);
		Bundle bundle = getIntent().getExtras();
		if (null != bundle && "pay".equals(bundle.getString("action")) && !TextUtils.isEmpty(bundle.getString("order_no")) && !TextUtils.isEmpty(bundle.getString("title")) && !TextUtils.isEmpty(bundle.getString("body")) && !TextUtils.isEmpty(bundle.getString("price"))) {
			doPay(bundle.getString("order_no"), bundle.getString("title"), bundle.getString("body"), bundle.getString("price"));
		}
	}

	private String getOrderInfo(String orderNo, String subject, String body, String price) {
		String orderInfo = "partner=" + "\"" + Consts.ALIPAY_PARTNER + "\"";
		orderInfo += "&seller_id=" + "\"" + Consts.ALIPAY_SELLER + "\"";
		orderInfo += "&out_trade_no=" + "\"" + orderNo + "\"";
		orderInfo += "&subject=" + "\"" + subject + "\"";
		orderInfo += "&body=" + "\"" + body + "\"";
		orderInfo += "&total_fee=" + "\"" + price + "\"";
		orderInfo += "&notify_url=" + "\"" + Consts.ALIPAY_RETURN + "\"";
		orderInfo += "&service=\"mobile.securitypay.pay\"";
		orderInfo += "&payment_type=\"1\"";
		orderInfo += "&_input_charset=\"utf-8\"";
		orderInfo += "&it_b_pay=\"30m\"";
		orderInfo += "&return_url=\"m.alipay.com\"";
		return orderInfo;
	}

	private String sign(String content) {
		return SignUtils.sign(content, Consts.ALIPAY_RSA_PRIVATE);
	}

	private void doPay(String orderNo, String title, String body, String price) {
		String payOrderInfo = getOrderInfo(orderNo, title, title, price);
		String sign = sign(payOrderInfo);
		try {
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		final String payInfo = payOrderInfo + "&sign=\"" + sign + "\"&sign_type=\"RSA\"";
		Runnable payRunnable = new Runnable() {
			@Override
			public void run() {
				PayTask alipay = new PayTask(AliEntryActivity.this);
				String result = alipay.pay(payInfo);
				Message msg = ShangXiang.aliPayHandler.obtainMessage(Consts.ALIPAY_PAY_FLAG, result);
				ShangXiang.aliPayHandler.sendMessage(msg);
				finish();
			}
		};
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}
}
