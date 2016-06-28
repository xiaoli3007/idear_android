package com.wyj.Activity;


import com.wyj.Activity.R;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.ImageButton;

public class Wish extends Activity implements OnClickListener {

	private int wishtype;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_wish);

		((ImageButton) findViewById(R.id.home_desire_0_button))
				.setOnClickListener(this);
		((ImageButton) findViewById(R.id.home_desire_1_button))
				.setOnClickListener(this);
		((ImageButton) findViewById(R.id.home_desire_2_button))
				.setOnClickListener(this);
		((ImageButton) findViewById(R.id.home_desire_3_button))
				.setOnClickListener(this);
		((ImageButton) findViewById(R.id.home_desire_4_button))
				.setOnClickListener(this);
		((ImageButton) findViewById(R.id.home_desire_5_button))
				.setOnClickListener(this);
		((ImageButton) findViewById(R.id.home_desire_6_button))
				.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_desire_0_button:
			wishtype=1;
			break;
		case R.id.home_desire_1_button:
			wishtype=2;
			break;
		case R.id.home_desire_2_button:
			wishtype=3;
			break;
		case R.id.home_desire_3_button:
			wishtype=4;
			break;
		case R.id.home_desire_4_button:
			wishtype=5;
			break;
		case R.id.home_desire_5_button:
			wishtype=6;
			break;
		case R.id.home_desire_6_button:
			wishtype=7;
			break;
		default:
			break;
		}

		Intent intent = new Intent(this, ListTemple.class);
		Bundle bu=new Bundle();
		bu.putInt("wishtype", wishtype);
		intent.putExtras(bu);
		WishGroupTab.getInstance().switchActivity("ListTemple", intent, -1, -1);
	}

}
