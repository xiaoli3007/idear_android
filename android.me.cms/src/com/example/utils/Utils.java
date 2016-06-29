package com.example.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.example.cms.Consts;
import com.example.cms.R;
import com.example.cms.Cms;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

@SuppressLint("DefaultLocale")
public class Utils {
	public static final String DATE_MODE1 = "yyyy/MM/dd";
	public static final String DATE_MODE2 = "yyyy-MM-dd";

	public interface Callback {
		void callFinished();
	}

	public static boolean CheckNetwork() {
		ConnectivityManager cm = (ConnectivityManager) Cms.APP.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (null == netInfo || !netInfo.isConnected()) {
			return false;
		}
		if (netInfo.isRoaming()) {
			return true;
		}
		return true;
	}

	public static void setAR(int status) {
		if (Cms.AR_ORIGINAL != status) {
			Settings.System.putInt(Cms.APP.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, status);
			Cms.AR_CUR = status;
		}
	}

	public static void resetAR() {
		if (Cms.AR_ORIGINAL != Cms.AR_CUR) {
			Settings.System.putInt(Cms.APP.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, Cms.AR_ORIGINAL);
			Cms.AR_CUR = Cms.AR_ORIGINAL;
		}
	}

	public static String formatDate(Calendar calendar) {
		return formatDate(DATE_MODE1, calendar);
	}

	public static String formatDate(int year, int month, int dayOfMonth) {
		return formatDate(DATE_MODE1, year, month, dayOfMonth);
	}

	public static String formatDate(String template, Calendar calendar) {
		return formatDate(template, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
	}

	@SuppressLint("SimpleDateFormat")
	public static String formatDate(String template, int year, int month, int dayOfMonth) {
		SimpleDateFormat formatter = new SimpleDateFormat(template);
		GregorianCalendar date = new GregorianCalendar(year, month, dayOfMonth);
		return formatter.format(date.getTime());
	}

	public static void ShowToast(Context context, int content) {
		ShowToast(context, context.getString(content));
	}

	public static void ShowToast(Context context, String content) {
		Toast.makeText(context, content, Toast.LENGTH_LONG).show();
	}

	public static void Dialog(Context context, int tip, int content) {
		Dialog(context, context.getString(tip), context.getString(content), null, null, null);
	}

	public static void Dialog(Context context, int tip, int content, final Callback callbackOK) {
		Dialog(context, context.getString(tip), context.getString(content), callbackOK, null, null);
	}

	public static void Dialog(Context context, int tip, int content, final Callback callbackOK, final Callback callbackCancel, final Callback callbackDismiss) {
		Dialog(context, context.getString(tip), context.getString(content), callbackOK, callbackCancel, callbackDismiss);
	}

	public static void Dialog(Context context, String tip, String content) {
		Dialog(context, tip, content, null, null, null);
	}

	public static void Dialog(Context context, String tip, String content, final Callback callbackOK, final Callback callbackCancel, final Callback callbackDismiss) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context).setTitle(tip).setMessage(content);
		dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if (null != callbackOK) {
					callbackOK.callFinished();
				}
			}
		});
		if (null != callbackCancel) {
			dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					callbackCancel.callFinished();
				}
			});
		}
		AlertDialog showDialog = dialog.create();
		if (null != callbackDismiss) {
			showDialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					callbackDismiss.callFinished();
				}
			});
		}
		showDialog.show();
	}

	public static void animView(View view, boolean display) {
		float alphaFrom, alphaTo, translateFrom, translateTo;
		int visible;
		if (display) {
			alphaFrom = 0.0f;
			alphaTo = 1.0f;
			translateFrom = -1.0f;
			translateTo = 0.0f;
			visible = View.VISIBLE;
		} else {
			alphaFrom = 1.0f;
			alphaTo = 0.0f;
			translateFrom = 0.0f;
			translateTo = -1.0f;
			visible = View.GONE;
		}
		AnimationSet animationSet = new AnimationSet(true);
		Animation animation = new AlphaAnimation(alphaFrom, alphaTo);
		animation.setDuration(500);
		animationSet.addAnimation(animation);
		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, translateFrom, Animation.RELATIVE_TO_SELF, translateTo);
		animation.setDuration(500);
		animationSet.addAnimation(animation);
		view.startAnimation(animationSet);
		view.setVisibility(visible);
	}

	public static void hideKeyboard(Context context) {
		View viewCur = ((Activity) context).getCurrentFocus();
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Application.INPUT_METHOD_SERVICE);
		if (null != viewCur && null != imm) {
			imm.hideSoftInputFromWindow(viewCur.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float sizeRound) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(1, 1, bitmap.getWidth() - 1, bitmap.getHeight() - 1);
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, sizeRound, sizeRound, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static void Log(String s) {
		Log.d(Consts.TAG, s);
	}

	public static String MD5(String string) {
		byte[] bytData = null;
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			bytData = digest.digest(string.getBytes());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		StringBuffer out = new StringBuffer();
		for (byte data : bytData) {
			String hex = Integer.toHexString(data);
			if (hex.length() == 1) {
				out.append("0");
			} else if (hex.length() > 2) {
				hex = hex.substring(hex.length() - 2);
			}
			out.append(hex);
		}
		return out.toString().toLowerCase();
	}

	public static String InputStreamToString(InputStream is, String encode) {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuffer sb = new StringBuffer();
		String line = "";
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static String encodeURIComponent(String component) {
		String result = null;
		try {
			result = URLEncoder.encode(component, "UTF-8").replaceAll("\\%28", "(").replaceAll("\\%29", ")").replaceAll("\\+", "%20").replaceAll("\\%27", "'").replaceAll("\\%21", "!").replaceAll("\\%7E", "~");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String getRegions() {
		return "[{\"name\":\"请选择\",\"sub\":[{\"name\":\"请选择\"}],\"type\":1},{\"name\":\"北京\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"东城区\"},{\"name\":\"西城区\"},{\"name\":\"崇文区\"},{\"name\":\"宣武区\"},{\"name\":\"朝阳区\"},{\"name\":\"海淀区\"},{\"name\":\"丰台区\"},{\"name\":\"石景山区\"},{\"name\":\"房山区\"},{\"name\":\"通州区\"},{\"name\":\"顺义区\"},{\"name\":\"昌平区\"},{\"name\":\"大兴区\"},{\"name\":\"怀柔区\"},{\"name\":\"平谷区\"},{\"name\":\"门头沟区\"},{\"name\":\"密云县\"},{\"name\":\"延庆县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"澳门\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"花地玛堂区\"},{\"name\":\"圣安多尼堂区\"},{\"name\":\"大堂区\"},{\"name\":\"望德堂区\"},{\"name\":\"风顺堂区\"},{\"name\":\"嘉模堂区\"},{\"name\":\"圣方济各堂区\"},{\"name\":\"路凼\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"台湾\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"台北市\"},{\"name\":\"高雄市\"},{\"name\":\"台北县\"},{\"name\":\"桃园县\"},{\"name\":\"新竹县\"},{\"name\":\"苗栗县\"},{\"name\":\"台中县\"},{\"name\":\"彰化县\"},{\"name\":\"南投县\"},{\"name\":\"云林县\"},{\"name\":\"嘉义县\"},{\"name\":\"台南县\"},{\"name\":\"高雄县\"},{\"name\":\"屏东县\"},{\"name\":\"宜兰县\"},{\"name\":\"花莲县\"},{\"name\":\"台东县\"},{\"name\":\"澎湖县\"},{\"name\":\"基隆市\"},{\"name\":\"新竹市\"},{\"name\":\"台中市\"},{\"name\":\"嘉义市\"},{\"name\":\"台南市\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"海外\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"其他\"}],\"type\":0}]";
	}
}
