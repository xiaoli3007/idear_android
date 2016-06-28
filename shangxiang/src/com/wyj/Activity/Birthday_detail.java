package com.wyj.Activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wyj.Activity.R;
import com.wyj.calendar.ChinaDate;
import com.wyj.http.WebApiUrl;
import com.wyj.pipe.Cms;
import com.wyj.pipe.SinhaPipeClient;
import com.wyj.pipe.SinhaPipeMethod;
import com.wyj.pipe.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;

import android.widget.ImageView;
import android.widget.TextView;

public class Birthday_detail extends Activity implements OnClickListener {

	ImageView birthday_detail_back;

	TextView birthday_detail_title, birthday_detail_date,
			birthday_detail_delete, birthday_detail_edit, birthday_detail_wish;

	private ProgressDialog pDialog = null;
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private String detail_id, rdate, rtime, relativesname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.birthdaydetail);
		Intent intess = this.getIntent();
		detail_id = intess.getStringExtra("bid");
		get_birthday(detail_id);
		Log.i("aaaa", "---------------------" + detail_id);
		findViewById();
		setListener();
	}

	private void findViewById() {
		birthday_detail_back = (ImageView) findViewById(R.id.birthday_detail_back);

		birthday_detail_title = (TextView) findViewById(R.id.birthday_detail_title);
		birthday_detail_date = (TextView) findViewById(R.id.birthday_detail_date);
		birthday_detail_delete = (TextView) findViewById(R.id.birthday_detail_delete);
		birthday_detail_edit = (TextView) findViewById(R.id.birthday_detail_edit);
		birthday_detail_wish = (TextView) findViewById(R.id.birthday_detail_wish);

	}

	private void setListener() {
		birthday_detail_back.setOnClickListener(this);
		birthday_detail_delete.setOnClickListener(this);
		birthday_detail_edit.setOnClickListener(this);
		birthday_detail_wish.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.birthday_detail_back:
			Intent intent = new Intent(Birthday_detail.this, Foli.class);
			FoLiGroupTab.getInstance().switchActivity("Foli", intent, -1, -1);
			break;
		case R.id.birthday_detail_delete:

			birthday_delete(detail_id);
			break;
		case R.id.birthday_detail_edit:
			Intent intens = new Intent(Birthday_detail.this, AddBirthday.class);
			intens.putExtra("bid", detail_id);
			intens.putExtra("rdate", rdate);
			intens.putExtra("rtime", rtime);
			intens.putExtra("relativesname", relativesname);

			FoLiGroupTab.getInstance().switchActivity("Foli", intens, -1, -1);

			break;
		case R.id.birthday_detail_wish:

			Intent intent2 = new Intent(Birthday_detail.this, Wish.class);
			WishGroupTab.getInstance().switchActivity("Wish", intent2, -1, -1);
			break;
		}

	}

	private void get_birthday(String i) {
		// TODO Auto-generated method stub

		this.httpClient = new SinhaPipeClient();
		if (Utils.CheckNetwork()) {

			if (!TextUtils.isEmpty(Cms.APP.getMemberId())) {
				showLoading();
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("crid", i));

				Log.i("bbbb", "-----请求----" + params.toString());
				this.httpClient.Config("get", WebApiUrl.Getcalendarremindinfo,
						params, true);
				this.httpMethod = new SinhaPipeMethod(this.httpClient,
						new SinhaPipeMethod.MethodCallback() {
							public void CallFinished(String error, Object result) {
								Log.i("bbbb", "-----请求回来----" + result);
								showLoading();
								if (null == error) {
									try {
										JSONObject jsonobject = new JSONObject(
												(String) result);
										if (jsonobject.optString("code", "")
												.equals("succeed")) {

											JSONObject jsons = jsonobject
													.getJSONObject("calendarremindinfo");

											birthday_detail_title.setText(jsons
													.optString("relativesname")
													+ "的生日");
											birthday_detail_date.setText(ChinaDate.get_day_format(jsons
													.optString("reminddate")));

											relativesname = jsons
													.optString("relativesname");
											rdate = ChinaDate.get_day_format2(jsons
													.optString("reminddate"));
											rtime = jsons
													.optString("remindtime");

										} else {
											Utils.ShowToast(
													Birthday_detail.this,
													jsonobject.optString("msg",
															""));
										}
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else {
									int err = R.string.dialog_system_error_content;
									if (error == httpClient.ERR_TIME_OUT) {
										err = R.string.dialog_network_error_timeout;
									}
									if (error == httpClient.ERR_GET_ERR) {
										err = R.string.dialog_network_error_getdata;
									}
									Utils.ShowToast(Birthday_detail.this, err);
								}
							}
						});
				this.httpMethod.start();

			} else {

				// Utils.Dialog(context, "提示","请先登录账户！");
				Utils.ShowToast(Birthday_detail.this, "请先登录账户！");
			}

		} else {
			Utils.ShowToast(Birthday_detail.this,
					R.string.dialog_network_check_content);
		}

	}

	private void birthday_delete(String i) {
		// TODO Auto-generated method stub

		this.httpClient = new SinhaPipeClient();
		if (Utils.CheckNetwork()) {

			if (!TextUtils.isEmpty(Cms.APP.getMemberId())) {
				showLoading();
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("crid", i));

				Log.i("bbbb", "-----请求----" + params.toString());
				this.httpClient.Config("get", WebApiUrl.Deletecalendarreminddo,
						params, true);
				this.httpMethod = new SinhaPipeMethod(this.httpClient,
						new SinhaPipeMethod.MethodCallback() {
							public void CallFinished(String error, Object result) {
								Log.i("bbbb", "-----请求回来----" + result);
								showLoading();
								if (null == error) {
									try {
										JSONObject jsonobject = new JSONObject(
												(String) result);
										if (jsonobject.optString("code", "")
												.equals("succeed")) {

											FoLiGroupTab
													.getInstance()
													.switchActivity(
															"User",
															new Intent(
																	Birthday_detail.this,
																	Foli.class),
															-1, -1);

											Utils.ShowToast(
													Birthday_detail.this,
													jsonobject.optString("msg",
															""));
										} else {
											Utils.ShowToast(
													Birthday_detail.this,
													jsonobject.optString("msg",
															""));
										}
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else {
									int err = R.string.dialog_system_error_content;
									if (error == httpClient.ERR_TIME_OUT) {
										err = R.string.dialog_network_error_timeout;
									}
									if (error == httpClient.ERR_GET_ERR) {
										err = R.string.dialog_network_error_getdata;
									}
									Utils.ShowToast(Birthday_detail.this, err);
								}
							}
						});
				this.httpMethod.start();

			} else {

				// Utils.Dialog(context, "提示","请先登录账户！");
				Utils.ShowToast(Birthday_detail.this, "请先登录账户！");
			}

		} else {
			Utils.ShowToast(Birthday_detail.this,
					R.string.dialog_network_check_content);
		}

	}

	private void showLoading() {

		if (pDialog != null) {
			pDialog.dismiss();
			pDialog = null;
		} else {

			pDialog = new ProgressDialog(getParent().getParent());
			pDialog.setMessage("数据请求中。。。");
			pDialog.show();
		}

	}

}
