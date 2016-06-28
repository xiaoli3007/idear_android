package com.shangxiang.android.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.shangxiang.android.R;
import com.shangxiang.android.BaseFragment;
import com.shangxiang.android.Consts;
import com.shangxiang.android.utils.Utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Browser extends BaseFragment {
	private RelativeLayout layoutLoading;
	private Button buttonBack;
	private WebView viewPage;
	private Handler handler;
	private Timer timer;
	private boolean showLoading = false;

	@SuppressLint({ "InflateParams", "SetJavaScriptEnabled" })
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		View view = inflater.inflate(R.layout.browser, null);

		this.layoutLoading = (RelativeLayout) view.findViewById(R.id.loading);
		this.buttonBack = (Button) view.findViewById(R.id.browser_title_back_button);
		this.buttonBack.setOnClickListener(this);
		this.viewPage = (WebView) view.findViewById(R.id.browser_content_view);
		this.viewPage.getSettings().setJavaScriptEnabled(true);
		this.viewPage.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		this.viewPage.getSettings().setSupportZoom(false);
		this.viewPage.getSettings().setDomStorageEnabled(true);
		this.viewPage.getSettings().setAllowFileAccess(true);
	//	this.viewPage.getSettings().setPluginsEnabled(true);
		this.viewPage.getSettings().setUseWideViewPort(true);
		this.viewPage.getSettings().setBuiltInZoomControls(false);
		this.viewPage.getSettings().setLoadWithOverviewMode(true);
		this.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				viewPage.stopLoading();
				showLoading();
			};
		};

		Bundle bundle = getArguments();
		if (null != bundle && null != bundle.getString("title")) {
			TextView viewTitle = (TextView) view.findViewById(R.id.browser_title_text);
			viewTitle.setText(bundle.getString("title"));
		}
		if (null != bundle && null != bundle.getString("url")) {
			String url = bundle.getString("url");
			this.viewPage.setWebViewClient(new WebViewClient() {
				@Override
				public void onPageStarted(WebView view, String url, Bitmap favicon) {
					super.onPageStarted(view, url, favicon);
					showLoading();
					timer = new Timer();
					TimerTask tt = new TimerTask() {
						@Override
						public void run() {
							if (viewPage.getProgress() < 100) {
								Message msg = handler.obtainMessage(0);
								handler.sendMessage(msg);
								timer.cancel();
								timer.purge();
							}
						}
					};
					timer.schedule(tt, Consts.WEBVIEW_TIMEOUT, 1);
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					timer.cancel();
					timer.purge();
					showLoading();
				}

				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					return super.shouldOverrideUrlLoading(view, url);
				}

				@Override
				public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
					showLoading();
				}
			});
			this.viewPage.loadUrl(url);
		}

		return view;
	}

	private void showLoading() {
		Utils.animLoading(this.layoutLoading, !this.showLoading);
		this.showLoading = !this.showLoading;
	}

	@Override
	public void onClick(View v) {
		Utils.hideKeyboard(getActivity());
		if (v == this.buttonBack) {
			getActivity().onBackPressed();
		}
	}
}