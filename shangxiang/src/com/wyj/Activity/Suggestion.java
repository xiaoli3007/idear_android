package com.wyj.Activity;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.wyj.Activity.R;
import com.wyj.dataprocessing.JsonHelper;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


public class Suggestion extends Activity implements OnClickListener {

	ImageView back_suggest;
	Button suggest_submit;
	EditText user_suggest;

	private ProgressDialog pDialog = null;
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.suggest);

		findViewById();
		setListener();
	}

	private void findViewById() {
		back_suggest = (ImageView) findViewById(R.id.back_suggest);
		suggest_submit = (Button) findViewById(R.id.suggest_submit);

		user_suggest = (EditText) findViewById(R.id.user_suggest);
	}

	private void setListener() {
		back_suggest.setOnClickListener(this);
		suggest_submit.setOnClickListener(this);
	}

	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_suggest:
			Intent intent = new Intent(Suggestion.this, My.class);
			UserGroupTab.getInstance().switchActivity("User", intent, -1,
					-1);
			break;
		case R.id.suggest_submit:
			submitData();
			break;
		}

	}

	private void submitData() {

		String suggest = user_suggest.getText().toString();

		if (TextUtils.isEmpty(suggest)) {
			
			Utils.Dialog(Suggestion.this.getParent(), "提示", "内容不能为空");
			// 设置焦点信息;
			user_suggest.setFocusable(true);
			user_suggest.setFocusableInTouchMode(true);
			user_suggest.requestFocus();
			user_suggest.requestFocusFromTouch();
		} else {
			
			submit_server(  WebApiUrl.GETaddfeedbackdo ,suggest);
		}

	}
	
	private void submit_server( String url ,String suggest) {
		this.httpClient = new SinhaPipeClient();
		
		if (Utils.CheckNetwork()) {
			if(!TextUtils.isEmpty(Cms.APP.getMemberId())){
				showLoading();
				List<NameValuePair> parm=new ArrayList<NameValuePair>();
				parm.add(new BasicNameValuePair("content",suggest));
				parm.add(new BasicNameValuePair("mid",Cms.APP.getMemberId()));
				this.httpClient.Config("post", url, parm, true);
				this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
					public void CallFinished(String error, Object result) {
					
							showLoading();
					
						if (null == error) {
							
							try {
								JSONObject object =new JSONObject((String) result);
								if(object.optString("code").equals("succeed")){
									
									Utils.ShowToast(Suggestion.this,object.optString("msg"));
								}else{
									Utils.ShowToast(Suggestion.this,object.optString("msg"));
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Log.i("bbbb", "-----请求回来-23344---" );
							
							
						} else {
							int err = R.string.dialog_system_error_content;
							if (error == httpClient.ERR_TIME_OUT) {
								err = R.string.dialog_network_error_timeout;
							}
							if (error == httpClient.ERR_GET_ERR) {
								err = R.string.dialog_network_error_getdata;
							}
							Utils.ShowToast(Suggestion.this, err);
						}
					}
				});
				this.httpMethod.start();
			}else{
				
				Utils.ShowToast(Suggestion.this, "请登录后操作！");
			}
			
		} else {
			Utils.ShowToast(Suggestion.this, R.string.dialog_network_check_content);
		}
		
	
	}
	
private void showLoading() {
		
		if(pDialog!=null){
			pDialog.dismiss();
		}else{
			
			pDialog = new ProgressDialog(getParent().getParent());
			pDialog.setMessage("数据加载中。。。");
			pDialog.show();
		}
		
	}

}
