package com.shangxiang.android.view;

import com.shangxiang.android.R;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SinhaPaper extends FrameLayout {
	private Context context;
	private ViewPager viewPager;
	private PagerAdapter pagerAdapter;
	private View[] pageViews = new View[] {};
	private ImageView[] dotViews = new ImageView[] {};
	private LinearLayout viewPageControl;
	private LayoutInflater inflater;
	private Handler handler;
	private int heightPageControl = -1;

	public int curIndex = 0;
	public boolean autoScroll = false;
	public boolean showPageControl = true;

	public SinhaPaper(Context context) {
		super(context);
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.inflater.inflate(R.layout.sinha_pager_layout, this, true);
		init();
	}

	public SinhaPaper(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.inflater.inflate(R.layout.sinha_pager_layout, this, true);
		init();
	}

	public void setPageList(View[] listPageView) {
		this.pageViews = listPageView;
		this.pagerAdapter.notifyDataSetChanged();
		addCircle();
		setAutoScroll();
		setPageControl();
	}

	public void init() {
		this.viewPager = (ViewPager) findViewById(R.id.sinha_paper_layout);
		this.viewPageControl = (LinearLayout) findViewById(R.id.sinha_paper_control);
		this.pagerAdapter = new SinhaPagerAdapter();
		this.viewPager.setAdapter(this.pagerAdapter);
		this.viewPager.setOnPageChangeListener(new SinhaPageChangeListener());
		this.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				viewPager.setCurrentItem(msg.arg1);
			}
		};
	}

	public void setPageControl() {
		if (this.showPageControl) {
			this.viewPageControl.setVisibility(View.VISIBLE);
		} else {
			this.viewPageControl.setVisibility(View.GONE);
		}
	}

	public void setAutoScroll() {
		if (this.autoScroll) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						if (pageViews.length > 0) {
							if (curIndex >= pageViews.length) {
								curIndex = 0;
							}
							Message msg = new Message();
							msg.arg1 = curIndex++;
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							handler.sendMessage(msg);
						}
					}

				}
			}).start();
		}
	}

	public void addCircle() {
		if (-1 != this.heightPageControl) {
			FrameLayout.LayoutParams paramsPageControl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, this.heightPageControl, Gravity.BOTTOM);
			this.viewPageControl.setLayoutParams(paramsPageControl);
		}
		this.viewPageControl.removeAllViews();
		if (this.dotViews != null) {
			this.dotViews = null;
		}
		this.dotViews = new ImageView[this.pageViews.length];
		ImageView dotView;
		for (int i = 0; i < this.pageViews.length; i++) {
			LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			margin.setMargins(10, 0, 0, 0);
			dotView = new ImageView(this.context);
			dotView.setLayoutParams(new LayoutParams(15, 15));
			this.dotViews[i] = dotView;
			if (i == 0) {
				this.dotViews[i].setBackgroundResource(R.drawable.page_indicator_focused);
			} else {
				this.dotViews[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
			}
			this.viewPageControl.addView(this.dotViews[i], margin);
		}
	}

	private class SinhaPagerAdapter extends PagerAdapter {

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public int getCount() {
			return pageViews.length;
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View pageView = pageViews[position];
			((ViewPager) view).addView(pageView, 0);
			return pageView;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View container) {
		}
	}

	class SinhaPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int position) {
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			for (int i = 0; i < dotViews.length; i++) {
				dotViews[position].setBackgroundResource(R.drawable.page_indicator_focused);
				if (position != i) {
					dotViews[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
				}
			}
		}
	}
}
