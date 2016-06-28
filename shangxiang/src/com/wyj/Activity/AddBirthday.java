package com.wyj.Activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wyj.Activity.R;
import com.wyj.db_memberbirthday.MemberBirthday;
import com.wyj.db_memberbirthday.MemberBirthday_model;
import com.wyj.http.WebApiUrl;
import com.wyj.pipe.Cms;
import com.wyj.pipe.SinhaPipeClient;
import com.wyj.pipe.SinhaPipeMethod;
import com.wyj.pipe.Utils;
import com.wyj.popupwindow.MyPopupWindowsBirthDayDate;
import com.wyj.popupwindow.MyPopupWindowsBirthDayDate.OnSelectListener;
import com.wyj.popupwindow.MyPopupWindowsRemind;
import com.wyj.popupwindow.MyPopupWindowsRemind.OnSelectRemindListener;

import android.app.Activity;
import android.app.ProgressDialog;

import android.content.Intent;

import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class AddBirthday extends Activity implements OnClickListener {

	ImageView add_birthday_back;
	EditText add_birthday_title;
	TextView add_birthday_submit, add_birthday_dates, add_birthday_remind;
	private ProgressDialog pDialog = null;
	private String rdate, rtime, birthdaytype, relativesname;
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private String detail_id = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.addbirthday);

		findViewById();
		setListener();
		Intent intess = this.getIntent();
		if (intess != null ) {
			
			if (intess.getStringExtra("bid") != null ) {
			detail_id = intess.getStringExtra("bid");
			
			rdate = intess.getStringExtra("rdate");
			rtime = intess.getStringExtra("rtime");
			relativesname = intess.getStringExtra("relativesname");
			birthdaytype = "0";
			
			Log.i("aaaa", "---------------------" + detail_id+"---"+rdate+"----"+rtime+"----"+relativesname);
			set_form_edit();
			}
		}
	}

	private void findViewById() {
		add_birthday_back = (ImageView) findViewById(R.id.add_birthday_back);
		add_birthday_submit = (TextView) findViewById(R.id.add_birthday_submit);
		add_birthday_title = (EditText) findViewById(R.id.add_birthday_title);
		add_birthday_dates = (TextView) findViewById(R.id.add_birthday_dates);
		add_birthday_remind = (TextView) findViewById(R.id.add_birthday_remind);
	}

	private void setListener() {
		add_birthday_back.setOnClickListener(this);
		add_birthday_submit.setOnClickListener(this);
		add_birthday_dates.setOnClickListener(this);
		add_birthday_remind.setOnClickListener(this);
	}

	private void set_form_edit() {
		// TODO Auto-generated method stub
		if(rtime!=null){
		add_birthday_title.setText(relativesname);
		add_birthday_dates.setText(rdate);
		add_birthday_remind.setText("提前"+rtime+"天");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_birthday_back:

			Intent intent = new Intent(AddBirthday.this, Foli.class);
			FoLiGroupTab.getInstance().switchActivity("Foli", intent, -1, -1);
			break;
		case R.id.add_birthday_remind:

			Utils.hideKeyboard(AddBirthday.this);
			try {

				String jsons = "[{\"name\":\"准时提醒\",\"value\":\"0\" } , {\"name\":\"提前一天\",\"value\":\"1\"}, {\"name\":\"提前两天\",\"value\":\"2\"}, {\"name\":\"提前三天\",\"value\":\"3\"}, {\"name\":\"提前四天\",\"value\":\"4\"}, {\"name\":\"提前五天\",\"value\":\"5\"}, {\"name\":\"提前六天\",\"value\":\"6\"}, {\"name\":\"提前七天\",\"value\":\"7\"}]";

				JSONArray jsonArray = new JSONArray(jsons);
				new MyPopupWindowsRemind(getApplicationContext(),
						add_birthday_remind, getParent().getParent(),
						jsonArray, new OnSelectRemindListener() {

							@Override
							public void OnSelect(String result, int type) {
								// TODO Auto-generated method stub
								Log.i("aaaa", "回调提醒-----------------" + result
										+ "----" + type);

								rtime = result;
							}
						});

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
		case R.id.add_birthday_dates:

			Utils.hideKeyboard(AddBirthday.this);
			new MyPopupWindowsBirthDayDate(getApplicationContext(),
					add_birthday_dates, getParent().getParent(),
					new OnSelectListener() {

						@Override
						public void OnSelect(String result, int type) {
							// TODO Auto-generated method stub
							Log.i("aaaa", "回调日期-----------------" + result
									+ "----" + type);

							rdate = result;
							if (type == 1) {
								birthdaytype = "1";
							} else {
								birthdaytype = "0";
							}
						}
					});
			break;
		case R.id.add_birthday_submit:
			submitData();
			break;
		}

	}

	private void submitData() {

		String birthday_title = add_birthday_title.getText().toString();
		String birthday_dates = add_birthday_dates.getText().toString();
		String birthday_remind = add_birthday_remind.getText().toString();

		if (TextUtils.isEmpty(birthday_title)) {

			Utils.ShowToast(AddBirthday.this, "请填写标题!");
		} else if (TextUtils.isEmpty(birthday_dates)) {

			Utils.ShowToast(AddBirthday.this, "请填写生日日期!");

		} else if (TextUtils.isEmpty(birthday_remind)) {

			Utils.ShowToast(AddBirthday.this, "请填写提醒时间！");
		} else {

			submit_server_api(birthday_title); // 接口请求
		}

	}
	
	private void submit_sql_api(String birthday_title) {
		
		
		MemberBirthday_model memberbirdaydb=new MemberBirthday_model(AddBirthday.this);
		
		//添加
		MemberBirthday memberbirdayinfo=new MemberBirthday(Cms.APP.getMemberId(),birthday_title,rdate,birthdaytype,rtime);
		boolean flag =memberbirdaydb.insert(memberbirdayinfo);
		
		//修改
//		MemberBirthday memberbirdayinfo=new MemberBirthday(Cms.APP.getMemberId(),birthday_title,rdate,birthdaytype,rtime);
//		boolean flag =memberbirdaydb.update(memberbirdayinfo,"1");
		if(flag){
			Utils.ShowToast(AddBirthday.this,
					"成功！");
		}else{
			Utils.ShowToast(AddBirthday.this,
					"失败！");
		} 
//		Intent intent = new Intent(AddBirthday.this, Foli.class);
//		FoLiGroupTab.getInstance().switchActivity("Foli", intent, -1,
//				-1);
		
		
		
		
	}
	

	private void submit_server_api(String birthday_title) {

		this.httpClient = new SinhaPipeClient();
		if (Utils.CheckNetwork()) {

			if (!TextUtils.isEmpty(Cms.APP.getMemberId())) {
				showLoading();
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("mid", Cms.APP.getMemberId()));
				params.add(new BasicNameValuePair("rname", birthday_title));
				params.add(new BasicNameValuePair("rdate", rdate));
				params.add(new BasicNameValuePair("rtime", rtime));
				params.add(new BasicNameValuePair("type", birthdaytype));

				
				if(!detail_id.equals("")){
					params.add(new BasicNameValuePair("crid", detail_id));
					this.httpClient.Config("post", WebApiUrl.Modifycalendarreminddo,
							params, true);
				}else{
					this.httpClient.Config("post", WebApiUrl.Addcalendarreminddo,
							params, true);
				}
				
				Log.i("bbbb", "-----请求----" + params.toString());
				
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

											Intent intent = new Intent(AddBirthday.this, Foli.class);
											FoLiGroupTab.getInstance().switchActivity("Foli", intent, -1,
													-1);
											
											if(!detail_id.equals("")){
												
												
												Utils.ShowToast(AddBirthday.this,
														"修改成功！");
											}else{
												
												Utils.ShowToast(AddBirthday.this,
														jsonobject.optString("msg",
																""));
											}
											
										} else {
											Utils.ShowToast(AddBirthday.this,
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
									Utils.ShowToast(AddBirthday.this, err);
								}
							}
						});
				this.httpMethod.start();

			} else {

				// Utils.Dialog(context, "提示","请先登录账户！");
				Utils.ShowToast(AddBirthday.this, "请先登录账户！");
			}

		} else {
			Utils.ShowToast(AddBirthday.this,
					R.string.dialog_network_check_content);
		}

	}

	private void showLoading() {

		if (pDialog != null) {
			pDialog.dismiss();
			pDialog=null;
		} else {

			pDialog = new ProgressDialog(AddBirthday.this.getParent().getParent());
			pDialog.setMessage("数据请求中。。。");
			pDialog.show();
		}

	}

}
