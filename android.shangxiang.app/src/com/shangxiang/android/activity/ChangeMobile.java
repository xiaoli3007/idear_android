package com.shangxiang.android.activity;

import com.shangxiang.android.R;
import com.shangxiang.android.BaseFragment;
import com.shangxiang.android.utils.Utils;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class ChangeMobile extends BaseFragment implements OnTouchListener {
	private RelativeLayout layoutLoading;
	private Button buttonBack;
	private ScrollView viewMain;
	private LinearLayout layoutForm;
	private EditText inputMobile;
	private EditText inputVerfyCode;
	private Button buttonSendVerfyCode;
	private Button buttonNext;
	private LinearLayout layoutNewMobile;
	private EditText inputNewMobile;
	private EditText inputNewVerfyCode;
	private Button buttonSendNewVerfyCode;
	private Button buttonSubmit;
	private boolean showLoading = false;
	private boolean isSubmiting = false;

	private SendVerfyCodeTimer countTimer = new SendVerfyCodeTimer(60000, 1000);

	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		View view = inflater.inflate(R.layout.change_mobile, null);

		this.layoutLoading = (RelativeLayout) view.findViewById(R.id.loading);
		this.buttonBack = (Button) view.findViewById(R.id.change_mobile_title_back_button);
		this.buttonBack.setOnClickListener(this);
		this.viewMain = (ScrollView) view.findViewById(R.id.change_mobile_main_layout);
		this.viewMain.setOnTouchListener(this);
		this.layoutForm = (LinearLayout) view.findViewById(R.id.change_mobile_form_layout);
		this.inputMobile = (EditText) view.findViewById(R.id.change_mobile_mobile_input);
		this.inputVerfyCode = (EditText) view.findViewById(R.id.change_mobile_verfycode_input);
		this.buttonSendVerfyCode = (Button) view.findViewById(R.id.change_mobile_send_verfycode_button);
		this.buttonSendVerfyCode.setOnClickListener(this);
		this.buttonNext = (Button) view.findViewById(R.id.change_mobile_next_button);
		this.buttonNext.setOnClickListener(this);
		this.layoutNewMobile = (LinearLayout) view.findViewById(R.id.change_mobile_new_mobile_layout);
		this.inputNewMobile = (EditText) view.findViewById(R.id.change_mobile_new_mobile_input);
		this.inputNewVerfyCode = (EditText) view.findViewById(R.id.change_mobile_new_verfycode_input);
		this.buttonSendNewVerfyCode = (Button) view.findViewById(R.id.change_mobile_send_new_verfycode_button);
		this.buttonSendNewVerfyCode.setOnClickListener(this);
		this.buttonSubmit = (Button) view.findViewById(R.id.change_mobile_submit_button);
		this.buttonSubmit.setOnClickListener(this);

		return view;
	}

	private void showLoading() {
		Utils.animLoading(this.layoutLoading, !this.showLoading);
		this.showLoading = !this.showLoading;
	}

	class SendVerfyCodeTimer extends CountDownTimer {
		public SendVerfyCodeTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			buttonSendVerfyCode.setClickable(true);
			buttonSendVerfyCode.setText(R.string.send_verfycode);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			buttonSendVerfyCode.setClickable(false);
			buttonSendVerfyCode.setText(millisUntilFinished / 1000 + getString(R.string.resend_verfycode));
		}
	}

	private void submitChangeMobile() {
		if (Utils.CheckNetwork()) {
			if (checkChangeMobile()) {
				this.isSubmiting = true;
				showLoading();
				this.isSubmiting = false;
			}
		} else {
			Utils.ShowToast(getActivity(), R.string.dialog_network_check_content);
		}
	}

	private boolean checkMobile() {
		boolean bolAlertPop = false;
		boolean bolCheckResult = true;
		Editable editableMobile = this.inputMobile.getText();
		if (!editableMobile.toString().matches(".{11,11}")) {
			bolAlertPop = true;
			bolCheckResult = false;
			this.inputMobile.requestFocus();
			Utils.Dialog(getActivity(), getString(R.string.dialog_form_check_title), getString(R.string.dialog_form_check_err_mobile));
		}
		Editable editableVerfyCode = this.inputVerfyCode.getText();
		if (!bolAlertPop && !editableVerfyCode.toString().matches(".{6,6}")) {
			bolAlertPop = true;
			bolCheckResult = false;
			this.inputVerfyCode.requestFocus();
			Utils.Dialog(getActivity(), getString(R.string.dialog_form_check_title), getString(R.string.dialog_form_check_err_verfycode));
		}
		return bolCheckResult;
	}

	private boolean checkChangeMobile() {
		boolean bolAlertPop = false;
		boolean bolCheckResult = true;
		Editable editableNewMobile = this.inputNewMobile.getText();
		if (!editableNewMobile.toString().matches(".{6,50}")) {
			bolAlertPop = true;
			bolCheckResult = false;
			this.inputNewMobile.requestFocus();
			Utils.Dialog(getActivity(), getString(R.string.dialog_form_check_title), getString(R.string.dialog_form_check_err_mobile));
		}
		Editable editableNewVerfyCode = this.inputNewVerfyCode.getText();
		if (!bolAlertPop && !editableNewVerfyCode.toString().matches(".{6,6}")) {
			bolAlertPop = true;
			bolCheckResult = false;
			this.inputNewVerfyCode.requestFocus();
			Utils.Dialog(getActivity(), getString(R.string.dialog_form_check_title), getString(R.string.dialog_form_check_err_verfycode));
		}
		return bolCheckResult;
	}

	@Override
	public void onClick(View v) {
		Utils.hideKeyboard(getActivity());
		if (v == this.buttonBack) {
			getActivity().onBackPressed();
		}
		if (v == this.buttonSendVerfyCode) {
			this.countTimer.start();
		}
		if (v == this.buttonNext) {
			this.countTimer.cancel();
			if (checkMobile()) {
				this.layoutForm.setVisibility(View.GONE);
				this.layoutNewMobile.setVisibility(View.VISIBLE);
			}
		}
		if (v == this.buttonSubmit) {
			this.countTimer.cancel();
			if (this.isSubmiting) {
				Utils.Dialog(getActivity(), getString(R.string.dialog_tip), getString(R.string.dialog_submiting_content));
			} else {
				submitChangeMobile();
			}
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		this.countTimer.cancel();
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		onClick(v);
		return false;
	}
}