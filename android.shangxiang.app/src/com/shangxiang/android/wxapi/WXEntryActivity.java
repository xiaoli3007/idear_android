package com.shangxiang.android.wxapi;

import com.shangxiang.android.ShangXiang;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	@Override
	public void onCreate(Bundle sinha) {
		super.onCreate(sinha);
		ShangXiang.wechatAPI.handleIntent(getIntent(), this);

		Bundle bundle = getIntent().getExtras();
		if (null != bundle && "login".equals(bundle.getString("action"))) {
			doLogin();
		}
		if (null != bundle && "shareto".equals(bundle.getString("action")) && !TextUtils.isEmpty(bundle.getString("shareto_content")) && !TextUtils.isEmpty(bundle.getString("shareto_where"))) {
			doShareto(bundle.getString("shareto_content"), bundle.getString("shareto_where"));
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
			Message msg;
			switch (resp.getType()) {
			case ConstantsAPI.COMMAND_SENDAUTH:
				SendAuth.Resp authResp = (SendAuth.Resp) resp;
				msg = ShangXiang.wxHandler.obtainMessage(1, authResp.code);
				ShangXiang.wxHandler.sendMessage(msg);
				break;
			case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
				msg = ShangXiang.wxHandler.obtainMessage(2);
				ShangXiang.wxHandler.sendMessage(msg);
				break;
			default:
				break;
			}
		}
		finish();
	}

	private void doLogin() {
		SendAuth.Req req = new SendAuth.Req();
		req.scope = "snsapi_userinfo";
		req.state = "wechat_sdk_demo";
		ShangXiang.wechatAPI.sendReq(req);
	}

	private void doShareto(String content, String where) {
		WXTextObject textObj = new WXTextObject();
		textObj.text = content;
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		msg.description = content;
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = "text" + System.currentTimeMillis();
		req.message = msg;
		req.scene = "1".equals(where) ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
		ShangXiang.wechatAPI.sendReq(req);
	}
}
