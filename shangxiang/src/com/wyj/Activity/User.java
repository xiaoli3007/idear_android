package com.wyj.Activity;

import java.io.File;

import java.util.HashMap;
import java.util.Map;


import com.wyj.dataprocessing.AccessNetwork;
import com.wyj.dataprocessing.BitmapManager;

import com.wyj.http.WebApiUrl;
import com.wyj.pipe.Cms;
import com.wyj.utils.FilePath;
import com.wyj.utils.Tools;
import com.wyj.Activity.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class User extends Activity implements OnClickListener {
	RelativeLayout avatar_edit;
	LinearLayout userinfo_edits1;
	LinearLayout userinfo_edits2;
	TextView username_input;
	TextView truename_input;
	TextView address_input;
	TextView sex_input;
	private static String TAG = "User";
	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 10; // 相册选择
	private static final int CAMERA_REQUEST_CODE = 30; // 拍照
	private static final int RESULT_REQUEST_CODE = 20; // 返回图片数据 处理图片把图片放置当前页面
	/* 头像名称 */
	private static final String IMAGE_FILE_NAME = Cms.APP.getMemberId() + "_faceImage.jpg";
	// private static final String IMAGE_FILE_NAME = "aaa.png";
	private ImageView faceImage;
	private ImageView userinfo_back;
	private ProgressDialog pDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user);

		findViewById();
		setListener();

	}

	private void findViewById() {

		userinfo_edits1 = (LinearLayout) findViewById(R.id.userinfo_edits1);
		userinfo_edits2 = (LinearLayout) findViewById(R.id.userinfo_edits2);

		avatar_edit = (RelativeLayout) findViewById(R.id.avatar_edit);
		userinfo_back = (ImageView) findViewById(R.id.userinfo_back);

		faceImage = (ImageView) findViewById(R.id.avatar_face);
		username_input = (TextView) findViewById(R.id.username_input);
		truename_input = (TextView) findViewById(R.id.truename_input);
		address_input = (TextView) findViewById(R.id.address_input);
		sex_input = (TextView) findViewById(R.id.sex_input);
		
		if (!TextUtils.isEmpty(Cms.APP.getMobile())) {
			username_input.setText(Cms.APP.getMobile());
		} else {
			username_input.setText("未登录");
		}
		
		Log.i("aaaa", "------修改hou的信息" + Cms.memberInfo.optString("truename", ""));
		
		if (Cms.memberInfo.optString("sex", "").equals("1")) {
			sex_input.setText("男");
		} else if (Cms.memberInfo.optString("sex", "").equals("2")) {
			sex_input.setText("女");
		} else {
			sex_input.setText("未填写");
		}
		truename_input
				.setText((Cms.memberInfo.optString("truename", "") != "") ? (Cms.memberInfo.optString("truename", "")) : "未填写");
		address_input
				.setText((Cms.memberInfo.optString("area", "") != "") ? (Cms.memberInfo.optString("area", "")) : "未填写");

		if (Tools.fileIsExists(FilePath.ROOT_DIRECTORY + IMAGE_FILE_NAME)) {
			Bitmap bitmap = BitmapFactory.decodeFile(FilePath.ROOT_DIRECTORY
					+ IMAGE_FILE_NAME);
			faceImage.setImageBitmap(bitmap);
		} else {
			BitmapManager.getInstance().loadBitmap(
					Cms.memberInfo.optString("headface", ""), faceImage,
					Tools.readBitmap(User.this, R.drawable.foot_07));
		}

	}

	private void setListener() {
		avatar_edit.setOnClickListener(this);
		userinfo_back.setOnClickListener(this);
		userinfo_edits1.setOnClickListener(this);
		userinfo_edits2.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.avatar_edit:
			avatar_Views();

			break;
		case R.id.userinfo_edits1:
			Intent intent2 = new Intent(User.this, UserInfo.class);
			UserGroupTab.getInstance().switchActivity("UserInfo", intent2, -1,
					-1);

			break;
		case R.id.userinfo_edits2:
			Intent intent1 = new Intent(User.this, UserInfo.class);
			UserGroupTab.getInstance().switchActivity("UserInfo", intent1, -1,
					-1);

			break;
		case R.id.userinfo_back:

			Intent intent = new Intent(User.this, My.class);
			UserGroupTab.getInstance().switchActivity("My", intent, -1, -1);
			break;
		}
	}

	private void upload_image_file(String image_file) {
		// TODO Auto-generated method stub

		// 利用Handler更新UI
		final Handler Discover = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 0x123) {
					pDialog.dismiss();
					String backmsg = msg.obj.toString();
					
					// RegularUtil.alert_msg(User.this, "加载失败");

				}
			}
		};

		pDialog = new ProgressDialog(this.getParent());
		pDialog.setMessage("上传服务器中。。。");
		pDialog.show();

		Map<String, String> params = new HashMap<String, String>();
		params.put("mid", Cms.APP.getMemberId());
		params.put("uploadimage", "1");

		Map<String, File> files = new HashMap<String, File>();
		files.put("avatar.jpg", new File(image_file));

		new Thread(new AccessNetwork("UPLOAD", WebApiUrl.CESHIURL, params,
				files, Discover)).start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// Log.i("aaaa", "------User-回来了-----------" + requestCode);
		switch (requestCode) {
		case IMAGE_REQUEST_CODE:
			startPhotoZoom(data.getData());
			break;
		case CAMERA_REQUEST_CODE:

			if (Tools.hasSdcard()) {
				Log.i("aaaa", "------返回相机 返回图片数据 去除剪切-----------");
				File tempFile = new File(FilePath.ROOT_DIRECTORY
						+ IMAGE_FILE_NAME);

				startPhotoZoom(Uri.fromFile(tempFile));
			} else {
				Toast.makeText(User.this, "未找到存储卡，无法存储照片！", Toast.LENGTH_LONG)
						.show();
			}

			break;
		case RESULT_REQUEST_CODE:
			if (data != null) {
				getImageToView(data);
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");

		intent.putExtra("output", Uri.fromFile(new File(FilePath.ROOT_DIRECTORY
				+ IMAGE_FILE_NAME))); // 专入目标文件
		intent.putExtra("outputFormat", "JPEG"); // 输入文件格式
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		UserGroupTab.getInstance().startActivityForResult(intent, 20);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();

		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			faceImage.setImageDrawable(drawable);
			upload_image_file(FilePath.ROOT_DIRECTORY + IMAGE_FILE_NAME);
		}
	}

	private void avatar_Views() {
		// TODO Auto-generated method stub
		final String[] arrayFruit = new String[] { "拍照", "从手机相册选择", "查看大图" };

		Dialog alertDialog = new AlertDialog.Builder(getParent())
				.setItems(arrayFruit, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Toast.makeText(Dialog_AlertDialogDemoActivity.this,
						// arrayFruit[which], Toast.LENGTH_SHORT).show();
						switch (which) {
						case 0:

							Intent intentFromCapture = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							// 判断存储卡是否可以用，可用进行存储
							if (Tools.hasSdcard()) {
								Log.i(TAG, "储存卡可用--------------");
								intentFromCapture.putExtra(
										MediaStore.EXTRA_OUTPUT,
										Uri.fromFile(new File(
												FilePath.ROOT_DIRECTORY,
												IMAGE_FILE_NAME)));

								UserGroupTab.getInstance()
										.startActivityForResult(
												intentFromCapture,
												CAMERA_REQUEST_CODE);
							} else {

								Toast.makeText(User.this, "未找到存储卡，无法存储照片！",
										Toast.LENGTH_LONG).show();
							}
							break;
						case 1:
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // 设置文件类型
							intentFromGallery
									.setAction(Intent.ACTION_GET_CONTENT);

							UserGroupTab.getInstance().startActivityForResult(
									intentFromGallery, IMAGE_REQUEST_CODE);

							break;
						case 2:		//查看大图
							
							break;
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				}).create();
		alertDialog.show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Log.i("aaaa", "后退总部user----------" );
			Intent bak_My_intent = new Intent(User.this, My.class);
			UserGroupTab.getInstance().switchActivity("My", bak_My_intent,
					-1, -1);
			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	

}
