package com.shangxiang.android.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.shangxiang.android.Consts;
import com.shangxiang.android.R;
import com.shangxiang.android.ShangXiang;
import com.shangxiang.android.pipe.SinhaPipeClient;
import com.shangxiang.android.pipe.SinhaPipeMethod;
import com.shangxiang.android.spinnerwheel.AbstractWheel;
import com.shangxiang.android.spinnerwheel.AbstractWheelTextAdapter;
import com.shangxiang.android.spinnerwheel.OnWheelChangedListener;
import com.shangxiang.android.spinnerwheel.OnWheelScrollListener;
import com.shangxiang.android.utils.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class CreateOrder extends Activity implements OnClickListener, OnTouchListener {
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private RelativeLayout layoutLoading;
	private ScrollView viewMain;
	private Button buttonBack;
	private ImageButton viewHallThumb;
	private ProgressBar viewHallThumbLoading;
	private ImageButton viewBuddhistThumb;
	private ProgressBar viewBuddhistThumbLoading;
	private TextView viewHallName;
	private TextView viewBuddhistName;
	private Button viewDesireType;
	private RelativeLayout layoutDesireTitle;
	private RelativeLayout layoutRedeemTitle;
	private TextView viewRedeemTitle;
	private EditText viewDesirer;
	private EditText viewMobile;
	private Bundle bundle;
	private JSONObject temple = new JSONObject();

	private LinearLayout layoutMobile;
	private LinearLayout layoutSelectContent;
	private TextView buttonSelectContentShow;
	private Button buttonSelectContentCancel;
	private Button buttonSelectContentOK;
	private EditText viewDesireContent;
	private LinearLayout layoutSelectJSC;
	private TextView buttonSelectJSCShow;
	private Button buttonSelectJSCCancel;
	private Button buttonSelectJSCOK;
	private LinearLayout layoutSelectRegion;
	private LinearLayout layoutSelectRegionShow;
	private LinearLayout layoutSelectRegionShowMoved;
	private TextView buttonSelectRegionShow;
	private Button buttonSelectRegionCancel;
	private Button buttonSelectRegionOK;
	private LinearLayout layoutSelectDate;
	private TextView buttonSelectDateShow;
	private Button buttonSelectDateCancel;
	private Button buttonSelectDateOK;

	private AbstractWheel selectContent;
	private ContentAdapter contentAdapter;
	private String[] contentData = new String[] {};
	private AbstractWheel selectJSC;
	private JSCAdapter JSCAdapter;
	private JSONArray jsonJSCs;
	private AbstractWheel selectRegionProv;
	private RegionProvAdapter regionProvAdapter;
	private AbstractWheel selectRegionCity;
	private RegionCityAdapter regionCityAdapter;
	private JSONArray jsonRegions;
	private AbstractWheel selectDateYear;
	private DateYearAdapter dateYearAdapter;
	private AbstractWheel selectDateMonth;
	private DateMonthAdapter dateMonthAdapter;
	private AbstractWheel selectDateDay;
	private DateDayAdapter dateDayAdapter;
	private Calendar calendar;
	private SimpleDateFormat formatter;

	private Button buttonSubmit;

	private int orderType = 0;
	private int desireType = 0;
	private int daySelected = 0;
	private boolean showLoading = false;
	private boolean isSubmiting = false;
	private boolean scrolling = false;

	@SuppressLint({ "InflateParams", "SimpleDateFormat" })
	public void onCreate(Bundle sinha) {
		super.onCreate(sinha);
		setContentView(R.layout.create_order);

		this.bundle = getIntent().getExtras();
		this.httpClient = new SinhaPipeClient();

		this.layoutLoading = (RelativeLayout) findViewById(R.id.loading);
		this.viewMain = (ScrollView) findViewById(R.id.create_order_main_layout);
		this.viewMain.setOnTouchListener(this);
		this.buttonBack = (Button) findViewById(R.id.create_order_title_back_button);
		this.buttonBack.setOnClickListener(this);
		this.viewHallThumb = (ImageButton) findViewById(R.id.create_order_hall_thumb_button);
		this.viewHallThumbLoading = (ProgressBar) findViewById(R.id.create_order_hall_thumb_loading);
		this.viewBuddhistThumb = (ImageButton) findViewById(R.id.create_order_buddhist_thumb_button);
		this.viewBuddhistThumbLoading = (ProgressBar) findViewById(R.id.create_order_buddhist_thumb_loading);
		this.viewHallName = (TextView) findViewById(R.id.create_order_hall_name_text);
		this.viewBuddhistName = (TextView) findViewById(R.id.create_order_buddhist_name_text);
		this.viewDesireType = (Button) findViewById(R.id.create_order_desire_type_button);
		this.layoutDesireTitle = (RelativeLayout) findViewById(R.id.create_order_content_desire_title_layout);
		this.layoutRedeemTitle = (RelativeLayout) findViewById(R.id.create_order_content_redeem_title_layout);
		this.viewRedeemTitle = (TextView) findViewById(R.id.create_order_content_redeem_title_text);
		this.viewDesireContent = (EditText) findViewById(R.id.create_order_content_text);
		this.viewDesirer = (EditText) findViewById(R.id.create_order_other_desirer_text);
		this.layoutMobile = (LinearLayout) findViewById(R.id.create_order_other_mobile_layout);
		this.viewMobile = (EditText) findViewById(R.id.create_order_other_mobile_text);

		this.layoutSelectContent = (LinearLayout) findViewById(R.id.create_order_select_content_layout);
		this.layoutSelectContent.setOnClickListener(this);
		this.buttonSelectContentShow = (TextView) findViewById(R.id.create_order_select_content_show_button);
		this.buttonSelectContentShow.setOnClickListener(this);
		this.buttonSelectContentCancel = (Button) findViewById(R.id.create_order_select_content_cancel_button);
		this.buttonSelectContentCancel.setOnClickListener(this);
		this.buttonSelectContentOK = (Button) findViewById(R.id.create_order_select_content_ok_button);
		this.buttonSelectContentOK.setOnClickListener(this);

		this.layoutSelectJSC = (LinearLayout) findViewById(R.id.create_order_select_JSC_layout);
		this.layoutSelectJSC.setOnClickListener(this);
		this.buttonSelectJSCShow = (TextView) findViewById(R.id.create_order_select_JSC_show_button);
		this.buttonSelectJSCShow.setOnClickListener(this);
		this.buttonSelectJSCCancel = (Button) findViewById(R.id.create_order_select_JSC_cancel_button);
		this.buttonSelectJSCCancel.setOnClickListener(this);
		this.buttonSelectJSCOK = (Button) findViewById(R.id.create_order_select_JSC_ok_button);
		this.buttonSelectJSCOK.setOnClickListener(this);

		this.layoutSelectRegion = (LinearLayout) findViewById(R.id.create_order_select_region_layout);
		this.layoutSelectRegion.setOnClickListener(this);
		this.layoutSelectRegionShow = (LinearLayout) findViewById(R.id.create_order_select_region_show_layout);
		this.layoutSelectRegionShowMoved = (LinearLayout) findViewById(R.id.create_order_select_region_show_moved_layout);
		this.buttonSelectRegionCancel = (Button) findViewById(R.id.create_order_select_region_cancel_button);
		this.buttonSelectRegionCancel.setOnClickListener(this);
		this.buttonSelectRegionOK = (Button) findViewById(R.id.create_order_select_region_ok_button);
		this.buttonSelectRegionOK.setOnClickListener(this);

		this.layoutSelectDate = (LinearLayout) findViewById(R.id.create_order_select_date_layout);
		this.layoutSelectDate.setOnClickListener(this);
		this.buttonSelectDateShow = (TextView) findViewById(R.id.create_order_select_date_show_button);
		this.buttonSelectDateShow.setOnClickListener(this);
		this.buttonSelectDateCancel = (Button) findViewById(R.id.create_order_select_date_cancel_button);
		this.buttonSelectDateCancel.setOnClickListener(this);
		this.buttonSelectDateOK = (Button) findViewById(R.id.create_order_select_date_ok_button);
		this.buttonSelectDateOK.setOnClickListener(this);

		try {
			this.jsonRegions = new JSONArray(Utils.getRegions());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.formatter = new SimpleDateFormat("yyyy-MM-dd");
		this.calendar = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
		this.calendar.add(Calendar.DATE, 1);
		this.daySelected = this.calendar.get(Calendar.DAY_OF_MONTH) - 1;

		if (null != this.bundle) {
			this.orderType = this.bundle.getInt("order_type");
			if (1 == this.orderType) {
				this.layoutDesireTitle.setVisibility(View.GONE);
				this.layoutRedeemTitle.setVisibility(View.VISIBLE);
				this.layoutMobile.setVisibility(View.GONE);
				this.layoutSelectRegionShow.removeAllViews();
				this.layoutSelectRegionShowMoved.setVisibility(View.VISIBLE);
			} else {
				this.layoutDesireTitle.setVisibility(View.VISIBLE);
				this.layoutRedeemTitle.setVisibility(View.GONE);
				this.layoutMobile.setVisibility(View.VISIBLE);
				this.layoutSelectRegionShow.setVisibility(View.VISIBLE);
				this.layoutSelectRegionShowMoved.removeAllViews();
			}
			this.buttonSelectRegionShow = (TextView) findViewById(R.id.create_order_select_region_show_button);
			this.buttonSelectRegionShow.setOnClickListener(this);
			this.desireType = this.bundle.getInt("desire_type");
			this.viewDesireType.setText(ShangXiang.desireTypeName.get(this.bundle.getInt("desire_type")));
		}
		if (null != this.bundle && !TextUtils.isEmpty(this.bundle.getString("desire_content"))) {
			this.viewDesireContent.setText(this.bundle.getString("desire_content"));
		}
		if (null != this.bundle && !TextUtils.isEmpty(this.bundle.getString("temple"))) {
			try {
				this.temple = new JSONObject(bundle.getString("temple"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			ShangXiang.imageLoader.displayImage(this.temple.optString("pic_tmb_path", ""), this.viewHallThumb, ShangXiang.imageLoaderOptions, new ImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					viewHallThumbLoading.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					viewHallThumbLoading.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					viewHallThumbLoading.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					viewHallThumbLoading.setVisibility(View.GONE);
				}
			});
			ShangXiang.imageLoader.displayImage(this.temple.optString("tmb_headface", ""), this.viewBuddhistThumb, ShangXiang.imageLoaderOptions, new ImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					viewBuddhistThumbLoading.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					viewBuddhistThumbLoading.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					viewBuddhistThumbLoading.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					viewBuddhistThumbLoading.setVisibility(View.GONE);
				}
			});
			this.viewHallName.setText(this.temple.optString("templename", ""));
			this.viewBuddhistName.setText(this.temple.optString("buddhistname", ""));
		}
		this.viewRedeemTitle.setText("信众 " + ShangXiang.memberInfo.optString("truename", "") + " 所求愿望已成，特此答谢还愿");
		this.viewDesirer.setText(ShangXiang.memberInfo.optString("truename", ""));
		this.buttonSelectRegionShow.setText(ShangXiang.memberInfo.optString("area", ""));
		this.viewMobile.setText(ShangXiang.APP.getMobile());
		this.buttonSelectDateShow.setText(this.formatter.format(this.calendar.getTime()));

		this.buttonSubmit = (Button) findViewById(R.id.create_order_submit_button);
		this.buttonSubmit.setOnClickListener(this);

		this.selectContent = (AbstractWheel) findViewById(R.id.create_order_select_content_view);
		this.contentAdapter = new ContentAdapter(this);
		this.contentAdapter.contents = new String[] { "请选择" };
		this.selectContent.setViewAdapter(this.contentAdapter);

		this.selectJSC = (AbstractWheel) findViewById(R.id.create_order_select_JSC_view);
		this.JSCAdapter = new JSCAdapter(this);
		this.JSCAdapter.jsonJSCs = new JSONArray();
		this.selectJSC.setViewAdapter(this.JSCAdapter);

		this.selectRegionCity = (AbstractWheel) findViewById(R.id.create_order_select_region_city_view);
		this.regionCityAdapter = new RegionCityAdapter(this);
		try {
			this.regionCityAdapter.regions = new JSONArray("[{\"name\":\"请选择\"}]");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.selectRegionCity.setViewAdapter(this.regionCityAdapter);

		this.selectRegionProv = (AbstractWheel) findViewById(R.id.create_order_select_region_prov_view);
		this.regionProvAdapter = new RegionProvAdapter(this);
		this.regionProvAdapter.regions = this.jsonRegions;
		this.selectRegionProv.setViewAdapter(this.regionProvAdapter);
		this.selectRegionProv.addScrollingListener(new OnWheelScrollListener() {
			public void onScrollingStarted(AbstractWheel wheel) {
				scrolling = true;
			}

			public void onScrollingFinished(AbstractWheel wheel) {
				scrolling = false;
				regionCityAdapter = new RegionCityAdapter(CreateOrder.this);
				regionCityAdapter.regions = regionProvAdapter.regions.optJSONObject(selectRegionProv.getCurrentItem()).optJSONArray("sub");
				selectRegionCity.setViewAdapter(regionCityAdapter);
			}
		});
		this.selectRegionProv.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
				if (!scrolling) {
					regionCityAdapter = new RegionCityAdapter(CreateOrder.this);
					regionCityAdapter.regions = regionProvAdapter.regions.optJSONObject(newValue).optJSONArray("sub");
					selectRegionCity.setViewAdapter(regionCityAdapter);
				}
			}
		});

		this.selectDateYear = (AbstractWheel) findViewById(R.id.create_order_select_date_year_view);
		this.selectDateMonth = (AbstractWheel) findViewById(R.id.create_order_select_date_month_view);
		this.selectDateDay = (AbstractWheel) findViewById(R.id.create_order_select_date_day_view);

		this.dateYearAdapter = new DateYearAdapter(this);
		this.selectDateYear.setViewAdapter(this.dateYearAdapter);
		this.selectDateYear.addScrollingListener(new OnWheelScrollListener() {
			public void onScrollingStarted(AbstractWheel wheel) {
				scrolling = true;
			}

			public void onScrollingFinished(AbstractWheel wheel) {
				scrolling = false;
				calendar.set(calendar.get(Calendar.YEAR) + selectDateYear.getCurrentItem(), selectDateMonth.getCurrentItem(), 1);
				dateDayAdapter = new DateDayAdapter(CreateOrder.this);
				dateDayAdapter.dayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				selectDateDay.setViewAdapter(dateDayAdapter);
				selectDateDay.setCurrentItem(daySelected);
			}
		});
		this.selectDateYear.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
				if (!scrolling) {
					calendar.set(calendar.get(Calendar.YEAR) + selectDateYear.getCurrentItem(), selectDateMonth.getCurrentItem(), 1);
					dateDayAdapter = new DateDayAdapter(CreateOrder.this);
					dateDayAdapter.dayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
					selectDateDay.setViewAdapter(dateDayAdapter);
					selectDateDay.setCurrentItem(daySelected);
				}
			}
		});

		this.dateMonthAdapter = new DateMonthAdapter(this);
		this.selectDateMonth.setViewAdapter(this.dateMonthAdapter);
		this.selectDateMonth.addScrollingListener(new OnWheelScrollListener() {
			public void onScrollingStarted(AbstractWheel wheel) {
				scrolling = true;
			}

			public void onScrollingFinished(AbstractWheel wheel) {
				scrolling = false;
				calendar.set(calendar.get(Calendar.YEAR) + selectDateYear.getCurrentItem(), selectDateMonth.getCurrentItem(), 1);
				dateDayAdapter = new DateDayAdapter(CreateOrder.this);
				dateDayAdapter.dayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				selectDateDay.setViewAdapter(dateDayAdapter);
				selectDateDay.setCurrentItem(daySelected);
			}
		});
		this.selectDateMonth.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
				if (!scrolling) {
					calendar.set(calendar.get(Calendar.YEAR) + selectDateYear.getCurrentItem(), selectDateMonth.getCurrentItem(), 1);
					dateDayAdapter = new DateDayAdapter(CreateOrder.this);
					dateDayAdapter.dayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
					selectDateDay.setViewAdapter(dateDayAdapter);
					selectDateDay.setCurrentItem(daySelected);
				}
			}
		});

		this.dateDayAdapter = new DateDayAdapter(this);
		this.dateDayAdapter.dayCount = this.calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		this.selectDateDay.setViewAdapter(this.dateDayAdapter);
		this.selectDateDay.addScrollingListener(new OnWheelScrollListener() {
			public void onScrollingStarted(AbstractWheel wheel) {
				scrolling = true;
			}

			public void onScrollingFinished(AbstractWheel wheel) {
				scrolling = false;
				daySelected = selectDateDay.getCurrentItem();
				calendar.set(calendar.get(Calendar.YEAR) + selectDateYear.getCurrentItem(), selectDateMonth.getCurrentItem(), daySelected + 1);
			}
		});
		this.selectDateDay.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
				if (!scrolling) {
					daySelected = selectDateDay.getCurrentItem();
					calendar.set(calendar.get(Calendar.YEAR) + selectDateYear.getCurrentItem(), selectDateMonth.getCurrentItem(), daySelected + 1);
				}
			}
		});

		this.selectDateMonth.setCurrentItem(this.calendar.get(Calendar.MONTH));
		this.selectDateDay.setCurrentItem(this.daySelected);

		loadSelectContent();
	}

	private class ContentAdapter extends AbstractWheelTextAdapter {
		String[] contents = new String[] {};

		protected ContentAdapter(Context context) {
			super(context, R.layout.select_custom_layout, NO_RESOURCE);
		}

		@Override
		public int getItemsCount() {
			return this.contents.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return "";
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			TextView JSCView = (TextView) view.findViewById(R.id.select_custom_text);
			JSCView.setText(this.contents[index]);
			return view;
		}
	}

	private class JSCAdapter extends AbstractWheelTextAdapter {
		JSONArray jsonJSCs = new JSONArray();

		protected JSCAdapter(Context context) {
			super(context, R.layout.select_custom_layout2, NO_RESOURCE);
		}

		@Override
		public int getItemsCount() {
			return this.jsonJSCs.length();
		}

		@Override
		protected CharSequence getItemText(int index) {
			return "";
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			JSONObject jsonJSC = this.jsonJSCs.optJSONObject(index);
			View view = super.getItem(index, cachedView, parent);
			TextView viewJSCName = (TextView) view.findViewById(R.id.select_custom_text);
			viewJSCName.setText(jsonJSC.optString("name", ""));
			TextView viewJSCPrice = (TextView) view.findViewById(R.id.select_custom_text2);
			viewJSCPrice.setText("￥" + jsonJSC.optString("price", ""));
			return view;
		}
	}

	private class RegionProvAdapter extends AbstractWheelTextAdapter {
		JSONArray regions = new JSONArray();

		protected RegionProvAdapter(Context context) {
			super(context, R.layout.select_custom_layout, NO_RESOURCE);
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
			JSONObject prov = this.regions.optJSONObject(index);
			View view = super.getItem(index, cachedView, parent);
			TextView provView = (TextView) view.findViewById(R.id.select_custom_text);
			provView.setText(prov.optString("name", ""));
			return view;
		}
	}

	private class RegionCityAdapter extends AbstractWheelTextAdapter {
		JSONArray regions = new JSONArray();

		protected RegionCityAdapter(Context context) {
			super(context, R.layout.select_custom_layout, NO_RESOURCE);
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
			JSONObject city = this.regions.optJSONObject(index);
			View view = super.getItem(index, cachedView, parent);
			TextView cityView = (TextView) view.findViewById(R.id.select_custom_text);
			cityView.setText(city.optString("name", ""));
			return view;
		}
	}

	private class DateYearAdapter extends AbstractWheelTextAdapter {
		protected DateYearAdapter(Context context) {
			super(context, R.layout.select_custom_layout, NO_RESOURCE);
		}

		@Override
		public int getItemsCount() {
			return 10;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return "";
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			TextView yearView = (TextView) view.findViewById(R.id.select_custom_text);
			yearView.setText((calendar.get(Calendar.YEAR) + index) + "年");
			return view;
		}
	}

	private class DateMonthAdapter extends AbstractWheelTextAdapter {
		protected DateMonthAdapter(Context context) {
			super(context, R.layout.select_custom_layout, NO_RESOURCE);
		}

		@Override
		public int getItemsCount() {
			return 12;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return "";
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			TextView monthView = (TextView) view.findViewById(R.id.select_custom_text);
			monthView.setText((index + 1) + "月");
			return view;
		}
	}

	private class DateDayAdapter extends AbstractWheelTextAdapter {
		int dayCount = 31;

		protected DateDayAdapter(Context context) {
			super(context, R.layout.select_custom_layout, NO_RESOURCE);
		}

		@Override
		public int getItemsCount() {
			return dayCount;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return "";
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			TextView monthView = (TextView) view.findViewById(R.id.select_custom_text);
			monthView.setText((index + 1) + "日");
			return view;
		}
	}

	private void showLoading() {
		Utils.animLoading(this.layoutLoading, !this.showLoading);
		this.showLoading = !this.showLoading;
	}

	private void loadSelectContent() {
		if (Utils.CheckNetwork()) {
			showLoading();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("wishtype", String.valueOf(this.desireType)));

			this.httpClient.Config("get", Consts.URI_SELECT_CONTENT, params, true);
			this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
				public void CallFinished(String error, Object result) {
					showLoading();
					if (null == error) {
						loadSelectContent((String) result);
					} else {
						int err = R.string.dialog_system_error_content;
						if (error == httpClient.ERR_TIME_OUT) {
							err = R.string.dialog_network_error_timeout;
						}
						if (error == httpClient.ERR_GET_ERR) {
							err = R.string.dialog_network_error_getdata;
						}
						Utils.ShowToast(CreateOrder.this, err);
					}
				}
			});
			this.httpMethod.start();
		} else {
			Utils.ShowToast(this, R.string.dialog_network_check_content);
		}
	}

	private void loadSelectContent(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					JSONArray jsonContent = result.optJSONArray("wishtextchoice");
					if (null != jsonContent) {
						String[] arr = new String[jsonContent.length()];
						for (int i = 0; i < jsonContent.length(); i++) {
							arr[i] = jsonContent.optString(i, "");
						}
						this.contentData = arr;
						this.contentAdapter = new ContentAdapter(this);
						this.contentAdapter.contents = arr;
						this.selectContent.setViewAdapter(this.contentAdapter);
						this.selectContent.setCurrentItem(0);
					}
				} else {
					Utils.Dialog(this, getString(R.string.dialog_normal_title), result.optString("msg", ""));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		loadJSC();
	}

	private void loadJSC() {
		if (Utils.CheckNetwork()) {
			showLoading();

			this.httpClient.Config("get", Consts.URI_JSC, null, true);
			this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
				public void CallFinished(String error, Object result) {
					showLoading();
					if (null == error) {
						loadJSC((String) result);
					} else {
						int err = R.string.dialog_system_error_content;
						if (error == httpClient.ERR_TIME_OUT) {
							err = R.string.dialog_network_error_timeout;
						}
						if (error == httpClient.ERR_GET_ERR) {
							err = R.string.dialog_network_error_getdata;
						}
						Utils.ShowToast(CreateOrder.this, err);
					}
				}
			});
			this.httpMethod.start();
		} else {
			Utils.ShowToast(this, R.string.dialog_network_check_content);
		}
	}

	private void loadJSC(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					this.jsonJSCs = result.optJSONArray("wishgradeinfo");
					if (null != this.jsonJSCs) {
						this.JSCAdapter = new JSCAdapter(this);
						this.JSCAdapter.jsonJSCs = this.jsonJSCs;
						this.selectJSC.setViewAdapter(this.JSCAdapter);
						this.selectJSC.setCurrentItem(3);
					}
				} else {
					Utils.Dialog(this, getString(R.string.dialog_normal_title), result.optString("msg", ""));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void submitOrder() {
		if (Utils.CheckNetwork()) {
			if (checkForm()) {
				this.isSubmiting = true;
				showLoading();

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("mid", ShangXiang.APP.getMemberId()));
				params.add(new BasicNameValuePair("tid", this.temple.optString("templeid", "")));
				params.add(new BasicNameValuePair("aid", this.temple.optString("attacheid", "")));
				params.add(new BasicNameValuePair("alsowish", String.valueOf(this.orderType)));
				params.add(new BasicNameValuePair("wishtype", String.valueOf(this.desireType)));
				params.add(new BasicNameValuePair("wishtext", this.viewDesireContent.getText().toString()));
				params.add(new BasicNameValuePair("wishname", this.viewDesirer.getText().toString()));
				params.add(new BasicNameValuePair("mobile", this.viewMobile.getText().toString()));
				params.add(new BasicNameValuePair("wishgrade", (String) this.buttonSelectJSCShow.getTag()));
				params.add(new BasicNameValuePair("buddhadate", this.buttonSelectDateShow.getText().toString()));
				params.add(new BasicNameValuePair("wishplace", this.buttonSelectRegionShow.getText().toString()));
				if (1 == this.bundle.getInt("order_type") && null != this.bundle.getString("vorderid")) {
					params.add(new BasicNameValuePair("vorderid", this.bundle.getString("vorderid")));
				}

				this.httpClient.Config("post", Consts.URI_CREATE_ORDER, params, true);
				this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
					public void CallFinished(String error, Object result) {
						showLoading();
						isSubmiting = false;
						if (null == error) {
							submitOrder((String) result);
						} else {
							int err = R.string.dialog_system_error_content;
							if (error == httpClient.ERR_TIME_OUT) {
								err = R.string.dialog_network_error_timeout;
							}
							if (error == httpClient.ERR_GET_ERR) {
								err = R.string.dialog_network_error_getdata;
							}
							Utils.ShowToast(CreateOrder.this, err);
						}
					}
				});
				this.httpMethod.start();
			}
		} else {
			Utils.ShowToast(this, R.string.dialog_network_check_content);
		}
	}

	private void submitOrder(String s) {
		if (null != s) {
			try {
				final JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					Utils.Dialog(this, R.string.dialog_normal_title, R.string.dialog_create_order_success, new Utils.Callback() {
						@Override
						public void callFinished() {
						}
					}, new Utils.Callback() {
						@Override
						public void callFinished() {
							doFinishOrder(result.optString("orderid", ""));
						}
					});
				} else {
					Utils.Dialog(this, getString(R.string.dialog_normal_title), result.optString("msg", ""));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void doFinishOrder(String orderId) {
		JSONObject jsonJSC = this.jsonJSCs.optJSONObject(this.selectJSC.getCurrentItem());
		this.bundle.putString("order_id", orderId);
		this.bundle.putInt("order_type", this.orderType);
		this.bundle.putString("desirer", this.viewDesirer.getText().toString());
		this.bundle.putString("JSC", jsonJSC.optString("name", ""));
		this.bundle.putString("date", this.buttonSelectDateShow.getText().toString());
		this.bundle.putString("desire_content", this.viewDesireContent.getText().toString());
		Intent intent = new Intent(ShangXiang.APP, ShowOrderRecord.class);
		intent.putExtras(this.bundle);
		this.startActivity(intent);
		finish();
	}

	@SuppressLint("SimpleDateFormat")
	private boolean checkForm() {
		boolean bolAlertPop = false;
		boolean bolCheckResult = true;
		if (!this.viewDesireContent.getText().toString().matches(".{1,100}")) {
			bolAlertPop = true;
			bolCheckResult = false;
			Utils.Dialog(this, getString(R.string.dialog_form_check_title), getString(R.string.dialog_form_check_err_create_order_desire_content));
		}
		if (!bolAlertPop && !this.viewDesirer.getText().toString().matches(".{1,50}")) {
			bolAlertPop = true;
			bolCheckResult = false;
			Utils.Dialog(this, getString(R.string.dialog_form_check_title), getString(R.string.dialog_form_check_err_create_order_desirer));
		}
		if (!bolAlertPop && !this.viewMobile.getText().toString().matches(".{11,11}")) {
			bolAlertPop = true;
			bolCheckResult = false;
			Utils.Dialog(this, getString(R.string.dialog_form_check_title), getString(R.string.dialog_form_check_err_create_order_mobile));
		}
		if (!bolAlertPop && !this.buttonSelectJSCShow.getText().toString().matches(".{1,}")) {
			bolAlertPop = true;
			bolCheckResult = false;
			Utils.Dialog(this, getString(R.string.dialog_form_check_title), getString(R.string.dialog_form_check_err_create_order_JSC));
		}
		if (!bolAlertPop && !this.buttonSelectDateShow.getText().toString().matches(".{1,}")) {
			bolAlertPop = true;
			bolCheckResult = false;
			Utils.Dialog(this, getString(R.string.dialog_form_check_title), getString(R.string.dialog_form_check_err_create_order_JSC));
		}
		if (!bolAlertPop && !this.buttonSelectRegionShow.getText().toString().matches(".{1,50}")) {
			bolAlertPop = true;
			bolCheckResult = false;
			Utils.Dialog(this, getString(R.string.dialog_form_check_title), getString(R.string.dialog_form_check_err_create_order_region));
		}
		Calendar tempCalendar = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
		SimpleDateFormat tempFormatter = new SimpleDateFormat("yyyyMMdd");
		int tempString = Integer.valueOf(tempFormatter.format(tempCalendar.getTime()));
		if (!bolAlertPop && !(Integer.valueOf(tempFormatter.format(this.calendar.getTime())) > tempString)) {
			bolAlertPop = true;
			bolCheckResult = false;
			Utils.Dialog(this, getString(R.string.dialog_form_check_title), getString(R.string.dialog_form_check_err_create_order_date));
		}
		return bolCheckResult;
	}

	@Override
	public void onClick(View v) {
		Utils.hideKeyboard(this);
		if (v == this.buttonBack) {
			finish();
		}
		if (v == this.buttonSelectContentShow) {
			this.layoutSelectContent.setVisibility(View.VISIBLE);
		}
		if (v == this.buttonSelectContentOK) {
			if (this.selectContent.getCurrentItem() < this.contentData.length) {
				this.viewDesireContent.setText(this.contentData[this.selectContent.getCurrentItem()]);
			}
		}
		if (v == this.layoutSelectContent || v == this.buttonSelectContentOK || v == this.buttonSelectContentCancel) {
			this.layoutSelectContent.setVisibility(View.GONE);
		}
		if (v == this.buttonSelectJSCShow) {
			this.layoutSelectJSC.setVisibility(View.VISIBLE);
		}
		if (v == this.buttonSelectJSCOK) {
			if (this.selectJSC.getCurrentItem() < this.jsonJSCs.length()) {
				JSONObject jsonJSC = this.jsonJSCs.optJSONObject(this.selectJSC.getCurrentItem());
				this.buttonSelectJSCShow.setTag(jsonJSC.optString("val", ""));
				this.buttonSelectJSCShow.setText(Html.fromHtml(jsonJSC.optString("name", "") + "  <font color=\"#dca358\">" + jsonJSC.optString("price", "") + "</font>"));
			}
		}
		if (v == this.layoutSelectJSC || v == this.buttonSelectJSCOK || v == this.buttonSelectJSCCancel) {
			this.layoutSelectJSC.setVisibility(View.GONE);
		}
		if (v == this.buttonSelectRegionShow) {
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
			if (!TextUtils.isEmpty(strRegion)) {
				this.buttonSelectRegionShow.setText(strRegion);
			}
		}
		if (v == this.layoutSelectRegion || v == this.buttonSelectRegionOK || v == this.buttonSelectRegionCancel) {
			this.layoutSelectRegion.setVisibility(View.GONE);
		}
		if (v == this.buttonSelectDateShow) {
			this.layoutSelectDate.setVisibility(View.VISIBLE);
		}
		if (v == this.buttonSelectDateOK) {
			this.buttonSelectDateShow.setText(this.formatter.format(this.calendar.getTime()));
		}
		if (v == this.layoutSelectDate || v == this.buttonSelectDateOK || v == this.buttonSelectDateCancel) {
			this.layoutSelectDate.setVisibility(View.GONE);
		}
		if (v == this.buttonSubmit) {
			if (this.isSubmiting) {
				Utils.Dialog(this, getString(R.string.dialog_tip), getString(R.string.dialog_submiting_content));
			} else {
				submitOrder();
			}
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		onClick(v);
		return false;
	}
}