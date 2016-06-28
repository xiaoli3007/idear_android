package com.wyj.Activity;


import com.wyj.Activity.R;

import android.app.Activity;


import android.content.Intent;

import android.os.Bundle;



import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;

import android.widget.ImageView;


public class Remind extends Activity implements OnClickListener {

	ImageView back_remind;

	private Boolean regflag = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.remind);

		findViewById();
		setListener();
	}

	private void findViewById() {
		back_remind = (ImageView) findViewById(R.id.back_remind);
		

	}

	private void setListener() {
		back_remind.setOnClickListener(this);
	
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_remind:
			
			Intent intent22 = new Intent(Remind.this, My.class);
			UserGroupTab.getInstance().switchActivity("My", intent22, -1,
					-1);
			break;
	
		}

	}



}
