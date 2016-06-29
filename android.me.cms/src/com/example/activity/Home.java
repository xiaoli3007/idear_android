package com.example.activity;

import android.util.Log;

import android.widget.TextView;

import com.example.cms.BaseFragment;
import com.example.cms.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.popupwindow.MyPopupWindowsBirthDayDate;
import com.example.popupwindow.MyPopupWindowsCity;
import com.example.popupwindow.MyPopupWindowsDate;
import com.example.popupwindow.MyPopupWindowsHomeSelectSingle;
import com.example.utils.JsonString;
import com.example.utils.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Home extends BaseFragment {

	private TextView home_single_select,home_double_select,home_three_select,home_select_birthday;
	//private  JSONArray home_single_select_data;
	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		View view = inflater.inflate(R.layout.home, null);

			this.home_single_select =(TextView) view.findViewById(R.id.home_single_select);
			this.home_double_select =(TextView) view.findViewById(R.id.home_double_select);
			this.home_three_select =(TextView) view.findViewById(R.id.home_three_select);
			this.home_select_birthday =(TextView) view.findViewById(R.id.home_select_birthday);

			this.home_single_select.setOnClickListener(this);
			this.home_double_select.setOnClickListener(this);
			this.home_three_select.setOnClickListener(this);
			this.home_select_birthday.setOnClickListener(this);

		return view;
	}

	public void onActivityCreated(Bundle sinha) {
		super.onActivityCreated(sinha);

	}

	@Override
	public void onClick(View v) {

		if (v == this.home_single_select) {

		//	Utils.hideKeyboard(getActivity()); 隐藏键盘
			try{
				JSONObject result = new JSONObject(JsonString.get_home_single_select());

				new MyPopupWindowsHomeSelectSingle(getActivity(),
						home_single_select, getActivity(), result.getJSONArray("wishgradeinfo"));
			} catch (JSONException e){
				e.printStackTrace();
			}

		}

		if (v == this.home_double_select) {

			try{
				JSONArray result = new JSONArray(Utils.getRegions());
				new MyPopupWindowsCity(getActivity(),
						home_double_select, getActivity(), result);
			} catch (JSONException e){
				e.printStackTrace();
			}

		}

		if (v == this.home_three_select) {

				new MyPopupWindowsDate(getActivity(),
						home_three_select, getActivity());
		}

		if (v == this.home_select_birthday) {

			new MyPopupWindowsBirthDayDate(getActivity(),
					home_select_birthday, getActivity(),new MyPopupWindowsBirthDayDate.OnSelectListener() {

				@Override
				public void OnSelect(String result, int type) {
					// TODO Auto-generated method stub
					Log.i("aaaa", "回调日期--------type看是阴历还是阳历---------" + result
							+ "----" + type);

					home_select_birthday.setText(result);

				}
			});
		}

	}







}