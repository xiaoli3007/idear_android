package com.example.activity;

import android.widget.*;
import com.example.utils.Utils;

import com.example.cms.R;

import android.app.Activity;
import android.os.Bundle;

import android.view.View;

import android.view.View.OnClickListener;

public class ShowDiscover extends Activity implements OnClickListener{
	private Button buttonBack;
	private RelativeLayout layoutLoading;
	private boolean showLoading = false;

	TextView show_discover_desc_text,show_discover_content_text;
	private Bundle bundle;

	@Override
	public void onCreate(Bundle sinha) {
		super.onCreate(sinha);
		setContentView(R.layout.show_discover);

		this.buttonBack = (Button) findViewById(R.id.show_discover_title_back_button);
		this.buttonBack.setOnClickListener(this);
		this.layoutLoading = (RelativeLayout) findViewById(R.id.loading);
		this.show_discover_desc_text= (TextView) findViewById(R.id.show_discover_desc_text1);
		this.show_discover_content_text= (TextView) findViewById(R.id.show_discover_content_text1);

		this.bundle = getIntent().getExtras();

		showLoading();


	}


	private void showLoading() {
//		Utils.animView(this.layoutLoading, !this.showLoading);
//		this.showLoading = !this.showLoading;
		if (null != this.bundle )
		{
			show_discover_desc_text.setText("sadsadsad");
			show_discover_content_text.setText("333333333333333");
		}
	}

	@Override
	public void onClick(View v) {
		if (v == this.buttonBack) {
			finish();
		}

	}
}