package com.example.activity;

import org.json.JSONArray;
import org.json.JSONException;

import com.example.adapter.ShowDiscoverAdapter;
import com.example.utils.Utils;

import com.example.cms.R;

import android.app.Activity;
import android.os.Bundle;

import android.view.View;

import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class ShowDiscover extends Activity implements OnClickListener{
	private Button buttonBack;
	private RelativeLayout layoutLoading;
	private Button buttonCreateOrder;
	private ListView viewList;
	private ShowDiscoverAdapter adapterShowDiscoverList;
	private boolean showLoading = false;


	
	@Override
	public void onCreate(Bundle sinha) {
		super.onCreate(sinha);
		setContentView(R.layout.show_discover);
		
		this.buttonBack = (Button) findViewById(R.id.show_discover_title_back_button);
		this.buttonBack.setOnClickListener(this);
		this.layoutLoading = (RelativeLayout) findViewById(R.id.loading);
	
		 showLoading();
	}




	private void showLoading() {
		Utils.animView(this.layoutLoading, !this.showLoading);
		this.showLoading = !this.showLoading;
	}

	@Override
	public void onClick(View v) {
		if (v == this.buttonBack) {
			finish();
		}
		
	}
}