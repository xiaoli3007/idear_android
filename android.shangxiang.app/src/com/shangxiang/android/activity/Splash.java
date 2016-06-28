package com.shangxiang.android.activity;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.shangxiang.android.Consts;
import com.shangxiang.android.R;
import com.shangxiang.android.ShangXiang;
import com.shangxiang.android.pipe.SinhaPipeClient;
import com.shangxiang.android.pipe.SinhaPipeMethod;
import com.shangxiang.android.services.AutoLoginService;
import com.shangxiang.android.utils.Utils;
import com.shangxiang.android.view.SinhaPaper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class Splash extends Activity implements OnClickListener {
	private LayoutInflater inflater;
	private SinhaPaper thumbPaper;

	@Override
	public void onCreate(Bundle sinha) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(sinha);
		setContentView(R.layout.splash);

		this.inflater = LayoutInflater.from(this);
		this.thumbPaper = (SinhaPaper) findViewById(R.id.splash_thumbs);

		TextView viewVersion = (TextView) findViewById(R.id.version_number);
		viewVersion.setText("Version " + ShangXiang.VERSION);

//		if (!TextUtils.isEmpty(ShangXiang.APP.getMobile()) && !TextUtils.isEmpty(ShangXiang.APP.getPassword())) {
//			Intent intent = new Intent(ShangXiang.APP, AutoLoginService.class);
//			startService(intent);
//		} else {
//			ShangXiang.APP.Logout();
//		}
		Intent intent = new Intent(ShangXiang.APP, AutoLoginService.class);
		startService(intent);

		checkPartnerLogin();
	}

	private void checkPartnerLogin() {
		if (Utils.CheckNetwork()) {
			SinhaPipeClient httpClient = new SinhaPipeClient();
			httpClient.Config("get", Consts.URI_CHECK_PARTNER_LOGIN, null, true);
			SinhaPipeMethod httpMethod = new SinhaPipeMethod(httpClient, new SinhaPipeMethod.MethodCallback() {
				public void CallFinished(String error, Object result) {
					if (null == error) {
						checkPartnerLogin((String) result);
					}
					showWhat();
				}
			});
			httpMethod.start();
		} else {
			showWhat();
		}
	}

	private void checkPartnerLogin(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					ShangXiang.allowPartnerLogin = result.optString("allow", "1");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void showWhat() {
		if (ShangXiang.APP.getFirstRun()) {
			this.thumbPaper.setVisibility(View.VISIBLE);
			View[] adViews = new View[] { getThumbView("assets://splash_thumb_1.png", false), getThumbView("assets://splash_thumb_2.png", false), getThumbView("assets://splash_thumb_3.png", false), getThumbView("assets://splash_thumb_4.png", true) };
			this.thumbPaper.autoScroll = false;
			this.thumbPaper.showPageControl = false;
			this.thumbPaper.setPageList(adViews);
			this.thumbPaper.setOnClickListener(this);
		} else {
			goHome();
		}
	}

	@SuppressLint("InflateParams")
	private View getThumbView(String imgThumb, boolean checkable) {
		View imageLayout = this.inflater.inflate(R.layout.sinha_paper_image_item_layout, null);
		final ProgressBar progressBar = (ProgressBar) imageLayout.findViewById(R.id.sinha_paper_image_item_loading);
		ImageView imageView = (ImageView) imageLayout.findViewById(R.id.sinha_paper_image_item_view);
		imageView.setScaleType(ScaleType.CENTER_CROP);
		if (checkable) {
			imageView.setOnClickListener(this);
		}
		ShangXiang.imageLoader.displayImage(imgThumb, imageView, ShangXiang.imageLoaderOptions, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				progressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				progressBar.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				progressBar.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				progressBar.setVisibility(View.GONE);
			}
		});
		return imageLayout;
	}

	private void goHome() {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				Intent intent = new Intent(Splash.this, Navigation.class);
				Splash.this.startActivity(intent);
				Splash.this.finish();
			}
		}, 500);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.sinha_paper_image_item_view) {
			ShangXiang.APP.setFirstRun(false);
			goHome();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
	}
}