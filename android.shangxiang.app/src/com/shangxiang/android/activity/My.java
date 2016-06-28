package com.shangxiang.android.activity;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.shangxiang.android.Consts;
import com.shangxiang.android.R;
import com.shangxiang.android.BaseFragment;
import com.shangxiang.android.ShangXiang;
import com.shangxiang.android.utils.Utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class My extends BaseFragment {
	private ImageView viewAvatar;
	private ProgressBar viewAvatarLoading;
	private TextView viewUserLabel;
	private Button buttonUserNext;
	private Button buttonMenuTomeDiscover;
	private Button buttonMenuTootherDiscover;
	private Button buttonMenuOrderRecord;
	private Button buttonMenuNotice;
	private Button buttonMenuSettings;
	private Button buttonMenuFeedback;
	private Button buttonMenuAbout;
	private LinearLayout layoutMenuLogout;
	private Button buttonMenuLogout;

	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		View view = inflater.inflate(R.layout.my, null);

		this.viewAvatar = (ImageView) view.findViewById(R.id.user_avatar);
		this.viewAvatarLoading = (ProgressBar) view.findViewById(R.id.user_avatar_loading);
		this.viewUserLabel = (TextView) view.findViewById(R.id.my_user_label_text);
		this.buttonUserNext = (Button) view.findViewById(R.id.my_user_next_button);
		this.buttonUserNext.setOnClickListener(this);
		this.buttonMenuTootherDiscover = (Button) view.findViewById(R.id.my_user_discover_total_toother_button);
		this.buttonMenuTootherDiscover.setOnClickListener(this);
		this.buttonMenuTomeDiscover = (Button) view.findViewById(R.id.my_user_discover_total_tome_button);
		this.buttonMenuTomeDiscover.setOnClickListener(this);
		this.buttonMenuOrderRecord = (Button) view.findViewById(R.id.my_menu_order_record_button);
		this.buttonMenuOrderRecord.setOnClickListener(this);
		this.buttonMenuNotice = (Button) view.findViewById(R.id.my_menu_notice_button);
		this.buttonMenuNotice.setOnClickListener(this);
		this.buttonMenuSettings = (Button) view.findViewById(R.id.my_menu_settings_button);
		this.buttonMenuSettings.setOnClickListener(this);
		this.buttonMenuFeedback = (Button) view.findViewById(R.id.my_menu_feedback_button);
		this.buttonMenuFeedback.setOnClickListener(this);
		this.buttonMenuAbout = (Button) view.findViewById(R.id.my_menu_about_button);
		this.buttonMenuAbout.setOnClickListener(this);
		this.layoutMenuLogout = (LinearLayout) view.findViewById(R.id.my_menu_logout_layout);
		this.buttonMenuLogout = (Button) view.findViewById(R.id.my_menu_logout_button);
		this.buttonMenuLogout.setOnClickListener(this);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		initUser();
	}

	private void initUser() {
		if (ShangXiang.APP.getLogin()) {
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
			String name = "";
			if (!TextUtils.isEmpty(ShangXiang.memberInfo.optString("truename", ""))) {
				name = ShangXiang.memberInfo.optString("truename", "");
			} else if (!TextUtils.isEmpty(ShangXiang.memberInfo.optString("nickname", ""))) {
				name = ShangXiang.memberInfo.optString("nickname", "");
			} else if (!TextUtils.isEmpty(ShangXiang.memberInfo.optString("mobile", ""))) {
				name = ShangXiang.memberInfo.optString("mobile", "");
			} else if (!TextUtils.isEmpty(ShangXiang.memberInfo.optString("memberid", ""))) {
				name = "用户" + ShangXiang.memberInfo.optString("memberid", "");
			}
			this.viewUserLabel.setText(name);
			this.buttonMenuTootherDiscover.setText(Html.fromHtml("我的加持 <font color=\"#dca358\">" + ShangXiang.memberInfo.optString("do_blessings", "") + "</font>"));
			this.buttonMenuTomeDiscover.setText(Html.fromHtml("为我加持 <font color=\"#dca358\">" + ShangXiang.memberInfo.optString("received_blessings", "") + "</font>"));
			this.layoutMenuLogout.setVisibility(View.VISIBLE);
		} else {
			this.viewAvatar.setImageResource(R.drawable.avatar_null);
			this.viewUserLabel.setText(R.string.my_user_need_login);
			this.buttonMenuTootherDiscover.setText(R.string.my_user_discover_total_toother_button);
			this.buttonMenuTomeDiscover.setText(R.string.my_user_discover_total_tome_button);
			this.layoutMenuLogout.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		if (v == this.buttonMenuLogout) {
			Utils.Dialog(getActivity(), R.string.dialog_logout_tip, R.string.dialog_logout_content, new Utils.Callback() {
				@Override
				public void callFinished() {
					ShangXiang.APP.Logout();
					initUser();
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
		}
		if (v == this.buttonUserNext || v == this.buttonMenuTootherDiscover || v == this.buttonMenuTomeDiscover || v == this.buttonMenuOrderRecord) {
			if (ShangXiang.APP.getLogin()) {
				if (v == this.buttonUserNext) {
					goFragment(new MyInfo());
				}
				if (v == this.buttonMenuTootherDiscover || v == this.buttonMenuTomeDiscover) {
					Bundle bundle = new Bundle();
					if (v == this.buttonMenuTomeDiscover) {
						bundle.putString("discover_type", "2");
					} else {
						bundle.putString("discover_type", "1");
					}
					goFragment(new MyDiscover(), bundle);
				}
				if (v == this.buttonMenuOrderRecord) {
					goFragment(new OrderRecord());
				}
			} else {
				goActivity(Login.class);
			}
		}
		if (v == this.buttonMenuNotice) {
			goFragment(new Notice());
		}
		if (v == this.buttonMenuSettings) {
			goFragment(new Settings());
		}
		if (v == this.buttonMenuFeedback) {
			goFragment(new Feedback());
		}
		if (v == this.buttonMenuAbout) {
			Bundle bundle = new Bundle();
			bundle.putString("title", getActivity().getString(R.string.about_title_text));
			bundle.putString("url", Consts.URI_ABOUT);
			goFragment(new Browser(), bundle);
		}
	}
}