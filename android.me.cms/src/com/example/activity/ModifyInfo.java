package com.example.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.pipe.SinhaPipeClient;
import com.example.pipe.SinhaPipeMethod;
import com.example.spinnerwheel.AbstractWheel;
import com.example.spinnerwheel.AbstractWheelTextAdapter;
import com.example.spinnerwheel.OnWheelChangedListener;
import com.example.spinnerwheel.OnWheelScrollListener;
import com.example.utils.Utils;
import com.example.cms.BaseFragment;
import com.example.cms.Consts;
import com.example.cms.R;
import com.example.cms.Cms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ModifyInfo extends BaseFragment {
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private RelativeLayout layoutLoading;
	private Button buttonBack;
	private Button buttonSubmit;
	private EditText inputRealname;
	private EditText inputRegion;
	private Button buttonRegion;
	private EditText inputSex;
	private Button buttonSex;
	private LinearLayout layoutSelectRegion;
	private Button buttonSelectRegionOK;
	private Button buttonSelectRegionCancel;
	private RelativeLayout layoutSelectSex;
	private TextView buttonSelectSexMale;
	private TextView buttonSelectSexFemale;
	private TextView buttonSelectSexCancel;
	private boolean showLoading = false;
	private boolean isSubmiting = false;
	private boolean scrolling = false;
	private int sex = 0;

	private AbstractWheel selectRegionProv;
	private RegionProvAdapter regionProvAdapter;
	private AbstractWheel selectRegionCity;
	private RegionCityAdapter regionCityAdapter;
	private JSONArray jsonRegions;

	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		View view = inflater.inflate(R.layout.modify_info, null);

		this.httpClient = new SinhaPipeClient();

		this.buttonBack = (Button) view.findViewById(R.id.modify_info_title_back_button);
		this.buttonBack.setOnClickListener(this);
		this.layoutLoading = (RelativeLayout) view.findViewById(R.id.loading);
		this.inputRealname = (EditText) view.findViewById(R.id.modify_info_realname_input);
		this.inputRealname.setText(Cms.memberInfo.optString("truename", ""));
		this.inputRegion = (EditText) view.findViewById(R.id.modify_info_region_input);
		this.inputRegion.setText(Cms.memberInfo.optString("area", ""));
		this.buttonRegion = (Button) view.findViewById(R.id.modify_info_region_button);
		this.buttonRegion.setOnClickListener(this);
		this.inputSex = (EditText) view.findViewById(R.id.modify_info_sex_input);
		String strSex = "";
		if (1 == Cms.memberInfo.optInt("sex", 0)) {
			this.sex = 1;
			strSex = getString(R.string.sex_male);
		} else if (2 == Cms.memberInfo.optInt("sex", 0)) {
			this.sex = 2;
			strSex = getString(R.string.sex_female);
		}
		this.inputSex.setText(strSex);
		this.buttonSex = (Button) view.findViewById(R.id.modify_info_sex_button);
		this.buttonSex.setOnClickListener(this);
		this.buttonSubmit = (Button) view.findViewById(R.id.modify_info_submit_button);
		this.buttonSubmit.setOnClickListener(this);
		this.layoutSelectRegion = (LinearLayout) view.findViewById(R.id.modify_info_select_region_layout);
		this.layoutSelectRegion.setOnClickListener(this);
		this.buttonSelectRegionOK = (Button) view.findViewById(R.id.modify_info_select_region_ok_button);
		this.buttonSelectRegionOK.setOnClickListener(this);
		this.buttonSelectRegionCancel = (Button) view.findViewById(R.id.modify_info_select_region_cancel_button);
		this.buttonSelectRegionCancel.setOnClickListener(this);
		this.layoutSelectSex = (RelativeLayout) view.findViewById(R.id.modify_info_select_sex_layout);
		this.layoutSelectSex.setOnClickListener(this);
		this.buttonSelectSexMale = (TextView) view.findViewById(R.id.modify_info_select_sex_male_text);
		this.buttonSelectSexMale.setOnClickListener(this);
		this.buttonSelectSexFemale = (TextView) view.findViewById(R.id.modify_info_select_sex_female_text);
		this.buttonSelectSexFemale.setOnClickListener(this);
		this.buttonSelectSexCancel = (TextView) view.findViewById(R.id.modify_info_select_sex_cancel_text);
		this.buttonSelectSexCancel.setOnClickListener(this);

		try {
			this.jsonRegions = new JSONArray(Utils.getRegions());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return view;
	}

	public void onActivityCreated(Bundle sinha) {
		super.onActivityCreated(sinha);

		this.selectRegionCity = (AbstractWheel) getActivity().findViewById(R.id.modify_info_select_region_city_view);
		this.regionCityAdapter = new RegionCityAdapter(getActivity());
		try {
			this.regionCityAdapter.regions = new JSONArray("[{\"name\":\"请选择\"}]");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.selectRegionCity.setViewAdapter(this.regionCityAdapter);

		this.selectRegionProv = (AbstractWheel) getActivity().findViewById(R.id.modify_info_select_region_prov_view);
		this.regionProvAdapter = new RegionProvAdapter(getActivity());
		this.regionProvAdapter.regions = this.jsonRegions;
		this.selectRegionProv.setViewAdapter(this.regionProvAdapter);
		this.selectRegionProv.addScrollingListener(new OnWheelScrollListener() {
			public void onScrollingStarted(AbstractWheel wheel) {
				scrolling = true;
			}

			public void onScrollingFinished(AbstractWheel wheel) {
				scrolling = false;
				regionCityAdapter = new RegionCityAdapter(getActivity());
				regionCityAdapter.regions = regionProvAdapter.regions.optJSONObject(selectRegionProv.getCurrentItem()).optJSONArray("sub");
				selectRegionCity.setViewAdapter(regionCityAdapter);
			}
		});
		this.selectRegionProv.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
				if (!scrolling) {
					regionCityAdapter = new RegionCityAdapter(getActivity());
					regionCityAdapter.regions = regionProvAdapter.regions.optJSONObject(newValue).optJSONArray("sub");
					selectRegionCity.setViewAdapter(regionCityAdapter);
				}
			}
		});
	}

	private void showLoading() {
		Utils.animView(this.layoutLoading, !this.showLoading);
		this.showLoading = !this.showLoading;
	}

	private class RegionProvAdapter extends AbstractWheelTextAdapter {
		JSONArray regions = new JSONArray();

		protected RegionProvAdapter(Context context) {
			super(context, R.layout.select_custom_text, NO_RESOURCE);
		}

		@Override
		public int getItemsCount() {
			return this.regions.length();
		}

		@Override
		protected CharSequence getItemText(int index) {
			return "";
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			JSONObject prov = this.regions.optJSONObject(index);
			TextView provView = (TextView) view.findViewById(R.id.select_custom_text);
			provView.setText(prov.optString("name", ""));
			return view;
		}
	}

	private class RegionCityAdapter extends AbstractWheelTextAdapter {
		JSONArray regions = new JSONArray();

		protected RegionCityAdapter(Context context) {
			super(context, R.layout.select_custom_text, NO_RESOURCE);
		}

		@Override
		public int getItemsCount() {
			return this.regions.length();
		}

		@Override
		protected CharSequence getItemText(int index) {
			return "";
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			JSONObject city = this.regions.optJSONObject(index);
			TextView cityView = (TextView) view.findViewById(R.id.select_custom_text);
			cityView.setText(city.optString("name", ""));
			return view;
		}
	}

	private void submitModifyInfo() {
//		if (Utils.CheckNetwork()) {
//			this.isSubmiting = true;
//			showLoading();
//
//			List<NameValuePair> params = new ArrayList<NameValuePair>();
//			params.add(new BasicNameValuePair("mid", Cms.APP.getMemberId()));
//			params.add(new BasicNameValuePair("truename", this.inputRealname.getText().toString()));
//			params.add(new BasicNameValuePair("area", this.inputRegion.getText().toString()));
//			params.add(new BasicNameValuePair("sex", String.valueOf(this.sex)));
//
//			this.httpClient.Config("post", Consts.URI_MODIFY_MEMBERINFO, params, true);
//			this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
//				public void CallFinished(String error, Object result) {
//					showLoading();
//					isSubmiting = false;
//					if (null == error) {
//						submitModifyInfo((String) result);
//					} else {
//						int err = R.string.dialog_system_error_content;
//						if (error == httpClient.ERR_TIME_OUT) {
//							err = R.string.dialog_network_error_timeout;
//						}
//						if (error == httpClient.ERR_GET_ERR) {
//							err = R.string.dialog_network_error_getdata;
//						}
//						Utils.ShowToast(getActivity(), err);
//					}
//				}
//			});
//			this.httpMethod.start();
//		} else {
//			Utils.ShowToast(getActivity(), R.string.dialog_network_check_content);
//		}
	}

	private void submitModifyInfo(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					Cms.memberInfo.put("truename", this.inputRealname.getText().toString());
					Cms.memberInfo.put("area", this.inputRegion.getText().toString());
					Cms.memberInfo.put("sex", this.sex);
					Cms.APP.setConfig(Cms.memberInfo.toString());
					getActivity().onBackPressed();
				} else {
					Utils.Dialog(getActivity(), getString(R.string.dialog_normal_title), result.optString("msg", ""));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClick(View v) {
		Utils.hideKeyboard(getActivity());
		if (v == this.buttonBack) {
			getActivity().onBackPressed();
		}
		if (v == this.buttonSubmit) {
		}
		if (v == this.buttonRegion) {
			this.layoutSelectRegion.setVisibility(View.VISIBLE);
		}
		if (v == this.buttonSelectRegionOK) {
			String strRegion = "";
			if (0 != this.selectRegionProv.getCurrentItem() && 0 != this.selectRegionCity.getCurrentItem()) {
				JSONObject jsonRegionProv = this.jsonRegions.optJSONObject(this.selectRegionProv.getCurrentItem());
				if (null != jsonRegionProv) {
					strRegion = jsonRegionProv.optString("name", "");
					JSONArray jsonRegionCities = jsonRegionProv.optJSONArray("sub");
					if (null != jsonRegionProv) {
						JSONObject jsonRegionCity = jsonRegionCities.optJSONObject(this.selectRegionCity.getCurrentItem());
						if (null != jsonRegionCity) {
							strRegion += "-" + jsonRegionCity.optString("name", "");
						}
					}
				}
			}
			this.inputRegion.setText(strRegion);
		}
		if (v == this.layoutSelectRegion || v == this.buttonSelectRegionOK || v == this.buttonSelectRegionCancel) {
			this.layoutSelectRegion.setVisibility(View.GONE);
		}
		if (v == this.buttonSex) {
			this.layoutSelectSex.setVisibility(View.VISIBLE);
		}
		if (v == this.buttonSelectSexMale) {
			this.sex = 1;
			this.inputSex.setText(R.string.sex_male);
		}
		if (v == this.buttonSelectSexFemale) {
			this.sex = 2;
			this.inputSex.setText(R.string.sex_female);
		}
		if (v == this.layoutSelectSex || v == this.buttonSelectSexMale || v == this.buttonSelectSexFemale || v == this.buttonSelectSexCancel) {
			this.layoutSelectSex.setVisibility(View.GONE);
		}
		if (v == this.buttonSubmit) {
			if (this.isSubmiting) {
				Utils.Dialog(getActivity(), getString(R.string.dialog_tip), getString(R.string.dialog_submiting_content));
			} else {
				submitModifyInfo();
			}
		}
	}
}