package com.shangxiang.android.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;

import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.shangxiang.android.Consts;
import com.shangxiang.android.R;
import com.shangxiang.android.BaseFragment;
import com.shangxiang.android.ShangXiang;
import com.shangxiang.android.clipimage.ClipImage;
import com.shangxiang.android.imageviewer.ImageViewer;
import com.shangxiang.android.pipe.SinhaMultiPartEntity;
import com.shangxiang.android.pipe.SinhaPipeClient;
import com.shangxiang.android.pipe.SinhaPipeMethod;
import com.shangxiang.android.utils.Utils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyInfo extends BaseFragment {
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private RelativeLayout layoutLoading;
	private Button buttonBack;
	private ImageView viewAvatar;
	private ProgressBar viewAvatarLoading;
	private Button buttonMenuAvatar;
	private Button buttonMenuMobile;
	private TextView viewMenuMobile;
	private Button buttonMenuResetPassword;
	private Button buttonMenuRealname;
	private TextView viewMenuRealname;
	private Button buttonMenuRegion;
	private TextView viewMenuRegion;
	private Button buttonMenuSex;
	private TextView viewMenuSex;
	private RelativeLayout layoutSelectAvatar;
	private TextView buttonSelectAvatarCarema;
	private TextView buttonSelectAvatarThumb;
	private TextView buttonSelectAvatarShow;
	private TextView buttonSelectAvatarCancel;
	private boolean showLoading = false;
	private boolean isSubmiting = false;
	private Uri imageUri;

	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		View view = inflater.inflate(R.layout.my_info, null);

		this.httpClient = new SinhaPipeClient();
		this.imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "avatar.jpg"));

		this.layoutLoading = (RelativeLayout) view.findViewById(R.id.loading);
		this.buttonBack = (Button) view.findViewById(R.id.my_info_title_back_button);
		this.buttonBack.setOnClickListener(this);
		this.viewAvatar = (ImageView) view.findViewById(R.id.user_avatar);
		this.viewAvatarLoading = (ProgressBar) view.findViewById(R.id.my_info_menu_avatar_loading);
		this.buttonMenuAvatar = (Button) view.findViewById(R.id.my_info_menu_avatar_button);
		this.buttonMenuAvatar.setOnClickListener(this);
		this.buttonMenuMobile = (Button) view.findViewById(R.id.my_info_menu_mobile_button);
		this.buttonMenuMobile.setOnClickListener(this);
		this.viewMenuMobile = (TextView) view.findViewById(R.id.my_info_menu_mobile_text);
		this.buttonMenuResetPassword = (Button) view.findViewById(R.id.my_info_menu_reset_password_button);
		this.buttonMenuResetPassword.setOnClickListener(this);
		this.buttonMenuRealname = (Button) view.findViewById(R.id.my_info_menu_realname_button);
		this.buttonMenuRealname.setOnClickListener(this);
		this.viewMenuRealname = (TextView) view.findViewById(R.id.my_info_menu_realname_text);
		this.buttonMenuRegion = (Button) view.findViewById(R.id.my_info_menu_region_button);
		this.buttonMenuRegion.setOnClickListener(this);
		this.viewMenuRegion = (TextView) view.findViewById(R.id.my_info_menu_region_text);
		this.buttonMenuSex = (Button) view.findViewById(R.id.my_info_menu_sex_button);
		this.buttonMenuSex.setOnClickListener(this);
		this.viewMenuSex = (TextView) view.findViewById(R.id.my_info_menu_sex_text);
		this.layoutSelectAvatar = (RelativeLayout) view.findViewById(R.id.my_info_select_avatar_layout);
		this.layoutSelectAvatar.setOnClickListener(this);
		this.buttonSelectAvatarCarema = (TextView) view.findViewById(R.id.my_info_select_avatar_carema_button);
		this.buttonSelectAvatarCarema.setOnClickListener(this);
		this.buttonSelectAvatarThumb = (TextView) view.findViewById(R.id.my_info_select_avatar_thumb_button);
		this.buttonSelectAvatarThumb.setOnClickListener(this);
		this.buttonSelectAvatarShow = (TextView) view.findViewById(R.id.my_info_select_avatar_show_button);
		this.buttonSelectAvatarShow.setOnClickListener(this);
		this.buttonSelectAvatarCancel = (TextView) view.findViewById(R.id.my_info_select_avatar_cancel_text);
		this.buttonSelectAvatarCancel.setOnClickListener(this);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		ShangXiang.imageLoader.displayImage(ShangXiang.memberInfo.optString("tmb_headface", ""), this.viewAvatar, ShangXiang.avatarLoaderOptions, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				viewAvatarLoading.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				viewAvatarLoading.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				viewAvatarLoading.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				viewAvatarLoading.setVisibility(View.GONE);
			}
		});
		if (!TextUtils.isEmpty(ShangXiang.APP.getMobile())) {
			this.viewMenuMobile.setText(ShangXiang.APP.getMobile());
		} else {
			this.viewMenuMobile.setText(R.string.other_login);
		}
		this.viewMenuRealname.setText(ShangXiang.memberInfo.optString("truename", ""));
		this.viewMenuRegion.setText(ShangXiang.memberInfo.optString("area", ""));
		String strSex = "";
		if (1 == ShangXiang.memberInfo.optInt("sex", 0)) {
			strSex = getString(R.string.sex_male);
		} else if (2 == ShangXiang.memberInfo.optInt("sex", 0)) {
			strSex = getString(R.string.sex_female);
		}
		this.viewMenuSex.setText(strSex);
	}

	private void showLoading() {
		Utils.animLoading(this.layoutLoading, !this.showLoading);
		this.showLoading = !this.showLoading;
	}

	private void modifyAvatar(String path) {
		Intent intent = new Intent();
		intent.setClass(getActivity(), ClipImage.class);
		intent.putExtra("path", path);
		this.startActivityForResult(intent, 97);
	}

	private void uploadAvatar(Bitmap image) {
		if (Utils.CheckNetwork()) {
			this.isSubmiting = true;
			showLoading();

			SinhaMultiPartEntity entity = new SinhaMultiPartEntity(null);
			ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
			image.compress(CompressFormat.JPEG, 80, imageStream);
			ByteArrayBody byteArrayBody = new ByteArrayBody(imageStream.toByteArray(), "avatar.jpg");
			try {
				entity.addPart("mid", new StringBody(ShangXiang.APP.getMemberId()));
				entity.addPart("uploadimage", new StringBody("1"));
			} catch (UnsupportedEncodingException e) {
			}
			entity.addPart("uploadedfile", byteArrayBody);

			this.httpClient.Config("upload", Consts.URI_MODIFY_AVATAR, null, true, entity);
			this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
				public void CallFinished(String error, Object result) {
					showLoading();
					isSubmiting = false;
					if (null == error) {
						uploadAvatar((String) result);
					} else {
						int err = R.string.dialog_system_error_content;
						if (error == httpClient.ERR_TIME_OUT) {
							err = R.string.dialog_network_error_timeout;
						}
						if (error == httpClient.ERR_GET_ERR) {
							err = R.string.dialog_network_error_getdata;
						}
						Utils.ShowToast(getActivity(), err);
					}
				}
			});
			this.httpMethod.start();
		} else {
			Utils.ShowToast(getActivity(), R.string.dialog_network_check_content);
		}
	}

	private void uploadAvatar(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					ShangXiang.memberInfo.put("tmb_headface", result.optString("filepath", ""));
					ShangXiang.memberInfo.put("headface", result.optString("filepath", ""));
					ShangXiang.APP.setConfig(ShangXiang.memberInfo.toString());
					ShangXiang.imageLoader.displayImage(ShangXiang.memberInfo.optString("tmb_headface", ""), this.viewAvatar, ShangXiang.avatarLoaderOptions, new ImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							viewAvatarLoading.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
							viewAvatarLoading.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
							viewAvatarLoading.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingCancelled(String imageUri, View view) {
							viewAvatarLoading.setVisibility(View.GONE);
						}
					});
				} else {
					Utils.Dialog(getActivity(), getString(R.string.dialog_normal_title), result.optString("msg", ""));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("static-access")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 99:
			if (resultCode == getActivity().RESULT_OK) {
				modifyAvatar(this.imageUri.getPath());
			}
			break;
		case 98:
			if (resultCode == getActivity().RESULT_OK) {
				Uri uri = data.getData();
				Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
				if (cursor != null && cursor.moveToFirst()) {
					String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
					modifyAvatar(path);
				}
			}
			break;
		case 97:
			if (resultCode == getActivity().RESULT_OK) {
				Bitmap bitmap = BitmapFactory.decodeFile(this.imageUri.getPath());
				uploadAvatar(bitmap);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		if (v == this.buttonBack) {
			getActivity().onBackPressed();
		}
		if (v == this.buttonMenuMobile) {
		}
		if (v == this.buttonMenuResetPassword) {
			if (!TextUtils.isEmpty(ShangXiang.APP.getMobile())) {
				goFragment(new ResetPassword());
			} else {
				Utils.Dialog(getActivity(), getString(R.string.dialog_reset_password_tip), getString(R.string.dialog_reset_password_other));
			}
		}
		if (v == this.buttonMenuRealname || v == this.buttonMenuRegion || v == this.buttonMenuSex) {
			goFragment(new ModifyInfo());
		}
		if (v == this.buttonMenuAvatar || v == this.layoutSelectAvatar || v == this.buttonSelectAvatarCancel) {
			if (View.VISIBLE == this.layoutSelectAvatar.getVisibility()) {
				this.layoutSelectAvatar.setVisibility(View.GONE);
			} else {
				this.layoutSelectAvatar.setVisibility(View.VISIBLE);
			}
		}
		if (v == this.buttonSelectAvatarCarema) {
			if (this.isSubmiting) {
				Utils.Dialog(getActivity(), getString(R.string.dialog_tip), getString(R.string.dialog_submiting_content));
			} else {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, this.imageUri);
				this.startActivityForResult(intent, 99);
			}
			this.buttonMenuAvatar.performClick();
		}
		if (v == this.buttonSelectAvatarThumb) {
			if (this.isSubmiting) {
				Utils.Dialog(getActivity(), getString(R.string.dialog_tip), getString(R.string.dialog_submiting_content));
			} else {
				Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				this.startActivityForResult(intent, 98);
			}
			this.buttonMenuAvatar.performClick();
		}
		if (v == this.buttonSelectAvatarShow) {
			Bundle bundle = new Bundle();
			bundle.putString("from", "network");
			bundle.putString("image_path", ShangXiang.memberInfo.optString("headface", ""));
			goActivity(ImageViewer.class, bundle);
			this.buttonMenuAvatar.performClick();
		}
	}
}