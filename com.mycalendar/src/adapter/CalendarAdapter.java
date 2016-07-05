package adapter;

import java.util.List;

import calendarutils.DataUtils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.mycalendar.MainActivity;
import com.mycalendar.R;

import data.DateInfo;

/**
 * 日历gridview适配器
 * */
public class CalendarAdapter extends BaseAdapter {

	private List<DateInfo> list = null;
	private Context context = null;
	private int selectedPosition = -1;
	private int selectedToday=-1;
	MainActivity activity;


	public static final int COLOR_TX_OTHER_MONTH_DAY = Color
			.parseColor("#ffcccccc"); // 灰色
	public static final int COLOR_TX_WEEK_TITLE = Color.parseColor("#FD3316");	//佛历的颜色


	public CalendarAdapter(MainActivity activity, List<DateInfo> list) {
		this.context = activity;
		this.list = list;
		this.activity = activity;
	}

	public List<DateInfo> getList() {
		return list;
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	/**
	 * 设置选中位置
	 * */
	public void setSelectedPosition(int position) {
        selectedPosition = position;
    }


	/**
	 * 设置当天
	 * */
	public void setTodayPosition(int position) {
        selectedToday = position;
    }

	/**
	 * 产生一个view
	 * */
	@SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup group) {
		//通过viewholder做一些优化
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.gridview_item, null);

			viewHolder.date = (TextView) convertView.findViewById(R.id.item_date);
			viewHolder.nongliDate = (TextView) convertView.findViewById(R.id.item_nongli_date);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		//根据数据源设置单元格的字体颜色、背景等
		viewHolder.date.setText(list.get(position).getDate() + "");
		viewHolder.nongliDate.setText(list.get(position).getNongliDate());
		if (selectedPosition == position) {		//手指选中位置的时候
//			viewHolder.date.setTextColor(Color.WHITE);
//			viewHolder.nongliDate.setTextColor(Color.WHITE);
		//	convertView.setBackgroundColor(COLOR_TX_OTHER_MONTH_DAY);

			convertView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.calendar_background_selected));

		}else if(selectedToday == position){

			//convertView.setBackgroundColor(COLOR_TX_OTHER_MONTH_DAY); 	//今天
			convertView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.calendar_background_today));

		} else {
			convertView.setBackgroundResource(Color.TRANSPARENT);
//			viewHolder.date.setTextColor(Color.BLACK);
//			viewHolder.nongliDate.setTextColor(Color.BLACK);

			if (list.get(position).isHoliday())		//节日的话
				viewHolder.nongliDate.setTextColor(COLOR_TX_WEEK_TITLE);
			else if (list.get(position).isThisMonth() == false) {	//不是本月的
				viewHolder.date.setTextColor(Color.rgb(210, 210, 210));
				viewHolder.nongliDate.setTextColor(Color.rgb(210, 210, 210));
			}
			else if (list.get(position).isWeekend()) {		//是周末把
			//	viewHolder.date.setTextColor(Color.rgb(255, 97, 0));
			}
		}
		if (list.get(position).getNongliDate().length() > 3)
			viewHolder.nongliDate.setTextSize(10);
		if (list.get(position).getNongliDate().length() >= 5)
			viewHolder.nongliDate.setTextSize(8);
		convertView.setLayoutParams(new GridView.LayoutParams(LayoutParams.WRAP_CONTENT, DataUtils.getScreenWidth(activity) / 7));
		return convertView;
	}

	private class ViewHolder {
		TextView date;
		TextView nongliDate;
	}

}
