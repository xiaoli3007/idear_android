package com.example.activity;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;
import com.example.cms.BaseFragment;
import com.example.cms.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class Home extends BaseFragment {

	private WebView mWebView;
	private  Button button;
	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		View view = inflater.inflate(R.layout.home, null);


		return view;
	}

	public void onActivityCreated(Bundle sinha) {
		super.onActivityCreated(sinha);

	}

	@Override
	public void onClick(View v) {

		if (v == this.button) {
			mWebView.loadUrl("javascript:callH5('Android OK !!!')");
		}

	}







}