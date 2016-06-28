package com.wyj.Activity;


import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import com.wyj.dataprocessing.BitmapManager;

import com.wyj.pipe.Cms;
import com.wyj.pipe.Utils;
import com.wyj.utils.FilePath;
import com.wyj.utils.Tools;
import com.wyj.Activity.R;

import android.app.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class My extends Activity implements OnClickListener {
	RelativeLayout action_login;
	TextView user;
	ImageView my_avatar_face;
	/* 头像名称 */
	private static String IMAGE_FILE_NAME="";
	private RelativeLayout memberlogout,about_we;
	private LinearLayout my_bless_order,formy_bless_order;
	private RelativeLayout myorder_in,remind_action,suggest_action;
	private TextView my_bless_nums_my,formy_bless_nums_my;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my);
		findViewById();
		setListener();
		
		
	   member_is_login();
		
	}

	private void findViewById() {

		my_avatar_face = (ImageView) findViewById(R.id.avatar);
		action_login = (RelativeLayout) findViewById(R.id.login_action);
		user = (TextView) findViewById(R.id.member_center_username);
		memberlogout =(RelativeLayout) findViewById(R.id.memberlogout);
		about_we =(RelativeLayout) findViewById(R.id.about_we);
		
		my_bless_order=(LinearLayout) findViewById(R.id.my_bless_order);
		formy_bless_order =(LinearLayout) findViewById(R.id.formy_bless_order);
		myorder_in=(RelativeLayout) findViewById(R.id.myorder_in);
		
		my_bless_nums_my=(TextView) findViewById(R.id.my_bless_nums_my);
		formy_bless_nums_my=(TextView) findViewById(R.id.formy_bless_nums_my);
		
		remind_action=(RelativeLayout)findViewById(R.id.remind_action);
		suggest_action=(RelativeLayout)findViewById(R.id.suggest_action);
		
	}

	private void setListener() {
		action_login.setOnClickListener(this);
		memberlogout.setOnClickListener(this);
		my_bless_order.setOnClickListener(this);
		formy_bless_order.setOnClickListener(this);
		myorder_in.setOnClickListener(this);
		about_we.setOnClickListener(this);
		remind_action.setOnClickListener(this);
		suggest_action.setOnClickListener(this);
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.i("aaaa", "------登录回来了"+IMAGE_FILE_NAME);
		member_login_action();
	}

	private void member_login_action() {
		// TODO Auto-generated method stub
		
		try {
			Cms.memberInfo = new JSONObject(Cms.APP.getConfig());
			
			member_is_login();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void member_is_login() {
		
		if(!TextUtils.isEmpty(Cms.APP.getMemberId())){
			IMAGE_FILE_NAME= Cms.APP.getMemberId() + "_faceImage.jpg";	
		
		}else{
			IMAGE_FILE_NAME= "_faceImage.jpg";
		}
		
		my_bless_nums_my.setText((!TextUtils.isEmpty(Cms.memberInfo.optString("do_blessings", "")))?Cms.memberInfo.optString("do_blessings", ""):"");
		formy_bless_nums_my.setText((!TextUtils.isEmpty(Cms.memberInfo.optString("received_blessings", "")))?Cms.memberInfo.optString("received_blessings", ""):"");
		
		String username = (!TextUtils.isEmpty(Cms.memberInfo.optString("membername", "")))?Cms.memberInfo.optString("membername", ""):"";
		String avatar =  (!TextUtils.isEmpty(Cms.memberInfo.optString("headface", "")))?Cms.memberInfo.optString("headface", ""):"";
		Log.i("aaaa", "------登录回来了"+username);
		if (username != "") {
			
			memberlogout.setVisibility(View.VISIBLE);
			user.setText(username);
			if(avatar!=""){
				if (Tools.fileIsExists(FilePath.ROOT_DIRECTORY + IMAGE_FILE_NAME)) {
					Bitmap bitmap = BitmapFactory
							.decodeFile(FilePath.ROOT_DIRECTORY + IMAGE_FILE_NAME);
					my_avatar_face.setImageBitmap(bitmap);
				} else {
					BitmapManager.getInstance().loadBitmap(avatar, my_avatar_face,
							Tools.readBitmap(My.this, R.drawable.me));
				}
			}else{
				my_avatar_face.setBackgroundResource(R.drawable.me);
			}
			
		} else {
			user.setText("立即登录");
		}
	} 
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_action:
			if(TextUtils.isEmpty(Cms.APP.getMobile())){
				UserGroupTab.getInstance().startActivityForResult(
						new Intent(My.this, Login.class), 1);

			} else {
				Intent intent = new Intent(My.this, User.class);
				UserGroupTab.getInstance().switchActivity("User", intent, -1,
						-1);
			}
			break;
			
		case R.id.memberlogout:
			Cms.APP.Logout();
			Utils.ShowToast(My.this, "退出成功");
			memberlogout.setVisibility(View.GONE);
			user.setText("立即登录");
			my_avatar_face.setImageResource(R.drawable.me);
			break;
			
		case R.id.my_bless_order:
			if(!TextUtils.isEmpty(Cms.APP.getMobile())){
				Intent intent = new Intent(My.this, MyFind.class);
				UserGroupTab.getInstance().switchActivity("MyFind", intent, -1,
						-1);
			} else {
				Utils.ShowToast(My.this, "请先登录");
			}
			break;
			
		case R.id.formy_bless_order:
			if(!TextUtils.isEmpty(Cms.APP.getMobile())){
				Intent intent = new Intent(My.this, MyFind.class);
				UserGroupTab.getInstance().switchActivity("MyFind", intent, -1,
						-1);
			} else {
				Utils.ShowToast(My.this, "请先登录");
			}
			break;
		case R.id.myorder_in:
			if(!TextUtils.isEmpty(Cms.APP.getMobile())){
				Intent intent = new Intent(My.this, MyOrder.class);
				UserGroupTab.getInstance().switchActivity("MyOrder", intent, -1,
						-1);
			} else {
				Utils.ShowToast(My.this, "请先登录");
			}
			break;
		case R.id.about_we:
			
				Intent intent = new Intent(My.this, About.class);
				UserGroupTab.getInstance().switchActivity("About", intent, -1,
						-1);
			
			break;
		case R.id.suggest_action:
			
			Intent intent22 = new Intent(My.this, Suggestion.class);
			UserGroupTab.getInstance().switchActivity("Suggestion", intent22, -1,
					-1);
		
		break;
		case R.id.remind_action:
			
			Intent intent33 = new Intent(My.this, Remind.class);
			UserGroupTab.getInstance().switchActivity("Remind", intent33, -1,
					-1);
		
		break;
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			UserGroupTab.getInstance().onKeyDown(keyCode, event);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
