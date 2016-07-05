package calendarutils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.mycalendar.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

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

	public static String formatDate(Calendar calendar) {
		return formatDate(DATE_MODE1, calendar);
	}

	public static String formatDate(int year, int month, int dayOfMonth) {
		return formatDate(DATE_MODE1, year, month, dayOfMonth);
	}

	public static String formatDate(String template, Calendar calendar) {
		return formatDate(template, calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));
	}

	@SuppressLint("SimpleDateFormat")
	public static String formatDate(String template, int year, int month,
			int dayOfMonth) {
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
		Dialog(context, context.getString(tip), context.getString(content),
				null, null, null);
	}

	public static void Dialog(Context context, int tip, int content,
			final Callback callbackOK) {
		Dialog(context, context.getString(tip), context.getString(content),
				callbackOK, null, null);
	}

	public static void Dialog(Context context, int tip, int content,
			final Callback callbackOK, final Callback callbackCancel,
			final Callback callbackDismiss) {
		Dialog(context, context.getString(tip), context.getString(content),
				callbackOK, callbackCancel, callbackDismiss);
	}

	public static void Dialog(Context context, int tip, int content,
			final Callback callbackOK, final Callback callbackDismiss) {
		Dialog(context, context.getString(tip), context.getString(content),
				callbackOK, null, callbackDismiss);
	}

	public static void Dialog(Context context, String tip, String content) {
		Dialog(context, tip, content, null, null, null);
	}

	public static void Dialog(Context context, String tip, String content,
			final Callback callbackOK, final Callback callbackCancel,
			final Callback callbackDismiss) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context).setTitle(
				tip).setMessage(content);
		dialog.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (null != callbackOK) {
							callbackOK.callFinished();
						}
					}
				});
		if (null != callbackCancel) {
			dialog.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
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
		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				translateFrom, Animation.RELATIVE_TO_SELF, translateTo);
		animation.setDuration(500);
		animationSet.addAnimation(animation);
		view.startAnimation(animationSet);
		view.setVisibility(visible);
	}

	public static void animLoading(View view, boolean display) {
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
		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				translateFrom, Animation.RELATIVE_TO_SELF, translateTo);
		animation.setDuration(500);
		animationSet.addAnimation(animation);
		view.startAnimation(animationSet);
		view.setVisibility(visible);
	}

	public static void hideKeyboard(Context context) {
		View viewCur = ((Activity) context).getCurrentFocus();
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Application.INPUT_METHOD_SERVICE);
		if (null != viewCur && null != imm) {
			imm.hideSoftInputFromWindow(viewCur.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public static Bitmap compressBitmap(String path, int scalSize) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;
		options.inSampleSize = scalSize;
		Bitmap bmp = BitmapFactory.decodeFile(path, options);
		return bmp;
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float sizeRound) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(1, 1, bitmap.getWidth() - 1,
				bitmap.getHeight() - 1);
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, sizeRound, sizeRound, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
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
			result = URLEncoder.encode(component, "UTF-8")
					.replaceAll("\\%28", "(").replaceAll("\\%29", ")")
					.replaceAll("\\+", "%20").replaceAll("\\%27", "'")
					.replaceAll("\\%21", "!").replaceAll("\\%7E", "~");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressLint("SimpleDateFormat")
	public static String formatDate(String time) {
		return formatDate("yyyy-MM-dd HH:mm", time);
	}

	@SuppressLint("SimpleDateFormat")
	public static String formatDate(String temp, String time) {
		String returnTime = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Timestamp timeNow = new Timestamp(System.currentTimeMillis());
		Timestamp timeTarget = new Timestamp(Long.valueOf(time + "000"));
		long diff = (timeNow.getTime() - timeTarget.getTime()) / 1000;
		if (diff < 60 * 60) {
			returnTime = "刚刚";
		} else if (diff > 60 * 60 && diff < 60 * 60 * 2) {
			returnTime = "1小时前";
		} else if (diff >= 60 * 60 * 2 && diff < 60 * 60 * 3) {
			returnTime = "2小时前";
		} else if (diff >= 60 * 60 * 3 && diff <= 60 * 60 * 24) {
			returnTime = "今天";
		} else if (diff > 60 * 60 * 24 && diff <= 60 * 60 * 48) {
			returnTime = "昨天";
		} else {
			returnTime = formatter.format(timeTarget.getTime());
		}
		return returnTime;
	}

}
