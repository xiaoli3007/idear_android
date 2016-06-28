package com.wyj.popupwindow;


import java.util.Calendar;



import com.wyj.Activity.R;



import com.wyj.calendar.ChinaDate;

import com.wyj.select.AbstractWheel;
import com.wyj.select.AbstractWheelTextAdapter;
import com.wyj.select.OnWheelChangedListener;
import com.wyj.select.OnWheelScrollListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;



public class MyPopupWindowsBirthDayDate extends PopupWindow {
	
	private PopupWindow popupwindow;
	private Button addbirthday_date_confirm;
	private boolean scrolling = false;
	private LinearLayout addbirthday_select_yin,addbirthday_select_yang,addbirthday_yinli_layout,addbirthday_yangli_layout;
	private  AbstractWheel dateYearSelect ;
	private  AbstractWheel dateMonthSelect;
	private  AbstractWheel dateDaySelect;
	private int select_type=1;
	private boolean left = false;
	private View addbirthday_select_yin_bottom,addbirthday_select_yang_bottom;
	
	

	public MyPopupWindowsBirthDayDate( Context mContext, final View parent,  Activity activity,final OnSelectListener SelectListeners) {
		
		final Activity pactivity=activity;
		final Context mContexts=mContext;

		
		final View customView = View.inflate(mContext, R.layout.birthday_popwindow_date,
				null);
		addbirthday_date_confirm =(Button) customView.findViewById(R.id.addbirthday_date_confirm);
		
		addbirthday_select_yin=(LinearLayout) customView.findViewById(R.id.addbirthday_select_yin);
		addbirthday_select_yang=(LinearLayout) customView.findViewById(R.id.addbirthday_select_yang);
//		addbirthday_yinli_layout=(LinearLayout) customView.findViewById(R.id.addbirthday_yinli_layout);
//		addbirthday_yangli_layout=(LinearLayout) customView.findViewById(R.id.addbirthday_yangli_layout);
		
		addbirthday_select_yin_bottom=(View) customView.findViewById(R.id.addbirthday_select_yin_bottom);
		addbirthday_select_yang_bottom=(View) customView.findViewById(R.id.addbirthday_select_yang_bottom);
		scroll_select(mContexts, customView,select_type); //默认阴历
		
		addbirthday_select_yin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(left){
					select_type=1;
					scroll_select(mContexts, customView,select_type); //默认阴历
					left=false;
					addbirthday_select_yin_bottom.setVisibility(View.VISIBLE);
					addbirthday_select_yang_bottom.setVisibility(View.GONE);
				}
			}
		});
		
		addbirthday_select_yang.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!left){
					select_type=2;
					scroll_select(mContexts, customView,select_type); //默认阴历
					left=true;
					addbirthday_select_yin_bottom.setVisibility(View.GONE);
					addbirthday_select_yang_bottom.setVisibility(View.VISIBLE);
				}
			}
		});
		
		

		popupwindow = new PopupWindow(customView);
		
		 //以下为弹窗后面的背景色设置
	 	ColorDrawable cd = new ColorDrawable(0x000000);
	 	popupwindow.setBackgroundDrawable(cd); 
	   	//产生背景变暗效果
	    WindowManager.LayoutParams lp=activity.getWindow().getAttributes(); 
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
		
		// popupwindow.showAtLocation(parent, Gravity.NO_GRAVITY, location[0],
		// location[1]-popupwindow.getHeight());//显示在button的上面
		 popupwindow.showAsDropDown(parent,0,-200); //显示在button的下面

		// 自定义view添加触摸事件
		popupwindow.update();
		popupwindow.setOnDismissListener(new OnDismissListener() {		//恢复背景色
			
			public void onDismiss() {
				// TODO Auto-generated method stub
				WindowManager.LayoutParams lp=pactivity.getWindow().getAttributes();
    			lp.alpha = 1f;
    			pactivity.getWindow().setAttributes(lp);
			}
		});
		
		addbirthday_date_confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			
				Calendar calendar = Calendar.getInstance();
				int Year=calendar.get(Calendar.YEAR)+dateYearSelect.getCurrentItem();
				int Month=dateMonthSelect.getCurrentItem()+1;
				int Day=dateDaySelect.getCurrentItem()+1;
//				Log.i("aaaa", "-----时间-"+calendar.get(Calendar.YEAR));
//				Log.i("aaaa", "-----时间2-"+dateYearSelect.getCurrentItem());
				String date=Year+"年"+frontCompWithZore(Month,2)+"月"+frontCompWithZore(Day,2)+"日";
				if(select_type==1){
					date=Year+"年"+ChinaDate.getChinaDate_month(Month)+ChinaDate.getChinaDate(Day);
				}
				
				String date_h=Year+"-"+frontCompWithZore(Month,2)+"-"+frontCompWithZore(Day,2);
				SelectListeners.OnSelect(date_h,select_type);
				
				
				TextView list_find_zany=(TextView) parent;
				list_find_zany.setText(date);
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
	
	
	private void scroll_select(final Context mContexts,View customView,final int select){
		
		//插件滚动开始-------------------------------------------------------------------------------------------------
		final Calendar calendar = Calendar.getInstance();

		dateYearSelect = (AbstractWheel) customView.findViewById(R.id.addbirthday_year);
		dateMonthSelect = (AbstractWheel) customView.findViewById(R.id.addbirthday_month);
		dateDaySelect = (AbstractWheel) customView.findViewById(R.id.addbirthday_day);
		
		dateYearSelect.setCyclic(true);
		dateMonthSelect.setCyclic(true);
		dateDaySelect.setCyclic(true);
		DateYearAdapter dateYearAdapter = new DateYearAdapter(mContexts);
		dateYearSelect.setViewAdapter(dateYearAdapter);
		dateYearSelect.addScrollingListener(new OnWheelScrollListener() {
			public void onScrollingStarted(AbstractWheel wheel) {
				scrolling = true;
			}

			public void onScrollingFinished(AbstractWheel wheel) {
				scrolling = false;
				calendar.set(calendar.get(Calendar.YEAR) + dateYearSelect.getCurrentItem(), dateMonthSelect.getCurrentItem(), 1);
				DateDayAdapter dateDayAdapter = new DateDayAdapter(mContexts,select);
				dateDayAdapter.dayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				dateDaySelect.setViewAdapter(dateDayAdapter);
				dateDaySelect.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH) - 1);
			}
		});
		dateYearSelect.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
				if (!scrolling) {
					calendar.set(calendar.get(Calendar.YEAR) + dateYearSelect.getCurrentItem(), dateMonthSelect.getCurrentItem(), 1);
					DateDayAdapter dateDayAdapter = new DateDayAdapter(mContexts,select);
					dateDayAdapter.dayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
					dateDaySelect.setViewAdapter(dateDayAdapter);
					dateDaySelect.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH) - 1);
				}
			}
		});

		DateMonthAdapter dateMonthAdapter = new DateMonthAdapter(mContexts,select);
		dateMonthSelect.setViewAdapter(dateMonthAdapter);
		dateMonthSelect.addScrollingListener(new OnWheelScrollListener() {
			public void onScrollingStarted(AbstractWheel wheel) {
				scrolling = true;
			}

			public void onScrollingFinished(AbstractWheel wheel) {
				scrolling = false;
				calendar.set(calendar.get(Calendar.YEAR) + dateYearSelect.getCurrentItem(), dateMonthSelect.getCurrentItem(), 1);
				DateDayAdapter dateDayAdapter = new DateDayAdapter(mContexts,select);
				dateDayAdapter.dayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				dateDaySelect.setViewAdapter(dateDayAdapter);
				dateDaySelect.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH) - 1);
			}
		});
		dateMonthSelect.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
				if (!scrolling) {
					calendar.set(calendar.get(Calendar.YEAR) + dateYearSelect.getCurrentItem(), dateMonthSelect.getCurrentItem(), 1);
					DateDayAdapter dateDayAdapter = new DateDayAdapter(mContexts,select);
					dateDayAdapter.dayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
					dateDaySelect.setViewAdapter(dateDayAdapter);
					dateDaySelect.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH) - 1);
				}
			}
		});
		
		DateDayAdapter dateDayAdapter = new DateDayAdapter(mContexts,select);
		dateDayAdapter.dayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		dateDaySelect.setViewAdapter(dateDayAdapter);
		
		dateMonthSelect.setCurrentItem(calendar.get(Calendar.MONTH));
		dateDaySelect.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH) - 1);
		//插件滚动结束-------------------------------------------------------------------------------------------------
	}
	
	/** 
	  * 将元数据前补零，补后的总长度为指定的长度，以字符串的形式返回 
	  * @param sourceDate 
	  * @param formatLength 
	  * @return 重组后的数据 
	  */  
	 public  String frontCompWithZore(int sourceDate,int formatLength)  
	 {  
	  /* 
	   * 0 指前面补充零 
	   * formatLength 字符总长度为 formatLength 
	   * d 代表为正数。 
	   */  
	  String newString = String.format("%0"+formatLength+"d", sourceDate);  
	  return  newString;  
	 }
	
	
	
	private class DateYearAdapter extends AbstractWheelTextAdapter {
		Calendar calendar = Calendar.getInstance();

		protected DateYearAdapter(Context context) {
			super(context, R.layout.select_custom_text, NO_RESOURCE);
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
			yearView.setText((this.calendar.get(Calendar.YEAR) + index) + "年");
			return view;
		}
	}

	private class DateMonthAdapter extends AbstractWheelTextAdapter {
		int type;
		public   String[] nStr1 = new String[] { "", "正", "二", "三", "四",
			"五", "六", "七", "八", "九", "十", "冬", "腊" };
		protected DateMonthAdapter(Context context,int select) {
			super(context, R.layout.select_custom_text, NO_RESOURCE);
			this.type=select;
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
			if(this.type==2){
				monthView.setText((index + 1) + "月");
			}else{
				monthView.setText( ChinaDate.getChinaDate_month((index + 1)));
			}
			
			return view;
		}
	}

	private class DateDayAdapter extends AbstractWheelTextAdapter {
		int dayCount = 30;
		int type;
		
		protected DateDayAdapter(Context context,int select) {
			super(context, R.layout.select_custom_text, NO_RESOURCE);
			this.type=select;
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
			
			
			if(this.type==2){
				monthView.setText((index + 1) + "日");
			}else{
				monthView.setText( ChinaDate.getChinaDate((index + 1)));
			}
			return view;
		}
	}
	
	public interface OnSelectListener {
		void OnSelect(String result,int type);
	}


	
	

}


