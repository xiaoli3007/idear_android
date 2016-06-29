package com.example.activity;

import com.example.utils.Utils;
import com.example.cms.BaseFragment;
import com.example.cms.R;
import com.example.cms.Cms;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class User extends BaseFragment {
	private Button buttonBack;
	private Button buttonLogout;
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

	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		View view = inflater.inflate(R.layout.user, null);

		this.buttonBack = (Button) view.findViewById(R.id.user_title_back_button);
		this.buttonBack.setOnClickListener(this);
		this.buttonLogout = (Button) view.findViewById(R.id.user_title_logout_button);
		this.buttonLogout.setOnClickListener(this);
		this.buttonMenuAvatar = (Button) view.findViewById(R.id.user_menu_avatar_button);
		this.buttonMenuAvatar.setOnClickListener(this);
		this.buttonMenuMobile = (Button) view.findViewById(R.id.user_menu_mobile_button);
		this.buttonMenuMobile.setOnClickListener(this);
		this.viewMenuMobile = (TextView) view.findViewById(R.id.user_menu_mobile_text);
		this.buttonMenuResetPassword = (Button) view.findViewById(R.id.user_menu_reset_password_button);
		this.buttonMenuResetPassword.setOnClickListener(this);
		this.buttonMenuRealname = (Button) view.findViewById(R.id.user_menu_realname_button);
		this.buttonMenuRealname.setOnClickListener(this);
		this.viewMenuRealname = (TextView) view.findViewById(R.id.user_menu_realname_text);
		this.buttonMenuRegion = (Button) view.findViewById(R.id.user_menu_region_button);
		this.buttonMenuRegion.setOnClickListener(this);
		this.viewMenuRegion = (TextView) view.findViewById(R.id.user_menu_region_text);
		this.buttonMenuSex = (Button) view.findViewById(R.id.user_menu_sex_button);
		this.buttonMenuSex.setOnClickListener(this);
		this.viewMenuSex = (TextView) view.findViewById(R.id.user_menu_sex_text);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!TextUtils.isEmpty(Cms.APP.getMobile())) {
			this.viewMenuMobile.setText(Cms.APP.getMobile());
		} else {
			this.viewMenuMobile.setText(R.string.other_login);
		}
		this.viewMenuRealname.setText(Cms.memberInfo.optString("truename", ""));
		this.viewMenuRegion.setText(Cms.memberInfo.optString("area", ""));
		String strSex = "";
		if (1 == Cms.memberInfo.optInt("sex", 0)) {
			strSex = getString(R.string.sex_male);
		} else if (2 == Cms.memberInfo.optInt("sex", 0)) {
			strSex = getString(R.string.sex_female);
		}
		this.viewMenuSex.setText(strSex);
	}

	@Override
	public void onClick(View v) {
		if (v == this.buttonBack) {
			getActivity().onBackPressed();
		}
		if (v == this.buttonLogout) {
			Utils.Dialog(getActivity(), R.string.dialog_logout_tip, R.string.dialog_logout_content, new Utils.Callback() {
				@Override
				public void callFinished() {
					Cms.APP.Logout();
					getActivity().onBackPressed();
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
		if (v == this.buttonMenuAvatar) {
			//goActivity(PickPhoto.class);
		}
		if (v == this.buttonMenuMobile) {
		}
		if (v == this.buttonMenuResetPassword) {
			if (!TextUtils.isEmpty(Cms.APP.getMobile())) {
				goFragment(new ResetPassword());
			} else {
			//	Utils.Dialog(getActivity(), getString(R.string.dialog_reset_password_tip), getString(R.string.dialog_reset_password_other));
			}
		}
		if (v == this.buttonMenuRealname || v == this.buttonMenuRegion || v == this.buttonMenuSex) {
			goFragment(new ModifyInfo());
		}
	}
}