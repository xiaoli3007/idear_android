package com.shangxiang.android.adapter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import com.shangxiang.android.R;
import com.shangxiang.android.ShangXiang;
import com.shangxiang.android.utils.LunarCalendarUtil;
import com.shangxiang.android.view.SquareLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class CalendarViewAdapter extends BaseAdapter {
	private Activity activity;
	private Resources resources;
	private Calendar calDate = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
	private Calendar calStartDate = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
	private Calendar calSelected = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
	private Calendar calToday = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
	private ArrayList<Date> titles;
	private LunarCalendarUtil calLunar;
	private int showMode = 0;

	public CalendarViewAdapter(Activity a, Calendar cal, Calendar calStart, int mode) {
		this.showMode = mode;
		this.activity = a;
		this.resources = this.activity.getResources();
		this.calDate = cal;
		this.calStartDate = calStart;
		this.titles = getDates();
	}

	@Override
	public int getCount() {
		return this.titles.size();
	}

	@Override
	public Object getItem(int position) {
		return this.titles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Date date = (Date) getItem(position);
		Calendar calCalendar = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
		calCalendar.setTime(date);
		int iDay = calCalendar.get(Calendar.DAY_OF_WEEK);

		LinearLayout iv = new SquareLayout(this.activity);
		iv.setGravity(Gravity.CENTER);
		iv.setOrientation(1);
		iv.setId(position + 5000);
		iv.setTag(date);

		if (iDay == 7) {
			iv.setBackgroundColor(this.resources.getColor(R.color.background_normal));
		} else if (iDay == 1) {
			iv.setBackgroundColor(this.resources.getColor(R.color.background_normal));
		} else {
			iv.setBackgroundColor(this.resources.getColor(R.color.background_normal));
		}

		if (equalsDate(this.calToday.getTime(), date)) {
			iv.setBackgroundDrawable(this.resources.getDrawable(R.drawable.calendar_background_today));
		} else {
			if (equalsDate(this.calSelected.getTime(), date)) {
				iv.setBackgroundDrawable(this.resources.getDrawable(R.drawable.calendar_background_selected));
			}
		}

		TextView viewDay = new TextView(this.activity);
		viewDay.setPadding(0, -10, 0, -7);
		viewDay.setGravity(Gravity.CENTER_HORIZONTAL);
		viewDay.setText(String.valueOf(date.getDate()));
		viewDay.setTextSize(17);
		viewDay.setId(position + 6000);

		TextView viewLunarDay = new TextView(this.activity);
		viewLunarDay.setPadding(0, 0, 0, 0);
		viewLunarDay.setGravity(Gravity.CENTER_HORIZONTAL);
		viewLunarDay.setTextSize(9);
		viewLunarDay.setLines(1);
		viewLunarDay.setEllipsize(TruncateAt.END);
		viewLunarDay.setId(position + 7000);

		this.calLunar = new LunarCalendarUtil(calCalendar);
		String nameBuddhismHoliday = getBuddhismHoliday(this.calLunar);
		if (null != nameBuddhismHoliday) {
			viewLunarDay.setText(nameBuddhismHoliday);
		} else {
			viewLunarDay.setText(this.calLunar.toString());
		}

		if (calCalendar.get(Calendar.MONTH) == this.calDate.get(Calendar.MONTH) || this.showMode == 1) {
			if (equalsDate(this.calToday.getTime(), date)) {
				viewDay.setTextColor(this.resources.getColor(R.color.text_red));
				viewLunarDay.setTextColor(this.resources.getColor(R.color.text_red));
			} else {
				viewDay.setTextColor(this.resources.getColor(R.color.text_normal));
				if (null != nameBuddhismHoliday) {
					viewLunarDay.setTextColor(this.resources.getColor(R.color.text_red));
				} else {
					viewLunarDay.setTextColor(this.resources.getColor(R.color.text_title));
				}
			}
		} else {
			viewDay.setTextColor(this.resources.getColor(R.color.text_hint));
			viewLunarDay.setTextColor(this.resources.getColor(R.color.text_hint));
		}

		iv.addView(viewDay, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		iv.addView(viewLunarDay, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		if (checkHaveEvent(calCalendar)) {
			LinearLayout point = new SquareLayout(this.activity);
			point.setId(position + 8000);
			point.setBackgroundDrawable(this.resources.getDrawable(R.drawable.calendar_background_point));
			iv.addView(point, new LinearLayout.LayoutParams(15, 15));
		}
		return iv;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	private ArrayList<Date> getDates() {
		ArrayList<Date> listDate = new ArrayList<Date>();
		if (this.showMode == 0) {
			for (int i = 0; i < 42; i++) {
				listDate.add(this.calStartDate.getTime());
				this.calStartDate.add(Calendar.DATE, 1);
			}
		} else {
			for (int i = 0; i < 7; i++) {
				listDate.add(this.calStartDate.getTime());
				this.calStartDate.add(Calendar.DATE, 1);
			}
		}
		return listDate;
	}

	public void setSelectedDate(Calendar cal) {
		this.calSelected = cal;
	}

	@SuppressWarnings("deprecation")
	private Boolean equalsDate(Date date1, Date date2) {
		if (date1.getYear() == date2.getYear() && date1.getMonth() == date2.getMonth() && date1.getDate() == date2.getDate()) {
			return true;
		} else {
			return false;
		}
	}

	@SuppressLint("SimpleDateFormat")
	private String getBuddhismHoliday(LunarCalendarUtil calLunar) {
		for (int i = 0; i < ShangXiang.jsonHolidays.length(); i++) {
			String holiday = ShangXiang.jsonHolidays.optString(i);
			String sepHoliday = holiday.substring(0, 4);
			String nameHoliday = holiday.substring(5);
			if (sepHoliday.equals(calLunar.getDayNum())) {
				return nameHoliday;
			}
		}
		return null;
	}

	@SuppressLint({ "UseValueOf", "SimpleDateFormat" })
	private Boolean checkHaveEvent(Calendar calendar) {
		for (int i = 0; i < ShangXiang.jsonEvents.length(); i++) {
			JSONObject event = ShangXiang.jsonEvents.optJSONObject(i);
			Timestamp dateTarget = new Timestamp(Long.valueOf(event.optString("reminddate", "") + "000"));
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			if (formatter.format(dateTarget.getTime()).equals(formatter.format(calendar.getTime()))) {
				return true;
			}
		}
		return false;
	}
}
