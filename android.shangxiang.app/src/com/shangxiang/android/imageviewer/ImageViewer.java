package com.shangxiang.android.imageviewer;

import com.shangxiang.android.R;
import com.shangxiang.android.ShangXiang;
import com.shangxiang.android.imageviewer.ImageZoomView;
import com.shangxiang.android.imageviewer.SimpleZoomListener;
import com.shangxiang.android.imageviewer.ZoomState;
import com.shangxiang.android.utils.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ZoomControls;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import android.annotation.SuppressLint;
import android.app.Activity;

public class ImageViewer extends Activity implements OnClickListener {
	private SimpleZoomListener zoomListener;
	private ImageZoomView zoomView;
	private ImageView zoomImage;
	private ZoomState zoomState;
	private ProgressBar loading;
	private Bitmap bitmap;

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public void onCreate(Bundle sinha) {
		super.onCreate(sinha);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.image_viewer);

		this.loading = (ProgressBar) findViewById(R.id.loading);
		this.zoomView = (ImageZoomView) findViewById(R.id.zoom_view);
		this.zoomImage = (ImageView) findViewById(R.id.zoom_image);
		Button buttonClose = (Button) findViewById(R.id.close_button);
		buttonClose.setOnClickListener(this);
		ZoomControls zoomCtrl = (ZoomControls) findViewById(R.id.zoom_ctrl);
		zoomCtrl.setOnZoomInClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				float z = zoomState.getZoom() + 0.25f;
				zoomState.setZoom(z);
				zoomState.notifyObservers();
			}
		});
		zoomCtrl.setOnZoomOutClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				float z = zoomState.getZoom() - 0.25f;
				zoomState.setZoom(z);
				zoomState.notifyObservers();
			}
		});

		Bundle bundle = this.getIntent().getExtras();
		if (null != bundle && null != bundle.getString("from") && null != bundle.getString("image_path")) {
			if ("network".equals(bundle.getString("from"))) {
				ShangXiang.imageLoader.displayImage(bundle.getString("image_path"), this.zoomImage, ShangXiang.imageLoaderOptions, new ImageLoadingListener() {
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
						zoomImage.setVisibility(View.GONE);
						zoomView.setImage(loadedImage);
						zoomState = new ZoomState();
						zoomView.setZoomState(zoomState);
						zoomListener = new SimpleZoomListener();
						zoomListener.setZoomState(zoomState);
						zoomView.setOnTouchListener(zoomListener);
						ResetZoomState();
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						loading.setVisibility(View.GONE);
						zoomImage.setVisibility(View.GONE);
					}
				});
			} else {
				this.bitmap = BitmapFactory.decodeFile(bundle.getString("image_path"));
				this.zoomView.setImage(this.bitmap);
				this.zoomState = new ZoomState();
				this.zoomView.setZoomState(this.zoomState);
				this.zoomListener = new SimpleZoomListener();
				this.zoomListener.setZoomState(this.zoomState);
				this.zoomView.setOnTouchListener(this.zoomListener);
				ResetZoomState();
			}
		} else {
			Utils.Dialog(this, R.string.dialog_imageviewer_tip, R.string.dialog_imageviewer_err);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (bitmap != null) {
			this.bitmap.recycle();
		}
	}

	private void ResetZoomState() {
		this.zoomState.setPanX(0.5f);
		this.zoomState.setPanY(0.5f);
		this.zoomState.setZoom(1f);
		this.zoomState.notifyObservers();
	}

	@Override
	public void onClick(View v) {
		int sender = v.getId();
		if (R.id.close_button == sender) {
			this.finish();
		}
	}
}