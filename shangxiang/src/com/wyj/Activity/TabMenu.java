package com.wyj.Activity;


import com.wyj.Activity.R;


import com.wyj.pipe.Cms;
import com.wyj.services.AutoLoginService;

import android.app.AlertDialog;
import android.app.TabActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class TabMenu extends TabActivity {
	// TabHost
	public TabHost mth;
	public static final String TAB_HOME = "求愿";
	public static final String TAB_NEWS = "发现";
	public static final String TAB_ABOUT = "佛历";
	public static final String TAB_SEARCH = "我的";

	public RadioGroup radioGroup;
	public RadioButton button0, button1, button2, button3, button4;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去除标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		initViews();
		// 初始化底部菜单
		init();
		// 底部菜单点击事件
		clickevent();
		
		if (!TextUtils.isEmpty(Cms.APP.getMobile()) && !TextUtils.isEmpty(Cms.APP.getPassword())) {
			Intent intent = new Intent(Cms.APP, AutoLoginService.class);
			startService(intent);
			Log.i("cccc", "------tttttttttttttttttt");
		} else {
			Cms.APP.Logout();
			Log.i("cccc", "------tgggggggggggggggggg");
		}
		
	}

	private void initViews() {

		button0 = (RadioButton) findViewById(R.id.radio_button0);
		button1 = (RadioButton) findViewById(R.id.radio_button1);
		button2 = (RadioButton) findViewById(R.id.radio_button2);
		button3 = (RadioButton) findViewById(R.id.radio_button3);

	}

	/**
	 * 每一个底部按钮点击事件，切换相应的界面
	 */
	private void clickevent() {
		this.radioGroup = (RadioGroup) findViewById(R.id.main_radio);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// // 根据点击的按钮跳转到相应的界面
				switch (checkedId) {
				case R.id.radio_button0:
					// mth.setBackgroundResource(R.drawable.light);
					mth.setCurrentTabByTag(TAB_HOME);
					break;
				case R.id.radio_button1:
					mth.setCurrentTabByTag(TAB_NEWS);

					break;
				case R.id.radio_button2:
					mth.setCurrentTabByTag(TAB_ABOUT);
					break;
				case R.id.radio_button3:
					mth.setCurrentTabByTag(TAB_SEARCH);
					break;

				}
			}
		});
	}

	/**
	 * 实例化TabHost,往TabHost添加5个界面
	 */
	private void init() {
		// // 实例化TabHost
		mth = this.getTabHost();
		TabSpec ts1 = mth.newTabSpec(TAB_HOME).setIndicator(TAB_HOME);
		ts1.setContent(new Intent(TabMenu.this, WishGroupTab.class));
		mth.addTab(ts1);// 往TabHost中第一个底部菜单添加界面

		TabSpec ts2 = mth.newTabSpec(TAB_NEWS).setIndicator(TAB_NEWS);
		ts2.setContent(new Intent(TabMenu.this, FindGroupTab.class));
		mth.addTab(ts2);

		TabSpec ts3 = mth.newTabSpec(TAB_ABOUT).setIndicator(TAB_ABOUT);
		ts3.setContent(new Intent(TabMenu.this, FoLiGroupTab.class));
		mth.addTab(ts3);

		TabSpec ts4 = mth.newTabSpec(TAB_SEARCH).setIndicator(TAB_SEARCH);
		ts4.setContent(new Intent(TabMenu.this, UserGroupTab.class));
		mth.addTab(ts4);

		// RadioButton default_radioButton =
		// (RadioButton)findViewById(R.id.radio_button0);
		button0.setChecked(true); // 默认首页
		// mth.setCurrentTabByTag(TAB_ABOUT);
	}

	public void setCurrentActivity(int index) {
		switch (index) {
		case 1:
			onCheckedChanged(radioGroup, R.id.radio_button0);
			break;
		case 2:
			onCheckedChanged(radioGroup, R.id.radio_button1);
			break;	

		}
	}

	public void onCheckedChanged(RadioGroup group, int checkedId) {

		switch (checkedId) {
		case R.id.radio_button0:
			this.mth.setCurrentTabByTag(TAB_HOME);
			button0.setChecked(true);
			button1.setChecked(false);
			button2.setChecked(false);
			button3.setChecked(false);
			break;
		case R.id.radio_button1:
			this.mth.setCurrentTabByTag(TAB_HOME);
			button0.setChecked(false);
			button1.setChecked(true);
			button2.setChecked(false);
			button3.setChecked(false);
			break;	
		default:
			break;
		}
	}

	public void exitApp() {
		new AlertDialog.Builder(this)
				.setTitle("确定要退出么？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
						System.exit(0);
					}
				})
				.setNegativeButton("不确定",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).create().show();
	}

}
