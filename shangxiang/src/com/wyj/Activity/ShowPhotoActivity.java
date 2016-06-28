package com.wyj.Activity;


import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;



import com.wyj.dataprocessing.BitmapManager;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

public class ShowPhotoActivity extends Activity implements  ViewFactory,
		OnTouchListener {
	private static String TAG = "ShowTemple";
	/**
	 * ImagaSwitcher 的引用
	 */
	private ImageSwitcher mImageSwitcher;
	/**
	 * 图片id数组
	 */
	private int[] imgIds;
	/**
	 * 图片id数组
	 */
	private String[] bigimages;
	/**
	 * 当前选中的图片id序号
	 */
	private int currentPosition;
	/**
	 * 按下点的X坐标
	 */
	private float downX;
	/**
	 * 装载点点的容器
	 */
	private LinearLayout linearLayout;
	/**
	 * 点点数组
	 */
	private ImageView[] tips;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_photo);

		imgIds = new int[] { R.drawable.temp1, R.drawable.temp2,
				R.drawable.temp3 };
		// 实例化ImageSwitcher
		mImageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher1);
		// 设置Factory
		mImageSwitcher.setFactory(this);
		// 设置OnTouchListener，我们通过Touch事件来切换图片
		mImageSwitcher.setOnTouchListener(this);
		
		
		
		linearLayout = (LinearLayout) findViewById(R.id.viewPhotoGroup);

		Intent intent = this.getIntent(); // 接受的数据
		Bundle budle = intent.getExtras();
		bigimages = budle.getStringArray("bigimages");
		
		
		tips = new ImageView[bigimages.length];
		for (int i = 0; i < bigimages.length; i++) {
			ImageView mImageView = new ImageView(this);
			tips[i] = mImageView;
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
			layoutParams.rightMargin = 3;
			layoutParams.leftMargin = 3;

			mImageView
					.setBackgroundResource(R.drawable.page_indicator_unfocused);
			
//			mImageView.setOnClickListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					ShowPhotoActivity.this.finish();
//					Log.i(TAG, "------点击生效-------" );
//
//				}
//			});
			
			linearLayout.addView(mImageView, layoutParams);
		}
		
		

		// 这个我是从上一个界面传过来的，上一个界面是一个GridView
		currentPosition = getIntent().getIntExtra("position", 0);
		
		show_item_image(currentPosition,currentPosition);
		
//		mImageSwitcher.setImageResource(imgIds[currentPosition]);
//		setImageBackground(currentPosition);

	}
	
	
	private void show_item_image(int position,int area){
		
		String  image_url= bigimages[position];
		
		Bitmap	bitimage=BitmapManager.getInstance().getBitmapCacheByUrl(image_url);
		if(bitimage!=null){
			Drawable drawable = new BitmapDrawable(bitimage);  
			mImageSwitcher.setImageDrawable(drawable);  
			setImageBackground(area);
		}else{
			new MyImageTask(ShowPhotoActivity.this,position,area).execute(image_url);
		}
		
	}

	/**
	 * 设置选中的tip的背景
	 * 
	 * @param selectItems
	 */
	private void setImageBackground(int selectItems) {
		for (int i = 0; i < tips.length; i++) {
			if (i == selectItems) {
				tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
			} else {
				tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
			}
		}
	}

	@Override
	public View makeView() {
		final ImageView i = new ImageView(this);
		i.setBackgroundColor(0xff000000);
		i.setScaleType(ImageView.ScaleType.CENTER_CROP);
		i.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		return i;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			// 手指按下的X坐标
			downX = event.getX();
			
			break;
		}
	
		case MotionEvent.ACTION_UP: {
			float lastX = event.getX();
			// 抬起的时候的X坐标大于按下的时候就显示上一张图片
			if (lastX > downX) {
				if (currentPosition > 0) {
					// 设置动画，这里的动画比较简单，不明白的去网上看看相关内容
					mImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(
							getApplication(), R.anim.left_in));
					mImageSwitcher.setOutAnimation(AnimationUtils
							.loadAnimation(getApplication(), R.anim.right_out));
					currentPosition--;
					
					int poaition=currentPosition % bigimages.length;
//					mImageSwitcher.setImageResource(imgIds[currentPosition
//							% imgIds.length]);
//					setImageBackground(currentPosition);
					
					show_item_image(poaition,currentPosition);
					
				} else {
					Toast.makeText(getApplication(), "已经是第一张",
							Toast.LENGTH_SHORT).show();
				}
			}

			if (lastX < downX) {
				if (currentPosition < bigimages.length - 1) {
					mImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(
							getApplication(), R.anim.right_in));
					mImageSwitcher.setOutAnimation(AnimationUtils
							.loadAnimation(getApplication(), R.anim.lift_out));
					currentPosition++;
					show_item_image(currentPosition,currentPosition);
//					mImageSwitcher.setImageResource(imgIds[currentPosition]);
//					setImageBackground(currentPosition);
				} else {
					Toast.makeText(getApplication(), "到了最后一张",
							Toast.LENGTH_SHORT).show();
				}
			}
		}

			break;
		}

		return true;
	}
	
	
	 // 需要先继承AsyncTask接口，第一个参数是执行路径，第二个是进度，第三个是返回值
	private class MyImageTask extends AsyncTask<String, Void, Bitmap> {
	
		private ProgressDialog pDialog = null;
		private int position;	//显示哪个图片
		private int area;	//下面的点定位

		public MyImageTask( Context context, int position,int area) {
	
			this.position = position;
			this.area = area;
			pDialog = new ProgressDialog(context);
			pDialog.setMessage("图片加载中。。。");
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog.show();
		}

		@Override
		protected Bitmap  doInBackground(String... params) {
			  // 实例化网络客户端对象
            HttpClient httpClient = new DefaultHttpClient();
            // 实例化请求对象，并且取出参数列表中的参数
            HttpGet httpGet = new HttpGet(params[0]);
            Bitmap bitmap = null;
            bitmap = BitmapManager.getInstance().getBitmapByUrl(params[0]);

            return bitmap;
			
		}

		@Override
		protected void onPostExecute(Bitmap result) {

			// 通过回调接口来传递数据
			super.onPostExecute(result);
			Drawable drawable = new BitmapDrawable(result);  
			mImageSwitcher.setImageDrawable(drawable);  
			setImageBackground(area);
            pDialog.dismiss();
		}
	}





}
