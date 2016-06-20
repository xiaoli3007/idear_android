package com.guo.memorandum;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;

public class LinedEditText extends EditText {
	private Rect mRect;
	private Paint mPaint;
	//���캯��
	public LinedEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		mRect = new Rect();
		//����������ɫΪ��ɫ
		mPaint = new Paint();
		mPaint.setColor(Color.BLUE);
	}
	//������ͼ
	@Override
	protected void onDraw(Canvas canvas) {
		int count = getLineCount();
		Rect r = mRect;
		Paint paint = mPaint;
		//����ÿһ�еĸ�ʽ
		for (int i = 0; i < count; i++) {
			//ȡ��ÿһ�еĻ�׼Y���꣬����ÿһ�еĽ���ֵ��д��r��
			int baseline = getLineBounds(i, r);
			//����ÿһ�е������´��»���
			canvas.drawLine(r.left, baseline + 5, r.right, baseline + 5, paint);
		}
		super.onDraw(canvas);
	}
}
