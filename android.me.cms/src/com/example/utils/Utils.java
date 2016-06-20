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
		return "[{\"name\":\"请选择\",\"sub\":[{\"name\":\"请选择\"}],\"type\":1},{\"name\":\"北京\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"东城区\"},{\"name\":\"西城区\"},{\"name\":\"崇文区\"},{\"name\":\"宣武区\"},{\"name\":\"朝阳区\"},{\"name\":\"海淀区\"},{\"name\":\"丰台区\"},{\"name\":\"石景山区\"},{\"name\":\"房山区\"},{\"name\":\"通州区\"},{\"name\":\"顺义区\"},{\"name\":\"昌平区\"},{\"name\":\"大兴区\"},{\"name\":\"怀柔区\"},{\"name\":\"平谷区\"},{\"name\":\"门头沟区\"},{\"name\":\"密云县\"},{\"name\":\"延庆县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"广东\",\"sub\":[{\"name\":\"请选择\",\"sub\":[]},{\"name\":\"广州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"越秀区\"},{\"name\":\"荔湾区\"},{\"name\":\"海珠区\"},{\"name\":\"天河区\"},{\"name\":\"白云区\"},{\"name\":\"黄埔区\"},{\"name\":\"番禺区\"},{\"name\":\"花都区\"},{\"name\":\"南沙区\"},{\"name\":\"萝岗区\"},{\"name\":\"增城市\"},{\"name\":\"从化市\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"海北藏族自治州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"海晏县\"},{\"name\":\"祁连县\"},{\"name\":\"刚察县\"},{\"name\":\"门源回族自治县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"海南藏族自治州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"共和县\"},{\"name\":\"同德县\"},{\"name\":\"贵德县\"},{\"name\":\"兴海县\"},{\"name\":\"贵南县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"黄南藏族自治州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"同仁县\"},{\"name\":\"尖扎县\"},{\"name\":\"泽库县\"},{\"name\":\"河南蒙古族自治县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"果洛藏族自治州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"玛沁县\"},{\"name\":\"班玛县\"},{\"name\":\"甘德县\"},{\"name\":\"达日县\"},{\"name\":\"久治县\"},{\"name\":\"玛多县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"玉树藏族自治州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"玉树县\"},{\"name\":\"杂多县\"},{\"name\":\"称多县\"},{\"name\":\"治多县\"},{\"name\":\"囊谦县\"},{\"name\":\"曲麻莱县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"海西蒙古族藏族自治州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"德令哈市\"},{\"name\":\"格尔木市\"},{\"name\":\"乌兰县\"},{\"name\":\"都兰县\"},{\"name\":\"天峻县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"其他\"}],\"type\":1},{\"name\":\"新疆\",\"sub\":[{\"name\":\"请选择\",\"sub\":[]},{\"name\":\"乌鲁木齐\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"天山区\"},{\"name\":\"沙依巴克区\"},{\"name\":\"新市区\"},{\"name\":\"水磨沟区\"},{\"name\":\"头屯河区\"},{\"name\":\"达坂城区\"},{\"name\":\"东山区\"},{\"name\":\"乌鲁木齐县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"克拉玛依\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"克拉玛依区\"},{\"name\":\"独山子区\"},{\"name\":\"白碱滩区\"},{\"name\":\"乌尔禾区\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"吐鲁番地区\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"吐鲁番市\"},{\"name\":\"托克逊县\"},{\"name\":\"鄯善县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"哈密地区\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"哈密市\"},{\"name\":\"伊吾县\"},{\"name\":\"巴里坤哈萨克自治县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"和田地区\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"和田市\"},{\"name\":\"和田县\"},{\"name\":\"洛浦县\"},{\"name\":\"民丰县\"},{\"name\":\"皮山县\"},{\"name\":\"策勒县\"},{\"name\":\"于田县\"},{\"name\":\"墨玉县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"阿克苏地区\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"阿克苏市\"},{\"name\":\"温宿县\"},{\"name\":\"沙雅县\"},{\"name\":\"拜城县\"},{\"name\":\"阿瓦提县\"},{\"name\":\"库车县\"},{\"name\":\"柯坪县\"},{\"name\":\"新和县\"},{\"name\":\"乌什县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"喀什地区\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"喀什市\"},{\"name\":\"巴楚县\"},{\"name\":\"泽普县\"},{\"name\":\"伽师县\"},{\"name\":\"叶城县\"},{\"name\":\"岳普湖县\"},{\"name\":\"疏勒县\"},{\"name\":\"麦盖提县\"},{\"name\":\"英吉沙县\"},{\"name\":\"莎车县\"},{\"name\":\"疏附县\"},{\"name\":\"塔什库尔干塔吉克自治县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"克孜勒苏柯尔克孜自治州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"阿图什市\"},{\"name\":\"阿合奇县\"},{\"name\":\"乌恰县\"},{\"name\":\"阿克陶县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"巴音郭楞蒙古自治州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"库尔勒市\"},{\"name\":\"和静县\"},{\"name\":\"尉犁县\"},{\"name\":\"和硕县\"},{\"name\":\"且末县\"},{\"name\":\"博湖县\"},{\"name\":\"轮台县\"},{\"name\":\"若羌县\"},{\"name\":\"焉耆回族自治县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"昌吉回族自治州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"昌吉市\"},{\"name\":\"阜康市\"},{\"name\":\"奇台县\"},{\"name\":\"玛纳斯县\"},{\"name\":\"吉木萨尔县\"},{\"name\":\"呼图壁县\"},{\"name\":\"木垒哈萨克自治县\"},{\"name\":\"米泉市\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"博尔塔拉蒙古自治州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"博乐市\"},{\"name\":\"精河县\"},{\"name\":\"温泉县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"石河子\",\"sub\":[],\"type\":0},{\"name\":\"阿拉尔\",\"sub\":[],\"type\":0},{\"name\":\"图木舒克\",\"sub\":[],\"type\":0},{\"name\":\"五家渠\",\"sub\":[],\"type\":0},{\"name\":\"伊犁哈萨克自治州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"伊宁市\"},{\"name\":\"奎屯市\"},{\"name\":\"伊宁县\"},{\"name\":\"特克斯县\"},{\"name\":\"尼勒克县\"},{\"name\":\"昭苏县\"},{\"name\":\"新源县\"},{\"name\":\"霍城县\"},{\"name\":\"巩留县\"},{\"name\":\"察布查尔锡伯自治县\"},{\"name\":\"塔城地区\"},{\"name\":\"阿勒泰地区\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"其他\"}],\"type\":1},{\"name\":\"香港\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"中西区\"},{\"name\":\"湾仔区\"},{\"name\":\"东区\"},{\"name\":\"南区\"},{\"name\":\"深水埗区\"},{\"name\":\"油尖旺区\"},{\"name\":\"九龙城区\"},{\"name\":\"黄大仙区\"},{\"name\":\"观塘区\"},{\"name\":\"北区\"},{\"name\":\"大埔区\"},{\"name\":\"沙田区\"},{\"name\":\"西贡区\"},{\"name\":\"元朗区\"},{\"name\":\"屯门区\"},{\"name\":\"荃湾区\"},{\"name\":\"葵青区\"},{\"name\":\"离岛区\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"澳门\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"花地玛堂区\"},{\"name\":\"圣安多尼堂区\"},{\"name\":\"大堂区\"},{\"name\":\"望德堂区\"},{\"name\":\"风顺堂区\"},{\"name\":\"嘉模堂区\"},{\"name\":\"圣方济各堂区\"},{\"name\":\"路凼\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"台湾\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"台北市\"},{\"name\":\"高雄市\"},{\"name\":\"台北县\"},{\"name\":\"桃园县\"},{\"name\":\"新竹县\"},{\"name\":\"苗栗县\"},{\"name\":\"台中县\"},{\"name\":\"彰化县\"},{\"name\":\"南投县\"},{\"name\":\"云林县\"},{\"name\":\"嘉义县\"},{\"name\":\"台南县\"},{\"name\":\"高雄县\"},{\"name\":\"屏东县\"},{\"name\":\"宜兰县\"},{\"name\":\"花莲县\"},{\"name\":\"台东县\"},{\"name\":\"澎湖县\"},{\"name\":\"基隆市\"},{\"name\":\"新竹市\"},{\"name\":\"台中市\"},{\"name\":\"嘉义市\"},{\"name\":\"台南市\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"海外\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"其他\"}]";
	}
}
