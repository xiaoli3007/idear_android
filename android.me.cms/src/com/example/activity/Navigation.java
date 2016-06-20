package com.example.activity;

import java.util.ArrayList;

import com.example.utils.Utils;
import com.example.cms.R;
import com.example.cms.Cms;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;

public class Navigation extends FragmentActivity implements OnCheckedChangeListener {
	private ArrayList<Object[]> tabs;
	private int currTab = -1;

	@Override
	public void onCreate(Bundle sinha) {
		super.onCreate(sinha);
		setContentView(R.layout.navigation);

		this.tabs = new ArrayList<Object[]>();
		this.tabs.add(new Object[] { (RadioButton) findViewById(R.id.tab_bar_home), new Home(), 1 });
		this.tabs.add(new Object[] { (RadioButton) findViewById(R.id.tab_bar_discover), new Discover(), 0 });
		this.tabs.add(new Object[] { (RadioButton) findViewById(R.id.tab_bar_calendar), new LunarCalendar(), 0 });
		this.tabs.add(new Object[] { (RadioButton) findViewById(R.id.tab_bar_my), new My(), 0 });

		initTab();

		Cms.tabHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what >= 0 && msg.what < tabs.size()) {
					Object[] val = (Object[]) tabs.get(msg.what);
					RadioButton buttonTab = (RadioButton) val[0];
					buttonTab.performClick();
				}
			};
		};
	}

	private void initTab() {
		for (int i = 0; i < this.tabs.size(); i++) {
			Object[] val = (Object[]) this.tabs.get(i);
			RadioButton buttonTab = (RadioButton) val[0];
			buttonTab.setTag(i);
			if (null != val[1]) {
				buttonTab.setOnCheckedChangeListener(Navigation.this);
			}
			if (1 == (Integer) val[2]) {
				buttonTab.performClick();
			}
		}
	}

	public void goTab(Fragment fragment) {
		FragmentTransaction transFrogment = getSupportFragmentManager().beginTransaction();
		transFrogment.replace(Cms.tabContent, fragment);
		transFrogment.commit();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			this.currTab = (Integer) buttonView.getTag();
			Object[] val = (Object[]) this.tabs.get(this.currTab);
			goTab((Fragment) val[1]);
		}
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Class<?> location = Cms.classCurr;
			if (null == location || location == Home.class || location == LunarCalendar.class || location == My.class || location == Message.class) {
				Utils.Dialog(Navigation.this, getString(R.string.dialog_exit_tip), getString(R.string.dialog_exit_content), new Utils.Callback() {
					@Override
					public void callFinished() {
						System.exit(0);
					}
				}, new Utils.Callback() {
					@Override
					public void callFinished() {
					}
				}, new Utils.Callback() {
					@Override
					public void callFinished() {
					}
				});
				return false;
			}
		}
		return super.dispatchKeyEvent(event);
	}
}
