package com.shangxiang.android.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.shangxiang.android.Consts;
import com.shangxiang.android.R;
import com.shangxiang.android.BaseFragment;
import com.shangxiang.android.ShangXiang;
import com.shangxiang.android.pipe.SinhaPipeClient;
import com.shangxiang.android.pipe.SinhaPipeMethod;
import com.shangxiang.android.spinnerwheel.AbstractWheel;
import com.shangxiang.android.spinnerwheel.AbstractWheelTextAdapter;
import com.shangxiang.android.spinnerwheel.OnWheelChangedListener;
import com.shangxiang.android.spinnerwheel.OnWheelScrollListener;
import com.shangxiang.android.utils.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ModifyEvent extends BaseFragment implements OnCheckedChangeListener {
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private RelativeLayout layoutLoading;
	private Button buttonBack;
	private Button buttonSave;

	private EditText viewEventTitle;

	private LinearLayout layoutDateSelect;
	private TextView buttonDateSelectShow;
	private Button buttonDateSelectOK;
	private RadioButton buttonGregorianDate;
	private RadioButton buttonLunarDate;
	private TextView buttonRemindSelectShow;
	private Button buttonRemindSelectOK;
	private Button buttonRemindSelectCancel;
	private LinearLayout layoutRemindSelect;

	private AbstractWheel selectDateYear;
	private DateYearAdapter dateYearAdapter;
	private AbstractWheel selectDateMonth;
	private DateMonthAdapter dateMonthAdapter;
	private AbstractWheel selectDateDay;
	private DateDayAdapter dateDayAdapter;
	private SimpleDateFormat formatter;
	private Calendar calendar;
	private AbstractWheel selectRemind;
	private RemindAdapter remindAdapter;

	private JSONObject jsonEvent = new JSONObject();
	private int dateType = 0;
	private String dateString = "";
	private String[] months = new String[] { "正月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "腊月" };
	private String[] days = new String[] { "初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十", "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "廿十", "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十", "三一" };
	private int daySelected = 0;
	private boolean showLoading = false;
	private boolean isSubmiting = false;
	private boolean scrolling = false;

	@SuppressLint({ "InflateParams", "SimpleDateFormat" })
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		View view = inflater.inflate(R.layout.create_event, null);

		this.httpClient = new SinhaPipeClient();

		this.layoutLoading = (RelativeLayout) view.findViewById(R.id.loading);
		this.buttonBack = (Button) view.findViewById(R.id.create_event_title_back_button);
		this.buttonBack.setOnClickListener(this);
		this.buttonSave = (Button) view.findViewById(R.id.create_event_title_save_button);
		this.buttonSave.setOnClickListener(this);

		this.viewEventTitle = (EditText) view.findViewById(R.id.create_event_event_title_input);

		this.layoutDateSelect = (LinearLayout) view.findViewById(R.id.create_event_select_date_layout);
		this.layoutDateSelect.setOnClickListener(this);
		this.buttonDateSelectShow = (TextView) view.findViewById(R.id.create_event_select_date_show_button);
		this.buttonDateSelectShow.setTag("");
		this.buttonDateSelectShow.setOnClickListener(this);
		this.buttonDateSelectOK = (Button) view.findViewById(R.id.create_event_select_date_ok_button);
		this.buttonDateSelectOK.setOnClickListener(this);
		this.buttonGregorianDate = (RadioButton) view.findViewById(R.id.create_event_select_date_gregorian_button);
		this.buttonGregorianDate.setOnCheckedChangeListener(this);
		this.buttonLunarDate = (RadioButton) view.findViewById(R.id.create_event_select_date_lunar_button);
		this.buttonLunarDate.setOnCheckedChangeListener(this);

		this.layoutRemindSelect = (LinearLayout) view.findViewById(R.id.create_event_select_remind_layout);
		this.layoutRemindSelect.setOnClickListener(this);
		this.buttonRemindSelectShow = (TextView) view.findViewById(R.id.create_event_select_remind_show_button);
		this.buttonRemindSelectShow.setTag("1");
		this.buttonRemindSelectShow.setOnClickListener(this);
		this.buttonRemindSelectOK = (Button) view.findViewById(R.id.create_event_select_remind_ok_button);
		this.buttonRemindSelectOK.setOnClickListener(this);
		this.buttonRemindSelectCancel = (Button) view.findViewById(R.id.create_event_select_remind_cancel_button);
		this.buttonRemindSelectCancel.setOnClickListener(this);

		this.formatter = new SimpleDateFormat("yyyy-MM-dd");
		this.calendar = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
		this.daySelected = this.calendar.get(Calendar.DAY_OF_MONTH) - 1;

		Bundle bundle = getArguments();
		if (null != bundle && !TextUtils.isEmpty(bundle.getString("event"))) {
			try {
				this.jsonEvent = new JSONObject(bundle.getString("event"));
				this.viewEventTitle.setText(this.jsonEvent.optString("name", ""));
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				long time = Long.valueOf(this.jsonEvent.optString("date", ""));
				this.buttonDateSelectShow.setText(formatter.format(new Date(time * 1000L)));
				this.buttonDateSelectShow.setTag(this.buttonDateSelectShow.getText());
				this.buttonRemindSelectShow.setTag("" + this.jsonEvent.optString("remind", ""));
				this.buttonRemindSelectShow.setText("提前" + this.jsonEvent.optString("remind", "") + "天");
				this.buttonRemindSelectShow.setText("0".equals(this.jsonEvent.optString("remind", "0")) ? "准时提醒" : "提前" + this.jsonEvent.optString("remind", "0") + "天");
			} catch (JSONException e) {
			}
		}
		
		return view;
	}

	public void onActivityCreated(Bundle sinha) {
		super.onActivityCreated(sinha);

		this.selectDateYear = (AbstractWheel) getActivity().findViewById(R.id.create_event_select_date_year_view);
		this.selectDateMonth = (AbstractWheel) getActivity().findViewById(R.id.create_event_select_date_month_view);
		this.selectDateDay = (AbstractWheel) getActivity().findViewById(R.id.create_event_select_date_day_view);

		this.dateYearAdapter = new DateYearAdapter(getActivity());
		this.selectDateYear.setViewAdapter(this.dateYearAdapter);
		this.selectDateYear.addScrollingListener(new OnWheelScrollListener() {
			public void onScrollingStarted(AbstractWheel wheel) {
				scrolling = true;
			}

			public void onScrollingFinished(AbstractWheel wheel) {
				scrolling = false;
				calendar.set(calendar.get(Calendar.YEAR) - selectDateYear.getCurrentItem(), selectDateMonth.getCurrentItem(), 1);
				dateDayAdapter = new DateDayAdapter(getActivity());
				dateDayAdapter.dayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				selectDateDay.setViewAdapter(dateDayAdapter);
				selectDateDay.setCurrentItem(daySelected);
			}
		});
		this.selectDateYear.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
				if (!scrolling) {
					calendar.set(calendar.get(Calendar.YEAR) - selectDateYear.getCurrentItem(), selectDateMonth.getCurrentItem(), 1);
					dateDayAdapter = new DateDayAdapter(getActivity());
					dateDayAdapter.dayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
					selectDateDay.setViewAdapter(dateDayAdapter);
					selectDateDay.setCurrentItem(daySelected);
				}
			}
		});

		this.dateMonthAdapter = new DateMonthAdapter(getActivity());
		this.selectDateMonth.setViewAdapter(this.dateMonthAdapter);
		this.selectDateMonth.addScrollingListener(new OnWheelScrollListener() {
			public void onScrollingStarted(AbstractWheel wheel) {
				scrolling = true;
			}

			public void onScrollingFinished(AbstractWheel wheel) {
				scrolling = false;
				calendar.set(calendar.get(Calendar.YEAR), selectDateMonth.getCurrentItem(), 1);
				dateDayAdapter = new DateDayAdapter(getActivity());
				dateDayAdapter.dayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				selectDateDay.setViewAdapter(dateDayAdapter);
				selectDateDay.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH) - 1);
			}
		});
		this.selectDateMonth.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
				if (!scrolling) {
					calendar.set(calendar.get(Calendar.YEAR), selectDateMonth.getCurrentItem(), 1);
					dateDayAdapter = new DateDayAdapter(getActivity());
					dateDayAdapter.dayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
					selectDateDay.setViewAdapter(dateDayAdapter);
					selectDateDay.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH) - 1);
				}
			}
		});

		this.dateDayAdapter = new DateDayAdapter(getActivity());
		this.dateDayAdapter.dayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		this.selectDateDay.setViewAdapter(this.dateDayAdapter);
		this.selectDateDay.addScrollingListener(new OnWheelScrollListener() {
			public void onScrollingStarted(AbstractWheel wheel) {
				scrolling = true;
			}

			public void onScrollingFinished(AbstractWheel wheel) {
				scrolling = false;
				daySelected = selectDateDay.getCurrentItem();
				calendar.set(calendar.get(Calendar.YEAR) - selectDateYear.getCurrentItem(), selectDateMonth.getCurrentItem(), daySelected + 1);
			}
		});
		this.selectDateDay.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
				if (!scrolling) {
					daySelected = selectDateDay.getCurrentItem();
					calendar.set(calendar.get(Calendar.YEAR) - selectDateYear.getCurrentItem(), selectDateMonth.getCurrentItem(), daySelected + 1);
				}
			}
		});

		this.selectDateMonth.setCurrentItem(this.calendar.get(Calendar.MONTH));
		this.selectDateDay.setCurrentItem(this.daySelected);

		this.selectRemind = (AbstractWheel) getActivity().findViewById(R.id.create_event_select_remind_view);
		this.remindAdapter = new RemindAdapter(getActivity());
		this.selectRemind.setViewAdapter(this.remindAdapter);
		this.selectRemind.setCurrentItem(0);
	}

	private class DateYearAdapter extends AbstractWheelTextAdapter {
		protected DateYearAdapter(Context context) {
			super(context, R.layout.select_custom_layout, NO_RESOURCE);
		}

		@Override
		public int getItemsCount() {
			return 100;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return "";
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			TextView yearView = (TextView) view.findViewById(R.id.select_custom_text);
			yearView.setText((calendar.get(Calendar.YEAR) - index) + "年");
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
			monthView.setText(dateType == 1 ? months[index] : (index + 1) + "月");
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
			monthView.setText(dateType == 1 ? days[index] : (index + 1) + "日");
			return view;
		}
	}

	private class RemindAdapter extends AbstractWheelTextAdapter {
		String[] reminds = new String[] { "准时提醒", "1天", "2天", "3天", "4天", "5天", "6天", "7天" };

		protected RemindAdapter(Context context) {
			super(context, R.layout.select_custom_layout, NO_RESOURCE);
		}

		@Override
		public int getItemsCount() {
			return reminds.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return "";
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			TextView remindView = (TextView) view.findViewById(R.id.select_custom_text);
			remindView.setText(this.reminds[index]);
			return view;
		}
	}

	private void showLoading() {
		Utils.animLoading(this.layoutLoading, !this.showLoading);
		this.showLoading = !this.showLoading;
	}

	private void submitEvent() {
		if (Utils.CheckNetwork()) {
			if (checkForm()) {
				this.isSubmiting = true;
				showLoading();

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("mid", ShangXiang.APP.getMemberId()));
				params.add(new BasicNameValuePair("crid", this.jsonEvent.optString("id", "")));
				params.add(new BasicNameValuePair("rname", this.viewEventTitle.getText().toString()));
				params.add(new BasicNameValuePair("rdate", (String) this.buttonDateSelectShow.getTag()));
				params.add(new BasicNameValuePair("rtime", (String) this.buttonRemindSelectShow.getTag()));
				params.add(new BasicNameValuePair("type", String.valueOf(this.dateType)));

				this.httpClient.Config("post", Consts.URI_CALENDAR_MODIFY_EVENT, params, true);
				this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
					public void CallFinished(String error, Object result) {
						showLoading();
						isSubmiting = false;
						if (null == error) {
							submitEvent((String) result);
						} else {
							int err = R.string.dialog_system_error_content;
							if (error == httpClient.ERR_TIME_OUT) {
								err = R.string.dialog_network_error_timeout;
							}
							if (error == httpClient.ERR_GET_ERR) {
								err = R.string.dialog_network_error_getdata;
							}
							Utils.ShowToast(getActivity(), err);
						}
					}
				});
				this.httpMethod.start();
			}
		} else {
			Utils.ShowToast(getActivity(), R.string.dialog_network_check_content);
		}
	}

	private void submitEvent(String s) {
		if (null != s) {
			try {
				final JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					Utils.Dialog(getActivity(), R.string.dialog_normal_title, R.string.dialog_create_event_success, new Utils.Callback() {
						@Override
						public void callFinished() {
							goHomeFragment();
						}
					});
				} else {
					Utils.Dialog(getActivity(), getString(R.string.dialog_normal_title), result.optString("msg", ""));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean checkForm() {
		boolean bolAlertPop = false;
		boolean bolCheckResult = true;
		if (!this.viewEventTitle.getText().toString().matches(".{1,50}")) {
			bolAlertPop = true;
			bolCheckResult = false;
			Utils.Dialog(getActivity(), getString(R.string.dialog_form_check_title), getString(R.string.dialog_form_check_err_create_event_title));
		}
		if (!bolAlertPop && TextUtils.isEmpty((String) this.buttonDateSelectShow.getTag())) {
			bolAlertPop = true;
			bolCheckResult = false;
			Utils.Dialog(getActivity(), getString(R.string.dialog_form_check_title), getString(R.string.dialog_form_check_err_create_event_date));
		}
		return bolCheckResult;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.isPressed() && isChecked) {
			if (buttonView == this.buttonGregorianDate) {
				this.dateType = 0;
			}
			if (buttonView == this.buttonLunarDate) {
				this.dateType = 1;
			}
			this.dateMonthAdapter = new DateMonthAdapter(getActivity());
			this.selectDateMonth.setViewAdapter(this.dateMonthAdapter);
			this.selectDateMonth.setCurrentItem(this.calendar.get(Calendar.MONTH));
			this.dateDayAdapter = new DateDayAdapter(getActivity());
			this.dateDayAdapter.dayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			this.selectDateDay.setViewAdapter(dateDayAdapter);
			this.selectDateDay.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH) - 1);
		}
	}

	@Override
	public void onClick(View v) {
		if (v == this.buttonBack) {
			getActivity().onBackPressed();
		}
		if (v == this.buttonDateSelectShow) {
			this.layoutDateSelect.setVisibility(View.VISIBLE);
		}
		if (v == this.buttonDateSelectOK) {
			this.dateString = this.dateType == 0 ? this.formatter.format(this.calendar.getTime()) : (this.calendar.get(Calendar.YEAR) - this.selectDateYear.getCurrentItem()) + "年" + this.months[this.selectDateMonth.getCurrentItem()] + this.days[this.daySelected];
			this.buttonDateSelectShow.setTag(this.formatter.format(this.calendar.getTime()));
			this.buttonDateSelectShow.setText(this.dateString);
		}
		if (v == this.layoutDateSelect || v == this.buttonDateSelectOK) {
			this.layoutDateSelect.setVisibility(View.GONE);
		}
		if (v == this.buttonRemindSelectShow) {
			this.layoutRemindSelect.setVisibility(View.VISIBLE);
		}
		if (v == this.buttonRemindSelectOK) {
			this.buttonRemindSelectShow.setTag("" + this.selectRemind.getCurrentItem());
			this.buttonRemindSelectShow.setText(this.selectRemind.getCurrentItem() == 0 ? "准时提醒" : "提前" + this.selectRemind.getCurrentItem() + "天");
		}
		if (v == this.layoutRemindSelect || v == this.buttonRemindSelectCancel|| v == this.buttonRemindSelectOK) {
			this.layoutRemindSelect.setVisibility(View.GONE);
		}
		if (v == this.buttonSave) {
			if (this.isSubmiting) {
				Utils.Dialog(getActivity(), getString(R.string.dialog_tip), getString(R.string.dialog_submiting_content));
			} else {
				submitEvent();
			}
		}
	}
}