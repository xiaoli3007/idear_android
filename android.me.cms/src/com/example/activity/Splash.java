package com.example.activity;

import com.example.services.AutoLoginService;
import com.example.cms.R;
import com.example.cms.Cms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.TextView;

public class Splash extends Activity {

	@Override
	public void onCreate(Bundle sinha) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(sinha);
		setContentView(R.layout.splash);

		TextView viewVersion = (TextView) findViewById(R.id.version_number);
		viewVersion.setText("Version " + Cms.VERSION);

		if (!TextUtils.isEmpty(Cms.APP.getMobile()) && !TextUtils.isEmpty(Cms.APP.getPassword())) {
			Intent intent = new Intent(Cms.APP, AutoLoginService.class);
			startService(intent);
		} else {
			Cms.APP.Logout();
		}
		goHome();
	}

	private void goHome() {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				Intent intent = new Intent(Splash.this, Navigation.class);
				Splash.this.startActivity(intent);
				Splash.this.finish();
			}
		}, 1000);
	}
}