package com.shangxiang.android.clipimage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.shangxiang.android.R;
import com.shangxiang.android.clipimage.ClipView.OnDrawListenerComplete;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ClipImage extends FragmentActivity implements OnTouchListener, OnClickListener {
	private View buttonBack;
	private View buttonSubmit;
	private ImageView editorBox;
	private ClipView clipview;

	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();

	/** 动作标志：无 */
	private static final int NONE = 0;
	/** 动作标志：拖动 */
	private static final int DRAG = 1;
	/** 动作标志：缩放 */
	private static final int ZOOM = 2;
	/** 初始化动作标志 */
	private int mode = NONE;

	/** 记录起始坐标 */
	private PointF start = new PointF();
	/** 记录缩放时两指中间点坐标 */
	private PointF mid = new PointF();
	private float oldDist = 1f;

	private Bitmap bitmap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clip_image_editor);

		editorBox = (ImageView) this.findViewById(R.id.clip_image_editor_image);
		editorBox.setOnTouchListener(this);

		ViewTreeObserver observer = editorBox.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			public void onGlobalLayout() {
				editorBox.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				initClipView(editorBox.getTop());
			}
		});

		buttonBack = (View) this.findViewById(R.id.clip_image_title_back_button);
		buttonBack.setOnClickListener(this);
		buttonSubmit = (View) this.findViewById(R.id.clip_image_title_submit_button);
		buttonSubmit.setOnClickListener(this);
	}

	/**
	 * 初始化截图区域，并将源图按裁剪框比例缩放
	 * 
	 * @param top
	 */
	private void initClipView(int top) {
		Intent intent = getIntent();
		if (null != intent && null != intent.getStringExtra("path")) {
			bitmap = BitmapFactory.decodeFile(intent.getStringExtra("path"));
			clipview = new ClipView(this);
			clipview.setCustomTopBarHeight(top);
			clipview.addOnDrawCompleteListener(new OnDrawListenerComplete() {
				public void onDrawCompelete() {
					clipview.removeOnDrawCompleteListener();
					int clipHeight = clipview.getClipHeight();
					int clipWidth = clipview.getClipWidth();
					int midX = clipview.getClipLeftMargin() + (clipWidth / 2);
					int midY = clipview.getClipTopMargin() + (clipHeight / 2);

					int imageWidth = bitmap.getWidth();
					int imageHeight = bitmap.getHeight();

					float scale = (clipWidth * 1.0f) / imageWidth;
					if (imageWidth > imageHeight) {
						scale = (clipHeight * 1.0f) / imageHeight;
					}

					float imageMidX = imageWidth * scale / 2;
					float imageMidY = clipview.getCustomTopBarHeight() + imageHeight * scale / 2;
					editorBox.setScaleType(ScaleType.MATRIX);
					matrix.postScale(scale, scale);
					matrix.postTranslate(midX - imageMidX, midY - imageMidY);
					editorBox.setImageMatrix(matrix);
					editorBox.setImageBitmap(bitmap);
				}
			});
			this.addContentView(clipview, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	public boolean onTouch(View v, MotionEvent event) {
		ImageView view = (ImageView) v;
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					matrix.postScale(scale, scale, mid.x, mid.y);
				}
			}
			break;
		}
		view.setImageMatrix(matrix);
		return true;
	}

	/**
	 * 多点触控时，计算最先放下的两指距离
	 * 
	 * @param event
	 * @return
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	/**
	 * 多点触控时，计算最先放下的两指中心坐标
	 * 
	 * @param point
	 * @param event
	 */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	public void onClick(View v) {
		if (v == buttonBack) {
			setResult(RESULT_CANCELED);
		}
		if (v == buttonSubmit) {
			getBitmap();
			setResult(RESULT_OK);
		}
		finish();
	}

	/**
	 * 获取裁剪框内截图
	 * 
	 * @return
	 */
	private Bitmap getBitmap() {
		// 获取截屏
		View view = this.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();

		// 获取状态栏高度
		Rect frame = new Rect();
		this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;

		Bitmap finalBitmap = Bitmap.createBitmap(view.getDrawingCache(), clipview.getClipLeftMargin(), clipview.getClipTopMargin() + statusBarHeight, clipview.getClipWidth(), clipview.getClipHeight());
		File file = new File(Environment.getExternalStorageDirectory(), "avatar.jpg");
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			finalBitmap.compress(CompressFormat.JPEG, 80, fos);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 释放资源
		view.destroyDrawingCache();
		return finalBitmap;
	}

}