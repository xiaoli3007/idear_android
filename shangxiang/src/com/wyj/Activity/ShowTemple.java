package com.wyj.Activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.wyj.adapter.ListTempleAdapter;
import com.wyj.adapter.ShowTempleImageAdapter;
import com.wyj.dataprocessing.AccessNetwork;
import com.wyj.dataprocessing.BitmapManager;
import com.wyj.dataprocessing.JsonHelper;
import com.wyj.dataprocessing.JsonToListHelper;
import com.wyj.dataprocessing.MyApplication;
import com.wyj.dataprocessing.RegularUtil;
import com.wyj.http.WebApiUrl;
import com.wyj.pipe.Cms;
import com.wyj.pipe.Utils;
import com.wyj.utils.Tools;

public class ShowTemple extends Activity implements OnClickListener,
		OnCheckedChangeListener {

	private static String TAG = "ShowTemple";

	private ProgressDialog pDialog = null;
	private RadioButton radio_simiao;
	private RadioButton radio_fashi;
	private LinearLayout layoutHall;
	private LinearLayout layoutBuddhist;
	private int tid, aid;
	private Button show_temple_create_order_button;
	private TextView show_temple_hall_content_text, show_temple_hall_name_text,
			show_temple_hall_desc_text, show_temple_hall_wish_people;

	private ImageView attacheinfo_avatar;
	private TextView attacheinfo_name, attacheinfo_conversion,
			attacheinfo_description;

	private GridView mGridView;
	private int[] imgIds;
	private String[] thumb_small = {};
	private String[] thumb_big = {};

	private int wishtype;
	private String intent_info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_temple);

		ImageView back_button = (ImageView) findViewById(R.id.show_temple_layout_back);
		back_button.setOnClickListener(this);

		findViewById();
		Intent intent = this.getIntent(); // 接受的数据
		Bundle budle = intent.getExtras();

		tid = budle.getInt("tid");
		aid = budle.getInt("aid");
		wishtype = budle.getInt("wishtype");
		intent_info = budle.getString("info");

		Log.i(TAG, "------法师ID" + budle.getInt("aid"));
		loadTemplate();
		loadAttache();

	}

	private void findViewById() {
		// TODO Auto-generated method stub
		radio_simiao = (RadioButton) findViewById(R.id.show_temple_radio_simiao);
		radio_simiao.setChecked(true);
		radio_simiao.setOnCheckedChangeListener(this);
		radio_fashi = (RadioButton) findViewById(R.id.show_temple_radio_fashi);
		radio_fashi.setOnCheckedChangeListener(this);
		layoutHall = (LinearLayout) findViewById(R.id.show_temple_simiao_description);
		layoutBuddhist = (LinearLayout) findViewById(R.id.show_temple_fashi_description);
		show_temple_create_order_button = (Button) findViewById(R.id.show_temple_create_order_button);
		show_temple_create_order_button.setOnClickListener(this);

		show_temple_hall_content_text = (TextView) findViewById(R.id.show_temple_hall_content_text);
		show_temple_hall_name_text = (TextView) findViewById(R.id.show_temple_hall_name_text);
		show_temple_hall_desc_text = (TextView) findViewById(R.id.show_temple_hall_desc_text);
		show_temple_hall_wish_people = (TextView) findViewById(R.id.show_temple_hall_wish_people);

		attacheinfo_avatar = (ImageView) findViewById(R.id.attacheinfo_avatar);
		attacheinfo_name = (TextView) findViewById(R.id.attacheinfo_name);
		attacheinfo_conversion = (TextView) findViewById(R.id.attacheinfo_conversion);
		attacheinfo_description = (TextView) findViewById(R.id.attacheinfo_description);

		imgIds = new int[] { R.drawable.temp1, R.drawable.temp2,
				R.drawable.temp3 };

		mGridView = (GridView) findViewById(R.id.gridView1);

	}

	private void loadTemplate() {
		// TODO Auto-generated method stub

		// 利用Handler更新UI
		final Handler Discover = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 0x123) {
					pDialog.dismiss();
					String backmsg = msg.obj.toString();
					JSONObject allresult, allresultdata;
					List<Map<String, Object>> jsontosingle = new ArrayList<Map<String, Object>>();
					try {
						// Log.i(TAG, "------返回信息" + backmsg);
						allresult = new JSONObject(backmsg);
						String code = allresult.getString("code");
						String data = allresult.getString("templeinfo");
						allresultdata = new JSONObject(data);
						// String bullimgaes =
						// allresultdata.getString("templepic");
						jsontosingle = JsonHelper.jsonStringToList(data,
								WebApiUrl.simiaoimages, "templepic");
						Log.i(TAG, "------返回信息44444444444");

						thumb_small = new String[jsontosingle.size()];
						thumb_big = new String[jsontosingle.size()];

						for (int i = 0; i < jsontosingle.size(); i++) {
							Map<String, Object> mapssub = jsontosingle.get(i);

							thumb_small[i] = (String) mapssub
									.get("pic_tmb_path");
							thumb_big[i] = (String) mapssub.get("pic_path");

						}

						Log.i(TAG, "------遍历-" + thumb_small.toString());
						if (code.equals("succeed")) { // 更新UI

							show_temple_hall_content_text
									.setText((String) allresultdata
											.get("description"));
							show_temple_hall_name_text
									.setText((String) allresultdata
											.get("templename"));
							String p = (String) allresultdata.get("province");
							String buildtime = (String) allresultdata
									.get("buildtime");
							Integer co_order = (Integer) allresultdata
									.get("co_order");
							show_temple_hall_desc_text.setText("(" + p
									+ ",建于公元" + buildtime + "年)");
							show_temple_hall_wish_people.setText("已经有"
									+ co_order + "人在此求愿");

							mGridView.setAdapter(new ShowTempleImageAdapter(
									ShowTemple.this, thumb_small, thumb_big));

						} else {

							RegularUtil.alert_msg(ShowTemple.this, "加载失败");

						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		String parms = "tid=" + tid;
		pDialog = new ProgressDialog(this.getParent());
		pDialog.setMessage("请求中。。。");
		pDialog.show();
		new Thread(new AccessNetwork("GET", WebApiUrl.Gettemplelistinfo, parms,
				Discover)).start();
	}

	private void loadAttache() {
		// TODO Auto-generated method stub

		// 利用Handler更新UI
		final Handler Discover2 = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 0x123) {
					// pDialog.dismiss();
					String backmsg = msg.obj.toString();
					JSONObject allresults;

					try {
						allresults = new JSONObject(backmsg);
						String code = allresults.getString("code");

						JSONObject allresultdata = allresults
								.getJSONObject("attacheinfo");
						if (code.equals("succeed")) { // 更新UI

							Integer conversion = (Integer) allresultdata
									.getInt("conversion");

							String attacheinfonames = (allresultdata
									.getString("buddhistname").equals("")) ? (String) allresultdata
									.getString("attachename")
									: (String) allresultdata
											.getString("buddhistname");

							attacheinfo_name.setText(attacheinfonames);
							if (conversion != 0) {
								attacheinfo_conversion.setText("[皈依："
										+ conversion + "年]");
							} else {
								attacheinfo_conversion.setText("");
							}
							attacheinfo_description
									.setText((String) allresultdata
											.getString("description"));

							BitmapManager
									.getInstance()
									.loadBitmap(
											(String) allresultdata
													.getString("headface"),
											attacheinfo_avatar,
											Tools.readBitmap(ShowTemple.this,
													R.drawable.temp2));

						} else {
							RegularUtil.alert_msg(ShowTemple.this, "加载失败");

						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		String parms = "aid=" + aid;
		// pDialog = new ProgressDialog(this.getParent());
		// pDialog.setMessage("请求中。。。");
		// pDialog.show();
		new Thread(new AccessNetwork("GET", WebApiUrl.Getattacheinfo, parms,
				Discover2)).start();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.show_temple_layout_back:
			// 要跳转的Activity
			Intent intent = new Intent(ShowTemple.this, Wish.class);
			WishGroupTab.getInstance().switchActivity("Wish", intent, -1,
					-1);
			break;
		case R.id.show_temple_create_order_button:
			// 要跳转的Activity

			if (TextUtils.isEmpty(Cms.APP.getMemberId())) {

				Utils.Dialog(ShowTemple.this.getParent(), "提示", "请登录后操作！");

			} else {
				Intent intent2 = new Intent(ShowTemple.this, OrderForm.class);
				Bundle bu = new Bundle();
				bu.putInt("tid", tid);
				bu.putInt("aid", aid);
				bu.putInt("wishtype", wishtype);
				bu.putString("info", intent_info);
				intent2.putExtras(bu);
				WishGroupTab.getInstance().switchActivity("OrderForm", intent2,
						-1, -1);
			}
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

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if (buttonView == radio_simiao && isChecked) {
			layoutHall.setVisibility(View.VISIBLE);
			layoutBuddhist.setVisibility(View.GONE);
		}
		if (buttonView == radio_fashi && isChecked) {

			layoutHall.setVisibility(View.GONE);
			layoutBuddhist.setVisibility(View.VISIBLE);

		}
	}


}
