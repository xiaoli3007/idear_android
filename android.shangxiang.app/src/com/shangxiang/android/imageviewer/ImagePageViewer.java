package com.shangxiang.android.imageviewer;

import com.shangxiang.android.R;
import com.shangxiang.android.ShangXiang;
import com.shangxiang.android.utils.Utils;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import android.annotation.SuppressLint;
import android.app.Activity;

public class ImagePageViewer extends Activity implements OnClickListener {
	private ProgressBar loading;
	private ViewPager viewPage;
	private TextView viewPageCount;
	private ThumbsAdapter adapterPage;
	private String[] thumbs;

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public void onCreate(Bundle sinha) {
		super.onCreate(sinha);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.image_page_viewer);

		Button buttonClose = (Button) findViewById(R.id.close_button);
		buttonClose.setOnClickListener(this);
		this.loading = (ProgressBar) findViewById(R.id.loading);
		this.viewPageCount = (TextView) findViewById(R.id.zoom_count);
		this.viewPage = (ViewPager) findViewById(R.id.zoom_view);

		Bundle bundle = this.getIntent().getExtras();
		if (null != bundle && null != bundle.getStringArray("thumbs")) {
			this.thumbs = bundle.getStringArray("thumbs");
			this.viewPageCount.setText("1/" + this.thumbs.length);
			this.adapterPage = new ThumbsAdapter();
			this.viewPage.setAdapter(this.adapterPage);
			this.viewPage.setOnPageChangeListener(new MyOnPageChangeListener());
			this.viewPage.setOffscreenPageLimit(this.thumbs.length);
		} else {
			Utils.Dialog(this, R.string.dialog_imageviewer_tip, R.string.dialog_imageviewer_err);
		}
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int position) {
			viewPageCount.setText((position + 1) + "/" + thumbs.length);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		@Override
		public void onPageScrollStateChanged(int position) {
		}
	}

	public class ThumbsAdapter extends PagerAdapter {
		public ThumbsAdapter() {
		}

		@Override
		public int getCount() {
			return thumbs.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return (view == object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			String path = thumbs[position];
			ImageView thumb = new ImageView(ImagePageViewer.this);
			thumb.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			thumb.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			ShangXiang.imageLoader.displayImage(path, thumb, ShangXiang.imageLoaderOptions, new ImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					loading.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					loading.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					loading.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					loading.setVisibility(View.GONE);
				}
			});
			((ViewPager) container).addView(thumb, position);
			return thumb;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((ImageView) object);
		}
	}

	@Override
	public void onClick(View v) {
		int sender = v.getId();
		if (R.id.close_button == sender) {
			this.finish();
		}
	}
}