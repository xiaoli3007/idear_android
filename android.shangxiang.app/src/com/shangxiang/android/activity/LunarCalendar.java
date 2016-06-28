package com.shangxiang.android.activity;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.shangxiang.android.BaseFragment;
import com.shangxiang.android.ShangXiang;
import com.shangxiang.android.adapter.CalendarViewAdapter;
import com.shangxiang.android.pipe.SinhaPipeClient;
import com.shangxiang.android.pipe.SinhaPipeMethod;
import com.shangxiang.android.utils.LunarCalendarUtil;
import com.shangxiang.android.utils.Utils;
import com.shangxiang.android.view.CalendarView;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.LinearLayout.LayoutParams;

public class LunarCalendar extends BaseFragment implements OnTouchListener {
	private SinhaPipeClient httpClient;
	private SinhaPipeMethod httpMethod;
	private RelativeLayout layoutLoading;
	private ImageView viewBackground;
	private TextView viewTitle;
	private Button buttonPrevMonth;
	private Button buttonNextMonth;
	private Button buttonToday;
	private TextView viewSelectedDateDay;
	private TextView viewSelectedDateFull;
	private TextView viewSelectedDateLunar;
	private Button buttonCreateEvent;
	private LinearLayout layoutEventList;
	private RelativeLayout viewCalendar;
	private boolean showLoading = false;

	private static final int CAL_LAYOUT_ID = 557733;
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	private LayoutInflater inflater;
	private SimpleDateFormat formatter;

	private GestureDetector gesturer = null;
	private ViewFlipper viewFlipper;
	private Calendar calMiddleDate = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
	private Calendar calMiddleStartDate = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
	private Calendar calFirstDate = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
	private Calendar calFirstStartDate = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
	private Calendar calLastDate = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
	private Calendar calLastStartDate = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
	private Calendar calSelected = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
	private Calendar calToday = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
	private LinearLayout layoutFirst;
	private LinearLayout layoutMiddle;
	private LinearLayout layoutLast;
	private Animation slideLeftIn;
	private Animation slideLeftOut;
	private Animation slideRightIn;
	private Animation slideRightOut;
	private int showMode = 0;
	private int calendarAction = 0;

	@SuppressLint({ "InflateParams" })
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		View view = inflater.inflate(R.layout.calendar, null);

		this.inflater = LayoutInflater.from(getActivity());
		this.httpClient = new SinhaPipeClient();
		this.gesturer = new GestureDetector(getActivity(), new CalendarGestureListener());

		this.slideLeftIn = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_left_in);
		this.slideLeftIn.setAnimationListener(this.animationListener);
		this.slideLeftOut = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_left_out);
		this.slideRightIn = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_right_in);
		this.slideRightIn.setAnimationListener(this.animationListener);
		this.slideRightOut = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_right_out);

		this.layoutLoading = (RelativeLayout) view.findViewById(R.id.loading);
		this.viewBackground = (ImageView) view.findViewById(R.id.calendar_background_image);
		this.viewBackground.setOnClickListener(this);
		this.viewTitle = (TextView) view.findViewById(R.id.calendar_title_text);
		this.buttonPrevMonth = (Button) view.findViewById(R.id.calendar_prev_button);
		this.buttonPrevMonth.setOnClickListener(this);
		this.buttonNextMonth = (Button) view.findViewById(R.id.calendar_next_button);
		this.buttonNextMonth.setOnClickListener(this);
		this.buttonToday = (Button) view.findViewById(R.id.calendar_today_button);
		this.buttonToday.setOnClickListener(this);
		this.viewSelectedDateDay = (TextView) view.findViewById(R.id.calendar_selected_date_day_text);
		this.viewSelectedDateFull = (TextView) view.findViewById(R.id.calendar_selected_date_full_text);
		this.viewSelectedDateLunar = (TextView) view.findViewById(R.id.calendar_selected_date_lunar_text);
		this.buttonCreateEvent = (Button) view.findViewById(R.id.calendar_create_event_button);
		this.buttonCreateEvent.setOnClickListener(this);
		this.layoutEventList = (LinearLayout) view.findViewById(R.id.calendar_event_list_layout);
		this.viewCalendar = (RelativeLayout) view.findViewById(R.id.calendar_view);

		return view;
	}

	public void onActivityCreated(Bundle sinha) {
		super.onActivityCreated(sinha);
		displayDateDetail(this.calToday);
		loadAllHolidayList();
	}

	private void showLoading() {
		Utils.animLoading(this.layoutLoading, !this.showLoading);
		this.showLoading = !this.showLoading;
	}

	@SuppressLint("SimpleDateFormat")
	private void displayDateDetail(Calendar calendar) {
		this.formatter = new SimpleDateFormat("dd");
		String dateDay = this.formatter.format(calendar.getTime());
		this.viewSelectedDateDay.setText(dateDay);
		int weekNameId = getResources().getIdentifier("week_" + calendar.get(Calendar.DAY_OF_WEEK), "string", getActivity().getPackageName());
		this.formatter = new SimpleDateFormat("yyyy年MM月dd日");
		String dateFull = this.formatter.format(calendar.getTime()) + " 星期" + getString(weekNameId);
		this.viewSelectedDateFull.setText(dateFull);
		String dateLuner = "农历" + new LunarCalendarUtil(calendar).getShortDay();
		this.viewSelectedDateLunar.setText(dateLuner);
	}

	@SuppressLint("SimpleDateFormat")
	private void displayDateEvent(Calendar calendar) {
		this.layoutEventList.removeAllViews();
		LunarCalendarUtil calLunar = new LunarCalendarUtil(calendar);
		displayBackground("assets://" + calLunar.getDayNum() + ".png");
		for (int i = 0; i < ShangXiang.jsonHolidays.length(); i++) {
			String holiday = ShangXiang.jsonHolidays.optString(i);
			String sepHoliday = holiday.substring(0, 4);
			String nameHoliday = holiday.substring(5);
			if (sepHoliday.equals(calLunar.getDayNum())) {
				try {
					JSONObject event = new JSONObject("{\"id\":\"\",\"name\":\"" + nameHoliday + "\",\"date\":\"\",\"remind\":\"\"}");
					this.layoutEventList.addView(createEventList(0, event));
				} catch (JSONException e) {
				}
			}
		}
		for (int i = 0; i < ShangXiang.jsonEvents.length(); i++) {
			try {
				JSONObject event = ShangXiang.jsonEvents.optJSONObject(i);
				Timestamp dateTarget = new Timestamp(Long.valueOf(event.optString("reminddate", "") + "000"));
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				if (formatter.format(dateTarget.getTime()).equals(formatter.format(calendar.getTime()))) {
					JSONObject eventNew = new JSONObject("{\"id\":\"" + event.optString("id", "") + "\",\"name\":\"" + event.optString("relativesname", "") + "\",\"date\":\"" + event.optString("reminddate", "") + "\",\"remind\":\"" + event.optString("remindtime", "") + "\"}");
					this.layoutEventList.addView(createEventList(1, eventNew));
				}
			} catch (JSONException e) {
			}
		}
	}

	private void displayBackground(String path) {
		this.viewBackground.setBackgroundResource(R.drawable.background_calendar);
		ShangXiang.imageLoader.displayImage(path, this.viewBackground, ShangXiang.calendarLoaderOptions, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
			}
		});
	}

	private View createEventList(int type, JSONObject event) {
		View layoutEvent = this.inflater.inflate(type == 1 ? R.layout.calendar_list_item_private : R.layout.calendar_list_item_public, null);
		Button buttonEvent = (Button) layoutEvent.findViewById(R.id.calendar_show_event_button);
		buttonEvent.setTag(event.toString());
		buttonEvent.setText(type == 1 ? event.optString("name", "") + "的生日" : event.optString("name", ""));
		if (type == 1) {
			buttonEvent.setOnClickListener(this);
		}
		return layoutEvent;
	}

//	private void loadAllHolidayList() {
//		if (Utils.CheckNetwork()) {
//			showLoading();
//
//			List<NameValuePair> params = new ArrayList<NameValuePair>();
//			params.add(new BasicNameValuePair("mid", ShangXiang.APP.getMemberId()));
//			params.add(new BasicNameValuePair("nl", ""));
//			params.add(new BasicNameValuePair("month", ""));
//
//			this.httpClient.Config("get", Consts.URI_CALENDAR_ALL_HOLIDAY_LIST, params, true);
//			this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
//				public void CallFinished(String error, Object result) {
//					showLoading();
//					if (null == error) {
//						loadAllHolidayList((String) result);
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
//	}
//
//	private void loadAllHolidayList(String s) {
//		if (null != s) {
//			try {
//				JSONObject result = new JSONObject(s);
//				if (result.optString("code", "").equals("succeed")) {
//					if (null != result.optJSONArray("buddhismholiday")) {
//						ShangXiang.jsonHolidays = result.optJSONArray("buddhismholiday");
//					}
//					if (null != result.optJSONArray("calendarlist")) {
//						ShangXiang.jsonEvents = result.optJSONArray("calendarlist");
//					}
//				}
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
//		displayDateEvent(calToday);
//		loadEventList();
//	}

	private void loadAllHolidayList() {
		try {
			ShangXiang.jsonHolidays = new JSONArray(Consts.ALL_HOLIDAYS);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		displayDateEvent(calToday);
		loadEventList();
	}

	private void loadEventList() {
		if (Utils.CheckNetwork()) {
			showLoading();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("mid", ShangXiang.APP.getMemberId()));

			this.httpClient.Config("get", Consts.URI_CALENDAR_EVENT_LIST, params, true);
			this.httpMethod = new SinhaPipeMethod(this.httpClient, new SinhaPipeMethod.MethodCallback() {
				public void CallFinished(String error, Object result) {
					showLoading();
					if (null == error) {
						loadEventList((String) result);
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
		} else {
			Utils.ShowToast(getActivity(), R.string.dialog_network_check_content);
		}
	}

	private void loadEventList(String s) {
		if (null != s) {
			try {
				JSONObject result = new JSONObject(s);
				if (result.optString("code", "").equals("succeed")) {
					if (null != result.optJSONArray("calendarlist")) {
						ShangXiang.jsonEvents = result.optJSONArray("calendarlist");
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		setAllDate();
		initCalendarView();
	}

	private void setAllDate() {
		setAllDate(this.calToday);
	}

	@SuppressLint("SimpleDateFormat")
	private void setAllDate(Calendar calendar) {
		Calendar calTemp = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
		calTemp.setTime(calendar.getTime());
		this.calMiddleDate.setTime(calTemp.getTime());
		this.calMiddleStartDate.setTime(getTimeBegin(calTemp).getTime());
		if (this.showMode == 0) {
			calTemp.add(Calendar.MONTH, -1);
			this.calFirstDate.setTime(calTemp.getTime());
			this.calFirstStartDate.setTime(getTimeBegin(calTemp).getTime());
			calTemp.add(Calendar.MONTH, 2);
			this.calLastDate.setTime(calTemp.getTime());
			this.calLastStartDate.setTime(getTimeBegin(calTemp).getTime());
		} else {
			calTemp.add(Calendar.DATE, -7);
			this.calFirstDate.setTime(calTemp.getTime());
			this.calFirstStartDate.setTime(getTimeBegin(calTemp).getTime());
			calTemp.add(Calendar.DATE, 14);
			this.calLastDate.setTime(calTemp.getTime());
			this.calLastStartDate.setTime(getTimeBegin(calTemp).getTime());
		}

		this.calSelected.setTime(this.calMiddleDate.getTime());
		this.calSelected.set(Calendar.DAY_OF_MONTH, 1);
		this.formatter = new SimpleDateFormat("yyyy年MM月");
		String currDate = this.formatter.format(this.calMiddleDate.getTime());
		this.viewTitle.setText(currDate);
	}

	private Calendar getTimeBegin(Calendar calendar) {
		Calendar calTemp = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
		calTemp.setTime(calendar.getTime());
		if (this.showMode == 0) {
			calTemp.set(Calendar.DATE, 1);
		}
		int iDay = calTemp.get(Calendar.DAY_OF_WEEK) - (ShangXiang.firstDayOfWeek == Calendar.MONDAY ? Calendar.MONDAY : Calendar.SUNDAY);
		if (iDay < 0) {
			iDay = 6;
		}
		calTemp.add(Calendar.DAY_OF_WEEK, -iDay);
		return calTemp;
	}

	private void setChangedDate() {
		LinearLayout layoutCurrent = (LinearLayout) this.viewFlipper.getCurrentView();
		GridView gridCurrent = (GridView) layoutCurrent.getChildAt(0);
		CalendarViewAdapter adapterCurrent = (CalendarViewAdapter) gridCurrent.getAdapter();
		adapterCurrent.setSelectedDate(calSelected);
		adapterCurrent.notifyDataSetChanged();
		displayDateDetail(calSelected);
		displayDateEvent(calSelected);
	}

	private void initCalendarView() {
		if (null != this.viewFlipper) {
			Utils.animView(this.viewFlipper, false);
			this.viewCalendar.removeView(this.viewFlipper);
		}
		try {
			this.viewFlipper = new ViewFlipper(getActivity());
			this.viewFlipper.setMeasureAllChildren(false);
			this.viewFlipper.setId(CAL_LAYOUT_ID);

			this.layoutFirst = new LinearLayout(getActivity());
			this.layoutFirst.addView(createCalendarView(this.calFirstDate, this.calFirstStartDate));
			this.viewFlipper.addView(this.layoutFirst);

			this.layoutMiddle = new LinearLayout(getActivity());
			this.layoutMiddle.addView(createCalendarView(this.calMiddleDate, this.calMiddleStartDate));
			this.viewFlipper.addView(this.layoutMiddle);

			this.layoutLast = new LinearLayout(getActivity());
			this.layoutLast.addView(createCalendarView(this.calLastDate, this.calLastStartDate));
			this.viewFlipper.addView(this.layoutLast);

			this.viewFlipper.setDisplayedChild(1);

			RelativeLayout.LayoutParams paramsCal = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			this.viewCalendar.addView(this.viewFlipper, paramsCal);
			LinearLayout br = new LinearLayout(getActivity());
			RelativeLayout.LayoutParams paramsBr = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1);
			paramsBr.addRule(RelativeLayout.BELOW, CAL_LAYOUT_ID);
			br.setBackgroundColor(getResources().getColor(R.color.background_normal));
			Utils.animView(this.viewFlipper, true);
			this.viewCalendar.addView(br, paramsBr);
		} catch (Exception e) {
		} finally {
		}
	}

	private void buildCalendarView() {
		Calendar calendar = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
		calendar.setTime(this.calMiddleDate.getTime());
		switch (this.calendarAction) {
		case -1:
			if (this.showMode == 0) {
				calendar.add(Calendar.MONTH, -1);
			} else {
				calendar.add(Calendar.DATE, -7);
			}
			setAllDate(calendar);
			switch (this.viewFlipper.getDisplayedChild()) {
			case 0:
				this.layoutLast.removeAllViews();
				this.layoutLast.addView(createCalendarView(this.calFirstDate, this.calFirstStartDate));
				break;
			case 1:
				this.layoutFirst.removeAllViews();
				this.layoutFirst.addView(createCalendarView(this.calFirstDate, this.calFirstStartDate));
				break;
			case 2:
				this.layoutMiddle.removeAllViews();
				this.layoutMiddle.addView(createCalendarView(this.calFirstDate, this.calFirstStartDate));
				break;
			default:
				break;
			}
			break;
		case 1:
			if (this.showMode == 0) {
				calendar.add(Calendar.MONTH, 1);
			} else {
				calendar.add(Calendar.DATE, 7);
			}
			setAllDate(calendar);
			switch (this.viewFlipper.getDisplayedChild()) {
			case 0:
				this.layoutMiddle.removeAllViews();
				this.layoutMiddle.addView(createCalendarView(this.calLastDate, this.calLastStartDate));
				break;
			case 1:
				this.layoutLast.removeAllViews();
				this.layoutLast.addView(createCalendarView(this.calLastDate, this.calLastStartDate));
				break;
			case 2:
				this.layoutFirst.removeAllViews();
				this.layoutFirst.addView(createCalendarView(this.calLastDate, this.calLastStartDate));
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
	}

	private GridView createCalendarView(Calendar cal, Calendar calStart) {
		Calendar calTemp = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
		calTemp.setTime(cal.getTime());
		Calendar calStartTemp = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
		calStartTemp.setTime(calStart.getTime());
		GridView viewCalendar = new CalendarView(getActivity());
		viewCalendar.setCacheColorHint(Color.TRANSPARENT);
		viewCalendar.setSelector(new ColorDrawable(Color.TRANSPARENT));
		CalendarViewAdapter adapterCalendar = new CalendarViewAdapter(getActivity(), calTemp, calStartTemp, this.showMode);
		adapterCalendar.setSelectedDate(calSelected);
		viewCalendar.setAdapter(adapterCalendar);
		viewCalendar.setOnTouchListener(this);
		return viewCalendar;
	}

	private void doPrevViewItem() {
		this.calendarAction = -1;
		this.viewFlipper.setInAnimation(slideRightIn);
		this.viewFlipper.setOutAnimation(slideRightOut);
		this.viewFlipper.showPrevious();
	}

	private void doNextViewItem() {
		this.calendarAction = 1;
		this.viewFlipper.setInAnimation(slideLeftIn);
		this.viewFlipper.setOutAnimation(slideLeftOut);
		this.viewFlipper.showNext();
	}

	class CalendarGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
					if (e1.getY() > e2.getY()) {
						showMode = 1;
					} else {
						showMode = 0;
					}
					setAllDate(calSelected);
					initCalendarView();
				}
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					doNextViewItem();
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					doPrevViewItem();
				}
			} catch (Exception e) {
			}
			return false;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			LinearLayout layoutCurrent = (LinearLayout) viewFlipper.getCurrentView();
			GridView gridCurrent = (GridView) layoutCurrent.getChildAt(0);
			CalendarViewAdapter adapterCurrent = (CalendarViewAdapter) gridCurrent.getAdapter();
			int pos = gridCurrent.pointToPosition((int) e.getX(), (int) e.getY());
			LinearLayout viewDay = (LinearLayout) gridCurrent.findViewById(pos + 5000);
			if (viewDay != null) {
				if (viewDay.getTag() != null) {
					Date date = (Date) viewDay.getTag();
					calSelected.setTime(date);
					adapterCurrent.setSelectedDate(calSelected);
					adapterCurrent.notifyDataSetChanged();
					displayDateDetail(calSelected);
					displayDateEvent(calSelected);
				}
			}
			return false;
		}
	}

	AnimationListener animationListener = new AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			buildCalendarView();
			setChangedDate();
		}
	};

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return this.gesturer.onTouchEvent(event);
	}

	@Override
	public void onClick(View v) {
		if (v == this.viewBackground) {
			if (this.showMode == 0) {
				this.showMode = 1;
			} else {
				this.showMode = 0;
			}
			setAllDate(calSelected);
			initCalendarView();
		}
		if (v == this.buttonPrevMonth) {
			doPrevViewItem();
		}
		if (v == this.buttonNextMonth) {
			doNextViewItem();
		}
		if (v == this.buttonToday) {
			this.calendarAction = 0;
			this.calSelected.setTime(this.calToday.getTime());
			setAllDate();
			initCalendarView();
			displayDateDetail(this.calSelected);
			displayDateEvent(this.calSelected);
		}
		if (v == this.buttonCreateEvent) {
			if (ShangXiang.APP.getLogin()) {
				goFragment(new CreateEvent());
			} else {
				goActivity(Login.class);
			}
		}
		if (v.getId() == R.id.calendar_show_event_button) {
			if (!TextUtils.isEmpty((String) v.getTag())) {
				Bundle bundle = new Bundle();
				bundle.putString("event", (String) v.getTag());
				goFragment(new ShowEvent(), bundle);
			}
		}
	}
}