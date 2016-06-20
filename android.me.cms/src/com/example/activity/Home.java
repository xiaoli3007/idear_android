package com.example.activity;

import com.example.cms.BaseFragment;
import com.example.cms.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class Home extends BaseFragment {

	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		View view = inflater.inflate(R.layout.home, null);


		return view;
	}

	public void onActivityCreated(Bundle sinha) {
		super.onActivityCreated(sinha);
	}

	@Override
	public void onClick(View v) {
		int desireType = 1;

		Bundle bundle = new Bundle();
		bundle.putInt("desire_type", desireType);
		//goFragment(new ListTemple(), bundle);
	}
}