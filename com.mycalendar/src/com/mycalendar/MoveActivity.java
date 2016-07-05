package com.mycalendar;



import android.os.Bundle;

import android.app.Activity;


import android.util.Log;
import android.view.GestureDetector;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import android.widget.Toast;

/**
 * 主界面
 * */
public class MoveActivity extends Activity {

	/**
	 * 收缩展开的面板
	 * */
	private Panel panel;

	private ImageView bottom_image;

	private LinearLayout calendar_main;

	private GestureDetector mGestureDetector;

	private MyGestureListener MymGestureListener;



	public static final int TOP = 0;
	public static final int BOTTOM = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;

	private enum State {
		ABOUT_TO_ANIMATE, ANIMATING, READY, TRACKING, FLYING,
	};

	private State mState;
	private Interpolator mInterpolator;

	private float mTrackX;
	private float mTrackY;
	private float mVelocity;

	private boolean mIsShrinking;
	private int mPosition;
	private int mDuration;
	private boolean mLinearFlying;

	private RelativeLayout zong;
	private LinearLayout middle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.move);

		initView();
	}

	/**
	 * 初始化view
	 * */
	private void initView() {
		
		
		mDuration = 750;		// duration defaults to 750 ms
		
		calendar_main = (LinearLayout) findViewById(R.id.calendar_main);
		bottom_image = (ImageView) findViewById(R.id.bottom_image);

		zong = (RelativeLayout) findViewById(R.id.zong);
		
		middle =(LinearLayout) findViewById(R.id.middle);
		bottom_image.setOnTouchListener(touchListener);
		zong.setOnTouchListener(touchListener);
	

		MymGestureListener = new MyGestureListener();
		mGestureDetector = new GestureDetector(MymGestureListener);
		mGestureDetector.setIsLongpressEnabled(false);

		mIsShrinking = true;
		mPosition = 1;
	}

	OnTouchListener touchListener = new OnTouchListener() {
		float touchX, touchY;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			Log.i("MyGesture", "---------触摸----->" );
			// TODO Auto-generated method stub
			// Toast.makeText(MoveActivity.this, "Touch Touch",
			// Toast.LENGTH_SHORT).show();

//			int action = event.getAction();
//			if (action == MotionEvent.ACTION_DOWN) {
//
//				touchX = event.getX();
//				touchY = event.getY();
//			}
//
//			if (!mGestureDetector.onTouchEvent(event)) {
//				
//			}
			return mGestureDetector.onTouchEvent(event);
		}
	};

	private float ensureRange(float v, int min, int max) {
		v = Math.max(v, min);
		v = Math.min(v, max);
		return v;
	}

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
			Toast.makeText(MoveActivity.this, "onDown", Toast.LENGTH_SHORT)
					.show();
			return true;
		}

		/*
		 * 用户轻触触摸屏，尚未松开或拖动，由一个1个MotionEvent ACTION_DOWN触发
		 * 注意和onDown()的区别，强调的是没有松开或者拖动的状态
		 */
		public void onShowPress(MotionEvent e) {
			Log.i("MyGesture", "onShowPress");
			Toast.makeText(MoveActivity.this, "onShowPress", Toast.LENGTH_SHORT)
					.show();
		}

		// 用户（轻触触摸屏后）松开，由一个1个MotionEvent ACTION_UP触发
		public boolean onSingleTapUp(MotionEvent e) {
			Log.i("MyGesture", "onSingleTapUp");

			// calendar_main.post(startAnimation);
			Toast.makeText(MoveActivity.this, "onSingleTapUp",
					Toast.LENGTH_SHORT).show();
			return true;
		}

		// 用户按下触摸屏、快速移动后松开，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE,
		// 1个ACTION_UP触发
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			Log.i("MyGesture", "onFling" + "e1.getY()-----" + e1.getY()
					+ "e2.getY()-----" + e2.getY() + "velocityX-----"
					+ velocityX + "velocityY-----" + velocityY);
			
			mState = State.FLYING;
			
			mVelocity = velocityY;

			final int FLING_MIN_DISTANCE = 20, FLING_MIN_VELOCITY = 200;

			if (e1.getY() - e2.getY() > FLING_MIN_DISTANCE
					&& Math.abs(velocityY) > FLING_MIN_VELOCITY) {
				// Fling left
				Log.i("MyGesture", "------------------>向上滑了");
				if(mIsShrinking){
				 calendar_main.post(startAnimation);
				}
				
			}else if (e2.getY() - e1.getY() > FLING_MIN_DISTANCE
					) {
				// Fling left
				Log.i("MyGesture", "------------------>向下滑了");
				if(!mIsShrinking){
					calendar_main.post(startAnimation);
				}
			}
			

			Toast.makeText(MoveActivity.this, "onFling", Toast.LENGTH_LONG)
					.show();
			return true;
		}

		// 用户按下触摸屏，并拖动，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE触发
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			Log.i("MyGesture", "onScroll");
			
			int height = calendar_main.getHeight();
			
			mState = State.TRACKING;
			float tmpY = 0, tmpX = 0;
			scrollY -= distanceY;
			if (mIsShrinking) {
				tmpY = ensureRange(scrollY, -height, 0);
			} else {
				tmpY = ensureRange(scrollY, 0, height);
			}
			if (tmpX != mTrackX || tmpY != mTrackY) {
				mTrackX = tmpX;
				mTrackY = tmpY;
				zong.invalidate();
			}
			// Toast.makeText(MoveActivity.this, "onScroll",
			// Toast.LENGTH_LONG).show();
			return true;
		}

		// 用户长按触摸屏，由多个MotionEvent ACTION_DOWN触发
		public void onLongPress(MotionEvent e) {
			Log.i("MyGesture", "onLongPress");
			Toast.makeText(MoveActivity.this, "onLongPress", Toast.LENGTH_LONG)
					.show();
		}

	}

	private AnimationListener animationListener = new AnimationListener() {
		
		
	
		public void onAnimationEnd(Animation animation) {
			mState = State.READY;
			
			
			Log.i("aaaa", " -没执行么-----" + mIsShrinking);
			 if (mIsShrinking) {
				 LinearLayout row1 = (LinearLayout) calendar_main.getChildAt(0);
					LinearLayout row2 = (LinearLayout) calendar_main.getChildAt(1);
					LinearLayout row3 = (LinearLayout) calendar_main.getChildAt(2);
					LinearLayout row4 = (LinearLayout) calendar_main.getChildAt(3);
					LinearLayout row5 = (LinearLayout) calendar_main.getChildAt(4);
				row1.setVisibility(View.GONE);
				row2.setVisibility(View.GONE);
				// row3.setVisibility(View.GONE);
				row4.setVisibility(View.GONE);
				row5.setVisibility(View.GONE);
				//row3.setPadding(0, -200, 0, 0);
				Log.i("aaaa", " -控件高度-----" + bottom_image.getHeight()+"-------y--"+ bottom_image.getTop());
			
			     mIsShrinking =false;
			 }else{
				 
				mIsShrinking =true;
			 }
		
		}

		public void onAnimationRepeat(Animation animation) {
		} 

		public void onAnimationStart(Animation animation) {
			
			 if (!mIsShrinking) {
				 LinearLayout row1 = (LinearLayout) calendar_main.getChildAt(0);
					LinearLayout row2 = (LinearLayout) calendar_main.getChildAt(1);
					LinearLayout row3 = (LinearLayout) calendar_main.getChildAt(2);
					LinearLayout row4 = (LinearLayout) calendar_main.getChildAt(3);
					LinearLayout row5 = (LinearLayout) calendar_main.getChildAt(4);
				 row1.setVisibility(View.VISIBLE);
					row2.setVisibility(View.VISIBLE);
					// row3.setVisibility(View.GONE);
					row4.setVisibility(View.VISIBLE);
					row5.setVisibility(View.VISIBLE);
			 }
			mState = State.ANIMATING;
		}
	};

	Runnable startAnimation = new Runnable() {
		public void run() {
			TranslateAnimation animation;
			int fromXDelta = 0, toXDelta = 0, fromYDelta = 0, toYDelta = 0;
			
			float from_y,to_y,relative_y;
			
			Log.i("aaaa", " -向上滑动的距离？-----" + mVelocity);
			Log.i("aaaa", " -是否展开-----" + mIsShrinking);
			int calculatedDuration; // 运动的时间

			int height = calendar_main.getHeight();
			int image_height = bottom_image.getHeight();
			
	
			int activityWidth=findViewById(android.R.id.content).getWidth();
			int activityHeight=findViewById(android.R.id.content).getHeight();
			//获取图片的真实大小
			  
			Log.i("aaaa", " -图片高----" + image_height+"总高"+activityHeight+"总宽"+activityWidth);
			Log.i("aaaa", " -控件高度-----" + height+"---精度--->");
			calculatedDuration = 300;
			
			if (mIsShrinking) {
					
				
				 toYDelta=-(height/5)*4;
				 from_y=1f;
				  to_y=((float)(image_height+(height/5)*4)/image_height)*1f;
				  relative_y =1f;
			} else {
				
				fromYDelta=-(height/5)*4;
				toYDelta=0;
				  from_y=((float)(image_height+(height/5)*4)/image_height)*1f;  //算出来的比例  应该是  810/410 =1.97560976f  
				  to_y=1f;
				  relative_y = 1f;
			}
			
			if (mState == State.TRACKING) {
					if (Math.abs(mTrackY - fromYDelta) < Math.abs(mTrackY - toYDelta)) {
						mIsShrinking = !mIsShrinking;
						toYDelta = fromYDelta;
					}
					fromYDelta = (int) mTrackY;
			} else{
				if (mState == State.FLYING) {
					fromYDelta = (int) mTrackY;
				}
			}
			
			if (mState == State.FLYING ) {
				calculatedDuration = (int) (1000 * Math.abs((toYDelta - fromYDelta) / mVelocity));
				calculatedDuration = Math.max(calculatedDuration, 20);
			} else {
				calculatedDuration = mDuration * Math.abs(toYDelta - fromYDelta) / ((height/5)*4);
			}
			
	
			Log.i("aaaa", " 日历的高-------" + calculatedDuration + "----"
					+ fromXDelta + "---" + toXDelta + "------" + fromYDelta
					+ "------" + toYDelta);
			
			mTrackX = mTrackY = 0;
			
			 animation = new TranslateAnimation(fromXDelta, toXDelta,
			 fromYDelta, toYDelta);

			final AlphaAnimation animation2 = new AlphaAnimation(1, 0f);
			animation.setDuration(calculatedDuration);
			animation.setAnimationListener(animationListener);
			//calendar_main.startAnimation(animation);
			animation.setFillAfter(true);
			middle.startAnimation(animation);
			
			 /** 设置缩放动画 */
			 AnimationSet animationSet = new AnimationSet(true);
	//		 animationSet.setInterpolator(new AccelerateInterpolator());
			 TranslateAnimation translateAnimation=new TranslateAnimation(0, 0, 0, 20);
			 final ScaleAnimation animation3 =new ScaleAnimation(1f, 1f, from_y, to_y,   
					 Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, relative_y);
			 animationSet.setDuration(calculatedDuration);//设置动画持续时间
			 animationSet.addAnimation(animation3);// 比例
			// animationSet.addAnimation(translateAnimation);	//移动
			 animationSet.setFillAfter(true);
			 bottom_image.startAnimation(animationSet);

		}
	};

}
