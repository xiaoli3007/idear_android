package com.example.activity;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.cms.BaseFragment;
import com.example.cms.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.popupwindow.MyPopupWindowsHomeSelectSingle;
import com.example.utils.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Home extends BaseFragment {

	private EditText home_single_select,home_double_select,home_three_select;
	//private  JSONArray home_single_select_data;
	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		View view = inflater.inflate(R.layout.home, null);

			this.home_single_select =(EditText) view.findViewById(R.id.home_single_select);
			this.home_double_select =(EditText) view.findViewById(R.id.home_double_select);
			this.home_three_select =(EditText) view.findViewById(R.id.home_three_select);

		this.home_single_select.setOnClickListener(this);

		return view;
	}

	public void onActivityCreated(Bundle sinha) {
		super.onActivityCreated(sinha);

	}

	@Override
	public void onClick(View v) {

		if (v == this.home_single_select) {

			Utils.hideKeyboard(getActivity());
			try{
				JSONObject result = new JSONObject("{\"code\":\"succeed\",\"msg\":\"\\u8bfb\\u53d6\\u6210\\u529f\\uff01\",\"wishgradeinfo\":[{\"val\":1,\"name\":\"\\u6e05\\u9999\",\"price\":19,\"desc\":\"\\u4e00\\u822c\\u7684\\u7948\\u798f\\u796d\\u62dc\\u4f7f\\u7528\"},{\"val\":2,\"name\":\"\\u9ad8\\u9999\",\"price\":99,\"desc\":\"\\u4f53\\u79ef\\u8f83\\u5927\\u7528\\u6599\\u66f4\\u4f73\\u7684\\u9999\"},{\"val\":3,\"name\":\"\\u5f00\\u5149\\u9999\",\"price\":198,\"desc\":\"\\u7ecf\\u7531\\u9ad8\\u50e7\\u5f00\\u5149\\u52a0\\u6301\\u7684\\u9ad8\\u9999\"}]}");
			//	home_single_select_data = result.getJSONArray("wishgradeinfo");

				new MyPopupWindowsHomeSelectSingle(getActivity(),
						home_single_select, getActivity(), result.getJSONArray("wishgradeinfo"));
			} catch (JSONException e){
				e.printStackTrace();
			}

		}

	}







}