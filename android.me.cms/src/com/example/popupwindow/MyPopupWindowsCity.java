package com.example.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.cms.R;
import com.example.spinnerwheel.AbstractWheel;
import com.example.spinnerwheel.AbstractWheelTextAdapter;
import com.example.spinnerwheel.OnWheelChangedListener;
import com.example.spinnerwheel.OnWheelScrollListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyPopupWindowsCity extends PopupWindow {

	private PopupWindow popupwindow;
	private Button orderform_confirm_city;
	private boolean scrolling = false;

	public MyPopupWindowsCity(Context mContext, final View parent,
							  Activity activity, final JSONArray data) {

		final Activity pactivity = activity;
		final Context mContexts = mContext;

		View customView = View.inflate(mContext,
				R.layout.home_select_double, null);
		orderform_confirm_city = (Button) customView
				.findViewById(R.id.orderform_confirm_city);

		// 插件滚动开始-------------------------------------------------------------------------------------------------
		final AbstractWheel locationCitySelect = (AbstractWheel) customView
				.findViewById(R.id.orderform_city_city_view);
		LocationCityAdapter locationCityAdapter = new LocationCityAdapter(
				mContext);
		try {
			locationCityAdapter.locations_city = new JSONArray(
					"[{\"name\":\"请选择\"}]");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		locationCitySelect.setViewAdapter(locationCityAdapter);
		// locationCitySelect.setCyclic(true);

		final AbstractWheel locationProvSelect = (AbstractWheel) customView
				.findViewById(R.id.orderform_city_prov_view);
		final LocationProvAdapter locationProvAdapter = new LocationProvAdapter(
				mContext);
		locationProvAdapter.locations = data;
		locationProvSelect.setViewAdapter(locationProvAdapter);

		locationProvSelect.addScrollingListener(new OnWheelScrollListener() {
			public void onScrollingStarted(AbstractWheel wheel) {
				scrolling = true;
			}

			public void onScrollingFinished(AbstractWheel wheel) {
				scrolling = false;
				LocationCityAdapter locationCityAdapter = new LocationCityAdapter(
						mContexts);
				locationCityAdapter.locations_city = locationProvAdapter.locations
						.optJSONObject(locationProvSelect.getCurrentItem())
						.optJSONArray("sub");
				locationCitySelect.setViewAdapter(locationCityAdapter);
				locationCitySelect.setCurrentItem(0);
			}
		});

		locationProvSelect.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(AbstractWheel wheel, int oldValue,
					int newValue) {
				if (!scrolling) {
					LocationCityAdapter locationCityAdapter = new LocationCityAdapter(
							mContexts);
					locationCityAdapter.locations_city = locationProvAdapter.locations
							.optJSONObject(newValue).optJSONArray("sub");
					locationCitySelect.setViewAdapter(locationCityAdapter);
					locationCitySelect.setCurrentItem(0);
				}
			}
		});
		locationProvSelect.setCyclic(true);

		locationProvSelect.setCurrentItem(1);
		// 插件滚动结束-------------------------------------------------------------------------------------------------

		popupwindow = new PopupWindow(customView);

		// 以下为弹窗后面的背景色设置
		ColorDrawable cd = new ColorDrawable(0x000000);
		popupwindow.setBackgroundDrawable(cd);
		// 产生背景变暗效果
		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
		lp.alpha = 0.7f;
		activity.getWindow().setAttributes(lp);

		popupwindow.setWidth(LayoutParams.FILL_PARENT);
		popupwindow.setHeight(LayoutParams.FILL_PARENT);
		popupwindow.setBackgroundDrawable(new BitmapDrawable());
		popupwindow.setFocusable(true);
		popupwindow.setOutsideTouchable(true);
		popupwindow.setContentView(customView);

		int[] location = new int[2];
		parent.getLocationOnScreen(location);

//		Log.i("aaaa", "-----1" + location[0]);
//		Log.i("aaaa", "-----2" + location[1]);
//		Log.i("aaaa", "-----3" + popupwindow.getHeight());
		// popupwindow.showAtLocation(parent, Gravity.NO_GRAVITY, location[0],
		// location[1]-popupwindow.getHeight());//显示在button的上面
		popupwindow.showAsDropDown(parent); // 显示在button的下面

		// 自定义view添加触摸事件
		popupwindow.update();
		popupwindow.setOnDismissListener(new OnDismissListener() { // 恢复背景色

					public void onDismiss() {
						// TODO Auto-generated method stub
						WindowManager.LayoutParams lp = pactivity.getWindow()
								.getAttributes();
						lp.alpha = 1f;
						pactivity.getWindow().setAttributes(lp);
					}
				});

		orderform_confirm_city.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub


				JSONObject prov =data.optJSONObject(locationProvSelect.getCurrentItem());

				JSONArray city=	prov.optJSONArray("sub");
				JSONObject object =city.optJSONObject(locationCitySelect.getCurrentItem());

				String address=prov.optString("name", "")+"-"+object.optString("name", "");

				TextView list_find_zany = (TextView) parent;
				list_find_zany.setText(address);
				popupwindow.dismiss();
			}
		});

		customView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (popupwindow != null && popupwindow.isShowing()) {
					popupwindow.dismiss();
					popupwindow = null;
				}

				return false;
			}
		});
	}

	private class LocationProvAdapter extends AbstractWheelTextAdapter {
		JSONArray locations = new JSONArray();

		protected LocationProvAdapter(Context context) {
			super(context, R.layout.select_custom_text, NO_RESOURCE);
		}

		@Override
		public int getItemsCount() {
			return this.locations.length();
		}

		@Override
		protected CharSequence getItemText(int index) {
			return "";
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			JSONObject prov = this.locations.optJSONObject(index);
			TextView provView = (TextView) view
					.findViewById(R.id.select_custom_text);
			provView.setText(prov.optString("name", ""));
			return view;
		}
	}

	private class LocationCityAdapter extends AbstractWheelTextAdapter {
		JSONArray locations_city = new JSONArray();

		protected LocationCityAdapter(Context context) {
			super(context, R.layout.select_custom_text, NO_RESOURCE);
		}

		@Override
		public int getItemsCount() {
			return this.locations_city.length();
		}

		@Override
		protected CharSequence getItemText(int index) {
			return "";
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			JSONObject city = this.locations_city.optJSONObject(index);
			TextView cityView = (TextView) view
					.findViewById(R.id.select_custom_text);
			cityView.setText(city.optString("name", ""));
			return view;
		}
	}

}
