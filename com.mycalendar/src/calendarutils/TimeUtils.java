package calendarutils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import data.DateInfo;


import android.annotation.SuppressLint;
import android.text.format.Time;
import android.util.Log;


public class TimeUtils {
	public static int getCurrentYear() {
		Time t = new Time();
		t.setToNow();
		return t.year;
	}

	public static int getCurrentMonth() {
		Time t = new Time();
		t.setToNow();
		return t.month + 1;
	}

	public static int getCurrentDay() {
		Time t = new Time();
		t.setToNow();
		return t.monthDay;
	}

	/**
     * 将元数据前补零，补后的总长度为指定的长度，以字符串的形式返回
     * @param sourceDate
     * @param formatLength
     * @return 重组后的数据
     */
    public static  String frontCompWithZore(int sourceDate,int formatLength)
    {
     /*
      * 0 指前面补充零
      * formatLength 字符总长度为 formatLength
      * d 代表为正数。
      */
     String newString = String.format("%0"+formatLength+"d", sourceDate);
     return  newString;
    }

   @SuppressLint("SimpleDateFormat")
public static int compare_date(String DATE1, String DATE2) {


        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                //System.out.println("dt1 在dt2前");
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                //System.out.println("dt1在dt2后");
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

   @SuppressLint("SimpleDateFormat")
public static long getdays(String today,String otherday){

	   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		 Date beginDate ,endDate;
		try {

			 beginDate = format.parse(today);
			 endDate= format.parse(otherday);

			 long days=(endDate.getTime()-beginDate.getTime())/(24*60*60*1000);
			 Log.i("dddd", "week---相隔的天数=-"+days);
			 return days;

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	//算出这一天的position （月历用到）
	public static int getmonthposition( int originYear, int originMonth, int originDay ) {

		int position=500; //默认是500
		int year = TimeUtils.getCurrentYear(), month = TimeUtils.getCurrentMonth(); 	//当天的 年 月

		String a=getFormatDate(year,month,1);
		String b=getFormatDate(originYear,originMonth,1);

		int compare=compare_date(a,b);
		Log.i("eeee", "----"+compare);
		if(compare>0){



			position=500-((year-originYear)*12-(originMonth-month));

			Log.i("eeee", "月历----时间小于当前的时间 position在500之前"+position);
		}else if(compare<0){

			Log.i("eeee", "月历----时间大于当前的时间 position在500之后");

			position = 500+((originYear-year)*12-(month-originMonth));

		}else{
			position=500;
		}

		Log.i("eeee", "月历----最终的"+position);
		return position;
	}
	//算出这一天的position （周历用到）
	public static int getweekposition( int originYear, int originMonth, int originDay ) {
    	//int year = TimeUtils.getCurrentYear(), month = TimeUtils.getCurrentMonth() ,day = TimeUtils.getCurrentDay();
		int position=500;

		int year = TimeUtils.getCurrentYear(), month = TimeUtils.getCurrentMonth(),day = TimeUtils.getCurrentDay();
		//当天的 年 月

		String a=getFormatDate(year,month,day);
		String b=getFormatDate(originYear,originMonth,originDay);	//传过来的年 月日

		String formatDate= TimeUtils.getFormatDate(year, month,day);
		int weekday = TimeUtils.getWeekDay(formatDate); // 算出 当天是周几  , 周日是 0

		Log.i("dddd", "week--对比当前日期--"+a+"传过来的日期"+b);

		int compare=compare_date(a,b);
		Log.i("dddd", "week----"+compare);
		if(compare>0){


			long days=getdays(b,a);
			if(days<=weekday){	//当天那周
				position=500;
			}else{
				int cc=Integer.parseInt(String.valueOf(days));
				//int c = cc/7>0?cc/7:1;
				int c = 0 ;
				if(cc/7>0){
					if(cc%7>=0){

						if(cc%7>weekday){
							c =cc/7+1;
						}else{
							c =cc/7;
						}
					}
				}else{
					c =1;
				}
				position = 500-c;

				Log.i("dddd", "week----余数是-"+cc%7);
				Log.i("dddd", "week----时间小于当前的时间 position在500之前"+position+"--减位置--"+c+"相差天数--"+cc);
			}
		}else if(compare<0){

			long days=getdays(a,b);
			if(days<7-weekday){	//当天那周
				position=500;
			}else{

				int cc=Integer.parseInt(String.valueOf(days));
				int c = 0 ;
				if(cc/7>0){
					if(cc%7>=0){

						if(cc%7>=7-weekday){
							c =cc/7+1;
						}else{
							c =cc/7;
						}
					}
				}else{
					c =1;
				}



				position = 500+c;
				Log.i("dddd", "week----时间大于当前的时间 position在500之后-位置-"+position+"--加位置--"+c+"相差天数--"+cc);
			}
		}else{

			position=500;
		}
    	return position;
	}


	public static int getTimeByPosition(int position, int originYear, int originMonth, String type) {
    	int year = TimeUtils.getCurrentYear(), month = TimeUtils.getCurrentMonth();
    	if (position > 500) {
    		for (int i = 500; i < position; i++) {
    			month++;
    			if (month == 13) {
    				month = 1;
    				year++;
    			}
    		}
    	} else if (position < 500) {
    		for (int i = 500; i > position; i--) {
    			month--;
    			if (month == 0) {
    				month = 12;
    				year--;
    			}
    		}
    	}
    	if (type.equals("year")) {
    		return year;
    	}
    	return month;
	}
	//算出周历 的 上几天 后几天的 日期
	public static Date getWeekTimeByPosition(int position ) {
    	//int year = TimeUtils.getCurrentYear(), month = TimeUtils.getCurrentMonth() ,day = TimeUtils.getCurrentDay();

    	Calendar calTemp = Calendar.getInstance();
    	//Date calendarday = new Date(originYear - 1900, originMonth, originDay);
    	//Log.i("cccc", "--------"+originYear+"-------"+originMonth+"-----------"+originDay);
    	//calTemp.set(originYear, originMonth-1, originDay);		//设置当前天
    	//calTemp.set(year, month-1, day);		//设置当前天

    	calTemp.setTime(calTemp.getTime());
    	int cha=0;
    	if (position < 500) {

    		cha=(500-position)*7;
    		Date jizhun= get_weekday_time(calTemp.getTime(), -cha); 		//这是那天的具体 时间   但是要算出 那一周的 最后一天 也就是 周六那天
    		@SuppressWarnings("deprecation")
			String formatDate= TimeUtils.getFormatDate(jizhun.getYear()+1900, jizhun.getMonth()+1,jizhun.getDate());
    		int weekday = TimeUtils.getWeekDay(formatDate); // 算出 传过来的天是周几  , 周日是 0
    		if(weekday!=6){	//不是周六 算出周六
    			Calendar newcalTemp = Calendar.getInstance();
    			newcalTemp.setTime(jizhun);
    			Date jizhunnew= get_weekday_time(newcalTemp.getTime(), 6-weekday);
    			return  jizhunnew;
    		}else{

    			return  jizhun;
    		}
    	}

    	if (position > 500) {

    		cha=(position-500)*7;
    		Date jizhun= get_weekday_time(calTemp.getTime(), cha);
    		@SuppressWarnings("deprecation")
			String formatDate= TimeUtils.getFormatDate(jizhun.getYear()+1900, jizhun.getMonth()+1,jizhun.getDate());
    		int weekday = TimeUtils.getWeekDay(formatDate); // 算出 传过来的天是周几  , 周日是 0
    		if(weekday!=6){	//不是周六 算出周六
    			Calendar newcalTemp = Calendar.getInstance();
    			newcalTemp.setTime(jizhun);
    			Date jizhunnew= get_weekday_time(newcalTemp.getTime(), 6-weekday);
    			return  jizhunnew;
    		}else{

    			return  jizhun;
    		}

    	}

    	return calTemp.getTime();
	}

	//获得上周下周的 基准天的 时间
	@SuppressLint("SimpleDateFormat")
	public static Date  get_weekday_time(Date calendarday ,int days){

		// 日期处理模块 (将日期加上某些天或减去天数)返回字符串
        Calendar canlendar = Calendar.getInstance(); // java.util包
        canlendar.setTime(calendarday);
        canlendar.add(Calendar.DAY_OF_WEEK, days); // 日期减 如果不够减会将月变动

        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
//        Date todayDate=new Date();
//        long beforeTime=(todayDate.getTime()/1000)-60*60*24*7;
//        todayDate.setTime(beforeTime*1000);
//        java.text.Format formatter=new SimpleDateFormat("yyyy-MM-dd");
//        String beforeDate=formatter.format(todayDate);


        Log.i("cccc", "ceshi----------月-"+"--最后的"+formatter.format(canlendar.getTime()));

        return canlendar.getTime();
	}



	@SuppressLint("SimpleDateFormat")
	public static int getWeekDay(String date) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            calendar.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1) {
            dayOfWeek = 0;
        }
        else {
            dayOfWeek -= 1;
        }
        return dayOfWeek;
    }


	public static boolean isLeapYear(int year) {
		if (year % 400 == 0 || year % 100 != 0 && year % 4 == 0) {
			return true;
		}
		return false;
	}

	public static int getDaysOfMonth(int year, int month) {
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			return 31;
		case 4:
		case 6:
		case 9:
		case 11:
			return 30;
		default:
			if (isLeapYear(year)) {
				return 29;
			}
			return 28;
		}
	}

	public static String getFormatDate(int year, int month) {
    	String formatYear = year + "";
    	String formatMonth = "";
    	if (month < 10) {
    		formatMonth = "0" + month;
    	} else {
    		formatMonth = month + "";
    	}
    	return formatYear + "-" + formatMonth + "-01";
	}

	public static String getFormatDate(int year, int month, int day) {
    	String formatYear = year + "";
    	String formatMonth = "";
    	String formatDay = "";
    	if (month < 10) {
    		formatMonth = "0" + month;
    	} else {
    		formatMonth = month + "";
    	}
    	if (day < 10) {
    		formatDay = "0" + day;
    	} else {
    		formatDay = day + "";
    	}
    	return formatYear + "-" + formatMonth + "-" + formatDay;
	}

	//月历数据遍历
	@SuppressLint("SimpleDateFormat")
	public static List<DateInfo> initCalendar(String formatDate, int month)  {


		int dates = 1;
		int year = Integer.parseInt(formatDate.substring(0, 4));
		int [] allDates = new int[42];
		for (int i = 0; i < allDates.length; i++) {
			allDates[i] = -1;
		}
		int firstDayOfMonth = TimeUtils.getWeekDay(formatDate);
		int totalDays = TimeUtils.getDaysOfMonth(year, month);
		//Log.i("aaaa", "异常是-----是------------"+totalDays +"------------"+ firstDayOfMonth);
		int curdays=totalDays + firstDayOfMonth;
		for (int i = firstDayOfMonth; i < curdays; i++) {
    		allDates[i] = dates;
    		dates++;
    	}

		List<DateInfo> list = new ArrayList<DateInfo>();
		DateInfo dateInfo;
		for (int i = 0; i < allDates.length; i++) {
    		dateInfo = new DateInfo();
    		dateInfo.setDate(allDates[i]);
    		dateInfo.setYear(year);
    		dateInfo.setMonth(month);

    		if (allDates[i] == -1) {
    			dateInfo.setNongliDate("---");
    			dateInfo.setThisMonth(false);
    			dateInfo.setWeekend(false);
    		}else {
    			String date = TimeUtils.getFormatDate(year, month, allDates[i]);
    			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    			long time = 0;
				try {
					time = sdf.parse(date).getTime();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			Lunar lunar = new Lunar(time);
    			if (lunar.isSFestival()) {
					dateInfo.setNongliDate(StingUtil.toLength(lunar.getSFestivalName(), 12));
					dateInfo.setHoliday(true);
					dateInfo.setHoliday(lunar.getSFestivalName());
				} else {
					if (lunar.isLFestival() && lunar.getLunarMonthString().substring(0, 1).equals("闰") == false) {
						dateInfo.setNongliDate(StingUtil.toLength(lunar.getLFestivalName(), 12));
						dateInfo.setHoliday(true);
						dateInfo.setHoliday(lunar.getLFestivalName());
					} else {
						if (lunar.getLunarDayString().equals("初一")) {
							dateInfo.setNongliDate(lunar.getLunarMonthString() + "月");
						} else {
							dateInfo.setNongliDate(lunar.getLunarDayString());
						}
						dateInfo.setHoliday(false);
					}
				}

    			dateInfo.setNongliInfo(lunar.getLunarMonthString() + "月"+lunar.getLunarDayString());	//农历日期 info

    			dateInfo.setNonglinumber(frontCompWithZore(lunar.getLunarMonth(),2)+frontCompWithZore(lunar.getLunarDay(),2));
    			dateInfo.setThisMonth(true);
    			int t = getWeekDay(getFormatDate(year, month, allDates[i]));
    			if (t == 0 || t == 6) {
    				dateInfo.setWeekend(true);
    			}
    			else {
    				dateInfo.setWeekend(false);
    			}
    		}
    		list.add(dateInfo);
    	}



    	int front = DataUtils.getFirstIndexOf(list);
    	int back = DataUtils.getLastIndexOf(list);
    	int lastmonth=month - 1;
    	if(lastmonth==0){
    		lastmonth=12;
    	}
    	int lastMonthDays = getDaysOfMonth(year, lastmonth);
    	//Log.i("aaaa", "上个月最后一天"+lastMonthDays+"本月集合的最后是"+back+"本月集合的第一个是"+front+"集合的大小为"+list.size());
    	int nextMonthDays = 1;
	    	for (int i = front - 1; i >= 0; i--) {
	    		list.get(i).setDate(lastMonthDays);
	    		list.get(i).setNongliDate(get_nongli(year,month - 1,lastMonthDays));

	    		lastMonthDays--;
	    	}
	    	for (int i = back + 1; i < list.size(); i++) {
	    		list.get(i).setDate(nextMonthDays);
	    		list.get(i).setNongliDate(get_nongli(year,month + 1,nextMonthDays));
	    		nextMonthDays++;
	    	}

    	return list;
	}



	//周历数据遍历
	@SuppressLint("SimpleDateFormat")
	public static List<DateInfo> initCalendarWeek(int year, int month,int day)  {

		String formatDate= TimeUtils.getFormatDate(year, month,day);
		int [] allDates = new int[7];
		for (int i = 0; i < allDates.length; i++) {
			allDates[i] = -1;
		}
		int weekday = TimeUtils.getWeekDay(formatDate); // 算出 传过来的天是周几  , 周日是 0

		//int howmanydays = TimeUtils.getDaysOfMonth(year, month); //当天月的总天数

		 Calendar canlendar = Calendar.getInstance();
		 canlendar.set(year, month-1, day) ;
		 canlendar.add(Calendar.DAY_OF_MONTH, -weekday);
		 Date weekfirstday=canlendar.getTime();
		 SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

		 Log.i("bbbb", "第一天是-----------------老天-"+formatDate+"------------周--"+weekday+"---第一天是---"+sdf2.format(weekfirstday));
		 List<DateInfo> list = new ArrayList<DateInfo>();
		 DateInfo dateInfo;
		for (int i = 0; i < allDates.length; i++) {

			Calendar forcanlendar = Calendar.getInstance();
			forcanlendar.setTime(weekfirstday);
			forcanlendar.add(Calendar.DAY_OF_MONTH, i);

			Log.i("aaaa", "异常是是---------"+formatDate +"-------周---"+ weekday+"----这周第"+(i+1)+"天为"+sdf2.format(forcanlendar.getTime()));
			dateInfo = new DateInfo();
    		dateInfo.setDate(forcanlendar.get(Calendar.DATE));
    		dateInfo.setYear(forcanlendar.get(Calendar.YEAR));
    		dateInfo.setMonth(forcanlendar.get(Calendar.MONTH)+1);

			String date = TimeUtils.getFormatDate(forcanlendar.get(Calendar.YEAR), forcanlendar.get(Calendar.MONTH)+1, forcanlendar.get(Calendar.DATE));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			long time = 0;
			try {
				time = sdf.parse(date).getTime();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Lunar lunar = new Lunar(time);
			if (lunar.isSFestival()) {
				dateInfo.setNongliDate(StingUtil.toLength(lunar.getSFestivalName(), 12));
				dateInfo.setHoliday(true);
				dateInfo.setHoliday(lunar.getSFestivalName());
			} else {
				if (lunar.isLFestival() && lunar.getLunarMonthString().substring(0, 1).equals("闰") == false) {
					dateInfo.setNongliDate(StingUtil.toLength(lunar.getLFestivalName(), 12));
					dateInfo.setHoliday(true);
					dateInfo.setHoliday(lunar.getLFestivalName());
				} else {
					if (lunar.getLunarDayString().equals("初一")) {
						dateInfo.setNongliDate(lunar.getLunarMonthString() + "月");
					} else {
						dateInfo.setNongliDate(lunar.getLunarDayString());
					}
					dateInfo.setHoliday(false);
				}
			}

			dateInfo.setNongliInfo(lunar.getLunarMonthString() + "月"+lunar.getLunarDayString());	//农历日期 info
			dateInfo.setNonglinumber(frontCompWithZore(lunar.getLunarMonth(),2)+frontCompWithZore(lunar.getLunarDay(),2));
			dateInfo.setThisMonth(true);
			int t = getWeekDay(getFormatDate(year, month, allDates[i]));
			if (t == 0 || t == 6) {
				dateInfo.setWeekend(true);
			}
			else {
				dateInfo.setWeekend(false);
			}

    		list.add(dateInfo);
    	}

    	return list;
	}


	@SuppressLint("SimpleDateFormat")
	public static String get_nongli(int year,int  month ,int day){

		String date = TimeUtils.getFormatDate(year, month, day);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long time = 0;
		try {
			time = sdf.parse(date).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Lunar lunar = new Lunar(time);
		if (lunar.isSFestival()) {

			return 	StingUtil.toLength(lunar.getSFestivalName(), 12);

		} else {
			if (lunar.isLFestival() && lunar.getLunarMonthString().substring(0, 1).equals("闰") == false) {
				return	StingUtil.toLength(lunar.getLFestivalName(), 12);
			} else {
				if (lunar.getLunarDayString().equals("初一")) {
					return	lunar.getLunarMonthString() + "月";
				} else {
					return	lunar.getLunarDayString();
				}
			}

		}
	}

	@SuppressLint("SimpleDateFormat")
	public static <lunar> Lunar get_nongli_info(int year,int  month ,int day){

		String date = TimeUtils.getFormatDate(year, month, day);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long time = 0;
		try {
			time = sdf.parse(date).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Lunar lunar = new Lunar(time);
		return lunar;
	}



}
