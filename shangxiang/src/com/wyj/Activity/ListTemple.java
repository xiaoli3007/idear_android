package com.wyj.Activity;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wyj.adapter.ListTempleAdapter;
import com.wyj.dataprocessing.AccessNetwork;

import com.wyj.dataprocessing.RegularUtil;
import com.wyj.http.WebApiUrl;

public class ListTemple extends Activity implements OnClickListener {

	private ListView viewList;
	private ListTempleAdapter adapterListTemple;
	private static String TAG = "ListTemple";
	private RelativeLayout layoutLoading;
	private Button buttonBack;
	private boolean showLoading = false;
	private ProgressDialog pDialog = null;
	private int wishtype;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_temple);

		Button back_button = (Button) findViewById(R.id.list_temple_back_button);
		back_button.setOnClickListener(this);

		viewList = (ListView) findViewById(R.id.list_temple_container);
		
		Intent intent = this.getIntent();
		Bundle budle=intent.getExtras();
		wishtype=budle.getInt("wishtype");
		loadDiscover();
	}

	private void loadDiscover() {
		// TODO Auto-generated method stub

		// 利用Handler更新UI
		final Handler Discover = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 0x123) {
					pDialog.dismiss();
					String backmsg = msg.obj.toString();
					JSONObject allresult;
					try {
						Log.i(TAG, "------返回信息" + backmsg);
						allresult = new JSONObject(backmsg);
						String code = allresult.getString("code");
						String data = allresult.getString("wishtemplelist");
						if (code.equals("succeed")) {
							Log.i(TAG, "------返回信息" + data);
							adapterListTemple = new ListTempleAdapter(
									ListTemple.this, new JSONArray(data),wishtype,getParent().getParent());
							viewList.setAdapter(adapterListTemple);
						} else {

							RegularUtil.alert_msg(ListTemple.this, "加载失败");

						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};

		pDialog = new ProgressDialog(this.getParent());
		pDialog.setMessage("请求中。。。");
		pDialog.show();
		new Thread(new AccessNetwork("GET", WebApiUrl.Getwishtemplelist, "",
				Discover)).start();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.list_temple_back_button:
			// 要跳转的Activity
			Intent intent = new Intent(ListTemple.this, Wish.class);
			WishGroupTab.getInstance().switchActivity("Wish", intent, -1,
					-1);
			break;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			WishGroupTab.getInstance().onKeyDown(keyCode, event);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
