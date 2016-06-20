package com.guanfu.rmstest;



import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * @title BaseActivity.java
 * @package com.chinat2t.lovgou.activity
 * @description Activity的基类
 * @author 徐Sing xusx1024@gmail.com
 * @update 2013-12-16 下午2:37:09
 * @version 1.0
 **/
public abstract class BaseActivity extends Activity implements OnClickListener {


	/** 是否是正在请求 */
	protected boolean is_requesting = false;
	/**
	 * 线程名字，用来取消线程
	 */
	protected String threadName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//IApplication.activities.add(this);

		setContentView();
		initView();
		setOnClickListener();
		processLogic();
	}

	/** 1.设置xml文件 */
	protected abstract void setContentView();

	/** 2.查找View */
	protected abstract void initView();

	/** 3.设置点击事件 */
	protected abstract void setOnClickListener();

	/** 4.处理逻辑 */
	protected abstract void processLogic();

	@Override
	public void onClick(View v) {

	}




	/**
	 * 关闭请求中弹出的进度条对话框
	 */
	protected void closeDialog() {

	}

	/**
	 * 弹出Toast提示
	 *
	 * @param text
	 */
	protected void alertToast(String text) {
		try {
			Toast.makeText(BaseActivity.this.getApplicationContext(), text,
					Toast.LENGTH_SHORT).show();
		} catch (Exception e) {

		}
	}
}
