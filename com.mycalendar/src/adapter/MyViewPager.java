package adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * ViewPager wrapContent解决方案
 */
public class MyViewPager extends ViewPager {


    /**
     * Constructor
     *
     * @param context the context
     */
    public MyViewPager(Context context) {
        super(context);
    }

    /**
     * Constructor
     *
     * @param context the context
     * @param attrs the attribute set
     */
    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = 0;
       // Log.i("eeee", "元素---"+getChildCount());
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight() * 6;
	           if(child.getTag().equals("22")){
	        	   h = child.getMeasuredHeight();
	        	  // Log.i("eeee", "元素--gao-执行了么-"+ h);
	           }
            Log.i("eeee", "元素--gao--"+ h);
            if(h > height) height = h;
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
