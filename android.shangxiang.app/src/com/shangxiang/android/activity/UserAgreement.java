package com.shangxiang.android.activity;

import com.shangxiang.android.R;
import com.shangxiang.android.utils.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class UserAgreement extends Activity implements OnClickListener {
	private RelativeLayout layoutLoading;
	private boolean showLoading = false;

	@Override
	public void onCreate(Bundle sinha) {
		super.onCreate(sinha);
		setContentView(R.layout.user_agreement);

		Button buttonBack = (Button) findViewById(R.id.user_agreement_title_back_button);
		buttonBack.setOnClickListener(this);

		this.layoutLoading = (RelativeLayout) findViewById(R.id.loading);
	}

	private void showLoading() {
		Utils.animLoading(this.layoutLoading, !this.showLoading);
		this.showLoading = !this.showLoading;
	}

	private void loadUserAgreement() {
		if (Utils.CheckNetwork()) {
			showLoading();
		} else {
			Utils.ShowToast(this, R.string.dialog_network_check_content);
		}
	}

	@Override
	public void onClick(View v) {
		int Id = v.getId();
		if (Id == R.id.user_agreement_title_back_button) {
			UserAgreement.this.finish();
			loadUserAgreement();
		}
	}
}