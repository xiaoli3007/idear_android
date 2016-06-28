package com.wyj.Activity;

import com.wyj.Activity.R;

import android.app.Activity;
import android.app.ProgressDialog;

import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;

import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

public class About extends Activity implements OnClickListener {

	ImageView login_bak;
	WebView mWebView;
	private Handler mHandler;
	private ProgressDialog pDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about);

		findViewById();
		setListener();
		mHandler = new Handler();
	}

	private void findViewById() {
		login_bak = (ImageView) findViewById(R.id.about_back_login);

		mWebView = (WebView) findViewById(R.id.webview);
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		showLoading();
		mWebView.loadUrl("http://demo123.shangxiang.com/app_aboutus.html");
		showLoading();
	}

	private void setListener() {
		login_bak.setOnClickListener(this);
	}

	private void showLoading() {

		if (pDialog != null) {
			pDialog.dismiss();
		} else {

			pDialog = new ProgressDialog(getParent());
			pDialog.setMessage("数据加载中。。。");
			pDialog.show();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.about_back_login:
			// finish();

			Intent intent = new Intent(About.this, My.class);
			UserGroupTab.getInstance().switchActivity("My", intent, -1, -1);
			break;

		}

	}

}
