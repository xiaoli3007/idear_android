package com.shangxiang.android.wxapi;

import com.shangxiang.android.Consts;
import com.shangxiang.android.ShangXiang;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ShangXiang.wechatAPI.handleIntent(getIntent(), this);

		Bundle bundle = getIntent().getExtras();
		if (null != bundle && "pay".equals(bundle.getString("action")) && !TextUtils.isEmpty(bundle.getString("prepayid")) && !TextUtils.isEmpty(bundle.getString("noncestr")) && !TextUtils.isEmpty(bundle.getString("timestamp")) && !TextUtils.isEmpty(bundle.getString("package")) && !TextUtils.isEmpty(bundle.getString("sign"))) {
			doPay(bundle.getString("prepayid"), bundle.getString("noncestr"), bundle.getString("timestamp"), bundle.getString("package"), bundle.getString("sign"));
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		ShangXiang.wechatAPI.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		if (0 == resp.errCode) {
			if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
				Message msg = ShangXiang.wxPayHandler.obtainMessage(3);
				ShangXiang.wxPayHandler.sendMessage(msg);
			}
		}
		finish();
	}

	private void doPay(String prepayId, String nonceStr, String timeStamp, String packageValue, String sign) {
		Log.d("SinhaCrash", "---" + prepayId);
		Log.d("SinhaCrash", "---" + nonceStr);
		Log.d("SinhaCrash", "---" + packageValue);
		Log.d("SinhaCrash", "---" + sign);
		PayReq req = new PayReq();
		req.appId = Consts.WECHAT_APPID;
		req.partnerId = Consts.WECHAT_PARTNERID;
		req.prepayId = prepayId;
		req.nonceStr = nonceStr;
		req.timeStamp = timeStamp;
		req.packageValue = packageValue;
		req.sign = sign;
		ShangXiang.wechatAPI.sendReq(req);
	}
}