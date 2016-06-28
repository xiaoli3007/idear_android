package com.wyj.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import android.view.KeyEvent;

public class MainActivity extends Activity {

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// Log.i("TAG","----"+keyCode);
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:

			// webView.goBack();
			exitApp();
			return false;
		default:
			break;
		}

		return super.onKeyDown(keyCode, event);
	}

	public void exitApp() {
		new AlertDialog.Builder(this)
				.setTitle("确定要退出么？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
						System.exit(0);
					}
				})
				.setNegativeButton("不确定",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).create().show();
	}
}
