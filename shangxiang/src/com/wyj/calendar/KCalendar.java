package com.wyj.calendar;


import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import com.wyj.Activity.R;
import com.wyj.utils.StingUtil;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

/**
 * 日历控件
 * 
 * @author huangyin
 */

public class KCalendar extends ViewFlipper implements
		android.view.GestureDetector.OnGestureListener {
	public static final int COLOR_BG_WEEK_TITLE = Color.parseColor("#F6F8F7"); // 星期标题背景颜色
	public static final int COLOR_TX_WEEK_TITLE = Color.parseColor("#DC9B7B"); // 星期标题文字颜色  黄色
	public static final int COLOR_TX_THIS_MONTH_DAY = Color
			.parseColor("#aa564b4b"); // 当前月日历数字颜色
	public static final int COLOR_TX_OTHER_MONTH_DAY = Color
			.parseColor("#ffcccccc"); // 其他月日历数字颜色
	public static final int COLOR_TX_THIS_DAY = Color.parseColor("#FD3316"); // 当天日历数字颜色	红色
	public static final int COLOR_BG_THIS_DAY = Color.parseColor("#FCF3D6"); // 当天日历背景颜色
	public static final int COLOR_BG_CALENDAR = Color.parseColor("#F6F8F7"); // 日历背景色

	private GestureDetector gd; // 手势监听器
	private Animation push_left_in; // 动画-左进
	private Animation push_left_out; // 动画-左出
	private Animation push_right_in; // 动画-右进
	private Animation push_right_out; // 动画-右出
	
	private Animation fade_in,fade_out; // 动画

	private int ROWS_TOTAL = 6; // 日历的行数
	private int COLS_TOTAL = 7; // 日历的列数
	private String[][] dates = new String[ROWS_TOTAL][7]; // 当前日历日期 (月)
	private String[][] week_dates = new String[1][7]; // 当前日历日期 （周）
	private float tb;

	private OnCalendarClickListener onCalendarClickListener; // 日历翻页回调
	private OnCalendarDateChangedListener onCalendarDateChangedListener; // 日历点击回调

	private String[] weekday = new String[] { "日", "一", "二", "三", "四", "五", "六" }; // 星期标题

	private int calendarYear; // 日历年份
	private int calendarMonth; // 日历月份
	private Date thisday = new Date(); // 今天
	private Date calendarday; // 日历这个月第一天(1号)

	private LinearLayout firstCalendar; // 第一个日历
	private LinearLayout secondCalendar; // 第二个日历
	private LinearLayout currentCalendar; // 当前显示的日历
	ChinaDate china = new ChinaDate();
	private Resources resources;

	private Map<String, Integer> marksMap = new HashMap<String, Integer>(); // 储存某个日子被标注(Integer
																			// 为bitmap
																			// res
																			// id)
	private Map<String, Integer> dayBgColorMap = new HashMap<String, Integer>(); // 储存某个日子的背景色
	
	public int isweek=0; // 0为月历  1 为周历  主要是区别 左右切换
	
	private boolean isweek_bool; // 

	public KCalendar(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		resources=context.getResources();
		Log.i("bbbb", "-------------------->佛里2222");
		init();
	}

	public KCalendar(Context context) {
		super(context);
		resources=context.getResources();
		Log.i("bbbb", "-------------------->佛里11");
		init();
	}
	
	
	/**
	 * enable or 周月 历 切换
	 * 
	 * @param enable
	 */
	public void setWeek(boolean enable) {
		
//		fade_in = AnimationUtils.loadAnimation(getContext(),
//				R.anim.fade_in);
//		fade_in.setDuration(1000);
//		fade_out = AnimationUtils.loadAnimation(getContext(),
//				R.anim.fade_out);
//		fade_out.setDuration(1000);
//		setInAnimation(fade_in);
//		setOutAnimation(fade_out);
		
		if (!enable) { // disable, hide the content
			removeAllViews();
			isweek=0;
			init();
		} else {
			
			Log.i("bbbb", "-----周历来了---");
			removeAllViews();
			
			isweek=1;
			init_week();
		}
	}


	private void init() {
		
		Resources res = getResources();
		tb = res.getDimension(R.dimen.historyscore_tb);
		setBackgroundColor(COLOR_BG_CALENDAR);
		// 实例化收拾监听器
		gd = new GestureDetector(this);
		// 初始化日历翻动动画
		push_left_in = AnimationUtils.loadAnimation(getContext(),
				R.anim.push_left_in);
		push_left_out = AnimationUtils.loadAnimation(getContext(),
				R.anim.push_left_out);
		push_right_in = AnimationUtils.loadAnimation(getContext(),
				R.anim.push_right_in);
		push_right_out = AnimationUtils.loadAnimation(getContext(),
				R.anim.push_right_out);
		push_left_in.setDuration(200);
		push_left_out.setDuration(200);
		push_right_in.setDuration(200);
		push_right_out.setDuration(200);
		// 初始化第一个日历
		firstCalendar = new LinearLayout(getContext());
		firstCalendar.setOrientation(LinearLayout.VERTICAL);
		firstCalendar.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
		// 初始化第二个日历
		secondCalendar = new LinearLayout(getContext());
		secondCalendar.setOrientation(LinearLayout.VERTICAL);
		secondCalendar.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
		// 设置默认日历为第一个日历
		currentCalendar = firstCalendar;
		// 加入ViewFlipper
		addView(firstCalendar);
		addView(secondCalendar);
		// 绘制线条框架
		drawFrame(firstCalendar);
		drawFrame(secondCalendar);
		if(calendarday==null){
		// 设置日历上的日子(1号)
		calendarYear = thisday.getYear() + 1900;
		calendarMonth = thisday.getMonth();
		calendarday = new Date(calendarYear - 1900, calendarMonth, 1);
		// 回调
				if (onCalendarDateChangedListener != null) {
					onCalendarDateChangedListener.onCalendarDateChanged(calendarYear,
							calendarMonth + 1);
				}
		}else{
			
			calendarYear = calendarday.getYear() + 1900;
			calendarMonth = calendarday.getMonth();
			calendarday = new Date(calendarYear - 1900, calendarMonth, 1);
			// 回调
					if (onCalendarDateChangedListener != null) {
						onCalendarDateChangedListener.onCalendarDateChanged(calendarYear,
								calendarMonth + 1);
					}
			
			
		}
		

		
		
		// 填充展示日历
		setCalendarDate();
	}

	private void drawFrame(LinearLayout oneCalendar) {
		
		// 添加周末线性布局
		LinearLayout title = new LinearLayout(getContext());
//		title.setBackgroundColor(COLOR_BG_WEEK_TITLE);
//		title.setOrientation(LinearLayout.HORIZONTAL);
//		LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(-1, 0,
//				0.5f);
//		Resources res = getResources();
//		tb = res.getDimension(R.dimen.historyscore_tb);
//		layout.setMargins(0, 0, 0, (int) (tb * 1.2));
//		title.setLayoutParams(layout);
		oneCalendar.addView(title);

		// 添加周末TextView
//		for (int i = 0; i < COLS_TOTAL; i++) {
//			TextView view = new TextView(getContext());
//			view.setGravity(Gravity.CENTER);
//			view.setText(weekday[i]);
//			if(weekday[i].equals("日") || weekday[i].equals("六") ){
//				view.setTextColor(COLOR_TX_WEEK_TITLE);	
//			}else{
//				view.setTextColor(COLOR_TX_OTHER_MONTH_DAY);	
//			}
//			
//			view.setLayoutParams(new LinearLayout.LayoutParams(0, -1, 1));
//			title.addView(view);
//		}

		// 添加日期布局
		LinearLayout content = new LinearLayout(getContext());
		content.setOrientation(LinearLayout.VERTICAL);
		content.setLayoutParams(new LinearLayout.LayoutParams(-1, 0, 7f));
		oneCalendar.addView(content);

		// 添加日期TextView
		for (int i = 0; i < ROWS_TOTAL; i++) {
			LinearLayout row = new LinearLayout(getContext());
			row.setOrientation(LinearLayout.HORIZONTAL);
			row.setLayoutParams(new LinearLayout.LayoutParams(
					android.view.ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
			content.addView(row);
			// 绘制日历上的列
			for (int j = 0; j < COLS_TOTAL; j++) {
				RelativeLayout col = new RelativeLayout(getContext());
				col.setLayoutParams(new LinearLayout.LayoutParams(0,
						android.view.ViewGroup.LayoutParams.MATCH_PARENT, 1));
				// col.setBackgroundResource(R.drawable.calendar_day_bg);
				row.addView(col);
				// 给每一个日子加上监听
				col.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						ViewGroup parent = (ViewGroup) v.getParent();
						int row = 0, col = 0;

						// 获取列坐标
						for (int i = 0; i < parent.getChildCount(); i++) {
							if (v.equals(parent.getChildAt(i))) {
								col = i;
								break;
							}
						}
						// 获取行坐标
						ViewGroup pparent = (ViewGroup) parent.getParent();
						for (int i = 0; i < pparent.getChildCount(); i++) {
							if (parent.equals(pparent.getChildAt(i))) {
								row = i;
								break;
							}
						}
						if (onCalendarClickListener != null) {
							onCalendarClickListener.onCalendarClick(row, col,
									dates[row][col]);
						}
					}
				});
			}
		}
		
		
	}

	/**
	 * 填充日历(包含日期、标记、背景等)
	 */
	private void setCalendarDate() {
		// 根据日历的日子获取这一天是星期几
		int weekday = calendarday.getDay();
		// 每个月第一天
		int firstDay = 1;
		// 每个月中间号,根据循环会自动++
		int day = firstDay;
		// 每个月的最后一天
		int lastDay = getDateNum(calendarday.getYear(), calendarday.getMonth());
		// 下个月第一天
		int nextMonthDay = 1;
		int lastMonthDay = 1;

		// 填充每一个空格
		for (int i = 0; i < ROWS_TOTAL; i++) {
			for (int j = 0; j < COLS_TOTAL; j++) {
				// 这个月第一天不是礼拜天,则需要绘制上个月的剩余几天
				if (i == 0 && j == 0 && weekday != 0) {
					int year = 0;
					int month = 0;
					int lastMonthDays = 0;
					// 如果这个月是1月，上一个月就是去年的12月
					if (calendarday.getMonth() == 0) {
						year = calendarday.getYear() - 1;
						month = Calendar.DECEMBER;
					} else {
						year = calendarday.getYear();
						month = calendarday.getMonth() - 1;
					}
					// 上个月的最后一天是几号
					lastMonthDays = getDateNum(year, month);
					// 第一个格子展示的是几号
					int firstShowDay = lastMonthDays - weekday + 1;
					// 上月
					for (int k = 0; k < weekday; k++) {
						lastMonthDay = firstShowDay + k;
						RelativeLayout group = getDateView(0, k);
						RelativeLayout rl = new RelativeLayout(getContext()); 
						RelativeLayout.LayoutParams params11 = new RelativeLayout.LayoutParams(
								ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
						rl.setGravity(Gravity.CENTER);
						
						//group.setGravity(Gravity.CENTER);
						TextView view = null;
						TextView view_2 = null; //阴历
						if (group.getChildCount() > 0) {
							
							RelativeLayout old = (RelativeLayout) group.getChildAt(0);
							view = (TextView) old.getChildAt(0);
							//阴历-----------------------------------
							view_2 = (TextView) old.getChildAt(1);
						} else {
							RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
									ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
								params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
								params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
								view = new TextView(getContext());
								view.setId(1);
								rl.addView(view,params);
									//阴历-----------------------------------
								RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
										ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
								params2.addRule(RelativeLayout.BELOW,1); 
								params2.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
								view_2 = new TextView(getContext());
								rl.addView(view_2,params2);
								group.addView(rl,params11);
						}
						view.setText(Integer.toString(lastMonthDay));
						view.setTextColor(COLOR_TX_OTHER_MONTH_DAY);
						//阴历-----------------------------------

						set_china_nongli(view_2,new Date(year, month, lastMonthDay));
	
							
						dates[0][k] = format(new Date(year, month, lastMonthDay));
						// 设置日期背景色
						if (dayBgColorMap.get(dates[0][k]) != null) {
							group.setBackgroundColor(COLOR_TX_OTHER_MONTH_DAY);
						} else {
							group.setBackgroundColor(Color.TRANSPARENT);
						}
						// 设置标记
						setMarker(group, 0, k);
					}
					j = weekday - 1;
					// 这个月第一天是礼拜天，不用绘制上个月的日期，直接绘制这个月的日期
					
				} else {
					
					RelativeLayout group = getDateView(i, j);
					RelativeLayout rl = new RelativeLayout(getContext()); 
					RelativeLayout.LayoutParams params11 = new RelativeLayout.LayoutParams(
							ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
					rl.setGravity(Gravity.CENTER);
					
					rl.setId(5);
					
					TextView view = null;
					TextView view_2 = null; //阴历
					if (group.getChildCount() > 0) {
						RelativeLayout old = (RelativeLayout) group.getChildAt(0);
						view = (TextView) old.getChildAt(0);
						//阴历-----------------------------------
						view_2 = (TextView) old.getChildAt(1);
						
					} else {
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
							ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
						params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
						params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
						view = new TextView(getContext());
						view.setId(1);
						//view.setLayoutParams(params);
						//view.setGravity(Gravity.CENTER);
						rl.addView(view,params);
							//阴历-----------------------------------
						RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
								ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
						params2.addRule(RelativeLayout.BELOW,1); 
						params2.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
						view_2 = new TextView(getContext());	
						rl.addView(view_2,params2);
						
						group.addView(rl,params11);
						}
					
					
					
					
					// 本月
					if (day <= lastDay) {
						dates[i][j] = format(new Date(calendarday.getYear(),
								calendarday.getMonth(), day));
						
						view.setText(Integer.toString(day));
						
						//阴历----------------------------------------------------------------------------------------------
						
						set_china_nongli(view_2,new Date(calendarday.getYear(),
								calendarday.getMonth(), day));
						//阴历结束----------------------------------------------------------------------------------------------
						
						// 上面首先设置了一下默认的"当天"背景色，当有特殊需求时，才给当日填充背景色
						// 设置日期背景色
						if (dayBgColorMap.get(dates[i][j]) != null) {
//							view.setTextColor(Color.WHITE);
						//	group.setBackgroundColor(COLOR_TX_OTHER_MONTH_DAY);
							group.setBackgroundDrawable(resources.getDrawable(R.drawable.calendar_background_selected));
						}else{
							// 当天
							if (thisday.getDate() == day
									&& thisday.getMonth() == calendarday.getMonth()
									&& thisday.getYear() == calendarday.getYear()) {
								//view.setText("今天");
								//group.setBackgroundColor(COLOR_BG_THIS_DAY);
								group.setBackgroundDrawable(resources.getDrawable(R.drawable.calendar_background_today));
								view.setTextColor(COLOR_TX_THIS_DAY);
								view_2.setTextColor(COLOR_TX_THIS_DAY); //COLOR_TX_WEEK_TITLE
							} else {
								view.setTextColor(COLOR_TX_THIS_MONTH_DAY);
								group.setBackgroundColor(Color.TRANSPARENT);
							}
							
						}
						// 设置标记
						setMarker(group, i, j);
						day++;
						// 下个月
					} else {
						

						Lunar lunar =null;
						if (calendarday.getMonth() == Calendar.DECEMBER) {
							dates[i][j] = format(new Date(
									calendarday.getYear() + 1,
									Calendar.JANUARY, nextMonthDay));
							
							//阴历-----------------------------------
							 
							set_china_nongli(view_2,new Date(
											calendarday.getYear() + 1,
											Calendar.JANUARY, nextMonthDay));
							
						} else {
							dates[i][j] = format(new Date(
									calendarday.getYear(),
									calendarday.getMonth() + 1, nextMonthDay));
							
							//阴历-----------------------------------
							set_china_nongli(view_2,new Date(
										calendarday.getYear(),
										calendarday.getMonth() + 1, nextMonthDay));
							 
						}
						//Log.i("aaaa", "------阴历4444-----"+(dates[i][j])+"--------"+(nextMonthDay));
						view.setText(Integer.toString(nextMonthDay));
						view.setTextColor(COLOR_TX_OTHER_MONTH_DAY);
						
						//阴历----------------------------------------------------------------------------------------------

						//阴历结束----------------------------------------------------------------------------------------------
						// 设置日期背景色
						if (dayBgColorMap.get(dates[i][j]) != null) {
							// view.setBackgroundResource(dayBgColorMap
							// .get(dates[i][j]));
							group.setBackgroundColor(COLOR_TX_OTHER_MONTH_DAY);
						} else {
							group.setBackgroundColor(Color.TRANSPARENT);
						}
						// 设置标记
						setMarker(group, i, j);
						nextMonthDay++;
					}
				}
			}
		}
	}
	
	
	
	 
	
	private void init_week() {
		
		Resources res = getResources();
		tb = res.getDimension(R.dimen.historyscore_tb);	//衡量大小的值
		// TODO Auto-generated method stub
		setBackgroundColor(COLOR_BG_CALENDAR);
		// 实例化收拾监听器
		gd = new GestureDetector(this);
		// 初始化日历翻动动画
		push_left_in = AnimationUtils.loadAnimation(getContext(),
				R.anim.push_left_in);
		push_left_out = AnimationUtils.loadAnimation(getContext(),
				R.anim.push_left_out);
		push_right_in = AnimationUtils.loadAnimation(getContext(),
				R.anim.push_right_in);
		push_right_out = AnimationUtils.loadAnimation(getContext(),
				R.anim.push_right_out);
		push_left_in.setDuration(300);
		push_left_out.setDuration(300);
		push_right_in.setDuration(300);
		push_right_out.setDuration(300);
		// 初始化第一个日历
		firstCalendar = new LinearLayout(getContext());
		firstCalendar.setOrientation(LinearLayout.VERTICAL);
		firstCalendar.setLayoutParams(new LinearLayout.LayoutParams(-1, (int) (tb * 5)));
		
	//	Log.i("cccc", "---------------->kuan---");
		// 初始化第二个日历
		secondCalendar = new LinearLayout(getContext());
		secondCalendar.setOrientation(LinearLayout.VERTICAL);
		secondCalendar.setLayoutParams(new LinearLayout.LayoutParams(-1, (int) (tb * 5)));
		// 设置默认日历为第一个日历
		currentCalendar = firstCalendar;
		// 加入ViewFlipper
		addView(firstCalendar);
		addView(secondCalendar);
		// 绘制线条框架
		drawFrame_week(firstCalendar);
		drawFrame_week(secondCalendar);
		if(calendarday==null){
			// 设置日历上的日子(1号)
			calendarYear = thisday.getYear() + 1900;
			calendarMonth = thisday.getMonth();
			calendarday = new Date(calendarYear - 1900, calendarMonth, 1);
			
			// 回调
			if (onCalendarDateChangedListener != null) {
				onCalendarDateChangedListener.onCalendarDateChanged(calendarYear,
						calendarMonth + 1);
			}
		}
		
		// 填充展示日历
		setCalendarDate_week();
	}
	
	private void drawFrame_week(LinearLayout oneCalendar) {
		
		// 添加周末线性布局
		LinearLayout title = new LinearLayout(getContext());
		oneCalendar.addView(title);
	
		// 添加日期布局
		LinearLayout content = new LinearLayout(getContext());
		content.setOrientation(LinearLayout.VERTICAL);
		content.setLayoutParams(new LinearLayout.LayoutParams(-1, 0, 7f));
		oneCalendar.addView(content);

		// 添加日期TextView
	
			LinearLayout row = new LinearLayout(getContext());
			row.setOrientation(LinearLayout.HORIZONTAL);
			row.setLayoutParams(new LinearLayout.LayoutParams(
					android.view.ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
			content.addView(row);
			// 绘制日历上的列
			for (int j = 0; j < COLS_TOTAL; j++) {
				RelativeLayout col = new RelativeLayout(getContext());
				col.setLayoutParams(new LinearLayout.LayoutParams(0,
						android.view.ViewGroup.LayoutParams.MATCH_PARENT, 1));
				// col.setBackgroundResource(R.drawable.calendar_day_bg);
				row.addView(col);
				// 给每一个日子加上监听
				col.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						ViewGroup parent = (ViewGroup) v.getParent();
						int row = 0, col = 0;

						// 获取列坐标
						for (int i = 0; i < parent.getChildCount(); i++) {
							if (v.equals(parent.getChildAt(i))) {
								col = i;
								break;
							}
						}
						// 获取行坐标
						ViewGroup pparent = (ViewGroup) parent.getParent();
						for (int i = 0; i < pparent.getChildCount(); i++) {
							if (parent.equals(pparent.getChildAt(i))) {
								row = i;
								break;
							}
						}
						if (onCalendarClickListener != null) {
							onCalendarClickListener.onCalendarClick(row, col,
									week_dates[row][col]);
						}
					}
				});
			}
		
		
		
	}

	/**
	 * 填充日历(包含日期、标记、背景等) 周历
	 */
	private void setCalendarDate_week() {
		// 根据日历的日子获取这一天是星期几
		int weekday = calendarday.getDay();
		// 当前的日历的主显示   day  也就是一个月的几号
		int day = calendarday.getDate();
		
		Log.i("cccc", "---->周历年--"+calendarday.getYear()+"------->月--"+calendarday.getMonth()+"------->日--"+day);
		Log.i("cccc", "---->------->星期--"+weekday);
			
		//算出 上个月剩余天数   基准月的天数  和  下个月的天数---------------------------------------------------------------------------
		
		int howmanydays = getDateNum(calendarday.getYear(), calendarday.getMonth());	//基准月的这个月一共多少天
		
		int pre_day_nums=0; //上个月的天数
		int numsjizhun=0; //基准月的天数
		int numsnext=0; //下月的天数
		
		
		if(weekday!=0){
			
			if(day-weekday<=0){
				
				if(day-1>weekday){
					numsjizhun=weekday;
				}else{
					numsjizhun=day+(7-weekday-1);
				}
				pre_day_nums=7-numsjizhun;  //绘制上个月的天的数目
			}else{
				
				pre_day_nums =0;  //上个月的天的数目
				
				if(day+(7-weekday-1)>howmanydays){  //说明有下个月的天数
					
					int  nums_next_year,nums_next_month;
					if (calendarday.getMonth() == Calendar.DECEMBER) {	 //基准月为12月那么 下个月为1 月
						nums_next_year = calendarday.getYear() + 1;
						nums_next_month = 0;
					} else {
						nums_next_year = calendarday.getYear();
						nums_next_month = calendarday.getMonth() + 1;
					}
					Date nextcalendarday = new Date(nums_next_year, nums_next_month, 1);
					
				  numsjizhun=nextcalendarday.getDay();	//基准月的天数就是以下月一号的 周几而定
				}else{
					
				  numsjizhun=7; 
				 
				}
				
			}
			
			
			if(7-numsjizhun-pre_day_nums>0){
				numsnext=7-numsjizhun-pre_day_nums; //下月的天数	
			}else{
			    numsnext=0; //下月的天数
			}
			
		}else{
			
			//当天是周末那么 这一天 就排第一个 （先输出基准月的这几天 然后 如果 有下个月 在输出下个月的前几天）
			
			pre_day_nums=0; //上个月为0天
			if(howmanydays-day<7){		//证明 该周历存在下个月的日子
				
				numsjizhun=howmanydays-day+1;  //基准月的天数
				
				numsnext=7-numsjizhun;  //下个月的天数
			}else{		//没有下个月的日子
				
				numsjizhun=7;  //基准月的天数  当天周历全是基准月的日子
				numsnext=0; //下个月日子为0天
			}
		}
		
		Log.i("cccc", "---->------->上个月天数"+pre_day_nums+"基准天数"+numsjizhun+"下月天数"+numsnext);
		
		
		 //-----------------------------------------------------------------------------------绘制上个月的几天
		  int lastMonthDay = 1; // 上个月第一天
		 if(pre_day_nums>0){   
			int year = 0;
			int month = 0;
			int lastMonthDays = 0;
			// 如果这个月是1月，上一个月就是去年的12月
			if (calendarday.getMonth() == 0) {
				year = calendarday.getYear() - 1;
				month = Calendar.DECEMBER;
			} else {
				year = calendarday.getYear();
				month = calendarday.getMonth() - 1;
			}
			// 上个月的最后一天是几号
			lastMonthDays = getDateNum(year, month);
			// 第一个格子展示的是几号
			int firstShowDay = lastMonthDays - pre_day_nums + 1;
			
			for (int k = 0; k < pre_day_nums; k++) {
				
				 
				lastMonthDay = firstShowDay + k;
				RelativeLayout group = getDateView(0, k);
				RelativeLayout rl = new RelativeLayout(getContext()); 
				RelativeLayout.LayoutParams params11 = new RelativeLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
				rl.setGravity(Gravity.CENTER);
				
				//group.setGravity(Gravity.CENTER);
				TextView view = null;
				TextView view_2 = null; //阴历
				if (group.getChildCount() > 0) {
					
					RelativeLayout old = (RelativeLayout) group.getChildAt(0);
					view = (TextView) old.getChildAt(0);
					//阴历-----------------------------------
					view_2 = (TextView) old.getChildAt(1);
				} else {
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
							ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
						params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
						params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
						view = new TextView(getContext());
						view.setId(1);
						rl.addView(view,params);
							//阴历-----------------------------------
						RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
								ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
						params2.addRule(RelativeLayout.BELOW,1); 
						params2.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
						view_2 = new TextView(getContext());
						rl.addView(view_2,params2);
						group.addView(rl,params11);
				}
				view.setText(Integer.toString(lastMonthDay));
				view.setTextColor(COLOR_TX_OTHER_MONTH_DAY);
				//阴历-----------------------------------
//				String[] yinli=format_china(new Date(year, month, lastMonthDay));
//				view_2.setText(yinli[1]);
//				if(yinli[0].equals("1")){
//					view_2.setTextColor(COLOR_TX_THIS_DAY); //COLOR_TX_WEEK_TITLE
//				}else{
//					view_2.setTextColor(COLOR_TX_WEEK_TITLE);
//				}						
				
				set_china_nongli(view_2,new Date(year, month, lastMonthDay));
				//数据存储  背景色  和生日标记-------------------
				week_dates[0][k] = format(new Date(year, month, lastMonthDay)); //加入数据存储
				// 设置日期背景色
				if (dayBgColorMap.get(week_dates[0][k]) != null) {
					group.setBackgroundColor(COLOR_TX_OTHER_MONTH_DAY);
				} else {
					group.setBackgroundColor(Color.TRANSPARENT);
				}
				// 设置标记
				setMarker(group, 0, k); 
				
			}
			
		}
		//-----------------------------------------------------------------------------------绘制上个月的几天结束
		
		//-----------------------------------------------------------------------------------绘制基准月的几天
		 if(numsjizhun>0){		//绘制当天的前面几天 + 为基准的这一天
			 
			
			 
			
			  for (int b = 0; b < numsjizhun; b++) {		//循环的最后一个应该为基准的这一天
				 	
				 	
				 	int erevr=day-(weekday-pre_day_nums)+b; //遍历基准的天;
					RelativeLayout group = getDateView(0, b+pre_day_nums);  //列 数目加上上个月的几天
					RelativeLayout rl = new RelativeLayout(getContext()); 
					RelativeLayout.LayoutParams params11 = new RelativeLayout.LayoutParams(
							ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
					rl.setGravity(Gravity.CENTER);
					
					//group.setGravity(Gravity.CENTER);
					TextView view = null;
					TextView view_2 = null; //阴历
					if (group.getChildCount() > 0) {
						
						RelativeLayout old = (RelativeLayout) group.getChildAt(0);
						view = (TextView) old.getChildAt(0);
						//阴历-----------------------------------
						view_2 = (TextView) old.getChildAt(1);
					} else {
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
								ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
							params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
							params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
							view = new TextView(getContext());
							view.setId(1);
							rl.addView(view,params);
								//阴历-----------------------------------
							RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
									ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
							params2.addRule(RelativeLayout.BELOW,1); 
							params2.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
							view_2 = new TextView(getContext());
							rl.addView(view_2,params2);
							group.addView(rl,params11);
					}
					view.setText(Integer.toString(erevr));
					view.setTextColor(COLOR_TX_OTHER_MONTH_DAY);
					//阴历-----------------------------------
//					String[] yinli=format_china(new Date(calendarday.getYear(), calendarday.getMonth(), erevr));
//					view_2.setText(yinli[1]);
//					if(yinli[0].equals("1")){
//						view_2.setTextColor(COLOR_TX_THIS_DAY); //COLOR_TX_WEEK_TITLE
//					}else{
//						view_2.setTextColor(COLOR_TX_WEEK_TITLE);
//					}						
					set_china_nongli(view_2,new Date(calendarday.getYear(), calendarday.getMonth(), erevr));
					//数据存储  背景色  和生日标记-------------------
					week_dates[0][b+pre_day_nums] = format(new Date(calendarday.getYear(), calendarday.getMonth(), erevr)); //加入数据存储
					if (dayBgColorMap.get(week_dates[0][b+pre_day_nums]) != null) {

						group.setBackgroundDrawable(resources.getDrawable(R.drawable.calendar_background_selected));
					}else{
						// 当天
						if (thisday.getDate() == erevr
								&& thisday.getMonth() == calendarday.getMonth()
								&& thisday.getYear() == calendarday.getYear()) {
							//view.setText("今天");
							group.setBackgroundDrawable(resources.getDrawable(R.drawable.calendar_background_today));
							view.setTextColor(COLOR_TX_THIS_DAY);
							view_2.setTextColor(COLOR_TX_THIS_DAY); //COLOR_TX_WEEK_TITLE
						} else {
							view.setTextColor(COLOR_TX_THIS_MONTH_DAY);
							group.setBackgroundColor(Color.TRANSPARENT);
						}
					}
					// 设置标记
					setMarker(group, 0, b+pre_day_nums);
					
				}
			 
		 }
		 
		//-----------------------------------------------------------------------------------绘制基准月的几天结束 	
		
		//-----------------------------------------------------------------------------------绘制下月的几天 
		 
		 int nextMonthDay = 1; // 下个月第一天
		 int next_year,next_month;
		 
		 if(numsnext>0){
			 
			  for (int c = 0; c < numsnext; c++) {		//循环的最后一个应该为基准的这一天
				 	
				  if (calendarday.getMonth() == Calendar.DECEMBER) {	 //基准月为12月那么 下个月为1 月
						next_year = calendarday.getYear() + 1;
						next_month = 0;
					} else {
						next_year = calendarday.getYear();
						next_month = calendarday.getMonth() + 1;
					}
					RelativeLayout group = getDateView(0, c+pre_day_nums+numsjizhun);  //列数目
					RelativeLayout rl = new RelativeLayout(getContext()); 
					RelativeLayout.LayoutParams params11 = new RelativeLayout.LayoutParams(
							ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
					rl.setGravity(Gravity.CENTER);
					
					//group.setGravity(Gravity.CENTER);
					TextView view = null;
					TextView view_2 = null; //阴历
					if (group.getChildCount() > 0) {
						
						RelativeLayout old = (RelativeLayout) group.getChildAt(0);
						view = (TextView) old.getChildAt(0);
						//阴历-----------------------------------
						view_2 = (TextView) old.getChildAt(1);
					} else {
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
								ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
							params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
							params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
							view = new TextView(getContext());
							view.setId(1);
							rl.addView(view,params);
								//阴历-----------------------------------
							RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
									ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
							params2.addRule(RelativeLayout.BELOW,1); 
							params2.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
							view_2 = new TextView(getContext());
							rl.addView(view_2,params2);
							group.addView(rl,params11);
					}
					view.setText(Integer.toString(nextMonthDay));
					view.setTextColor(COLOR_TX_OTHER_MONTH_DAY);
					//阴历-----------------------------------
//					String[] yinli=format_china(new Date(next_year, next_month, nextMonthDay));
//					view_2.setText(yinli[1]);
//					if(yinli[0].equals("1")){
//						view_2.setTextColor(COLOR_TX_THIS_DAY); //COLOR_TX_WEEK_TITLE
//					}else{
//						view_2.setTextColor(COLOR_TX_WEEK_TITLE);
//					}						
					set_china_nongli(view_2,new Date(next_year, next_month, nextMonthDay));
					week_dates[0][c+pre_day_nums+numsjizhun] = format(new Date(next_year, next_month, nextMonthDay)); //加入数据存储
					if (dayBgColorMap.get(week_dates[0][c+pre_day_nums+numsjizhun]) != null) {

						group.setBackgroundColor(COLOR_TX_OTHER_MONTH_DAY);
					} else {
						group.setBackgroundColor(Color.TRANSPARENT);
					}
					// 设置标记
					setMarker(group, 0, c+pre_day_nums+numsjizhun);
					
					nextMonthDay++;  //
					
					
				}
			 
		 }
		//-----------------------------------------------------------------------------------绘制下月的几天结束 
		
		
	}
	

	
	/**
	 * onClick接口回调
	 */
	public interface OnCalendarClickListener {
		void onCalendarClick(int row, int col, String dateFormat);
	}

	/**
	 * ondateChange接口回调
	 */
	public interface OnCalendarDateChangedListener {
		void onCalendarDateChanged(int year, int month);
	}

	/**
	 * 根据具体的某年某月，展示一个日历
	 * 
	 * @param year
	 * @param month
	 */
	public void showCalendar(int year, int month) {
		calendarYear = year;
		calendarMonth = month - 1;
		calendarday = new Date(calendarYear - 1900, calendarMonth, 1);
		setCalendarDate();
	}

	/**
	 * 根据当前月，展示一个日历
	 * 
	 * @param year
	 * @param month
	 */
	public void showCalendar() {
		Date now = new Date();
		calendarYear = now.getYear() + 1900;
		calendarMonth = now.getMonth();
		calendarday = new Date(calendarYear - 1900, calendarMonth, 1);
		setCalendarDate();
	}

	/**
	 * 下一月日历
	 */
	public synchronized void nextMonth() {
		// 改变日历上下顺序
		if (currentCalendar == firstCalendar) {
			currentCalendar = secondCalendar;
		} else {
			currentCalendar = firstCalendar;
		}
		// 设置动画
		setInAnimation(push_left_in);
		setOutAnimation(push_left_out);
		// 改变日历日期
		if (calendarMonth == Calendar.DECEMBER) {
			calendarYear++;
			calendarMonth = Calendar.JANUARY;
		} else {
			calendarMonth++;
		}
		calendarday = new Date(calendarYear - 1900, calendarMonth, 1);
		dayBgColorMap.clear();
		// 填充日历
		setCalendarDate();
		// 下翻到下一月
		showNext();
		// 回调
		if (onCalendarDateChangedListener != null) {
			onCalendarDateChangedListener.onCalendarDateChanged(calendarYear,
					calendarMonth + 1);
		}
	}

	/**
	 * 上一月日历
	 */
	public synchronized void lastMonth() {
		if (currentCalendar == firstCalendar) {
			currentCalendar = secondCalendar;
		} else {
			currentCalendar = firstCalendar;
		}
		setInAnimation(push_right_in);
		setOutAnimation(push_right_out);
		if (calendarMonth == Calendar.JANUARY) {
			calendarYear--;
			calendarMonth = Calendar.DECEMBER;
		} else {
			calendarMonth--;
		}
		calendarday = new Date(calendarYear - 1900, calendarMonth, 1);
		
		dayBgColorMap.clear();
		setCalendarDate();
		showPrevious();
		if (onCalendarDateChangedListener != null) {
			onCalendarDateChangedListener.onCalendarDateChanged(calendarYear,
					calendarMonth + 1);
		}
	}
	
	/**
	 * 下一月周历
	 */
	public synchronized void nextWeek() {
		// 改变日历上下顺序
		

//		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 格式化日期
//		  String beforeDate = sdf.format(get_weekday_time(-7));
		  
		  Date newdate=get_weekday_time(7); //计算那周的这天是什么日子 
		  Log.i("cccc", "----------下周的--------->"+newdate.getYear());
		if (currentCalendar == firstCalendar) {
			currentCalendar = secondCalendar;
		} else {
			currentCalendar = firstCalendar;
		}
		// 设置动画
		setInAnimation(push_left_in);
		setOutAnimation(push_left_out);
		
//		Log.i("cccc", "下个月切换------》-----");
		if(newdate.getMonth()!=calendarday.getMonth()){  //月份变了 那就是下个月的了   月份响应加 一
			
			//改变月份
			if (calendarMonth == Calendar.DECEMBER) {
				calendarYear++;
				calendarMonth = Calendar.JANUARY;
			} else {
				calendarMonth++;
			}
			
		}
		
		calendarday = newdate;  //给予新的 日期 
		dayBgColorMap.clear();
		// 填充日历
		setCalendarDate_week();
		// 下翻到下一月
		showNext();
		// 回调
		if (onCalendarDateChangedListener != null) {
			onCalendarDateChangedListener.onCalendarDateChanged(calendarYear,
					calendarMonth + 1);
		}
	}

	/**
	 * 上一月周历
	 */
	public synchronized void lastWeek() {
		
		  Date newdate=get_weekday_time(-7); //计算那周的这天是什么日子 
		//  Log.i("cccc", "----------下周的--------->"+newdate.getYear());
		  
		if (currentCalendar == firstCalendar) {
			currentCalendar = secondCalendar;
		} else {
			currentCalendar = firstCalendar;
		}
		setInAnimation(push_right_in);
		setOutAnimation(push_right_out);
		
		
		//Log.i("cccc", "上个月切换------》");
		
		if(newdate.getMonth()!=calendarday.getMonth()){  //月份变了 那就是下个月的了   月份响应减 一
			
			if (calendarMonth == Calendar.JANUARY) {
				calendarYear--;
				calendarMonth = Calendar.DECEMBER;
			} else {
				calendarMonth--;
			}
			
		}
		calendarday = newdate;
		
		dayBgColorMap.clear();
		setCalendarDate_week();
		showPrevious();
		if (onCalendarDateChangedListener != null) {
			onCalendarDateChangedListener.onCalendarDateChanged(calendarYear,
					calendarMonth + 1);
		}
	}
	
	
	//设置农历节日的 文字和显示的颜色   （新的方法）
	public  void set_china_nongli(TextView view_2,Date date){
		//view_2.setSingleLine(true);
		Lunar lunar = new Lunar(date);
		if (lunar.isSFestival()) {
			view_2.setText(StingUtil.toLength(lunar.getSFestivalName(), 9));
			view_2.setTextColor(COLOR_TX_WEEK_TITLE);
		} else {
			if (lunar.isLFestival() && lunar.getLunarMonthString().substring(0, 1).equals("闰") == false) {
				view_2.setText(StingUtil.toLength(lunar.getLFestivalName(), 9));
				view_2.setTextColor(COLOR_TX_THIS_DAY);
			} else {
				if (lunar.getLunarDayString().equals("初一")) {
					
					view_2.setText(lunar.getLunarMonthString() + "月");
				} else {
					
					view_2.setText(lunar.getLunarDayString());
				}
				view_2.setTextColor(COLOR_TX_WEEK_TITLE);
			}
		}
	}
	
	//设置农历节日的 文字和显示的颜色   （新的方法）
	public  void set_china_nongli_old(TextView view_2,Date date){
			//view_2.setSingleLine(true);
			String[] yinli=format_china(date);
			view_2.setText(yinli[1]);
			if(yinli[0].equals("1")){
				view_2.setTextColor(COLOR_TX_THIS_DAY); //COLOR_TX_WEEK_TITLE
			}else{
				view_2.setTextColor(COLOR_TX_WEEK_TITLE);
			}	
	}
	
	public Date  get_weekday_time(int days){
		
		// 日期处理模块 (将日期加上某些天或减去天数)返回字符串
        Calendar canlendar = Calendar.getInstance(); // java.util包
        canlendar.setTime(calendarday);
        canlendar.add(Calendar.DATE, days); // 日期减 如果不够减会将月变动
        return canlendar.getTime();
	}
	/**
	 * 获取日历当前年份
	 */
	public int getCalendarYear() {
		return calendarday.getYear() + 1900;
	}

	/**
	 * 获取日历当前月份
	 */
	public int getCalendarMonth() {
		return calendarday.getMonth() + 1;
	}

	/**
	 * 在日历上做一个标记
	 * 
	 * @param date
	 *            日期
	 * @param id
	 *            bitmap res id
	 */
	public void addMark(Date date, int id) {
		addMark(format(date), id);
	}

	/**
	 * 在日历上做一个标记
	 * 
	 * @param date
	 *            日期
	 * @param id
	 *            bitmap res id
	 */
	void addMark(String date, int id) {
		marksMap.put(date, id);
		if(isweek==1){
			setCalendarDate_week();
		}else{
			setCalendarDate();
		}
	}

	/**
	 * 在日历上做一组标记
	 * 
	 * @param date
	 *            日期
	 * @param id
	 *            bitmap res id
	 */
	public void addMarks(Date[] date, int id) {
		for (int i = 0; i < date.length; i++) {
			marksMap.put(format(date[i]), id);
		}
		if(isweek==1){
			setCalendarDate_week();
		}else{
			setCalendarDate();
		}
	}

	/**
	 * 在日历上做一组标记
	 * 
	 * @param date
	 *            日期
	 * @param id
	 *            bitmap res id
	 */
	public void addMarks(List<String> date, int id) {
		for (int i = 0; i < date.size(); i++) {
			marksMap.put(date.get(i), id);
		}
		if(isweek==1){
			setCalendarDate_week();
		}else{
			setCalendarDate();
		}
		
		Log.i("cccc", "---------日历接受过来-----》"+marksMap.toString());
	}

	/**
	 * 移除日历上的标记
	 */
	public void removeMark(Date date) {
		removeMark(format(date));
	}

	/**
	 * 移除日历上的标记
	 */
	public void removeMark(String date) {
		marksMap.remove(date);
		if(isweek==1){
			setCalendarDate_week();
		}else{
			setCalendarDate();
		}
	}

	/**
	 * 移除日历上的所有标记
	 */
	public void removeAllMarks() {
		marksMap.clear();
		if(isweek==1){
			setCalendarDate_week();
		}else{
			setCalendarDate();
		}
	}

	/**
	 * 设置日历具体某个日期的背景色
	 * 
	 * @param date
	 * @param color
	 */
	public void setCalendarDayBgColor(Date date, int color) {
		setCalendarDayBgColor(format(date), color);
	}

	/**
	 * 设置日历具体某个日期的背景色
	 * 
	 * @param date
	 * @param color
	 */
	public void setCalendarDayBgColor(String date, int color) {
		dayBgColorMap.put(date, color);
		if(isweek==1){
		
		 setCalendarDate_week();
		}else{
		 setCalendarDate();
		}
	}

	/**
	 * 设置日历一组日期的背景色
	 * 
	 * @param date
	 * @param color
	 */
	public void setCalendarDaysBgColor(List<String> date, int color) {
		for (int i = 0; i < date.size(); i++) {
			dayBgColorMap.put(date.get(i), color);
		}
		if(isweek==1){
			setCalendarDate_week();
		}else{
			setCalendarDate();
		}
	}

	/**
	 * 设置日历一组日期的背景色
	 * 
	 * @param date
	 * @param color
	 */
	public void setCalendarDayBgColor(String[] date, int color) {
		for (int i = 0; i < date.length; i++) {
			dayBgColorMap.put(date[i], color);
		}
		if(isweek==1){
			setCalendarDate_week();
		}else{
			setCalendarDate();
		}
	}

	/**
	 * 移除日历具体某个日期的背景色
	 * 
	 * @param date
	 * @param color
	 */
	public void removeCalendarDayBgColor(Date date) {
		removeCalendarDayBgColor(format(date));
	}

	/**
	 * 移除日历具体某个日期的背景色
	 * 
	 * @param date
	 * @param color
	 */
	public void removeCalendarDayBgColor(String date) {
		dayBgColorMap.remove(date);
		if(isweek==1){
			setCalendarDate_week();
		}else{
			setCalendarDate();
		}
	}

	/**
	 * 移除日历具体某个日期的背景色
	 * 
	 * @param date
	 * @param color
	 */
	public void removeAllBgColor() {
		dayBgColorMap.clear();
		if(isweek==1){
		setCalendarDate_week();	
		}else{
		setCalendarDate();
		}
	}

	/**
	 * 根据行列号获得包装每一个日子的LinearLayout
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public String getDate(int row, int col) {
			if(isweek==1){
				return week_dates[row][col];
			}else{
				return dates[row][col];
			}
		
	}

	/**
	 * 某天是否被标记了
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public boolean hasMarked(String date) {
		return marksMap.get(date) == null ? false : true;
	}

	/**
	 * 清除所有标记以及背景
	 */
	public void clearAll() {
		marksMap.clear();
		dayBgColorMap.clear();
	}

	/***********************************************
	 * 
	 * private methods
	 * 
	 **********************************************/
	// 设置标记(生日)
	private void setMarker(RelativeLayout group, int i, int j) {
		int childCount = group.getChildCount();
		
		String curday="";
		if(isweek==1){
			curday=week_dates[i][j];
		}else{
			curday=dates[i][j]; 
		}
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				(int) (tb * 0.7), (int) (tb * 0.7));
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		ImageView markView= new ImageView(getContext());
		markView.setLayoutParams(params);
		RelativeLayout old = (RelativeLayout) group.getChildAt(0);
		old.setPadding(0, 0, 0, 10);
		group.addView(markView);

		if (marksMap.get(curday) != null) {
			
		//	Log.i("cccc", "到生日了啊----------"+curday+"----i->"+i+"------j->"+j+"子布局有"+childCount);
			ImageView birthday= (ImageView) group.getChildAt(1);
			birthday.setImageResource(marksMap.get(curday));
		  //birthday.setBackgroundResource(R.drawable.calendar_birthday_background);
		} else {
			if (childCount > 1) {
				group.removeView(group.getChildAt(1));
			}
		}

	}


	/**
	 * 计算某年某月有多少天
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	private int getDateNum(int year, int month) {
		Calendar time = Calendar.getInstance();
		time.clear();
		time.set(Calendar.YEAR, year + 1900);
		time.set(Calendar.MONTH, month);
		return time.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 根据行列号获得包装每一个日子的LinearLayout
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	private RelativeLayout getDateView(int row, int col) {
		return (RelativeLayout) ((LinearLayout) ((LinearLayout) currentCalendar
				.getChildAt(1)).getChildAt(row)).getChildAt(col);
	}

	/**
	 * 将Date转化成字符串->2013-3-3
	 */
	private String format(Date d) {
		return addZero(d.getYear() + 1900, 4) + "-"
				+ addZero(d.getMonth() + 1, 2) + "-" + addZero(d.getDate(), 2);
	}
	
	/**
	 * 将Date转化阴历
	 */
	private String[] format_china(Date d) {
		
		String[] yinli=china.oneDay(d.getYear() + 1900, d.getMonth() + 1, d.getDate());
		return yinli;
	}

	// 2或4
	private static String addZero(int i, int count) {
		if (count == 2) {
			if (i < 10) {
				return "0" + i;
			}
		} else if (count == 4) {
			if (i < 10) {
				return "000" + i;
			} else if (i < 100 && i > 10) {
				return "00" + i;
			} else if (i < 1000 && i > 100) {
				return "0" + i;
			}
		}
		return "" + i;
	}

	/***********************************************
	 * 
	 * Override methods  
	 * 
	 **********************************************/
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (gd != null) {
			if (gd.onTouchEvent(ev))
				return true;
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.gd.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// 向左/上滑动
		if (e1.getX() - e2.getX() > 20) {
			if(isweek==1){
			nextWeek();
			}else{	
			nextMonth();
			}
		}
		// 向右/下滑动
		else if (e1.getX() - e2.getX() < -20) {
			if(isweek==1){
				lastWeek();
				}else{	
				lastMonth();
				}
		
		}
		return false;
	}

	/***********************************************
	 * 
	 * get/set methods
	 * 
	 **********************************************/

	public OnCalendarClickListener getOnCalendarClickListener() {
		return onCalendarClickListener;
	}

	public void setOnCalendarClickListener(
			OnCalendarClickListener onCalendarClickListener) {
		this.onCalendarClickListener = onCalendarClickListener;
	}

	public OnCalendarDateChangedListener getOnCalendarDateChangedListener() {
		return onCalendarDateChangedListener;
	}

	public void setOnCalendarDateChangedListener(
			OnCalendarDateChangedListener onCalendarDateChangedListener) {
		this.onCalendarDateChangedListener = onCalendarDateChangedListener;
	}

	public Date getThisday() {
		return thisday;
	}

	public void setThisday(Date thisday) {
		this.thisday = thisday;
	}

	public Map<String, Integer> getDayBgColorMap() {
		return dayBgColorMap;
	}

	public void setDayBgColorMap(Map<String, Integer> dayBgColorMap) {
		this.dayBgColorMap = dayBgColorMap;
	}
}