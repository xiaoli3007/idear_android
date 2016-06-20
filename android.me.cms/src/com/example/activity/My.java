package com.example.activity;

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
import android.widget.ImageView;
import android.widget.TextView;

public class My extends BaseFragment {
	private ImageView viewUserHead;
	private TextView viewUserLabel;
	private Button buttonUserNext;
	private Button buttonMenuTomeDiscover;
	private Button buttonMenuTootherDiscover;
	private Button buttonMenuOrderRecord;
	private Button buttonMenuNotice;
	private Button buttonMenuSettings;
	private Button buttonMenuFeedback;
	private Button buttonMenuAbout;

	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		View view = inflater.inflate(R.layout.my, null);

		this.viewUserHead = (ImageView) view.findViewById(R.id.user_avatar);
		this.viewUserLabel = (TextView) view.findViewById(R.id.my_user_label_text);
		this.buttonUserNext = (Button) view.findViewById(R.id.my_user_next_button);
		this.buttonUserNext.setOnClickListener(this);
		this.buttonMenuTomeDiscover = (Button) view.findViewById(R.id.my_user_discover_total_tome_button);
		this.buttonMenuTomeDiscover.setOnClickListener(this);
		this.buttonMenuTootherDiscover = (Button) view.findViewById(R.id.my_user_discover_total_toother_button);
		this.buttonMenuTootherDiscover.setOnClickListener(this);
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

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		this.viewUserHead.setImageResource(R.drawable.avatar_null);
		String name = getString(R.string.my_user_need_login);
		if (!TextUtils.isEmpty(Cms.memberInfo.optString("truename", ""))) {
			name = Cms.memberInfo.optString("truename", "");
		} else if (!TextUtils.isEmpty(Cms.memberInfo.optString("nickname", ""))) {
			name = Cms.memberInfo.optString("nickname", "");
		} else if (!TextUtils.isEmpty(Cms.memberInfo.optString("mobile", ""))) {
			name = Cms.memberInfo.optString("mobile", "");
		} else if (!TextUtils.isEmpty(Cms.memberInfo.optString("memberid", ""))) {
			name = "用户" + Cms.memberInfo.optString("memberid", "");
		}
		this.viewUserLabel.setText(name);
	}

	@Override
	public void onClick(View v) {
		if (v == this.buttonUserNext) {
			if (Cms.APP.getLogin()) {
				goFragment(new User());
			} else {
				goActivity(Login.class);
			}
		}
		if (v == this.buttonMenuTomeDiscover || v == this.buttonMenuTootherDiscover) {
			goFragment(new MyDiscover());
		}
	
	
		if (v == this.buttonMenuAbout) {
			Bundle bundle = new Bundle();
			bundle.putString("title", getActivity().getString(R.string.about_title_text));
			bundle.putString("url", "http://www.shangxiang.com");
			//goFragment(new Browser(), bundle);
		}
	}
}