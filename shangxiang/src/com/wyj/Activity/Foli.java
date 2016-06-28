package com.wyj.Activity;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.wyj.calendar.ChinaDate;
import com.wyj.calendar.KCalendar;


import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;


import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wyj.calendar.KCalendar.OnCalendarClickListener;
import com.wyj.calendar.KCalendar.OnCalendarDateChangedListener;
import com.wyj.db_memberbirthday.MemberBirthday_model;
import com.wyj.http.WebApiUrl;
import com.wyj.pipe.Cms;
import com.wyj.pipe.SinhaPipeClient;
import com.wyj.pipe.SinhaPipeMethod;
import com.wyj.pipe.Utils;
import com.wyj.utils.StingUtil;
import com.wyj.Activity.R;

public class Foli extends MainActivity implements OnClickListener {

    String date = null;// 设置默认选中的日期 格式为 “2014-04-05” 标准DATE格式

    Button bt;
    KCalendar calendar;

    private TextView date_infos_left, date_infos_yangli, date_infos_yinli,
            date_infos_foli_or_birthday, add_user_birthday,
            date_infos_birthdayshow;
    private SinhaPipeClient httpClient;
    private SinhaPipeMethod httpMethod;
    private ProgressDialog pDialog = null;
    private JSONArray birthdaylist = new JSONArray();
    private LinearLayout date_add_layout;
    private ImageView foli_bottom_image;

    private LinearLayout Calendar_mains, Calendar_middle;
    private RelativeLayout Calendar_zong;

    // 滑动----------------------------------------
    private boolean mIsShrinking;
    private GestureDetector mGestureDetector;
    private MyGestureListener MymGestureListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.foli);
        findViewById();
        setAction();
        PopupWindows();
    }

    private void findViewById() {


        date_infos_left = (TextView) findViewById(R.id.date_infos_left);
        date_infos_yangli = (TextView) findViewById(R.id.date_infos_yangli);
        date_infos_yinli = (TextView) findViewById(R.id.date_infos_yinli);
        date_infos_foli_or_birthday = (TextView) findViewById(R.id.date_infos_foli_or_birthday);
        date_add_layout = (LinearLayout) findViewById(R.id.date_add_layout);

        date_infos_birthdayshow = (TextView) findViewById(R.id.date_infos_birthdayshow);
        add_user_birthday = (TextView) findViewById(R.id.add_user_birthday);
        add_user_birthday.setOnClickListener(this);
        date_infos_birthdayshow.setOnClickListener(this);

        Calendar_zong = (RelativeLayout) findViewById(R.id.Calendar_zong);
        Calendar_mains = (LinearLayout) findViewById(R.id.Calendar_mains);
        Calendar_middle = (LinearLayout) findViewById(R.id.Calendar_middle);

        foli_bottom_image = (ImageView) findViewById(R.id.foli_bottom_image);
        foli_bottom_image.setOnTouchListener(touchListener);

        Calendar_zong.setOnTouchListener(touchListener);
        MymGestureListener = new MyGestureListener();
        mGestureDetector = new GestureDetector(MymGestureListener);
        mGestureDetector.setIsLongpressEnabled(false);

        mIsShrinking = true;

    }

    private void setAction() {
        // TODO Auto-generated method stub
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // 获取当前年份
        int mMonth = c.get(Calendar.MONTH) + 1;// 获取当前月份
        int mDay = c.get(Calendar.DAY_OF_MONTH);// 获取当前月份的日期号码

        // Log.i("cccc", "------当前的日期----" + mYear + "---" + mMonth + "---" +
        // mDay);

        date_infos_left.setText(mDay + "");
        date_infos_yangli.setText(ChinaDate.get_yangli_today());
        date_infos_yinli.setText(ChinaDate.get_yinli(mYear, mMonth, mDay));
        if (!ChinaDate.oneDayiswhat(mYear, mMonth, mDay).equals("")) {
            date_infos_foli_or_birthday.setText(ChinaDate.oneDayiswhat(mYear,
                    mMonth, mDay));
        } else {
            date_infos_foli_or_birthday.setVisibility(View.GONE);
        }
    }

    private void set_action_time() {
        // TODO Auto-generated method stub

        String[] dates = StingUtil.split(date, "-");
        int mYear = Integer.valueOf(dates[0]).intValue(); // 获取当前年份
        int mMonth = Integer.valueOf(dates[1]).intValue();// 获取当前月份
        int mDay = Integer.valueOf(dates[2]).intValue();// 获取当前月份的日期号码
        // Log.i("cccc", "------点击日历选中的日期----" + mMonth);
        int weekday = 0;
        try {
            weekday = StingUtil.dayForWeek(date);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        date_infos_left.setText(mDay + "");
        date_infos_yangli.setText(ChinaDate.get_yangli(mYear, mMonth, mDay,
                weekday + ""));
        date_infos_yinli.setText(ChinaDate.get_yinli(mYear, mMonth, mDay));
        if (!ChinaDate.oneDayiswhat(mYear, mMonth, mDay).equals("")) {
            date_infos_foli_or_birthday.setText(ChinaDate.oneDayiswhat(mYear,
                    mMonth, mDay));
            date_infos_foli_or_birthday.setVisibility(View.VISIBLE);
        } else {
            date_infos_foli_or_birthday.setVisibility(View.GONE);
        }

        String birthd = "";
        String birth_id = "";
        if (birthdaylist.length() > 0) {
            for (int i = 0; i < birthdaylist.length(); i++) {

                try {
                    JSONObject jsonobject2 = birthdaylist.getJSONObject(i);
                    long retime = Integer.valueOf(
                            jsonobject2.optString("reminddate", "")).intValue();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String date2 = sdf.format(new Date(retime * 1000));
                    if (date2.equals(date)) { // 如果有当天的
                        birthd = jsonobject2.optString("relativesname", "");
                        birth_id = jsonobject2.optString("id", "");
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        if (!birthd.equals("")) {
            set_birthday_time(birthd, birth_id);
        } else {
            date_infos_birthdayshow.setVisibility(View.GONE);
        }

        // if (date_add_layout.getChildCount() > 1) {
        //
        // TextView view = (TextView) date_add_layout.getChildAt(0);
        // }
    }

    private void set_birthday_time(String name, String id) { // 设置生日显示

        if (!id.equals("")) {
            date_infos_birthdayshow.setText(name + "的生日");
            date_infos_birthdayshow.setVisibility(View.VISIBLE);
            date_infos_birthdayshow.setTag(id);
        }

    }

    public void PopupWindows() {

        LinearLayout ll_popup = (LinearLayout) findViewById(R.id.ll_popup);
        final TextView popupwindow_calendar_month = (TextView) findViewById(R.id.popupwindow_calendar_month);
        calendar = (KCalendar) findViewById(R.id.popupwindow_calendar);
        popupwindow_calendar_month.setText(calendar.getCalendarYear() + "年"
                + calendar.getCalendarMonth() + "月");

        if (null != date) {

            int years = Integer.parseInt(date.substring(0, date.indexOf("-")));
            int month = Integer.parseInt(date.substring(date.indexOf("-") + 1,
                    date.lastIndexOf("-")));
            popupwindow_calendar_month.setText(years + "年" + month + "月");

            calendar.showCalendar(years, month);
            calendar.setCalendarDayBgColor(date,
                    R.drawable.calendar_date_focused);
        }

        get_member_birthday();
        //	get_member_birthday_sql();

        // 监听所选中的日期
        calendar.setOnCalendarClickListener(new OnCalendarClickListener() {

            @Override
            public void onCalendarClick(int row, int col, String dateFormat) {
                int month = Integer.parseInt(dateFormat.substring(
                        dateFormat.indexOf("-") + 1,
                        dateFormat.lastIndexOf("-")));

                if (calendar.getCalendarMonth() - month == 1// 跨年跳转
                        || calendar.getCalendarMonth() - month == -11) {
                    //calendar.lastMonth(); //因为周历先放弃

                } else if (month - calendar.getCalendarMonth() == 1 // 跨年跳转
                        || month - calendar.getCalendarMonth() == -11) {
                    //calendar.nextMonth(); //因为周历先放弃

                } else {

                    calendar.removeAllBgColor();
                    calendar.setCalendarDayBgColor(dateFormat,
                            R.drawable.calendar_date_focused);
                    date = dateFormat;// 最后返回给全局 date
                    set_action_time();
                }
            }

        });

        // 监听当前月份
        calendar.setOnCalendarDateChangedListener(new OnCalendarDateChangedListener() {
            @Override
            public void onCalendarDateChanged(int year, int month) {
                popupwindow_calendar_month.setText(year + "年" + month + "月");
            }
        });

        // 上月监听按钮
        RelativeLayout popupwindow_calendar_last_month = (RelativeLayout) findViewById(R.id.popupwindow_calendar_last_month);
        popupwindow_calendar_last_month
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        calendar.lastMonth();
                    }

                });

        // 下月监听按钮
        RelativeLayout popupwindow_calendar_next_month = (RelativeLayout) findViewById(R.id.popupwindow_calendar_next_month);
        popupwindow_calendar_next_month
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        calendar.nextMonth();
                    }
                });

    }

//	private void get_member_birthday_sql() {
//		// TODO Auto-generated method stub
//		MemberBirthday_model memberbirdaydb=new MemberBirthday_model(Foli.this);
//
//		List<Map<String, String>> listmember=memberbirdaydb.select("");
//
//		for(int i=0;i<listmember.size();i++){
//			Map<String, String> maps=new HashMap<String, String>();
//
//			maps=listmember.get(i);
//
//		}
//	}

    private void get_member_birthday() {
        // TODO Auto-generated method stub

        this.httpClient = new SinhaPipeClient();
        if (Utils.CheckNetwork()) {

            if (!TextUtils.isEmpty(Cms.APP.getMemberId())) {
                showLoading();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("mid", Cms.APP.getMemberId()));

                this.httpClient.Config("get", WebApiUrl.Getcalendarremindlist,
                        params, true);
                this.httpMethod = new SinhaPipeMethod(this.httpClient,
                        new SinhaPipeMethod.MethodCallback() {
                            public void CallFinished(String error, Object result) {
                                Log.i("bbbb", "-----请求回来----" + result);
                                showLoading();
                                if (null == error) {
                                    try {
                                        List<String> list = new ArrayList<String>(); // 设置标记列表
                                        JSONObject jsonobject = new JSONObject(
                                                (String) result);
                                        JSONArray jsonarr = jsonobject
                                                .getJSONArray("calendarlist");
                                        if (jsonobject.optString("code", "")
                                                .equals("succeed")) {

                                            for (int i = 0; i < jsonarr
                                                    .length(); i++) {

                                                JSONObject jsonobject2 = jsonarr
                                                        .getJSONObject(i);

                                                long retime = Integer.valueOf(
                                                        jsonobject2.optString(
                                                                "reminddate",
                                                                "")).intValue();
                                                SimpleDateFormat sdf = new SimpleDateFormat(
                                                        "yyyy-MM-dd");
                                                String date = sdf
                                                        .format(new Date(
                                                                retime * 1000));
                                                if (date.equals(ChinaDate
                                                        .get_today_format())) { // 如果有当天的

                                                    set_birthday_time(
                                                            jsonobject2
                                                                    .optString(
                                                                            "relativesname",
                                                                            ""),
                                                            jsonobject2
                                                                    .optString(
                                                                            "id",
                                                                            ""));
                                                }
                                                Log.i("cccc", "---------->我的生日" + date);
                                                list.add(date);
                                            }
                                            birthdaylist = jsonarr;
                                            // list.add("2015-04-01");
                                            // list.add("2015-04-02");
                                            calendar.addMarks(list, R.drawable.calendar_birthday_background);

                                        } else {
                                            Utils.ShowToast(Foli.this,
                                                    jsonobject.optString("msg",
                                                            ""));
                                        }
                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                } else {
                                    int err = R.string.dialog_system_error_content;
                                    if (error == httpClient.ERR_TIME_OUT) {
                                        err = R.string.dialog_network_error_timeout;
                                    }
                                    if (error == httpClient.ERR_GET_ERR) {
                                        err = R.string.dialog_network_error_getdata;
                                    }
                                    Utils.ShowToast(Foli.this, err);
                                }
                            }
                        });
                this.httpMethod.start();

            } else {

                Utils.ShowToast(Foli.this, "未登录账户不能显示生日！");
            }

        } else {
            Utils.ShowToast(Foli.this, R.string.dialog_network_check_content);
        }
    }

    private void showLoading() {

        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        } else {

            pDialog = new ProgressDialog(Foli.this.getParent().getParent());
            pDialog.setMessage("数据请求中。。。");
            pDialog.show();
        }

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.add_user_birthday:
                if (TextUtils.isEmpty(Cms.APP.getMemberId())) {

                    Utils.Dialog(getParent().getParent(), "提示", "请先登录！");
                } else {
                    Intent intent = new Intent(Foli.this, AddBirthday.class);
                    FoLiGroupTab.getInstance().switchActivity("AddBirthday",
                            intent, -1, -1);
                }
                break;
            case R.id.date_infos_birthdayshow:
                String bid = (String) v.getTag();
                Intent inten = new Intent(Foli.this, Birthday_detail.class);
                inten.putExtra("bid", bid);
                FoLiGroupTab.getInstance().switchActivity("Birthday_detail",
                        inten, -1, -1);
                break;
//		case R.id.foli_bottom_image:
//			Log.i("bbbb", "-----点击了下面的图片---");
//			if(calendar.isweek==0){
//				calendar.setWeek(true);
//			}else{
//				calendar.setWeek(false);
//			}
//			break;
        }
    }


    OnTouchListener touchListener = new OnTouchListener() {
        float touchX, touchY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.i("MyGesture", "---------触摸----->");
            // TODO Auto-generated method stub
            return mGestureDetector.onTouchEvent(event);
        }
    };


    class MyGestureListener implements OnGestureListener {

        float scrollY;
        float scrollX;

        public void setScroll(int initScrollX, int initScrollY) {
            scrollX = initScrollX;
            scrollY = initScrollY;

            Log.i("MyGesture", "---------scrollX----->" + scrollX);
            Log.i("MyGesture", "---------scrollY----->" + scrollY);
        }

        // 用户轻触触摸屏，由1个MotionEvent ACTION_DOWN触发
        public boolean onDown(MotionEvent arg0) {
            Log.i("MyGesture", "onDown");

            return true;
        }

        /*
         * 用户轻触触摸屏，尚未松开或拖动，由一个1个MotionEvent ACTION_DOWN触发
         * 注意和onDown()的区别，强调的是没有松开或者拖动的状态
         */
        public void onShowPress(MotionEvent e) {
            Log.i("MyGesture", "onShowPress");

        }

        // 用户（轻触触摸屏后）松开，由一个1个MotionEvent ACTION_UP触发
        public boolean onSingleTapUp(MotionEvent e) {
            Log.i("MyGesture", "onSingleTapUp");

            return true;
        }

        // 用户按下触摸屏、快速移动后松开，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE,
        // 1个ACTION_UP触发
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            Log.i("MyGesture", "onFling" + "e1.getY()-----" + e1.getY()
                    + "e2.getY()-----" + e2.getY() + "velocityX-----"
                    + velocityX + "velocityY-----" + velocityY);

            final int FLING_MIN_DISTANCE = 20, FLING_MIN_VELOCITY = 200;

            if (e1.getY() - e2.getY() > FLING_MIN_DISTANCE
                    && Math.abs(velocityY) > FLING_MIN_VELOCITY) {
                // Fling left
                Log.i("MyGesture", "------------------>向上滑了");
                if (mIsShrinking) {
                    Calendar_mains.post(startAnimation);
                }

            } else if (e2.getY() - e1.getY() > FLING_MIN_DISTANCE
                    ) {
                // Fling left
                Log.i("MyGesture", "------------------>向下滑了");
                if (!mIsShrinking) {
                    Calendar_mains.post(startAnimation);
                }
            }

            return true;
        }

        // 用户按下触摸屏，并拖动，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE触发
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            Log.i("MyGesture", "onScroll");

            return true;
        }

        // 用户长按触摸屏，由多个MotionEvent ACTION_DOWN触发
        public void onLongPress(MotionEvent e) {
            Log.i("MyGesture", "onLongPress");

        }

    }

    private AnimationListener animationListener = new AnimationListener() {


        public void onAnimationEnd(Animation animation) {

            Log.i("aaaa", " -没执行么-----" + mIsShrinking);
            if (mIsShrinking) {
                calendar.setWeek(true);
                mIsShrinking = false;
            } else {
                mIsShrinking = true;
            }

        }

        public void onAnimationRepeat(Animation animation) {
        }

        public void onAnimationStart(Animation animation) {

            if (!mIsShrinking) {
                calendar.setWeek(false);
            }

        }
    };

    Runnable startAnimation = new Runnable() {
        public void run() {
            TranslateAnimation animation;
            int fromXDelta = 0, toXDelta = 0, fromYDelta = 0, toYDelta = 0;

            float from_y, to_y, relative_y;

            Log.i("aaaa", " -向上滑动的距离？-----");
            Log.i("aaaa", " -是否展开-----" + mIsShrinking);
            int calculatedDuration; // 运动的时间

            int height = Calendar_mains.getHeight();
            int image_height = foli_bottom_image.getHeight();


            int activityWidth = findViewById(android.R.id.content).getWidth();
            int activityHeight = findViewById(android.R.id.content).getHeight();
            //获取图片的真实大小

            Log.i("aaaa", " -图片高----" + image_height + "总高" + activityHeight + "总宽" + activityWidth);
            Log.i("aaaa", " -控件高度-----" + height + "---精度--->");
            calculatedDuration = 500;

            if (mIsShrinking) {


                toYDelta = -(height / 6) * 5;
                from_y = 1f;
                to_y = ((float) (image_height + (height / 6) * 5) / image_height) * 1f;
                relative_y = 1f;
            } else {

                fromYDelta = -(height / 6) * 5;
                toYDelta = 0;
                from_y = ((float) (image_height + (height / 6) * 5) / image_height) * 1f;  //算出来的比例  应该是  810/410 =1.97560976f
                to_y = 1f;
                relative_y = 1f;
            }


            Log.i("aaaa", " 日历的高-------" + calculatedDuration + "----"
                    + fromXDelta + "---" + toXDelta + "------" + fromYDelta
                    + "------" + toYDelta);


            animation = new TranslateAnimation(fromXDelta, toXDelta,
                    fromYDelta, toYDelta);

            final AlphaAnimation animation2 = new AlphaAnimation(1, 0f);
            animation.setDuration(calculatedDuration);
            animation.setAnimationListener(animationListener);
            //calendar_main.startAnimation(animation);
            animation.setFillAfter(true);
            Calendar_middle.startAnimation(animation);

            /** 设置缩放动画 */
            AnimationSet animationSet = new AnimationSet(true);
            //		 animationSet.setInterpolator(new AccelerateInterpolator());
            TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, 20);
            final ScaleAnimation animation3 = new ScaleAnimation(1f, 1f, from_y, to_y,
                    Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, relative_y);
            animationSet.setDuration(calculatedDuration);//设置动画持续时间
            animationSet.addAnimation(animation3);// 比例
            // animationSet.addAnimation(translateAnimation);	//移动
            animationSet.setFillAfter(true);
            foli_bottom_image.startAnimation(animationSet);

        }
    };


}
